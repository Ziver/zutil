/*******************************************************************************
 * Copyright (c) 2013 Ziver
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
 ******************************************************************************/

package zutil.net.http;

import java.io.IOException;
import java.util.Map;

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
	 * @param out           is the PrintStream to the client
	 * @param client_info   is information about the client
     * @param session       is session values for the client
     * @param cookie        is cookie information from the client
     * @param request       is POST and GET requests from the client
     */
	public abstract void respond(HttpPrintStream out,
			HttpHeaderParser client_info,
			Map<String,Object> session,
			Map<String,String> cookie, 
			Map<String,String> request) throws IOException;
}
