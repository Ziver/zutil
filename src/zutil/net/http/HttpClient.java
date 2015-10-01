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

import zutil.net.http.HttpPrintStream.HttpMessageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;

/**
 * This class connects to a HTTP server and 
 * parses the result
 * 
 * @author Ziver
 */
public class HttpClient {
	public static enum HttpRequestType{
		GET, POST
	}
	
	private HttpURL url;
	private HttpRequestType type;
	private HashMap<String,String> headers;
	private HashMap<String,String> cookies;
	
	
	public static HttpClient POST(){
		return new HttpClient( HttpRequestType.POST );
	}
	
	public static HttpClient GET(){
		return new HttpClient( HttpRequestType.GET );
	}
	
	
	private HttpClient(HttpRequestType type){
		this.type = type;
		headers = new HashMap<String,String>();
		cookies = new HashMap<String,String>();
	}
	
	
	public void setURL( URL url){
		this.url = new HttpURL( url );
	}
	
	/**
	 * Adds a parameter to the request
	 */
	public void setParameter( String key, String value ){
		url.setParameter(key, value);
	}
	
	/**
	 * Adds a cookie to the request
	 */
	public void setCookie( String key, String value ){
		cookies.put(key, value);
	}
	
	/**
	 * Adds a header value to the request
	 */
	public void setHeader( String key, String value ){
		headers.put(key, value);
	}
	
	public HttpHeaderParser send() throws IOException{
		Socket conn = new Socket( url.getHost(), url.getPort());
		
		// Request
		HttpPrintStream request = new HttpPrintStream( conn.getOutputStream(), HttpMessageType.REQUEST );
		request.setRequestType( type.toString() );
		request.setRequestURL( url.getHttpURL() );
		request.setHeaders( headers );
		request.setCookies( cookies );
		
		if( type == HttpRequestType.POST ){
			String data = url.getParameterString();
			request.setHeader("Content-Length", data);
			request.println();
			request.print( data );
		}
		else
			request.println();
		request.close();
		
		// Response
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		HttpHeaderParser response = new HttpHeaderParser( in );
		conn.close();
		
		return response;
	}
}
