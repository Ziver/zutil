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

import zutil.net.http.HttpPrintStream.HttpMessageType;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;

/**
 * This class connects to a HTTP server and
 * parses the result
 *
 * @author Ziver
 */
public class HttpClient implements AutoCloseable {
    public enum HttpRequestType {
        GET, POST
    }

    // Request variables
    private HttpURL url;
    private HttpRequestType type;
    private HashMap<String, String> headers;
    private HashMap<String, String> cookies;
    private String data;

    // Response variables
    private HttpHeaderParser rspHeader;
    private InputStream rspStream;


    public HttpClient(HttpRequestType type) {
        this.type = type;
        headers = new HashMap<>();
        cookies = new HashMap<>();
    }


    public void setURL(URL url) {
        setURL(new HttpURL(url));
    }

    public void setURL(HttpURL url) {
        this.url = url;
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
     * NOTE: this will override the POST data parameter inclusion.
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Will send a HTTP request to the target host.
     * NOTE: any previous request connections will be closed
     */
    public HttpHeaderParser send() throws IOException {
        Socket conn = new Socket(url.getHost(), url.getPort());

        // Request
        HttpPrintStream request = new HttpPrintStream(conn.getOutputStream(), HttpMessageType.REQUEST);
        request.setRequestType(type.toString());
        request.setRequestURL(url.getHttpURL());
        request.setHeaders(headers);
        request.setCookies(cookies);

        if (type == HttpRequestType.POST) {
            String postData;
            if (data != null)
                postData = data;
            else
                postData = url.getParameterString();
            request.setHeader("Content-Length", "" + postData.length());
            request.println();
            request.print(postData);
        } else
            request.println();
        request.close();

        // Response
        if (rspHeader != null || rspStream != null) // Close previous request
            this.close();
        rspStream = new BufferedInputStream(conn.getInputStream());
        rspHeader = new HttpHeaderParser(rspStream);

        return rspHeader;
    }

    public HttpHeaderParser getResponseHeader() {
        return rspHeader;
    }

    public InputStream getResponseInputStream() {
        return rspStream;
    }

    @Override
    public void close() throws IOException {
        if (rspStream != null)
            rspStream.close();
        rspStream = null;
        rspHeader = null;
    }
}
