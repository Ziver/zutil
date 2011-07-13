/*******************************************************************************
 * Copyright (c) 2011 Ziver Koc
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

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;

/**
 * This PrintStream is written for HTTP use
 * It has buffer capabilities and cookie management.
 * 
 * @author Ziver
 *
 */
public class HttpPrintStream extends PrintStream{
	// Defines the type of message
	public enum HttpMessageType{
		REQUEST,
		RESPONSE
	}

	// This defines the type of message that will be generated
	private HttpMessageType message_type;
	// The status code of the message, ONLY for response
	private Integer res_status_code;
	// The request type of the message ONLY for request
	private String req_type;
	// The requesting url ONLY for request
	private String req_url;
	// An Map of all the header values
	private HashMap<String, String> headers;
	// An Map of all the cookies
	private HashMap<String, String> cookies;
	// The buffered header
	private StringBuffer buffer;
	// If the header buffering is enabled
	private boolean buffer_enabled;	

	/**
	 * Creates an new instance of HttpPrintStream with
	 * message type of RESPONSE and buffering disabled.
	 * 
	 * @param out is the OutputStream to send the message
	 */
	public HttpPrintStream(OutputStream out) {
		this( out, HttpMessageType.RESPONSE );
	}
	/**
	 * Creates an new instance of HttpPrintStream with
	 * message type buffering disabled.
	 * 
	 * @param out is the OutputStream to send the message
	 * @param type is the type of message
	 */
	public HttpPrintStream(OutputStream out, HttpMessageType type) {
		super(out);

		this.message_type = type;
		res_status_code = 0;
		headers = new HashMap<String, String>();
		cookies = new HashMap<String, String>();
		buffer = new StringBuffer();
		buffer_enabled = false;
	}

	/**
	 * Enable the buffering capability of the PrintStream.
	 * Nothing will be sent to the client when buffering
	 * is enabled until you close or flush the stream. 
	 * This function will flush the stream if buffering is
	 * disabled.
	 * 
	 * @param b
	 */
	public void enableBuffering(boolean b){
		buffer_enabled = b;
		if(!buffer_enabled) flush();
	}

	/**
	 * Adds a cookie that will be sent to the client
	 * 
	 * @param key is the name of the cookie
	 * @param value is the value of the cookie
	 * @throws Exception Throws exception if the header has already been sent
	 */
	public void setCookie(String key, String value) throws RuntimeException{
		if(cookies == null)
			throw new RuntimeException("Header already sent!!!");
		cookies.put(key, value);
	}

	/**
	 * Adds an header value
	 * 
	 * @param key is the header name
	 * @param value is the value of the header
	 * @throws Exception Throws exception if the header has already been sent
	 */
	public void setHeader(String key, String value) throws RuntimeException{
		if(headers == null)
			throw new RuntimeException("Header already sent!!!");
		headers.put(key, value);
	}

	/**
	 * Sets the status code of the message, ONLY available in HTTP RESPONSE
	 * 
	 * @param code the code from 100 up to 599
	 * @throws RuntimeException if the header has already been sent or the message type is wrong
	 */
	public void setStatusCode(int code) throws RuntimeException{
		if( res_status_code == null )
			throw new RuntimeException("Header already sent!!!");
		if( message_type != HttpMessageType.RESPONSE )
			throw new RuntimeException("Status Code is only available in HTTP RESPONSE!!!");
		res_status_code = code;
	}

	/**
	 * Sets the request type of the message, ONLY available in HTTP REQUEST
	 * 
	 * @param req_type is the type of the message, e.g. GET, POST...
	 * @throws RuntimeException if the header has already been sent or the message type is wrong
	 */
	public void setRequestType(String req_type) throws RuntimeException{
		if( req_type == null )
			throw new RuntimeException("Header already sent!!!");
		if( message_type != HttpMessageType.REQUEST )
			throw new RuntimeException("Request Message Type is only available in HTTP REQUEST!!!");
		this.req_type = req_type;
	}
	/**
	 * Sets the requesting URL of the message, ONLY available in HTTP REQUEST
	 * 
	 * @param req_url is the URL
	 * @throws RuntimeException if the header has already been sent or the message type is wrong
	 */
	public void setRequestURL(String req_url) throws RuntimeException{
		if( req_url == null )
			throw new RuntimeException("Header already sent!!!");
		if( message_type != HttpMessageType.REQUEST )
			throw new RuntimeException("Request URL is only available in HTTP REQUEST!!!");
		this.req_url = req_url;
	}

	protected void setHeaders( HashMap<String,String> map ){
		headers = map;
	}
	protected void setCookies( HashMap<String,String> map ){
		cookies = map;
	}
	
	/**
	 * Prints with a new line
	 */
	public void println(String s){
		printOrBuffer(s+"\n");
	}

	/**
	 * Prints an string
	 */
	public void print(String s){
		printOrBuffer(s);
	}

	/**
	 * prints to all
	 */
	private void printOrBuffer(String s){
		if(buffer_enabled){
			buffer.append(s);
		}
		else{
			if(res_status_code != null){
				if( message_type==HttpMessageType.REQUEST ) 
					super.print(req_type+" "+req_url+" HTTP/1.0");	
				else 
					super.print("HTTP/1.0 "+res_status_code+" "+getStatusString(res_status_code));				
				super.println();
				res_status_code = null;
				req_type = null;
				req_url = null;
			}
			if(headers != null){
				for(String key : headers.keySet()){					
					super.print(key+": "+headers.get(key));
					super.println();
				}
				headers = null;
			}
			if(cookies != null){
				if( !cookies.isEmpty() ){
					if( message_type==HttpMessageType.REQUEST ){
						super.print("Cookie: ");
						for(String key : cookies.keySet()){					
							super.print(key+"="+cookies.get(key)+"; ");
						}
						super.println();
					}
					else{
						for(String key : cookies.keySet()){					
							super.print("Set-Cookie: "+key+"="+cookies.get(key)+";");
							super.println();
						}
					}
				}
				super.println();
				cookies = null;
			}
			super.print(s);
		}
	}

	/**
	 * Sends out the buffer and clears it
	 */
	public void flush(){
		if(buffer_enabled){
			buffer_enabled = false;
			printOrBuffer(buffer.toString());
			buffer.delete(0, buffer.length());
			buffer_enabled = true;
		}
		else if(res_status_code != null || headers != null || cookies != null){
			printOrBuffer("");
		}
		super.flush();
	}

	public void close(){
		flush();
		super.close();
	}

	public void println(){			println("");}
	public void println(boolean x){	println(String.valueOf(x));}
	public void println(char x){	println(String.valueOf(x));}
	public void println(char[] x){	println(new String(x));}
	public void println(double x){	println(String.valueOf(x));}
	public void println(float x){	println(String.valueOf(x));}
	public void println(int x){		println(String.valueOf(x));}
	public void println(long x){	println(String.valueOf(x));}
	public void println(Object x){	println(String.valueOf(x));}

	public void print(boolean x){	printOrBuffer(String.valueOf(x));}
	public void print(char x){		printOrBuffer(String.valueOf(x));}
	public void print(char[] x){	printOrBuffer(new String(x));}
	public void print(double x){	printOrBuffer(String.valueOf(x));}
	public void print(float x){		printOrBuffer(String.valueOf(x));}
	public void print(int x){		printOrBuffer(String.valueOf(x));}
	public void print(long x){		printOrBuffer(String.valueOf(x));}
	public void print(Object x){	printOrBuffer(String.valueOf(x));}

	/*
	public void write(int b) {		print((char)b);}	
	public void write(byte buf[], int off, int len){
									print(new String(buf, off, len));}
	 */
	private String getStatusString(int type){
		switch(type){
		case 100: return "Continue";
		case 200: return "OK";
		case 301: return "Moved Permanently";
		case 307: return "Temporary Redirect";
		case 400: return "Bad Request";
		case 401: return "Unauthorized";
		case 403: return "Forbidden";
		case 404: return "Not Found";
		case 500: return "Internal Server Error";
		case 501: return "Not Implemented";
		default:  return "";
		}
	}
}
