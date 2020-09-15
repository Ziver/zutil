/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Ziver Koc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package zutil.net.http;

import zutil.StringUtil;
import zutil.Timer;
import zutil.log.LogUtil;
import zutil.net.threaded.ThreadedTCPNetworkServer;
import zutil.net.threaded.ThreadedTCPNetworkServerThread;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * A simple web server that handles both cookies and
 * sessions for all the clients
 *
 * @author Ziver
 */
public class HttpServer extends ThreadedTCPNetworkServer{
    private static final Logger logger = LogUtil.getLogger();

    public static final String SESSION_KEY_ID  = "session_id";
    public static final String SESSION_KEY_TTL = "session_ttl";
    public static final String SERVER_NAME     = "Zutil HttpServer";
    public static final int SESSION_TTL = 10*60*1000; // in milliseconds


    private Map<String,HttpPage> pages;
    private HttpPage defaultPage;
    private Map<String,Map<String,Object>> sessions;
    private int nextSessionId;

    /**
     * Creates a new instance of the sever
     *
     * @param   port    The port that the server should listen to
     */
    public HttpServer(int port){
        this(port, null, null);
    }


    /**
     * Creates a new instance of the sever
     *
     * @param   port            The port that the server should listen to
     * @param   keyStore        If this is not null then the server will use SSL connection with this keyStore file path
     * @param   keyStorePass    If this is not null then the server will use a SSL connection with the given certificate
     */
    public HttpServer(int port, File keyStore, String keyStorePass){
        super( port, keyStore, keyStorePass );

        pages = new ConcurrentHashMap<>();
        sessions = new ConcurrentHashMap<>();
        nextSessionId = 0;

        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleWithFixedDelay(new SessionGarbageCollector(), 10000, SESSION_TTL / 2, TimeUnit.MILLISECONDS);

        logger.info("HTTP"+(keyStore==null?"":"S")+" Server ready!");
    }

    /**
     * This class acts as an garbage collector that
     * removes old sessions from the session HashMap
     */
    private class SessionGarbageCollector implements Runnable {
        public void run(){
            Object[] keys = sessions.keySet().toArray();
            int count = 0;
            for(Object key : keys){
                Map<String,Object> session = sessions.get(key);

                // Check if session is still valid
                if(((Timer) session.get(SESSION_KEY_TTL)).hasTimedOut()){
                    sessions.remove(key);
                    ++count;
                }
            }
            if (count > 0)
                logger.fine("Removed "+count+" old sessions");
        }
    }

    /**
     * Add a HttpPage to a specific URL
     *
     * @param   name    The URL or name of the page
     * @param   page    The page itself
     */
    public void setPage(String name, HttpPage page){
        if(name.charAt(0) != '/')
            name = "/"+name;
        pages.put(name, page);
    }

    /**
     * This is a default page that will be shown
     * if there is no other matching page,
     *
     * @param   page    The HttpPage that will be shown
     */
    public void setDefaultPage(HttpPage page){
        defaultPage = page;
    }

    protected ThreadedTCPNetworkServerThread getThreadInstance( Socket s ){
        try {
            return new HttpServerThread( s );
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not start new Thread", e);
        }
        return null;
    }

    /**
     * Internal class that handles all the requests
     */
    protected class HttpServerThread implements ThreadedTCPNetworkServerThread{
        private HttpPrintStream out;
        private BufferedInputStream in;
        private Socket socket;

        public HttpServerThread(Socket socket) throws IOException{
            out = new HttpPrintStream(socket.getOutputStream());
            in = new BufferedInputStream(socket.getInputStream());
            this.socket = socket;
        }

        public void run(){
            long time = System.currentTimeMillis();
            HttpHeaderParser headerParser;
            HttpHeader header = null;
            Map<String, Object> session = null;
            try {
                //**************************** PARSE REQUEST *********************************
                headerParser = new HttpHeaderParser(in);
                header = headerParser.read();
                if (header == null) {
                    logger.finer("No header received");
                    return;
                }

                //******* Read in the post data if available
                if (header.getHeader("Content-Length") != null &&
                        header.getHeader("Content-Type") != null &&
                        header.getHeader("Content-Type").contains("application/x-www-form-urlencoded")) {
                    // Reads the post data size
                    int postDataLength = Integer.parseInt(header.getHeader("Content-Length"));
                    // read the data
                    StringBuilder tmpBuff = new StringBuilder();
                    // read the data
                    for (int i = 0; i < postDataLength; i++) {
                        tmpBuff.append((char) in.read());
                    }
                    // get the variables
                    HttpHeaderParser.parseURLParameters(header, tmpBuff.toString());
                }

                //****************************  HANDLE REQUEST *********************************
                // Get the client session or create one
                String sessionCookie = header.getCookie(SESSION_KEY_ID);
                if (sessionCookie != null && sessions.containsKey(sessionCookie) &&
                        !((Timer) sessions.get(sessionCookie).get(SESSION_KEY_TTL)).hasTimedOut()) { // Check if session is still valid

                    session = sessions.get(sessionCookie);
                    ((Timer) sessions.get(sessionCookie).get(SESSION_KEY_TTL)).start(); // renew the session TTL
                } else {
                    synchronized (sessions) {
                        session = new ConcurrentHashMap<>();
                        session.put(SESSION_KEY_ID, ""+nextSessionId);
                        session.put(SESSION_KEY_TTL, new Timer(SESSION_TTL).start());
                        sessions.put(""+nextSessionId, session);
                        out.setCookie(SESSION_KEY_ID, ""+nextSessionId);
                        ++nextSessionId;
                    }
                }

                //****************************  RESPONSE  ************************************
                out.setProtocolVersion(1.0f);
                out.setResponseStatusCode(200);
                out.setHeader("Server", SERVER_NAME);
                out.setHeader("Content-Type", "text/html");

                if (header.getRequestURL() != null && pages.containsKey(header.getRequestURL())) {
                    HttpPage page = pages.get(header.getRequestURL());
                    page.respond(out, header, session, header.getCookieMap(), header.getURLAttributeMap());
                    if (LogUtil.isLoggable(page.getClass(), Level.FINER))
                        logRequest(header, session, time);
                } else if (header.getRequestURL() != null && defaultPage != null) {
                    defaultPage.respond(out, header, session, header.getCookieMap(), header.getURLAttributeMap());
                    if (LogUtil.isLoggable(defaultPage.getClass(), Level.FINER))
                        logRequest(header, session, time);
                } else {
                    out.setResponseStatusCode(404);
                    out.println("404 Page Not Found: " + header.getRequestURL());
                    logger.warning("Page not defined: " + header.getRequestURL());
                }
                //********************************************************************************
            } catch (Exception e) {
                logRequest(header, session, time);
                logger.log(Level.SEVERE, "500 Internal Server Error", e);

                if (!out.isHeaderSent())
                    out.setResponseStatusCode(500);
                if (e.getMessage() != null)
                    out.println("500 Internal Server Error: " + e.getMessage());
                else if (e.getCause() != null) {
                    out.println("500 Internal Server Error: " + e.getCause().getMessage());
                } else {
                    out.println("500 Internal Server Error: " + e);
                }
            }
            finally {
                try{
                    out.close();
                    in.close();
                    socket.close();
                } catch( Exception e ) {
                    logger.log(Level.WARNING, "Could not close connection", e);
                }
            }
        }
    }


    protected static void logRequest(HttpHeader header,
                                   Map<String,Object> session,
                                   long time){
        // Debug
        if(logger.isLoggable(Level.FINEST) ){
            StringBuilder buff = new StringBuilder();
            buff.append("Received request: ").append(header==null ? null : header.getRequestURL());
            buff.append(", (");
            buff.append("request: ").append(header==null ? null : header.toStringAttributes());
            buff.append(", cookies: ").append(header==null ? null : header.toStringCookies());
            buff.append(", session: ").append(session);
            buff.append(")");
            buff.append(", time: ").append(StringUtil.formatTimeToString(System.currentTimeMillis() - time));

            logger.finer(buff.toString());
        } else if(logger.isLoggable(Level.FINER)){
            logger.finer(
                    "Received request: " + (header==null ? null : header.getRequestURL())
                            + ", time: "+ StringUtil.formatTimeToString(System.currentTimeMillis() - time));
        }
    }
}
