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

public class HttpHeader {
    private boolean request = true;
    private String type = "GET";
    private String url = "/";
    private HashMap<String, String> urlAttributes;
    private float protocolVersion = 1.0f;
    private int statusCode = 200;
    private InputStream in;

    // Parameters
    private HashMap<String, String> headers;
    private HashMap<String, String> cookies;


    public HttpHeader() {
        urlAttributes = new HashMap<>();
        headers = new HashMap<>();
        cookies = new HashMap<>();
    }


    /**
     * @return true if this header represents a server response
     */
    public boolean isResponse() {
        return !request;
    }

    /**
     * @return true if this header represents a client request
     */
    public boolean isRequest() {
        return request;
    }

    /**
     * @return the HTTP message type( ex. GET,POST...)
     */
    public String getRequestType() {
        return type;
    }

    /**
     * @return the protocol version from this header
     */
    public float getProtocolVersion() {
        return protocolVersion;
    }

    /**
     * @return the HTTP Return Code from a Server
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * @return the URL that the client sent the server
     */
    public String getRequestURL() {
        return url;
    }

    /**
     * @return parses out the page name from the request url and returns it.
     */
    public String getRequestPage() {
        if (url != null) {
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
     * @return a Iterator with all defined url keys
     */
    public Iterator<String> getURLAttributeKeys() {
        return urlAttributes.keySet().iterator();
    }

    /**
     * @return the URL attribute value of the given name. null if there is no such attribute
     */
    public String getURLAttribute(String name) {
        return urlAttributes.get(name);
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

    /**
     * @return a Reader that contains the body of the http request.
     */
    public InputStream getInputStream() {
        return in;
    }


    public void setIsRequest(boolean request) {
        this.request = request;
    }

    public void setRequestType(String type) {
        this.type = type;
    }

    public void setProtocolVersion(float version) {
        this.protocolVersion = version;
    }

    public void setStatusCode(int code) {
        this.statusCode = code;
    }

    public void setRequestURL(String url) {
        this.url = url.trim().replaceAll("//", "/");
    }

    public void setHeader(String key, String value) {
        this.headers.put(key.toUpperCase(), value);
    }

    protected void setInputStream(InputStream in) {
        this.in = in;
    }

    protected HashMap<String, String> getHeaderMap() {
        return headers;
    }

    protected HashMap<String, String> getCookieMap() {
        return cookies;
    }

    protected HashMap<String, String> getUrlAttributeMap() {
        return urlAttributes;
    }


    public String toString() {
        StringBuilder tmp = new StringBuilder();
        tmp.append("{Type: ").append(type);
        tmp.append(", HTTP_version: HTTP/").append(protocolVersion);
        if (url == null)
            tmp.append(", URL: null");
        else
            tmp.append(", URL: \"").append(url).append('\"');

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
        return Converter.toString(urlAttributes);
    }

}
