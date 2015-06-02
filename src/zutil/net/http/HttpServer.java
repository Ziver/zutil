/*
 * Copyright (c) 2015 ezivkoc
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
import zutil.log.LogUtil;
import zutil.net.threaded.ThreadedTCPNetworkServer;
import zutil.net.threaded.ThreadedTCPNetworkServerThread;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
	public static final String SERVER_VERSION = "Ziver HttpServer 1.0";
	public static final int COOKIE_TTL = 200;
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

		pages = new ConcurrentHashMap<String,HttpPage>();
		sessions = new ConcurrentHashMap<String,Map<String,Object>>();
		nextSessionId = 0;

		Timer timer = new Timer();
		timer.schedule(new GarbageCollector(), 10000, SESSION_TTL / 2);

		logger.info("HTTP"+(keyStore==null?"":"S")+" Server ready!");
	}

	/**
	 * This class acts as an garbage collector that 
	 * removes old sessions from the session HashMap
	 * 
	 * @author Ziver
	 */
	private class GarbageCollector extends TimerTask {
		public void run(){
			Object[] keys = sessions.keySet().toArray();
			for(Object key : keys){
				Map<String,Object> client_session = sessions.get(key);

				// Check if session is still valid
				if((Long)client_session.get("ttl") < System.currentTimeMillis()){
					sessions.remove(key);
					logger.fine("Removing Session: "+key);
				}
			}
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
	 * 
	 * @author Ziver
	 *
	 */
	protected class HttpServerThread implements ThreadedTCPNetworkServerThread{
		private HttpPrintStream out;
		private BufferedReader in;
		private Socket socket;

		public HttpServerThread(Socket socket) throws IOException{
			out = new HttpPrintStream(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.socket = socket;
			//logger.finest("New Connection: " + socket.getInetAddress().getHostName());
		}

		public void run(){
			String tmp = null;

			HashMap<String,String> cookie = new HashMap<String,String>();
			HashMap<String,String> request = new HashMap<String,String>();

			//****************************  REQUEST *********************************
			try {
				long time = System.currentTimeMillis();
				HttpHeaderParser parser = new HttpHeaderParser(in);
				//logger.finest(parser.toString());
				request = parser.getURLAttributes();
				cookie = parser.getCookies();


				//******* Read in the post data if available
				if( parser.getHeader("Content-Length")!=null ){
					// Reads the post data size
					tmp = parser.getHeader("Content-Length");
					int post_data_length = Integer.parseInt( tmp );
					// read the data
					StringBuffer tmpb = new StringBuffer();
					// read the data
					for(int i=0; i<post_data_length ;i++){
						tmpb.append((char)in.read());
					}

					tmp = parser.getHeader("Content-Type");
					if( tmp.contains("application/x-www-form-urlencoded") ){
						// get the variables
						HttpHeaderParser.parseURLParameters( tmpb.toString(), request );
					}
					else if( tmp.contains("application/soap+xml" ) || 
							tmp.contains("text/xml") || 
							tmp.contains("text/plain") ){
						// save the variables
						request.put( "" , tmpb.toString() );
					}
					else if( tmp.contains("multipart/form-data") ){
						// TODO: File upload					
						throw new Exception( "\"multipart-form-data\" Not implemented." );
					}						
				}

				//****************************  HANDLE REQUEST *********************************
				// Get the client session or create one
				Map<String, Object> client_session;
				long ttl_time = System.currentTimeMillis()+SESSION_TTL;
				if( cookie.containsKey("session_id") && sessions.containsKey(cookie.get("session_id")) ){
					client_session = sessions.get( cookie.get("session_id") );
					// Check if session is still valid
					if( (Long)client_session.get("ttl") < System.currentTimeMillis() ){
						int session_id = (Integer)client_session.get("session_id");
						client_session = Collections.synchronizedMap(new HashMap<String, Object>());
						client_session.put( "session_id", session_id);
						sessions.put( ""+session_id, client_session);
					}
					// renew the session TTL
					client_session.put("ttl", ttl_time);
				}
				else{
					client_session = Collections.synchronizedMap(new HashMap<String, Object>());
					client_session.put( "session_id", nextSessionId );
					client_session.put( "ttl", ttl_time );
					sessions.put( ""+nextSessionId, client_session );
					++nextSessionId;
				}

				//****************************  RESPONSE  ************************************
				out.setStatusCode(200);
				out.setHeader( "Server", SERVER_VERSION );
				out.setHeader( "Content-Type", "text/html" );
				out.setCookie( "session_id", ""+client_session.get("session_id") );

				if( !parser.getRequestURL().isEmpty() && pages.containsKey(parser.getRequestURL()) ){
					pages.get(parser.getRequestURL()).respond(out, parser, client_session, cookie, request);
					logRequest(parser, client_session, cookie, request, time);
				}
				else if( defaultPage != null ){
					defaultPage.respond(out, parser, client_session, cookie, request);
					logRequest(parser, client_session, cookie, request, time);
				}
				else{
					out.setStatusCode( 404 );
					out.println( "404 Page Not Found: "+parser.getRequestURL() );
					logger.warning("Page not defined: " + parser.getRequestURL());
				}

				//********************************************************************************
			} catch (Exception e) {
				logger.log(Level.WARNING, "500 Internal Server Error", e);
				try {
					if (!out.isHeaderSent())
						out.setStatusCode(500);
					if (e.getMessage() != null)
						out.println("500 Internal Server Error: " + e.getMessage());
					else if (e.getCause() != null) {
						out.println("500 Internal Server Error: " + e.getCause().getMessage());
					} else {
						out.println("500 Internal Server Error: " + e);
					}
				}catch(IOException ioe){
					logger.log(Level.SEVERE, null, ioe);
				}
			}

			try{
				//logger.finest("Closing Connection: "+socket.getInetAddress().getHostName());
				out.close();
				in.close();
				socket.close();
			} catch( Exception e ) {
				logger.log(Level.WARNING, "Could not close connection", e);
			}
		}

		private void logRequest(HttpHeaderParser parser,
								Map<String,Object> client_session,
								Map<String,String> cookie,
								Map<String,String> request,
								long time){
			// Debug
			if(logger.isLoggable(Level.FINEST) ){
				logger.finer(
						"Received request: " + parser.getRequestURL()
						+ " (client_session: " + client_session
						+ ", cookie: " + cookie
						+ ", request: " + request + ")"
						+ ", time: "+ StringUtil.formatTimeToString(System.currentTimeMillis() - time));
			} else if(logger.isLoggable(Level.FINER)){
				logger.finer(
						"Received request: " + parser.getRequestURL()
						+ ", time: "+ StringUtil.formatTimeToString(System.currentTimeMillis() - time));
			}
		}
	}
}
