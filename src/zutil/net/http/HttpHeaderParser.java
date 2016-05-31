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

import zutil.parser.URLDecoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Pattern;

public class HttpHeaderParser {
    public static final String HEADER_COOKIE = "COOKIE";

	private static final Pattern PATTERN_COLON = Pattern.compile(":");
	private static final Pattern PATTERN_EQUAL = Pattern.compile("=");
	private static final Pattern PATTERN_AND = Pattern.compile("&");
	private static final Pattern PATTERN_SEMICOLON = Pattern.compile(";");


    private BufferedReader in;


	/**
	 * Parses the HTTP header information from a stream
	 * 
	 * @param 	in 				is the stream
	 */
	public HttpHeaderParser(BufferedReader in){
        this.in = in;
	}

	/**
	 * Parses the HTTP header information from a String
	 * 
	 * @param   in 		is the String
	 */
	public HttpHeaderParser(String in){
        this.in = new BufferedReader(new StringReader(in));
	}


    public HttpHeader read() throws IOException {
        HttpHeader header = null;
        String line = null;
        if( (line=in.readLine()) != null && !line.isEmpty() ){
            header = new HttpHeader();
            parseStatusLine(header, line);
            while( (line=in.readLine()) != null && !line.isEmpty() ){
                parseLine(header, line);
            }
            parseCookies(header);
        }
        return header;
    }


	/**
	 * Parses the first header line and ads the values to 
	 * the map and returns the file name and path
	 * 
	 * @param 	line 		The header String
	 * @return 				The path and file name as a String
	 */
	protected static void parseStatusLine(HttpHeader header, String line){
		// Server Response
		if( line.startsWith("HTTP/") ){
			header.setIsRequest(false);
			header.setHTTPVersion( Float.parseFloat( line.substring( 5 , 8)));
			header.setHTTPCode( Integer.parseInt( line.substring( 9, 12 )));
		}
		// Client Request
		else if(line.contains("HTTP/")){
			header.setIsRequest(true);
			header.setRequestType( line.substring(0, line.indexOf(" ")));
			header.setHTTPVersion( Float.parseFloat( line.substring(line.lastIndexOf("HTTP/")+5 , line.length()).trim()));
			line = (line.substring(header.getRequestType().length()+1, line.lastIndexOf("HTTP/")));

			// parse URL and attributes
			int index = line.indexOf('?');
			if(index > -1){
				header.setRequestURL( line.substring(0, index));
				line = line.substring( index+1, line.length());
				parseURLParameters(header, line);
			}
			else{
				header.setRequestURL(line);
			}
		}
	}

    /**
     * Parses the rest of the header
     *
     * @param 	line 	is the next line in the header
     */
    protected void parseLine(HttpHeader header, String line){
        String[] data = PATTERN_COLON.split( line, 2 );
        header.putHeader(
                data[0].trim().toUpperCase(), 					// Key
                (data.length>1 ? data[1] : "").trim()); 		//Value
    }

    /**
     * Parses the header "Cookie" and stores all cookies in the HttpHeader object
     */
    protected void parseCookies(HttpHeader header){
        String cookieHeader = header.getHeader(HEADER_COOKIE);
        if(cookieHeader != null && !cookieHeader.isEmpty()){
            String[] tmp = PATTERN_SEMICOLON.split(cookieHeader);
            for(String cookie : tmp){
                String[] tmp2 = PATTERN_EQUAL.split(cookie, 2);
                header.putCookie(
                        tmp2[0].trim(), 							// Key
                        (tmp2.length>1 ? tmp2[1] : "").trim()); 	//Value
            }
        }
    }

	/**
	 * Parses a String with variables from a get or post
	 * that was sent from a client
	 * 
	 * @param 	attributes 		is the String containing all the attributes
	 */
	protected static void parseURLParameters(HttpHeader header, String attributes){
		String[] tmp;
		attributes = URLDecoder.decode(attributes);
		// get the variables
		String[] data = PATTERN_AND.split( attributes );
		for(String element : data){
			tmp = PATTERN_EQUAL.split(element, 2);
			header.putURLAttribute(
					tmp[0].trim(), 								// Key
					(tmp.length>1 ? tmp[1] : "").trim()); 		//Value
		}
	}
}
