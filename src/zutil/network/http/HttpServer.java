package zutil.network.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import zutil.log.LogUtil;
import zutil.network.threaded.ThreadedTCPNetworkServer;
import zutil.network.threaded.ThreadedTCPNetworkServerThread;


/**
 * A simple web server that handles both cookies and
 * sessions for all the clients
 * 
 * @author Ziver
 */
public class HttpServer extends ThreadedTCPNetworkServer{
	public static final Logger logger = LogUtil.getLogger();
	public static final String SERVER_VERSION = "Ziver HttpServer 1.0";
	public static final int COOKIE_TTL = 200;
	public static final int SESSION_TTL = 10*60*1000; // in milliseconds

	public final String server_url;
	public final int server_port;

	private HashMap<String,HttpPage> pages;
	private HttpPage defaultPage;
	private Map<String,Map<String,Object>> sessions;
	private int nextSessionId;

	/**
	 * Creates a new instance of the sever
	 * 
	 * @param url The address to the server
	 * @param port The port that the server should listen to
	 */
	public HttpServer(String url, int port){
		this(url, port, null, null);
	}


	/**
	 * Creates a new instance of the sever
	 * 
	 * @param url The address to the server
	 * @param port The port that the server should listen to
	 * @param sslCert If this is not null then the server will use SSL connection with this keyStore file path
	 * @param sslCert If this is not null then the server will use a SSL connection with the given certificate
	 */
	public HttpServer(String url, int port, File keyStore, String keyStorePass){
		super( port, keyStore, keyStorePass );
		this.server_url = url;
		this.server_port = port;

		pages = new HashMap<String,HttpPage>();
		sessions = Collections.synchronizedMap(new HashMap<String,Map<String,Object>>());
		nextSessionId = 0;

		Timer timer = new Timer();
		timer.schedule(new GarbageCollector(), 0, SESSION_TTL / 2);

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
	 * @param name The URL or name of the page
	 * @param page The page itself
	 */
	public void setPage(String name, HttpPage page){
		pages.put(name, page);
	}

	/**
	 * This is a default page that will be shown
	 * if there is no other matching page,
	 * 
	 * @param page The HttpPage that will be shown
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
			logger.fine("New Connection!!! "+socket.getInetAddress().getHostName());
		}

		public void run(){
			String tmp = null;

			HashMap<String,String> client_info = new HashMap<String,String>();
			HashMap<String,String> cookie = new HashMap<String,String>();
			HashMap<String,String> request = new HashMap<String,String>();

			//****************************  REQUEST *********************************
			try {
				logger.finer("Reciving Http Request!!!");

				HTTPHeaderParser parser = new HTTPHeaderParser(in);
				logger.finest(parser.toString());
				client_info = parser.getAttributes();
				request = parser.getURLAttributes();
				cookie = parser.getCookies();


				//******* Read in the post data if available
				if( parser.getHTTPAttribute("Content-Length")!=null ){
					// Reads the post data size
					tmp = parser.getHTTPAttribute("Content-Length");
					int post_data_length = Integer.parseInt( tmp );
					// read the data
					StringBuffer tmpb = new StringBuffer();
					// read the data
					for(int i=0; i<post_data_length ;i++){
						tmpb.append((char)in.read());
					}

					tmp = parser.getHTTPAttribute("Content-Type");
					if( tmp.contains("application/x-www-form-urlencoded") ){
						// get the variables
						HTTPHeaderParser.parseUrlAttributes( tmpb.toString(), request );
					}
					else if( tmp.contains("application/soap+xml" ) || 
							tmp.contains("text/xml") || 
							tmp.contains("text/plain") ){
						// save the variables
						request.put( "" , tmpb.toString() );
					}
					else if( tmp.contains("multipart/form-data") ){
						// TODO: File upload					
						throw new Exception( "\"multipart-form-data\" Not implemented!!!" );
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
					client_session.put( "ttl", ttl_time );
				}
				else{
					client_session = Collections.synchronizedMap(new HashMap<String, Object>());
					client_session.put( "session_id", nextSessionId );
					client_session.put( "ttl", ttl_time );
					sessions.put( ""+nextSessionId, client_session );
					nextSessionId++;
				}
				// Debug
				if(logger.isLoggable(Level.FINE)){
					logger.finest( "# page_url: "+parser.getRequestURL() );
					logger.finest( "# cookie: "+cookie );
					logger.finest( "# client_session: "+client_session );
					logger.finest( "# client_info: "+client_info );
					logger.finest( "# request: "+request );
				}
				//****************************  RESPONSE  ************************************
				logger.finer("Sending Http Response!!!");
				out.setStatusCode(200);
				out.setHeader( "Server", SERVER_VERSION );
				out.setHeader( "Content-Type", "text/html" );
				out.setCookie( "session_id", ""+client_session.get("session_id") );

				if( !parser.getRequestURL().isEmpty() && pages.containsKey(parser.getRequestURL()) ){
					pages.get(parser.getRequestURL()).respond(out, client_info, client_session, cookie, request);
				}
				else if( defaultPage != null ){
					defaultPage.respond(out, client_info, client_session, cookie, request);
				}
				else{
					out.setStatusCode( 404 );
					out.println( "404 Page Not Found: "+parser.getRequestURL() );
					logger.fine( "404 Page Not Found: "+parser.getRequestURL() );
				}

				//********************************************************************************
			} catch (Exception e) {
				logger.log(Level.WARNING, "500 Internal Server Error", e);
				try {
					out.setStatusCode( 500 );
				} catch (Exception e1) {}
				if(e.getMessage() != null)
					out.println( "500 Internal Server Error: "+e.getMessage() );
				else{
					out.println( "500 Internal Server Error: "+e.getCause().getMessage() );
				}				
			}

			try{
				logger.fine("Conection Closed!!!");
				out.close();
				in.close();
				socket.close();
			} catch( Exception e ) {
				logger.log(Level.WARNING, "Could not close connection", e);
			}
		}
	}
}