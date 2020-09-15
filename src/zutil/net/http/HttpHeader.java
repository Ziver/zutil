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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class HttpHeader {
    private boolean isRequest = true;

    /** Specifies the protocol that should be used */
    private String protocol = "HTTP";
    /** The protocol version specified in the header */
    private float protocolVersion = 1.0f;

    /** HTTP type specified in a HTTP request, e.g GET POST DELETE PUT etc */
    private String requestType = "GET";
    /** String containing the target URL */
    private String requestUrl = "/";
    /** Map containing all the properties from the URL */
    private Map<String, String> requestUrlAttributes = new HashMap<>();

    /** Status code specified in a HTTP response message */
    private int responseStatusCode = 200;

    /** An Map of all header fields */
    private Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    /** An Map of all cookies */
    private Map<String, String> cookies = new HashMap<>();

    private InputStream in;


    public HttpHeader() {  }


    /**
     * @return true if this header represents a server response
     */
    public boolean isResponse() {
        return !isRequest;
    }

    /**
     * @return true if this header represents a client request
     */
    public boolean isRequest() {
        return isRequest;
    }

    public void setIsRequest(boolean request) {
        this.isRequest = request;
    }


    /**
     * @return the protocol specified in the header. e.g. HTTP, HTTPS, RTSP etc.
     */
    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }


    /**
     * @return the protocol version from this header
     */
    public float getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(float version) {
        this.protocolVersion = version;
    }


    /**
     * @return the HTTP message type( ex. GET,POST...)
     */
    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String type) {
        this.requestType = type;
    }


    /**
     * @return the HTTP Return Code from a Server
     */
    public int getResponseStatusCode() {
        return responseStatusCode;
    }

    public void setResponseStatusCode(int code) {
        this.responseStatusCode = code;
    }


    /**
     * @return the URL that the client sent the server
     */
    public String getRequestURL() {
        return requestUrl;
    }

    public void setRequestURL(String url) {
        this.requestUrl = url.trim().replaceAll("//", "/");
    }

    /**
     * @return parses out the page name from the request url and returns it.
     */
    public String getRequestPage() {
        if (requestUrl != null) {
            int start = 0;
            if (requestUrl.charAt(0) == '/')
                start = 1;
            int end = requestUrl.indexOf('?');
            if (end < 0)
                end = requestUrl.length();

            return requestUrl.substring(start, end);
        }
        return null;
    }


    /**
     * @return a Iterator with all defined url keys
     */
    public Iterator<String> getURLAttributeKeys() {
        return requestUrlAttributes.keySet().iterator();
    }

    /**
     * @return the URL attribute value of the given name. null if there is no such attribute
     */
    public String getURLAttribute(String name) {
        return requestUrlAttributes.get(name);
    }

    protected Map<String, String> getURLAttributeMap() {
        return requestUrlAttributes;
    }

    public void setURLAttribute(String key, String value) {
        this.requestUrlAttributes.put(key, value);
    }


    /**
     * @return a Iterator with all defined headers
     */
    public Iterator<String> getHeaderKeys() {
        return headers.keySet().iterator();
    }

    /**
     * @return the HTTP attribute value of the given name. null if there is no such attribute
     */
    public String getHeader(String name) {
        return headers.get(name.toUpperCase());
    }

    protected Map<String, String> getHeaderMap() {
        return headers;
    }

    public void setHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public void setHeaders(Map headerSrc) {
        this.headers.putAll(headerSrc);
    }


    /**
     * @return a Iterator with all defined cookies
     */
    public Iterator<String> getCookieKeys() {
        return cookies.keySet().iterator();
    }

    /**
     * @return the cookie value of the given name. null if there is no such attribute.
     */
    public String getCookie(String name) {
        return cookies.get(name);
    }

    protected Map<String, String> getCookieMap() {
        return cookies;
    }

    public void setCookie(String key, String value) {
        this.cookies.put(key, value);
    }

    public void setCookies(Map cookieSrc) {
        this.cookies.putAll(cookieSrc);
    }


    /**
     * @return a Reader that contains the body of the http request.
     */
    public InputStream getInputStream() {
        return in;
    }

    protected void setInputStream(InputStream in) {
        this.in = in;
    }



    public String toString() {
        StringBuilder tmp = new StringBuilder();
        tmp.append("{Type: ").append(requestType);
        tmp.append(", HTTP_version: ").append(protocol).append("/").append(protocolVersion);
        tmp.append(", URL: \"").append((String) requestUrl).append('\"');
        tmp.append(", URL_attr: ").append(toStringAttributes());
        tmp.append(", Headers: ").append(toStringHeaders());
        tmp.append(", Cookies: ").append(toStringCookies());
        tmp.append('}');
        return tmp.toString();
    }
    public String toStringHeaders() {
        return Converter.toString(headers);
    }
    public String toStringCookies() {
        return Converter.toString(cookies);
    }
    public String toStringAttributes() {
        return Converter.toString(requestUrlAttributes);
    }


    public String getResponseStatusString() {
        return getResponseStatusString(responseStatusCode);
    }

    public static String getResponseStatusString(int type) {
        switch (type) {
            case 100:
                return "Continue";
            case 200:
                return "OK";
            case 301:
                return "Moved Permanently";
            case 304:
                return "Not Modified";
            case 307:
                return "Temporary Redirect";
            case 400:
                return "Bad Request";
            case 401:
                return "Unauthorized";
            case 403:
                return "Forbidden";
            case 404:
                return "Not Found";
            case 500:
                return "Internal Server Error";
            case 501:
                return "Not Implemented";
            default:
                return "";
        }
    }
}
