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
	 * @param out			is the PrintStream to the client
	 * @param client_info	is information about the client
	 * @param session		is session values for the client
	 * @param cookie		is cookie information from the client
	 * @param request		is POST and GET requests from the client
	 */
	public abstract void respond(HttpPrintStream out,
			HashMap<String,String> client_info,
			HashMap<String,String> session,
			HashMap<String,String> cookie, 
			HashMap<String,String> request);
}