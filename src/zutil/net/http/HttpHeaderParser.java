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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;

public class HttpHeaderParser {
	// Some Cached regex's
	private static final Pattern colonPattern = Pattern.compile(":");	
	private static final Pattern equalPattern = Pattern.compile("=");
	private static final Pattern andPattern = Pattern.compile("&");
	private static final Pattern semiColonPattern = Pattern.compile(";");

	// HTTP info
	private String type;
	private String url;
	private HashMap<String, String> url_attr;
	private float version;
	private int httpCode;

	// Parameters
	private HashMap<String, String> headers;
	private HashMap<String, String> cookies;

	/**
	 * Parses the HTTP header information from the stream
	 * 
	 * @param 	in 				is the stream
	 * @throws 	IOException 
	 */
	public HttpHeaderParser(BufferedReader in) throws IOException{
		url_attr = new HashMap<String, String>();
		headers = new HashMap<String, String>();
		cookies = new HashMap<String, String>();

		String tmp = null;
		if( (tmp=in.readLine()) != null && !tmp.isEmpty() ){
			parseStatusLine( tmp );
			while( (tmp=in.readLine()) != null && !tmp.isEmpty() ){
				parseLine( tmp );
			}
		}
		parseCookies();
	}

	/**
	 * Parses the HTTP header information from an String
	 * 
	 * @param   in 		is the string
	 */
	public HttpHeaderParser(String in){
		url_attr = new HashMap<String, String>();
		headers = new HashMap<String, String>();
		cookies = new HashMap<String, String>();

		Scanner sc = new Scanner(in);
		sc.useDelimiter("\n");
		String tmp = null;
		if( sc.hasNext() && !(tmp=sc.next()).isEmpty() ){
			parseStatusLine( tmp );
			while( sc.hasNext() && !(tmp=sc.next()).isEmpty() ){
				parseLine( tmp );
			}
		}
		sc.close();
		parseCookies();
	}

	/**
	 * Parses the first header line and ads the values to 
	 * the map and returns the file name and path
	 * 
	 * @param 	line 		The header String
	 * @return 				The path and file name as a String
	 */
	protected void parseStatusLine(String line){
		// Server Response
		if( line.startsWith("HTTP/") ){
			version = Float.parseFloat( line.substring( 5 , 8) );
			httpCode = Integer.parseInt( line.substring( 9, 12 ));
		}
		// Client Request
		else if(line.contains("HTTP/")){
			type = (line.substring(0, line.indexOf(" "))).trim();
			version = Float.parseFloat( line.substring(line.lastIndexOf("HTTP/")+5 , line.length()).trim() );
			line = (line.substring(type.length()+1, line.lastIndexOf("HTTP/"))).trim();

			// parse URL and attributes
			int index = line.indexOf('?');
			if(index > -1){ 
				url = line.substring(0, index );
				line = line.substring( index+1, line.length());
				parseURLParameters(line, url_attr);
			}
			else{
				url = line;
			}
			
			url = url.replaceAll("//", "/");
		}
	}

	/**
	 * Parses a String with variables from a get or post
	 * that was sent from a client and puts the data into a HashMap
	 * 
	 * @param 	attributes 		is the String containing all the attributes
	 */
	public static HashMap<String, String> parseURLParameters( String attributes ){
		HashMap<String, String> map = new HashMap<String, String>();
		parseURLParameters(attributes, map);
		return map;
	}
	
	/**
	 * Parses a String with variables from a get or post
	 * that was sent from a client and puts the data into a HashMap
	 * 
	 * @param 	attributes 		is the String containing all the attributes
	 * @param 	map 			is the HashMap to put all the values into
	 */
	public static void parseURLParameters(String attributes, HashMap<String, String> map){
		String[] tmp;
		// get the variables
		String[] data = andPattern.split( attributes );
		for(String element : data){
			tmp = equalPattern.split(element, 2);
			map.put(
					tmp[0].trim(), 								// Key
					(tmp.length>1 ? tmp[1] : "").trim()); 		//Value
		}
	}

	/**
	 * Parses the rest of the header
	 * 
	 * @param 	line 	is the next line in the header
	 */
	protected void parseLine(String line){
		String[] data = colonPattern.split( line, 2 );
		headers.put(
				data[0].trim().toUpperCase(), 					// Key
				(data.length>1 ? data[1] : "").trim()); 		//Value
	}

	/**
	 * Parses the attribute "Cookie" and returns a HashMap
	 * with the values
	 * 
	 * @return 		a HashMap with cookie values
	 */
	protected void parseCookies(){
		if( headers.containsKey("COOKIE") ){
			String[] tmp = semiColonPattern.split( headers.get("COOKIE") );
			String[] tmp2;
			for(String cookie : tmp){
				tmp2 = equalPattern.split(cookie, 2);
				cookies.put(
						tmp2[0].trim(), 							// Key
						(tmp2.length>1 ? tmp2[1] : "").trim()); 	//Value
			}
		}
	}

	/**
	 * @return 		the HTTP message type( ex. GET,POST...)
	 */
	public String getRequestType(){
		return type;
	}
	/**
	 * @return 		the HTTP version of this header
	 */
	public float getHTTPVersion(){
		return version;
	}
	/**
	 * @return 		the HTTP Return Code from a Server
	 */
	public float getHTTPCode(){
		return httpCode;
	}
	/**
	 * @return 		the URL that the client sent the server
	 */
	public String getRequestURL(){
		return url;
	}
	/**
	 * Returns the URL attribute value of the given name.
	 * 
	 * returns 		null if there is no such attribute
	 */
	public String getURLAttribute(String name){
		return url_attr.get( name );
	}
	/**
	 * Returns the HTTP attribute value of the given name.
	 * 
	 * returns 		null if there is no such attribute
	 */
	public String getHeader(String name){
		return headers.get( name.toUpperCase() );
	}
	/**
	 * Returns the cookie value of the given name.
	 * 
	 * returns 		null if there is no such attribute
	 */
	public String getCookie(String name){
		return cookies.get( name );
	}	


	/**
	 * @return 		a map of the parsed cookies
	 */
	public HashMap<String, String> getCookies(){
		return cookies;
	}
	/**
	 * @return 		a map of the parsed URL attributes
	 */
	public HashMap<String, String> getURLAttributes(){
		return url_attr;
	}
	/**
	 * @return 		a map of the parsed headers
	 */
	public HashMap<String, String> getHeaders(){
		return headers;
	}


	public String toString(){
		StringBuffer tmp = new StringBuffer();
		tmp.append("{Type: ").append(type);
		tmp.append(", HTTP Version: HTTP/").append(version);
		tmp.append(", URL: ").append(url);

		tmp.append(", URL Attr: { ");
		for( String key : url_attr.keySet() ){
			tmp.append(key);
			tmp.append(": ");
			tmp.append( url_attr.get(key) );
		}
		tmp.append('}');

		tmp.append(", Header: {");
		for( String key : headers.keySet() ){
			tmp.append(key);
			tmp.append(": ");
			tmp.append( headers.get(key) );
		}
		tmp.append('}');

		tmp.append(", Cookie: {");
		for( String key : cookies.keySet() ){
			tmp.append(key);
			tmp.append(": ");
			tmp.append( cookies.get(key) );
		}
		tmp.append('}');

		tmp.append('}');
		return tmp.toString();
	}
}
