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

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class HttpHeaderParserTest {

    @Test
    public void firstLineRequest() throws IOException {
        HttpHeaderParser parser = new HttpHeaderParser("GET / HTTP/1.1");
        HttpHeader header = parser.read();

        assertTrue(header.isRequest());
        assertEquals("GET", header.getRequestType());
        assertEquals("/", header.getRequestURL());
        assertEquals("HTTP", header.getProtocol());
        assertEquals(1.1f, header.getProtocolVersion(), 0);
        assertEquals(-1, header.getResponseStatusCode());
        assertEquals(null, header.getResponseStatusString());

        parser = new HttpHeaderParser("DESCRIBE http://example.com RTSP/1.0");
        header = parser.read();

        assertTrue(header.isRequest());
        assertEquals("DESCRIBE", header.getRequestType());
        assertEquals("http://example.com", header.getRequestURL());
        assertEquals("RTSP", header.getProtocol());
        assertEquals(1.0f, header.getProtocolVersion(), 0f);
    }

    @Test
    public void firstLineResponse() throws IOException {
        HttpHeaderParser parser = new HttpHeaderParser("HTTP/1.0 200 OK");
        HttpHeader header = parser.read();

        assertTrue(header.isResponse());
        assertEquals("HTTP", header.getProtocol());
        assertEquals(1.0f, header.getProtocolVersion(), 0f);
        assertEquals(200, header.getResponseStatusCode(), 0f);
        assertEquals("OK", header.getResponseStatusString());
        assertNull(header.getRequestType());
        assertNull(header.getRequestURL());

        parser = new HttpHeaderParser("RTSP/1.0 404 File not found Special");
        header = parser.read();

        assertTrue(header.isResponse());
        assertEquals("RTSP", header.getProtocol());
        assertEquals(1.0f, header.getProtocolVersion(), 0f);
        assertEquals(404, header.getResponseStatusCode(), 0f);
        assertEquals("File not found Special", header.getResponseStatusString());
    }
}