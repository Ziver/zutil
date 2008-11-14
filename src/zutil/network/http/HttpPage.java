package zutil.network.http;

import java.util.HashMap;

/**
 * This is a interface for a ordinary page for the HttpServer
 * 
 * @author Ziver
 *
 */
public interface HttpPage{	
	/**
	 * This method has to be implemented for every page.
	 * This method is called when a client wants a response
	 * from this specific page.
	 * 
	 * @param out			The PrintStream to the client
	 * @param client_info	Information about the client
	 * @param session		Session values for the client
	 * @param cookie		Cookie information from the client
	 * @param request		POST and GET requests from the client
	 */
	public abstract void respond(HttpPrintStream out,
			HashMap<String,String> client_info,
			HashMap<String,String> session,
			HashMap<String,String> cookie, 
			HashMap<String,String> request);
}