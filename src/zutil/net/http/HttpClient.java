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
import zutil.net.http.HttpPrintStream.HttpMessageType;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;

/**
 * This class connects to a HTTP server and parses the result.
 */
public class HttpClient implements AutoCloseable {
    public enum HttpRequestType {
        GET,
        POST
    }

    // Request variables

    private String protocol = "HTTP";
    private HttpURL url;
    private boolean absoluteURL;
    private String type;
    private HashMap<String, String> headers;
    private HashMap<String, String> cookies;
    private String content;

    // Response variables

    private HttpHeaderParser responseHeader;
    private InputStream responseStream;


    public HttpClient() {
        this(HttpRequestType.GET);
    }

    public HttpClient(HttpRequestType type) {
        this(type.toString());
    }

    public HttpClient(String type) {
        this.type = type;
        headers = new HashMap<>();
        cookies = new HashMap<>();
    }


    /**
     * Set the protocol that should be provided by the request. Default is HTTP.
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * Set the type of request. Default is GET.
     */
    public void setType(String type) {
        this.type = type;
    }

    public void setURL(URL url) {
        setURL(new HttpURL(url));
    }

    public void setURL(HttpURL url) {
        this.url = url;
    }

    /**
     * If set to true the request will contain the full URL instead of only the path to the page.
     */
    public void setAbsoluteURL(boolean absoluteURL) {
        this.absoluteURL = absoluteURL;
    }

    /**
     * Adds a parameter to the request
     */
    public void setParameter(String key, String value) {
        url.setParameter(key, value);
    }

    /**
     * Adds a cookie to the request
     */
    public void setCookie(String key, String value) {
        cookies.put(key, value);
    }

    /**
     * Adds a header value to the request
     */
    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    /**
     * Sets the content data that will be included in the request.
     * NOTE: this will disable the POST data parameter inclusion.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Will send a HTTP request to the target host.
     * NOTE: any previous request connections will be closed
     */
    public HttpHeader send() throws IOException {
        int port = 80;
        if (url.getPort() > 0)
            port = url.getPort();
        else if ("https".equals(url.getProtocol()))
            port = 443;

        // ---------------------------------
        // Create Socket
        // ---------------------------------

        Socket conn;
        if ("https".equals(url.getProtocol())) {
            conn = SSLSocketFactory.getDefault().createSocket(url.getHost(), port);
            ((SSLSocket)conn).startHandshake();
        } else {
            conn = new Socket(url.getHost(), port);
        }

        // ---------------------------------
        // Request
        // ---------------------------------

        HttpPrintStream request = new HttpPrintStream(conn.getOutputStream(), HttpMessageType.REQUEST);
        request.setProtocol(protocol);
        request.setRequestType(type);
        request.setRequestURL(absoluteURL ? url.getURL() : url.getHttpURL());
        request.setHeaders(headers);
        request.setCookies(cookies);

        // Set headers

        request.setHeader(HttpHeader.HEADER_HOST, url.getHost() + ":" + port);

        // send payload

        if (HttpRequestType.POST.toString().equals(type)) {
            String postData;
            if (content != null)
                postData = content;
            else
                postData = url.getParameterString();
            request.setHeader(HttpHeader.HEADER_CONTENT_LENGTH, "" + postData.length());
            request.println();
            request.print(postData);
        } else
            request.println();
        request.flush();

        // ---------------------------------
        // Response
        // ---------------------------------

        if (responseHeader != null || responseStream != null) // Close previous request
            this.close();
        responseStream = new BufferedInputStream(conn.getInputStream());
        responseHeader = new HttpHeaderParser(responseStream);

        return responseHeader.read();
    }

    public InputStream getResponseInputStream() {
        return responseStream;
    }

    @Override
    public void close() throws IOException {
        if (responseStream != null)
            responseStream.close();
        responseStream = null;
        responseHeader = null;
    }


    public String toString() {
        StringBuilder tmp = new StringBuilder();
        tmp.append("{Type: ").append(type);
        tmp.append(", URL: \"").append(url).append('\"');
        tmp.append(", Headers: ").append(Converter.toString(headers));
        tmp.append(", Cookies: ").append(Converter.toString(cookies));
        tmp.append('}');
        return tmp.toString();
    }
}
