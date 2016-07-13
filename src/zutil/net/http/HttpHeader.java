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

import zutil.converter.Converter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

public class HttpHeader {
	// HTTP info
	private boolean request;
	private String type;
	private String url;
	private HashMap<String, String> urlAttributes;
	private float version;
	private int httpCode;
	private InputStream in;

	// Parameters
	private HashMap<String, String> headers;
	private HashMap<String, String> cookies;


    protected HttpHeader(){
        urlAttributes = new HashMap<String, String>();
        headers = new HashMap<String, String>();
        cookies = new HashMap<String, String>();
    }


	/**
	 * @return      true if this header represents a server response
	 */
	public boolean isResponse(){
		return !request;
	}
	/**
	 * @return      true if this header represents a client request
     */
	public boolean isRequest(){
		return request;
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
	public int getHTTPCode(){
		return httpCode;
	}
	/**
	 * @return 		the URL that the client sent the server
	 */
	public String getRequestURL(){
		return url;
	}
	/**
	 * @return parses out the page name from the request url and returns it.
     */
	public String getRequestPage() {
		if (url != null){
            int start = 0;
            if (url.charAt(0) == '/')
                start = 1;
			int end = url.indexOf('?');
			if (end < 0)
                end = url.length();

            return url.substring(start, end);
		}
		return null;
	}
    /**
     * @return      a Iterator with all defined url keys
     */
    public Iterator<String> getURLAttributeKeys(){
        return urlAttributes.keySet().iterator();
    }
    /**
	 * @return     the URL attribute value of the given name. null if there is no such attribute
	 */
	public String getURLAttribute(String name){
		return urlAttributes.get( name );
	}
    /**
     * @return      a Iterator with all defined headers
     */
    public Iterator<String> getHeaderKeys(){
        return headers.keySet().iterator();
    }
	/**
	 * @return     the HTTP attribute value of the given name. null if there is no such attribute
	 */
	public String getHeader(String name){
		return headers.get( name.toUpperCase() );
	}
    /**
     * @return      a Iterator with all defined cookies
     */
    public Iterator<String> getCookieKeys(){
        return cookies.keySet().iterator();
    }
	/**
	 * @return     the cookie value of the given name. null if there is no such attribute.
	 */
	public String getCookie(String name){
		return cookies.get( name );
	}
    /**
     * @return     a Reader that contains the body of the http request.
     */
    public InputStream getInputStream(){
        return in;
    }


	protected void setIsRequest(boolean request) { this.request = request; }
    protected void setRequestType(String type){
        this.type = type;
    }
    protected void setHTTPVersion(float version){
        this.version = version;
    }
    protected void setHTTPCode(int code){
        this.httpCode = code;
    }
    protected void setRequestURL(String url){
        this.url = url.trim().replaceAll("//", "/");
    }
    protected void setInputStream(InputStream in){
        this.in = in;
    }

	protected HashMap<String,String> getHeaderMap(){
		return headers;
	}
	protected HashMap<String,String> getCookieMap(){
		return cookies;
	}
	protected HashMap<String,String> getUrlAttributeMap(){
		return urlAttributes;
	}



	public String toString(){
		StringBuilder tmp = new StringBuilder();
		tmp.append("{Type: ").append(type);
		tmp.append(", HTTP_version: HTTP/").append(version);
		if(url == null)
			tmp.append(", URL: null");
		else
			tmp.append(", URL: \"").append(url).append('\"');

		tmp.append(", URL_attr: ").append(toStringAttributes());
		tmp.append(", Headers: ").append(toStringHeaders());
		tmp.append(", Cookies: ").append(toStringCookies());

		tmp.append('}');
		return tmp.toString();
	}
	public String toStringHeaders(){
		return Converter.toString(headers);
	}
	public String toStringCookies(){
		return Converter.toString(cookies);
	}
	public String toStringAttributes(){
		return Converter.toString(urlAttributes);
	}

}
