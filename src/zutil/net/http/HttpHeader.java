/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Ziver Koc
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
    // Constants

    public static final String HEADER_CACHE_CONTROL  = "Cache-Control";
    public static final String HEADER_COOKIE         = "Cookie";
    public static final String HEADER_CONTENT_TYPE   = "Content-Type";
    public static final String HEADER_CONTENT_LENGTH = "Content-Length";
    public static final String HEADER_HOST           = "Host";
    public static final String HEADER_IF_NONE_MATCH  = "If-None-Match";
    public static final String HEADER_LOCATION       = "Location";
    public static final String HEADER_SET_COOKIE     = "Set-Cookie";
    public static final String HEADER_SERVER         = "Server";
    public static final String HEADER_USER_AGENT     = "User-Agent";

    // Variables

    private boolean isRequest = true;

    /** Specifies the protocol that should be used */
    private String protocol = null;
    /** The protocol version specified in the header */
    private float protocolVersion = -1;

    /** HTTP type specified in a HTTP request, e.g GET POST DELETE PUT etc */
    private String requestType = null;
    /** String containing the target URL */
    private String requestUrl = null;
    /** Map containing all the properties from the URL */
    private Map<String, String> requestUrlAttributes = new HashMap<>();

    /** Status code specified in a HTTP response message */
    private int responseStatusCode = -1;
    private String responseStatusString = null;

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
        this.responseStatusString = getResponseStatusString(code);
    }

    public String getResponseStatusString() {
        return responseStatusString;
    }

    public void getResponseStatusString(String msg) {
        this.responseStatusString = msg;
    }

    /**
     * @return the URL that the client sent the server
     */
    public String getRequestURL() {
        return requestUrl;
    }

    public void setRequestURL(String url) {
        this.requestUrl = url;
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
     * @return true if the specified header has been set, false otherwise.
     */
    public boolean containsHeader(String name) {
        return headers.containsKey(name);
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
     * @return true if the specified cookie has been set, false otherwise.
     */
    public boolean containsCookie(String name) {
        return cookies.containsKey(name);
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
        tmp.append("{");

        if (isRequest) {
            tmp.append("Type: ").append(requestType);
            tmp.append(", HTTP_version: ").append(protocol).append("/").append(protocolVersion);
            tmp.append(", URL: \"").append(requestUrl).append('\"');
            tmp.append(", URL_attr: ").append(toStringAttributes());
        } else {
            tmp.append("HTTP_version: ").append(protocol).append("/").append(protocolVersion);
            tmp.append(", Status_code: ").append(responseStatusCode);
            tmp.append(", Status_msg: \"").append(getResponseStatusString()).append('\"');
        }

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


    public static String getResponseStatusString(int type) {
        switch (type) {
            case 100: return "Continue";
            case 200: return "OK";
            case 201: return "Created";
            case 250: return "Low on Storage Space";
            case 300: return "Multiple Choices";
            case 301: return "Moved Permanently";
            case 302: return "Moved Temporarily";
            case 303: return "See Other";
            case 304: return "Not Modified";
            case 305: return "Use Proxy";
            case 307: return "Temporary Redirect";
            case 400: return "Bad Request";
            case 401: return "Unauthorized";
            case 403: return "Forbidden";
            case 404: return "Not Found";
            case 405: return "Method Not Allowed";
            case 406: return "Not Acceptable";
            case 407: return "Proxy Authentication Required";
            case 408: return "Request Time-out";
            case 410: return "Gone";
            case 411: return "Length Required";
            case 412: return "Precondition Failed";
            case 413: return "Request Entity Too Large";
            case 414: return "Request-URI Too Large";
            case 415: return "Unsupported Media Type";
            case 451: return "Parameter Not Understood";
            case 452: return "Conference Not Found";
            case 453: return "Not Enough Bandwidth";
            case 454: return "Session Not Found";
            case 455: return "Method Not Valid in This State";
            case 456: return "Header Field Not Valid for Resource";
            case 457: return "Invalid Range";
            case 458: return "Parameter Is Read-Only";
            case 459: return "Aggregate operation not allowed";
            case 460: return "Only aggregate operation allowed";
            case 461: return "Unsupported transport";
            case 462: return "Destination unreachable";
            case 500: return "Internal Server Error";
            case 501: return "Not Implemented";
            case 502: return "Bad Gateway";
            case 503: return "Service Unavailable";
            case 504: return "Gateway Time-out";
            case 505: return "RTSP Version not supported";
            case 551: return "Option not supported";
            default:  return "";
        }
    }
}
