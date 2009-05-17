package zutil.network.http;

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
	private Integer status_code;
	private HashMap<String, String> header;
	private HashMap<String, String> cookie;
	private StringBuffer buffer;
	private boolean buffer_enabled;

	public HttpPrintStream(OutputStream out) {
		super(out);

		status_code = 0;
		header = new HashMap<String, String>();
		cookie = new HashMap<String, String>();
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
	public void setCookie(String key, String value) throws Exception{
		if(cookie == null)
			throw new Exception("Header already sent!!!");
		cookie.put(key, value);
	}

	/**
	 * Adds an header value
	 * 
	 * @param key is the header name
	 * @param value is the value of the header
	 * @throws Exception Throws exception if the header has already been sent
	 */
	public void setHeader(String key, String value) throws Exception{
		if(header == null)
			throw new Exception("Header already sent!!!");
		header.put(key, value);
	}
	
	/**
	 * Sets the return status code
	 * 
	 * @param code the code from 100 up to 599
	 * @throws Exception Throws exception if the header has already been sent
	 */
	public void setStatusCode(int code) throws Exception{
		if(status_code == null)
			throw new Exception("Header already sent!!!");
		status_code = code;
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
			if(status_code != null){
				super.print("HTTP/1.0 "+status_code+" "+getStatusString(status_code));
				super.println();
				status_code = null;
			}
			if(header != null){
				for(String key : header.keySet()){					
					super.print(key+": "+header.get(key));
					super.println();
				}
				header = null;
			}
			if(cookie != null){
				for(String key : cookie.keySet()){					
					super.print("Set-Cookie: "+key+"="+cookie.get(key)+";");
					super.println();
				}
				super.println();
				cookie = null;
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
		else if(status_code != null || header != null || cookie != null){
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
