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
import zutil.io.StringOutputStream;
import zutil.net.http.HttpPrintStream.HttpMessageType;

import static org.junit.Assert.*;

public class HttpPrintStreamTest {

    @Test
    public void requestDefaultOutput() {
        StringOutputStream out = new StringOutputStream();
        HttpPrintStream httpOut = new HttpPrintStream(out, HttpMessageType.REQUEST);

        httpOut.flush();
        assertEquals(
                "GET / HTTP/1.0" + System.lineSeparator() +
                        System.lineSeparator(),
                out.toString()
        );
    }

    @Test
    public void requestSetType() {
        StringOutputStream out = new StringOutputStream();
        HttpPrintStream httpOut = new HttpPrintStream(out, HttpMessageType.REQUEST);

        httpOut.setRequestType("POST");

        httpOut.flush();
        assertEquals(
                "POST / HTTP/1.0" + System.lineSeparator() +
                        System.lineSeparator(),
                out.toString()
        );
    }

    @Test
    public void requestSetUrl() {
        StringOutputStream out = new StringOutputStream();
        HttpPrintStream httpOut = new HttpPrintStream(out, HttpMessageType.REQUEST);

        httpOut.setRequestURL("/test/path/to/page?param=aa&tt=aa");

        httpOut.flush();
        assertEquals(
                "GET /test/path/to/page?param=aa&tt=aa HTTP/1.0" + System.lineSeparator() +
                        System.lineSeparator(),
                out.toString()
        );
    }

    @Test
    public void requestSetCookie() {
        StringOutputStream out = new StringOutputStream();
        HttpPrintStream httpOut = new HttpPrintStream(out, HttpMessageType.REQUEST);

        httpOut.setCookie("Test", "value");

        httpOut.flush();
        assertEquals(
                "GET / HTTP/1.0" + System.lineSeparator() +
                        "Cookie: Test=value;" + System.lineSeparator() +
                        System.lineSeparator(),
                out.toString()
        );
    }

    @Test
    public void requestSetCookieMultiple() {
        StringOutputStream out = new StringOutputStream();
        HttpPrintStream httpOut = new HttpPrintStream(out, HttpMessageType.REQUEST);

        httpOut.setCookie("Test1", "value1");
        httpOut.setCookie("Test2", "value2");

        httpOut.flush();
        assertEquals(
                "GET / HTTP/1.0" + System.lineSeparator() +
                        "Cookie: Test1=value1; Test2=value2;" + System.lineSeparator() +
                        System.lineSeparator(),
                out.toString()
        );
    }


    @Test
    public void responseDefaultOutput() {
        StringOutputStream out = new StringOutputStream();
        HttpPrintStream httpOut = new HttpPrintStream(out, HttpMessageType.RESPONSE);

        httpOut.flush();
        assertEquals(
                "HTTP/1.0 200 OK" + System.lineSeparator() +
                        System.lineSeparator(),
                out.toString()
        );
    }

    @Test
    public void responseStatusCode() {
        StringOutputStream out = new StringOutputStream();
        HttpPrintStream httpOut = new HttpPrintStream(out, HttpMessageType.RESPONSE);

        httpOut.setResponseStatusCode(400);

        httpOut.flush();
        assertEquals(
                "HTTP/1.0 400 Bad Request" + System.lineSeparator() +
                        System.lineSeparator(),
                out.toString()
        );
    }

    @Test
    public void responseSetCookie() {
        StringOutputStream out = new StringOutputStream();
        HttpPrintStream httpOut = new HttpPrintStream(out, HttpMessageType.RESPONSE);

        httpOut.setCookie("Test", "value");

        httpOut.flush();
        assertEquals(
                "HTTP/1.0 200 OK" + System.lineSeparator() +
                        "Set-Cookie: Test=value;" + System.lineSeparator() +
                        System.lineSeparator(),
                out.toString()
        );
    }

    @Test
    public void responseSetCookieMultiple() {
        StringOutputStream out = new StringOutputStream();
        HttpPrintStream httpOut = new HttpPrintStream(out, HttpMessageType.RESPONSE);

        httpOut.setCookie("Test1", "value1");
        httpOut.setCookie("Test2", "value2");

        httpOut.flush();
        assertEquals(
                "HTTP/1.0 200 OK" + System.lineSeparator() +
                        "Set-Cookie: Test1=value1;" + System.lineSeparator() +
                        "Set-Cookie: Test2=value2;" + System.lineSeparator() +
                        System.lineSeparator(),
                out.toString()
        );
    }


    @Test
    public void setHeader() {
        StringOutputStream out = new StringOutputStream();
        HttpPrintStream httpOut = new HttpPrintStream(out);

        httpOut.setHeader("Test1", "value1");
        httpOut.setHeader("Test2", "value2");

        httpOut.flush();
        assertEquals(
                "HTTP/1.0 200 OK" + System.lineSeparator() +
                        "Test1: value1" + System.lineSeparator() +
                        "Test2: value2" + System.lineSeparator() +
                        System.lineSeparator(),
                out.toString()
        );
    }


    // TODO @Test
    public void enableBuffering() {

    }
}