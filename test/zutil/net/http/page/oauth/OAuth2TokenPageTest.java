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
import zutil.io.IOUtil;
import zutil.net.http.HttpHeader;
import zutil.net.http.HttpTestUtil;
import zutil.parser.DataNode;
import zutil.parser.json.JSONParser;

import java.io.IOException;

import static org.junit.Assert.*;

public class OAuth2TokenPageTest {

    private static final String VALID_CLIENT_ID = "12345";
    private static final String VALID_REDIRECT_URI = "https://example.com";
    private static final String VALID_AUTH_CODE = "secret_code";
    private static final String VALID_GRANT_TYPE = "authorization_code";

    private OAuth2Registry registry;
    private OAuth2TokenPage tokenPage;

    @Before
    public void init(){
        registry = new OAuth2Registry();
        tokenPage = new OAuth2TokenPage(registry);

        registry.addWhitelist(VALID_CLIENT_ID);
        registry.registerAuthorizationCode(VALID_CLIENT_ID, VALID_AUTH_CODE);
    }

    @Test
    public void errorInvalidRequest() throws IOException {
        HttpHeader reqHeader = new HttpHeader();
        HttpHeader rspHeader = HttpTestUtil.makeRequest(tokenPage, reqHeader);

        assertEquals(400, rspHeader.getResponseStatusCode());
        DataNode json = JSONParser.read(IOUtil.readContentAsString(rspHeader.getInputStream()));
        assertEquals("invalid_request", json.getString("error"));

        // Missing grant_type

        reqHeader = new HttpHeader();
        reqHeader.setURLAttribute("client_id", VALID_CLIENT_ID);
        reqHeader.setURLAttribute("redirect_uri", VALID_REDIRECT_URI);
        reqHeader.setURLAttribute("code", VALID_AUTH_CODE);
        rspHeader = HttpTestUtil.makeRequest(tokenPage, reqHeader);

        assertEquals(400, rspHeader.getResponseStatusCode());
        json = JSONParser.read(IOUtil.readContentAsString(rspHeader.getInputStream()));
        assertEquals("invalid_request", json.getString("error"));

        // Missing code

        reqHeader = new HttpHeader();
        reqHeader.setURLAttribute("client_id", VALID_CLIENT_ID);
        reqHeader.setURLAttribute("redirect_uri", VALID_REDIRECT_URI);
        reqHeader.setURLAttribute("grant_type", VALID_GRANT_TYPE);
        rspHeader = HttpTestUtil.makeRequest(tokenPage, reqHeader);

        assertEquals(400, rspHeader.getResponseStatusCode());
        json = JSONParser.read(IOUtil.readContentAsString(rspHeader.getInputStream()));
        assertEquals("invalid_request", json.getString("error"));

        // Missing redirect_uri

        reqHeader = new HttpHeader();
        reqHeader.setURLAttribute("client_id", VALID_CLIENT_ID);
        reqHeader.setURLAttribute("grant_type", VALID_GRANT_TYPE);
        reqHeader.setURLAttribute("code", VALID_AUTH_CODE);
        rspHeader = HttpTestUtil.makeRequest(tokenPage, reqHeader);

        assertEquals(400, rspHeader.getResponseStatusCode());
        json = JSONParser.read(IOUtil.readContentAsString(rspHeader.getInputStream()));
        assertEquals("invalid_request", json.getString("error"));

        // Missing client_id

        reqHeader = new HttpHeader();
        reqHeader.setURLAttribute("redirect_uri", VALID_REDIRECT_URI);
        reqHeader.setURLAttribute("grant_type", VALID_GRANT_TYPE);
        reqHeader.setURLAttribute("code", VALID_AUTH_CODE);
        rspHeader = HttpTestUtil.makeRequest(tokenPage, reqHeader);

        assertEquals(400, rspHeader.getResponseStatusCode());
        json = JSONParser.read(IOUtil.readContentAsString(rspHeader.getInputStream()));
        assertEquals("invalid_request", json.getString("error"));
    }

    @Test
    public void errorInvalidClient() throws IOException {
        HttpHeader reqHeader = new HttpHeader();
        reqHeader.setURLAttribute("client_id", "67890");
        reqHeader.setURLAttribute("redirect_uri", VALID_REDIRECT_URI);
        reqHeader.setURLAttribute("grant_type", VALID_GRANT_TYPE);
        reqHeader.setURLAttribute("code", VALID_AUTH_CODE);
        HttpHeader rspHeader = HttpTestUtil.makeRequest(tokenPage, reqHeader);

        assertEquals(400, rspHeader.getResponseStatusCode());
        DataNode json = JSONParser.read(IOUtil.readContentAsString(rspHeader.getInputStream()));
        assertEquals("invalid_client", json.getString("error"));

    }

    @Test
    public void errorInvalidGrant() throws IOException {
        HttpHeader reqHeader = new HttpHeader();
        reqHeader.setURLAttribute("client_id", VALID_CLIENT_ID);
        reqHeader.setURLAttribute("redirect_uri", VALID_REDIRECT_URI);
        reqHeader.setURLAttribute("grant_type", VALID_GRANT_TYPE);
        reqHeader.setURLAttribute("code", "fake_code");
        HttpHeader rspHeader = HttpTestUtil.makeRequest(tokenPage, reqHeader);

        assertEquals(400, rspHeader.getResponseStatusCode());
        DataNode json = JSONParser.read(IOUtil.readContentAsString(rspHeader.getInputStream()));
        assertEquals("invalid_grant", json.getString("error"));
    }

    @Test
    public void errorUnsupportedGrantType() throws IOException {
        HttpHeader reqHeader = new HttpHeader();
        reqHeader.setURLAttribute("client_id", VALID_CLIENT_ID);
        reqHeader.setURLAttribute("redirect_uri", VALID_REDIRECT_URI);
        reqHeader.setURLAttribute("grant_type", "fake_grant_type");
        reqHeader.setURLAttribute("code", VALID_AUTH_CODE);
        HttpHeader rspHeader = HttpTestUtil.makeRequest(tokenPage, reqHeader);

        assertEquals(400, rspHeader.getResponseStatusCode());
        DataNode json = JSONParser.read(IOUtil.readContentAsString(rspHeader.getInputStream()));
        assertEquals("unsupported_grant_type", json.getString("error"));
    }


    private HttpHeader doBasicRequest() throws IOException {
        HttpHeader reqHeader = new HttpHeader();
        reqHeader.setURLAttribute("client_id", VALID_CLIENT_ID);
        reqHeader.setURLAttribute("redirect_uri", VALID_REDIRECT_URI);
        reqHeader.setURLAttribute("grant_type", VALID_GRANT_TYPE);
        reqHeader.setURLAttribute("code", VALID_AUTH_CODE);
        HttpHeader rspHeader = HttpTestUtil.makeRequest(tokenPage, reqHeader);

        return rspHeader;
    }

    @Test
    public void requestBasic() throws IOException {
        HttpHeader rspHeader = doBasicRequest();

        assertEquals(200, rspHeader.getResponseStatusCode());
        assertEquals("application/json", rspHeader.getHeader("Content-Type"));
        DataNode json = JSONParser.read(IOUtil.readContentAsString(rspHeader.getInputStream()));
        assertNotNull(json.getString("refresh_token"));
        assertNotNull(json.getString("access_token"));
        assertNotNull(json.getString("expires_in"));
        assertEquals("bearer", json.getString("token_type"));

        assertTrue(registry.isAccessTokenValid(json.getString("access_token")));
    }

    @Test
    public void revocationCode() throws IOException {
        requestBasic();

        HttpHeader reqHeader = new HttpHeader();
        reqHeader.setURLAttribute("client_id", VALID_CLIENT_ID);
        reqHeader.setURLAttribute("redirect_uri", VALID_REDIRECT_URI);
        reqHeader.setURLAttribute("grant_type", VALID_GRANT_TYPE);
        reqHeader.setURLAttribute("code", VALID_AUTH_CODE);
        HttpHeader rspHeader = HttpTestUtil.makeRequest(tokenPage, reqHeader);

        assertEquals(400, rspHeader.getResponseStatusCode());
        DataNode json = JSONParser.read(IOUtil.readContentAsString(rspHeader.getInputStream()));
        assertEquals("invalid_grant", json.getString("error"));
    }

    @Test
    public void requestRefreshToken() throws IOException {
        HttpHeader rspHeader = doBasicRequest();
        DataNode json = JSONParser.read(IOUtil.readContentAsString(rspHeader.getInputStream()));
        String refreshToken = json.getString("refresh_token");

        assertTrue(registry.isAuthorizationCodeValid(refreshToken));

        HttpHeader reqHeader = new HttpHeader();
        reqHeader.setURLAttribute("grant_type", "refresh_token");
        reqHeader.setURLAttribute("refresh_token", refreshToken);
        rspHeader = HttpTestUtil.makeRequest(tokenPage, reqHeader);

        assertEquals(200, rspHeader.getResponseStatusCode());
        json = JSONParser.read(IOUtil.readContentAsString(rspHeader.getInputStream()));
        assertNotNull(json.getString("refresh_token"));
        assertNotNull(json.getString("access_token"));
        assertNotNull(json.getString("expires_in"));
        assertEquals("bearer", json.getString("token_type"));

        assertTrue(registry.isAccessTokenValid(json.getString("access_token")));
        assertTrue(registry.isAuthorizationCodeValid(json.getString("refresh_token")));
        assertFalse(registry.isAuthorizationCodeValid(refreshToken));
    }
}