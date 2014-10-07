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

package zutil.test;

import java.io.IOException;
import java.util.Map;

import zutil.net.http.HttpHeaderParser;
import zutil.net.http.HttpPage;
import zutil.net.http.HttpPrintStream;
import zutil.net.http.HttpServer;



public class HTTPUploaderTest implements HttpPage{

	public static void main(String[] args) throws IOException{
		HttpServer server = new HttpServer("localhost", 80);
		server.setDefaultPage(new HTTPUploaderTest());
		server.run();
	}

	public void respond(HttpPrintStream out,
			HttpHeaderParser client_info,
			Map<String, Object> session, 
			Map<String, String> cookie,
			Map<String, String> request) {

		if(!session.containsKey("file1")){
			out.println("</html>" +
					"	<form enctype='multipart/form-data' method='post'>" +
					"		<p>Please specify a file, or a set of files:<br>" +
					"		<input type='file' name='datafile' size='40'>" +
					"		</p>" +
					"		<input type='submit' value='Send'>" +
					"	</form>" +
					"</html>");
		}	
	}

}
