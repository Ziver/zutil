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

import zutil.log.LogUtil;
import zutil.net.http.HttpHeader;
import zutil.net.http.HttpPage;
import zutil.net.http.HttpPrintStream;
import zutil.net.http.HttpURL;

import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

/**
 * This endpoint is the first step in the OAuth 2 procedure.
 * The purpose of this page is get authorization from the user to share a resource.
 *
 * <pre>
 * From RFC:
 *      +---------+                                  +---------------+
 *      |         |&#62;---(D)-- Authorization Code ----&#62;|               |
 *      |  Client |          and Redirection URI     | Authorization |
 *      |         |                                  |     Server    |
 *      |         |&#60;---(E)----- Access Token -------&#60;|               |
 *      +---------+     (w/ Optional Refresh Token)  +---------------+
 * </pre>
 *
 * @see <a href="https://tools.ietf.org/html/rfc6749#section-4">RFC 6749: Chapter 4</a>
 */
public class OAuth2AuthorizationPage implements HttpPage {
    private static final Logger logger = LogUtil.getLogger();

    /** The request is missing a required parameter, includes an invalid parameter value, includes a parameter
     more than once, or is otherwise malformed. **/
    private static final String ERROR_INVALID_REQUEST = "invalid_request";
    /** The client is not authorized to request an authorization code using this method. **/
    private static final String ERROR_UNAUTHORIZED_CLIENT = "unauthorized_client";
    /** The resource owner or authorization server denied the request. **/
    private static final String ERROR_ACCESS_DENIED = "access_denied";
    /** The authorization server does not support obtaining an authorization code using this method. **/
    private static final String ERROR_UNSUPPORTED_RESP_TYPE = "unsupported_response_type";
    /** The requested scope is invalid, unknown, or malformed. **/
    private static final String ERROR_INVALID_SCOPE = "invalid_scope";
    /** The authorization server encountered an unexpected condition that prevented it from fulfilling the request.
     (This error code is needed because a 500 Internal Server Error HTTP status code cannot be returned to the client
     via an HTTP redirect.) **/
    private static final String ERROR_SERVER_ERROR = "server_error";
    /** The authorization server is currently unable to handle the request due to a temporary overloading or maintenance
     of the server.  (This error code is needed because a 503 Service Unavailable HTTP status code cannot be returned
     to the client via an HTTP redirect.) **/
    private static final String ERROR_TEMPORARILY_UNAVAILABLE = "temporarily_unavailable";

    private static final String RESPONSE_TYPE_CODE = "code";
    private static final String RESPONSE_TYPE_PASSWORD = "password";
    private static final String RESPONSE_TYPE_CREDENTIALS = "client_credentials";

    private Random random = new Random();
    private OAuth2Registry registry;


    public OAuth2AuthorizationPage(OAuth2Registry registry) {
        this.registry = registry;
    }


    @Override
    public void respond(
            HttpPrintStream out,
            HttpHeader headers,
            Map<String, Object> session,
            Map<String, String> cookie,
            Map<String, String> request) {

        // -----------------------------------------------
        // Validate parameters
        // -----------------------------------------------

        // Validate redirect_uri

        if (!request.containsKey("redirect_uri")) {
            errorResponse(out, "Bad Request, missing parameter: redirect_uri");
            return;
        }

        HttpURL url = null;
        try {
            url = new HttpURL(URLDecoder.decode(request.get("redirect_uri")));
        } catch(Exception e) {}

        if (url == null || !"HTTPS".equalsIgnoreCase(url.getProtocol())) {
            errorResponse(out, "Invalid redirect URL: " + request.get("redirect_uri"));
            return;
        }

        // Validate client_id

        if (!request.containsKey("client_id")) {
            errorResponse(out, "Bad Request, missing parameter: client_id");
            return;
        }

        String clientId = request.get("client_id");

        if (!registry.isClientIdValid(clientId)) {
            errorRedirect(out, url, ERROR_UNAUTHORIZED_CLIENT, request.get("state"),
                    "Bad Request, invalid client_id value.");
            return;
        }

        // Validate response_type

        if (!request.containsKey("response_type")) {
            errorRedirect(out, url, ERROR_INVALID_REQUEST, request.get("state"),
                    "Missing parameter response_type.");
            return;
        }

        // -----------------------------------------------
        // Handle request
        // -----------------------------------------------

        switch (request.get("response_type")) {
            case RESPONSE_TYPE_CODE:
                String code = generateCode();
                registry.registerAuthorizationCode(clientId, code);

                url.setParameter("code", code);
                if (request.containsKey("state"))
                    url.setParameter("state", request.get("state"));
                break;
            case RESPONSE_TYPE_PASSWORD:
            case RESPONSE_TYPE_CREDENTIALS:
            default:
                errorRedirect(out, url, ERROR_INVALID_REQUEST, request.get("state"),
                        "unsupported response_type: " + request.get("response_type"));
                return;
        }

        // Setup the redirect

        redirect(out, url);
    }

    private String generateCode() {
        return String.valueOf(Math.abs(random.nextLong()));
    }

    // ------------------------------------------------------
    // Error handling
    // ------------------------------------------------------

    private static void errorResponse(HttpPrintStream out, String description) {
        out.setResponseStatusCode(400);
        out.println(description);
    }

    /**
     * @see <a href="https://tools.ietf.org/html/rfc6749#section-4.2.2.1">RFC 6749: Chapter 4.2.2.1</a>
     *
     * @param out
     * @param url
     * @param error
     * @param state
     * @param description
     */
    private static void errorRedirect(HttpPrintStream out, HttpURL url, String error, String state, String description) {
        out.setHeader(HttpHeader.HEADER_CONTENT_TYPE, "application/x-www-form-urlencoded");
        url.setParameter("error", error);
        if (description != null) url.setParameter("error_description", description);
        //if (uri != null)         url.setParameter("error_uri", uri);
        if (state != null)       url.setParameter("state", state);

        redirect(out, url);
    }

    private static void redirect(HttpPrintStream out, HttpURL url) {
        out.setResponseStatusCode(302);
        out.setHeader(HttpHeader.HEADER_LOCATION, url.toString());
    }
}
