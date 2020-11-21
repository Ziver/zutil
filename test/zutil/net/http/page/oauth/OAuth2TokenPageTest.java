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

package zutil.net.http.page.oauth;

import org.junit.Before;
import org.junit.Test;
import zutil.net.http.HttpHeader;
import zutil.net.http.HttpTestUtil;
import zutil.net.http.HttpURL;

import java.io.IOException;
import java.net.MalformedURLException;

import static org.junit.Assert.*;

public class OAuth2TokenPageTest {

    private OAuth2Registry registry;
    private OAuth2AuthorizationPage authPage;

    @Before
    public void init(){
        registry = new OAuth2Registry();
        authPage = new OAuth2AuthorizationPage(registry);
    }

    @Test
    public void invalidRedirect() throws IOException {
        registry.addWhitelist("12345");
        HttpHeader reqHeader = new HttpHeader();
        HttpHeader rspHeader = HttpTestUtil.makeRequest(authPage, reqHeader);

        // redirect_uri and client_id not provided

        assertEquals(400, rspHeader.getResponseStatusCode());
        assertNull(rspHeader.getHeader("Location"));

        // redirect_uri not provided

        reqHeader.setURLAttribute("client_id", "12345");
        rspHeader = HttpTestUtil.makeRequest(authPage, reqHeader);

        assertEquals(400, rspHeader.getResponseStatusCode());
        assertNull(rspHeader.getHeader("Location"));

        // redirect_uri is not a valid URL

        reqHeader.setURLAttribute("redirect_uri", "incorrect url");
        rspHeader = HttpTestUtil.makeRequest(authPage, reqHeader);

        assertEquals(400, rspHeader.getResponseStatusCode());
        assertNull(rspHeader.getHeader("Location"));

        // redirect_uri is not HTTPS

        reqHeader.setURLAttribute("redirect_uri", "http://example.com");
        rspHeader = HttpTestUtil.makeRequest(authPage, reqHeader);

        assertEquals(400, rspHeader.getResponseStatusCode());
        assertNull(rspHeader.getHeader("Location"));
    }

    @Test
    public void invalidClientId() throws IOException {
        HttpHeader reqHeader = new HttpHeader();
        reqHeader.setURLAttribute("redirect_uri", "https://example.com");
        HttpHeader rspHeader = HttpTestUtil.makeRequest(authPage, reqHeader);

        // client_id not provided

        assertEquals(400, rspHeader.getResponseStatusCode());
        assertNull(rspHeader.getHeader("Location"));

    }

    /** The request is missing a required parameter, includes an invalid parameter value, includes a parameter
     more than once, or is otherwise malformed. **/
    @Test
    public void errorInvalidRequest() throws IOException {
        registry.addWhitelist("12345");
        HttpHeader reqHeader = new HttpHeader();
        reqHeader.setURLAttribute("client_id", "12345");
        reqHeader.setURLAttribute("redirect_uri", "https://example.com");
        HttpHeader rspHeader = HttpTestUtil.makeRequest(authPage, reqHeader);

        // Missing response_type

        assertEquals(302, rspHeader.getResponseStatusCode());
        HttpURL url = new HttpURL(rspHeader.getHeader("Location"));
        assertNull(url.getParameter("code"));
        assertEquals("invalid_request", url.getParameter("error"));

        // response_type is not code

        reqHeader.setURLAttribute("response_type", "not_code");
        rspHeader = HttpTestUtil.makeRequest(authPage, reqHeader);

        assertEquals(302, rspHeader.getResponseStatusCode());
        assertNull(url.getParameter("code"));
        assertEquals("invalid_request", url.getParameter("error"));
    }

    /** The client is not authorized to request an authorization code using this method. **/
    @Test
    public void errorUnauthorizedClient() throws IOException {
        registry.addWhitelist("12345");
        HttpHeader reqHeader = new HttpHeader();
        reqHeader.setURLAttribute("client_id", "67890");
        reqHeader.setURLAttribute("redirect_uri", "https://example.com");
        HttpHeader rspHeader = HttpTestUtil.makeRequest(authPage, reqHeader);

        // Missing response_type

        assertEquals(302, rspHeader.getResponseStatusCode());
        HttpURL url = new HttpURL(rspHeader.getHeader("Location"));
        assertNull(url.getParameter("code"));
        assertEquals("unauthorized_client", url.getParameter("error"));
    }

    @Test
    public void requestBasic() throws IOException {
        registry.addWhitelist("12345");
        HttpHeader reqHeader = new HttpHeader();
        reqHeader.setURLAttribute("client_id", "12345");
        reqHeader.setURLAttribute("redirect_uri", "https://example.com");
        reqHeader.setURLAttribute("response_type", "code");
        HttpHeader rspHeader = HttpTestUtil.makeRequest(authPage, reqHeader);

        assertEquals(302, rspHeader.getResponseStatusCode());
        HttpURL url = new HttpURL(rspHeader.getHeader("Location"));
        assertEquals("example.com", url.getHost());
        assertNotNull(url.getParameter("code"));
        assertNull(url.getParameter("state"));
    }

    @Test
    public void requestWithState() throws IOException {
        registry.addWhitelist("12345");
        HttpHeader reqHeader = new HttpHeader();
        reqHeader.setURLAttribute("client_id", "12345");
        reqHeader.setURLAttribute("redirect_uri", "https://example.com");
        reqHeader.setURLAttribute("response_type", "code");
        reqHeader.setURLAttribute("state", "app_state");
        HttpHeader rspHeader = HttpTestUtil.makeRequest(authPage, reqHeader);

        assertEquals(302, rspHeader.getResponseStatusCode());
        HttpURL url = new HttpURL(rspHeader.getHeader("Location"));
        assertEquals("app_state", url.getParameter("state"));
    }
}