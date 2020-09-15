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

import static org.junit.Assert.*;

public class HttpHeaderTest {

    @Test
    public void setIsRequest() {
        HttpHeader header = new HttpHeader();
        assertTrue(header.isRequest());
        assertFalse(header.isResponse());

        header.setIsRequest(false);
        assertFalse(header.isRequest());
        assertTrue(header.isResponse());
    }

    @Test
    public void setProtocol() {
        HttpHeader header = new HttpHeader();
        assertEquals("HTTP", header.getProtocol());

        header.setProtocol("RTSP");
        assertEquals("RTSP", header.getProtocol());
    }


    @Test
    public void setProtocolVersion() {
        HttpHeader header = new HttpHeader();
        assertEquals(1.0f, header.getProtocolVersion(), 0);

        header.setProtocolVersion(1.1f);
        assertEquals(1.1f, header.getProtocolVersion(), 0);
    }


    @Test
    public void setRequestType() {
        HttpHeader header = new HttpHeader();
        assertEquals("GET", header.getRequestType());

        header.setRequestType("POST");
        assertEquals("POST", header.getRequestType());
    }


    @Test
    public void setResponseStatusCode() {
        HttpHeader header = new HttpHeader();
        assertEquals(200, header.getResponseStatusCode());

        header.setResponseStatusCode(400);
        assertEquals(400, header.getResponseStatusCode());
    }

    @Test
    public void setRequestURL() {
        HttpHeader header = new HttpHeader();
        assertEquals("/", header.getRequestURL());

        header.setRequestURL("/page/test");
        assertEquals("/page/test", header.getRequestURL());

        header.setRequestURL(" /page/1test ");
        assertEquals("/page/1test", header.getRequestURL());

        header.setRequestURL("/page//2test ");
        assertEquals("/page/2test", header.getRequestURL());
    }

    @Test
    public void getRequestPage() {
        HttpHeader header = new HttpHeader();
        header.setRequestURL("/page/test?param=a&dd=tt");
        assertEquals("page/test", header.getRequestPage());
    }


    @Test
    public void setURLAttribute() {
        HttpHeader header = new HttpHeader();
        assertNull(header.getURLAttribute("param1"));

        header.setURLAttribute("param1", "value1");
        assertEquals("value1", header.getURLAttribute("param1"));
        assertNull(header.getURLAttribute("Param1"));
    }


    @Test
    public void setHeader() {
        HttpHeader header = new HttpHeader();
        assertNull(header.getHeader("param1"));

        header.setHeader("param1", "value1");
        header.setHeader("param2", "value2");
        assertEquals("value1", header.getHeader("param1"));
        assertEquals("value1", header.getHeader("PARAM1"));
        assertEquals("value2", header.getHeader("param2"));
        assertEquals("value2", header.getHeader("Param2"));

        header.setHeader("param2", "value3");
        assertEquals("value3", header.getHeader("param2"));
    }


    @Test
    public void setCookie() {
        HttpHeader header = new HttpHeader();
        assertNull(header.getCookie("param1"));

        header.setCookie("param1", "value1");
        header.setCookie("param2", "value2");
        assertEquals("value1", header.getCookie("param1"));
        assertEquals("value2", header.getCookie("param2"));
        assertNull("value2", header.getCookie("Param2"));

        header.setCookie("param2", "value3");
        assertEquals("value3", header.getCookie("param2"));
    }


    @Test
    public void getResponseStatusString() {
        HttpHeader header = new HttpHeader();
        assertEquals("OK", header.getResponseStatusString());

        header.setResponseStatusCode(400);
        assertEquals("Bad Request", header.getResponseStatusString());
    }
}