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

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;

/**
 * This PrintStream is written for HTTP use
 * It has buffer capabilities and cookie management.
 *
 * @author Ziver
 */
public class HttpPrintStream extends OutputStream {
    /**
     * Specifies if the HTTP message is a request (client) or a response (server).
     */
    public enum HttpMessageType {
        REQUEST,
        RESPONSE
    }

    /**
     * The actual output stream
     */
    private PrintStream out;
    /**
     * This defines the type of message that will be generated
     */
    private final HttpMessageType messageType;
    /**
     * Specifies the protocol that should be used
     */
    private String protocol;
    /**
     * Specifies the protocol version that should be used
     */
    private String protocolVersion;
    /**
     * The status code of the message, ONLY for response
     */
    private Integer responseStatusCode;
    /**
     * The request type of the message ONLY for request
     */
    private String requestType;
    /**
     * The requesting url ONLY for request
     */
    private String requestUrl;
    /**
     * An Map of all the header values
     */
    private HashMap<String, String> headers;
    /**
     * An Map of all the cookies
     */
    private HashMap<String, String> cookies;
    /**
     * The header data buffer
     */
    private StringBuffer buffer;
    /**
     * If the header buffering is enabled
     */
    private boolean bufferEnabled;


    /**
     * Creates an new instance of HttpPrintStream with
     * message type of RESPONSE and buffering disabled.
     *
     * @param out is the OutputStream where the data will be written to
     */
    public HttpPrintStream(OutputStream out) {
        this(out, HttpMessageType.RESPONSE);
    }

    /**
     * Creates an new instance of HttpPrintStream with
     * message type buffering disabled.
     *
     * @param out  is the OutputStream where the data will be written to
     * @param type is the type of message
     */
    public HttpPrintStream(OutputStream out, HttpMessageType type) {
        this.out = new PrintStream(out);
        this.protocol = "HTTP";
        this.protocolVersion = "1.0";
        this.messageType = type;
        this.responseStatusCode = 200;
        this.headers = new HashMap<>();
        this.cookies = new HashMap<>();
        this.buffer = new StringBuffer();
        this.bufferEnabled = false;
    }


    /**
     * Enable the buffering capability of the PrintStream.
     * Nothing will be sent to the client when buffering
     * is enabled until you close or flush the stream.
     * This function will flush the stream if buffering is
     * disabled.
     */
    public void enableBuffering(boolean enable) {
        bufferEnabled = enable;
        if (!bufferEnabled) flush();
    }

    /**
     * Set the http version that will be used in the http header
     */
    public void setProtocolVersion(String version) {
        this.protocolVersion = version;
    }

    /**
     * Adds a cookie that will be sent to the client
     *
     * @param key   is the name of the cookie
     * @param value is the value of the cookie
     * @throws IllegalStateException if the header has already been sent
     */
    public void setCookie(String key, String value) {
        if (cookies == null)
            throw new IllegalStateException("Header already sent");
        cookies.put(key, value);
    }

    /**
     * Adds an header value
     *
     * @param key   is the header name
     * @param value is the value of the header
     * @throws IllegalStateException if the header has already been sent
     */
    public void setHeader(String key, String value) {
        if (headers == null)
            throw new IllegalStateException("Header already sent");
        headers.put(key, value);
    }

    /**
     * Sets the status code of the message, ONLY available in HTTP RESPONSE
     *
     * @param code the code from 100 up to 599
     * @throws IllegalStateException if the header has already been sent or the message type is wrong
     */
    public void setStatusCode(int code) {
        if (responseStatusCode == null)
            throw new IllegalStateException("Header already sent.");
        if (messageType != HttpMessageType.RESPONSE)
            throw new IllegalStateException("Status Code is only available with HTTP requests");
        responseStatusCode = code;
    }

    /**
     * Sets the request type of the message, ONLY available in HTTP REQUEST
     *
     * @param req_type is the type of the message, e.g. GET, POST...
     * @throws IllegalStateException if the header has already been sent or the message type is wrong
     */
    public void setRequestType(String req_type) {
        if (req_type == null)
            throw new IllegalStateException("Header already sent.");
        if (messageType != HttpMessageType.REQUEST)
            throw new IllegalStateException("Request Message Type is only available with HTTP requests");
        this.requestType = req_type;
    }

    /**
     * Sets the requesting URL of the message. Only available with HTTP requests
     *
     * @param req_url is the URL
     * @throws IllegalStateException if the header has already been sent or the message type is wrong
     */
    public void setRequestURL(String req_url) {
        if (req_url == null)
            throw new IllegalStateException("Header already sent.");
        if (messageType != HttpMessageType.REQUEST)
            throw new IllegalStateException("Request URL is only available with a HTTP request");
        this.requestUrl = req_url;
    }

    protected void setHeaders(HashMap<String, String> map) {
        headers = map;
    }

    protected void setCookies(HashMap<String, String> map) {
        cookies = map;
    }


    /**
     * Prints a new line
     */
    public void println() {
        printOrBuffer(System.lineSeparator());
    }

    /**
     * Prints with a new line
     */
    public void println(String s) {
        printOrBuffer(s + System.lineSeparator());
    }

    /**
     * Prints an string
     */
    public void print(String s) {
        printOrBuffer(s);
    }

    /**
     * Will buffer the body data or directly send the headers if needed and then the append the body
     */
    private void printOrBuffer(String s) {
        if (bufferEnabled) {
            buffer.append(s);
        } else {
            if (responseStatusCode != null) {
                if (messageType == HttpMessageType.REQUEST)
                    out.print(requestType + " " + requestUrl + " " + protocol + "/" + protocolVersion);
                else
                    out.print("HTTP/" + protocolVersion + " " + responseStatusCode + " " + getStatusString(responseStatusCode));
                out.println();
                responseStatusCode = null;
                requestType = null;
                requestUrl = null;
            }

            if (headers != null) {
                for (String key : headers.keySet()) {
                    out.println(key + ": " + headers.get(key));
                }
                headers = null;
            }

            if (cookies != null) {
                if (!cookies.isEmpty()) {
                    if (messageType == HttpMessageType.REQUEST) {
                        out.print("Cookie: ");
                        for (String key : cookies.keySet()) {
                            out.print(key + "=" + cookies.get(key) + "; ");
                        }
                        out.println();
                    } else {
                        for (String key : cookies.keySet()) {
                            out.print("Set-Cookie: " + key + "=" + cookies.get(key) + ";");
                            out.println();
                        }
                    }
                }
                out.println();
                cookies = null;
            }
            out.print(s);
        }
    }

    /**
     * @return if headers has been sent. The setHeader, setStatusCode, setCookie method will throw IllegalStateException
     */
    public boolean isHeaderSent() {
        return responseStatusCode == null && headers == null && cookies == null;
    }


    /**
     * Sends out the buffer and clear it
     */
    @Override
    public void flush() {
        flushBuffer();
        out.flush();
    }

    @Override
    public void close() {
        flush();
        out.close();
    }

    protected void flushBuffer() {
        if (bufferEnabled) {
            bufferEnabled = false;
            printOrBuffer(buffer.toString());
            buffer.delete(0, buffer.length());
            bufferEnabled = true;
        } else if (responseStatusCode != null || headers != null || cookies != null) {
            printOrBuffer("");
        }
    }

    /**
     * Will flush all buffers and write binary data to stream
     */
    @Override
    public void write(int b) {
        flushBuffer();
        out.write(b);
    }

    /**
     * * Will flush all buffers and write binary data to stream
     */
    @Override
    public void write(byte[] buf, int off, int len) {
        flushBuffer();
        out.write(buf, off, len);
    }

    private String getStatusString(int type) {
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

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("{http_type: ").append(messageType);
        if (responseStatusCode != null) {
            if (messageType == HttpMessageType.REQUEST) {
                str.append(", req_type: ").append(requestType);
                if (requestUrl == null)
                    str.append(", req_url: null");
                else
                    str.append(", req_url: \"").append(requestUrl).append('\"');
            } else if (messageType == HttpMessageType.RESPONSE) {
                str.append(", status_code: ").append(responseStatusCode);
                str.append(", status_str: ").append(getStatusString(responseStatusCode));
            }

            if (headers != null) {
                str.append(", Headers: ").append(Converter.toString(headers));
            }
            if (cookies != null) {
                str.append(", Cookies: ").append(Converter.toString(cookies));
            }
        } else
            str.append(", HEADER ALREADY SENT ");
        str.append('}');

        return str.toString();
    }
}
