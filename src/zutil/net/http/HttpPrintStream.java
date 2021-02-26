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


    private HttpHeader header = new HttpHeader();

    /**
     * The header data buffer, buffering is enabled if this variable is not null
     */
    private StringBuffer buffer = null;

    /**
     * The actual output stream
     */
    private PrintStream out;


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

        // Set defaults

        header.setProtocol("HTTP");
        header.setProtocolVersion(1.0f);

        if (type == HttpMessageType.REQUEST) {
            header.setIsRequest(true);
            header.setRequestType("GET");
            header.setRequestURL("/");
        } else {
            header.setIsRequest(false);
            header.setResponseStatusCode(200);
        }
    }


    /**
     * Enable the buffering capability of the PrintStream.
     * Nothing will be sent to the client when buffering
     * is enabled until you close or flush the stream.
     * This function will flush the stream if buffering is
     * disabled.
     */
    public void enableBuffering(boolean enable) {
        if (enable && !isBufferEnabled()) {
            buffer = new StringBuffer();
        } else if (!enable && isBufferEnabled()) {
            flush();
            buffer = null;
        }
    }

    /**
     * Set the protocol name that should be provided in the HTTP header.
     */
    public void setProtocol(String protocol) {
        header.setProtocol(protocol);
    }

    /**
     * Set the http version that will be used in the http header
     */
    public void setProtocolVersion(float version) {
        header.setProtocolVersion(version);
    }

    /**
     * Sets the status code of the message, ONLY available in HTTP RESPONSE
     *
     * @param code the code from 100 up to 599
     * @throws IllegalStateException if the header has already been sent or the message type is wrong
     */
    public void setResponseStatusCode(int code) {
        if (!header.isResponse())
            throw new IllegalStateException("Status Code is only available with HTTP requests");
        headerSentCheck();

        header.setResponseStatusCode(code);
    }

    /**
     * Sets the request type of the message, ONLY available in HTTP REQUEST
     *
     * @param req_type is the type of the message, e.g. GET, POST...
     * @throws IllegalStateException if the header has already been sent or the message type is wrong
     */
    public void setRequestType(String req_type) {
        if (!header.isRequest())
            throw new IllegalStateException("Request Message Type is only available with HTTP requests");
        headerSentCheck();

        header.setRequestType(req_type);
    }

    /**
     * Sets the requesting URL of the message. Only available with HTTP requests
     *
     * @param req_url is the URL
     * @throws IllegalStateException if the header has already been sent or the message type is wrong
     */
    public void setRequestURL(String req_url) {
        if (!header.isRequest())
            throw new IllegalStateException("Request URL is only available with a HTTP request");
        headerSentCheck();

        header.setRequestURL(req_url);
    }

    /**
     * Adds a cookie that will be sent to the client
     *
     * @param key   is the name of the cookie
     * @param value is the value of the cookie
     * @throws IllegalStateException if the header has already been sent
     */
    public void setCookie(String key, String value) {
        headerSentCheck();

        header.setCookie(key, value);
    }

    /**
     * Adds an header value
     *
     * @param key   is the header name
     * @param value is the value of the header
     * @throws IllegalStateException if the header has already been sent
     */
    public void setHeader(String key, String value) {
        headerSentCheck();

        header.setHeader(key, value);
    }

    protected void setHeaders(HashMap<String, String> map) {
        header.setHeaders(map);
    }

    protected void setCookies(HashMap<String, String> map) {
        header.setCookies(map);
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
     * Will buffer the body data or directly send the headers if needed and then append the body
     */
    private void printOrBuffer(String s) {
        if (isBufferEnabled()) {
            buffer.append(s);
        } else {
            printForced(s);
        }
    }

    /**
     * Method will directly print the provided String, if any headers are set then they will firstly be sent and cleared proceeded by the given String.
     */
    private void printForced(String s) {
        if (header != null) {
            if (header.isRequest()) {
                out.print(header.getRequestType() + " " +
                        header.getRequestURL() + " " +
                        header.getProtocol() + "/" + header.getProtocolVersion());
            } else {
                out.print(header.getProtocol() + "/" + header.getProtocolVersion() + " " +
                        header.getResponseStatusCode() + " " +
                        header.getResponseStatusString());
            }
            out.println();

            // Send headers

            for (String key : header.getHeaderMap().keySet()) {
                out.println(key + ": " + header.getHeader(key));
            }

            // Send cookies

            if (!header.getCookieMap().isEmpty()) {
                if (header.isRequest()) {
                    out.print(HttpHeader.HEADER_COOKIE + ":");
                    for (String key : header.getCookieMap().keySet()) {
                        out.print(" " + key + "=" + header.getCookie(key) + ";");
                    }
                    out.println();
                } else {
                    for (String key : header.getCookieMap().keySet()) {
                        out.print(HttpHeader.HEADER_SET_COOKIE + ": " + key + "=" + header.getCookie(key) + ";");
                        out.println();
                    }
                }
            }

            out.println();
            header = null;

            // Check for errors

            if (out.checkError())
                throw new RuntimeException("Underlying stream has thrown a error.");
        }

        out.print(s);
    }

    /**
     * @return true if the HTTP header buffer is enabled. If enabled all output will be buffered until the headers has been sent.
     */
    public boolean isBufferEnabled() {
        return buffer != null;
    }

    /**
     * @return true if headers has been sent. The setHeader, setResponseStatusCode, setCookie method will throw IllegalStateException
     */
    public boolean isHeaderSent() {
        return header == null;
    }

    public void headerSentCheck() {
        if (isHeaderSent())
            throw new IllegalStateException("Header already sent.");
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
        if (isBufferEnabled()) {
            printForced(buffer.toString());
            buffer.delete(0, buffer.length());
        } else if (!isHeaderSent()) {
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


    public String toString() {
        StringBuilder str = new StringBuilder();
        if (!isHeaderSent())
            str.append(header);
        else
            str.append("{HEADER ALREADY SENT}");

        return str.toString();
    }
}
