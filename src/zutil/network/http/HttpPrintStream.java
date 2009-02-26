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
	private HashMap<String, String> cookie;
	private StringBuffer buffer;
	private boolean buffer_enabled;

	public HttpPrintStream(OutputStream out) {
		super(out);

		cookie = new HashMap<String, String>();
		buffer = new StringBuffer();
		buffer_enabled = false;
	}

	/**
	 * Enable the buffering capability of the PrintStream.
	 * Nothing will be sent to the client when buffering
	 * is enabled until you close or flush the stream.
	 * 
	 * @param b
	 */
	public void enableBuffering(boolean b){
		buffer_enabled = b;
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
	 * Sends the given header directly to the client.
	 * No buffering involved.
	 * 
	 * @param header is the header to send
	 * @throws Exception Throws exception if the header has already been sent
	 */
	public void sendHeader(String header) throws Exception{
		if(cookie == null)
			throw new Exception("Header already sent!!!");
		super.println(header);
	}

	/**
	 * Prints with a new line
	 */
	public void println(String s){
		printOrBuffer(s+"\n");
	}

	/**
	 * NOT TO BE USED!!!!
	 * use printOrBuffer(String s) instead
	 */
	@Deprecated
	public void print(String s){
		super.print(s);
	}
	
	/**
	 * prints to all
	 */
	public void printOrBuffer(String s){
		if(buffer_enabled){
			buffer.append(s);
		}
		else{
			if(cookie != null){
				for(String key : cookie.keySet()){					
					super.println("Set-Cookie: "+key+"="+cookie.get(key)+"; ");
				}
				super.println(" \n");
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
		super.flush();
	}
	
	public void close(){
		flush();
		super.close();
	}
	
	public void println(){			println("");}
	public void println(boolean x){	println(""+x);}
	public void println(char x){	println(""+x);}
	public void println(char[] x){	println(new String(x));}
	public void println(double x){	println(""+x);}
	public void println(float x){	println(""+x);}
	public void println(int x){		println(""+x);}
	public void println(long x){	println(""+x);}
	public void println(Object x){	println(""+x);}
	
	public void print(boolean x){	printOrBuffer(""+x);}
	public void print(char x){		printOrBuffer(""+x);}
	public void print(char[] x){	printOrBuffer(new String(x));}
	public void print(double x){	printOrBuffer(""+x);}
	public void print(float x){		printOrBuffer(""+x);}
	public void print(int x){		printOrBuffer(""+x);}
	public void print(long x){		printOrBuffer(""+x);}
	public void print(Object x){	printOrBuffer(""+x);}
}
