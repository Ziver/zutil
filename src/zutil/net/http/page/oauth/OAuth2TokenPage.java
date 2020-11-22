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

import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import zutil.log.LogUtil;
import zutil.net.http.HttpHeader;
import zutil.net.http.HttpPrintStream;
import zutil.net.http.page.HttpJsonPage;
import zutil.parser.DataNode;

/**
 * This endpoint is the second step in the OAuth 2 procedure.
 * The purpose of this page is give a token that should be used for all consequent HTTP.
 *
 * <pre>
 * From RFC:
 *   +---------+                                  +---------------+
 *   |         |                                  |               |
 *   |         |&#62;--(A)- Client Authentication ---&#62;| Authorization |
 *   | Client  |                                  |     Server    |
 *   |         |&#60;--(B)---- Access Token ---------&#60;|               |
 *   |         |                                  |               |
 *   +---------+                                  +---------------+
 * </pre>
 *
 *
 * @see <a href="https://tools.ietf.org/html/rfc6749#section-5">RFC 6749: Chapter 5</a>
 */
public class OAuth2TokenPage extends HttpJsonPage {
    private static final Logger logger = LogUtil.getLogger();

    /** The request is missing a required parameter, includes an unsupported parameter value (other than grant type),
     repeats a parameter, includes multiple credentials, utilizes more than one mechanism for authenticating the
     client, or is otherwise malformed. **/
    private static final String ERROR_INVALID_REQUEST = "invalid_request";
    /** Client authentication failed (e.g., unknown client, no client authentication included, or unsupported
     authentication method). **/
    private static final String ERROR_INVALID_CLIENT = "invalid_client";
    /** The provided authorization grant (e.g., authorization code, resource owner credentials) or refresh token is
     invalid, expired, revoked, does not match the redirection URI used in the authorization request, or was issued to
     another client. **/
    private static final String ERROR_INVALID_GRANT = "invalid_grant";
    /** The authenticated client is not authorized to use this authorization grant type. **/
    private static final String ERROR_UNAUTHORIZED_CLIENT = "unauthorized_client";
    /** The authorization grant type is not supported by the authorization server. **/
    private static final String ERROR_UNSUPPORTED_GRANT_TYPE = "unsupported_grant_type";
    /** The requested scope is invalid, unknown, malformed, or exceeds the scope granted by the resource owner. **/
    private static final String ERROR_INVALID_SCOPE = "invalid_scope";

    private Random random = new Random();
    private OAuth2Registry registry;


    public OAuth2TokenPage(OAuth2Registry registry) {
        this.registry = registry;
    }


    @Override
    public DataNode jsonRespond(
            HttpPrintStream out,
            HttpHeader headers,
            Map<String, Object> session,
            Map<String, String> cookie,
            Map<String, String> request) {

        // POST

        out.setHeader("Access-Control-Allow-Origin", "*");
        out.setHeader("Cache-Control", "no-store");
        out.setHeader("Pragma", "no-cache");

        // -----------------------------------------------
        // Validate parameters
        // -----------------------------------------------

        DataNode jsonRes = new DataNode(DataNode.DataType.Map);

        // Validate client_id

        if (!request.containsKey("client_id"))
            return errorResponse(out, ERROR_INVALID_REQUEST , request.get("state"), "Missing mandatory parameter: client_id");

        String clientId = request.get("client_id");

        if (!registry.isClientIdValid(clientId))
            return errorResponse(out, ERROR_INVALID_CLIENT , request.get("state"), "Invalid client_id value.");

        // Validate code

        if (!request.containsKey("code"))
            return errorResponse(out, ERROR_INVALID_REQUEST , request.get("state"), "Missing mandatory parameter: code");

        if (!registry.isAuthorizationCodeValid(clientId, request.get("code")))
            return errorResponse(out, ERROR_INVALID_GRANT, request.get("state"), "Invalid authorization code value.");

        // Validate redirect_uri

        if (!request.containsKey("redirect_uri"))
            return errorResponse(out, ERROR_INVALID_REQUEST , request.get("state"), "Missing mandatory parameter: redirect_uri");

        // TODO: ensure that the "redirect_uri" parameter is present if the
        //      "redirect_uri" parameter was included in the initial authorization
        //      request as described in Section 4.1.1, and if included ensure that
        //      their values are identical.

        // Validate grant_type

        if (!request.containsKey("grant_type"))
            return errorResponse(out, ERROR_INVALID_REQUEST , request.get("state"), "Missing mandatory parameter grant_type.");

        // -----------------------------------------------
        // Handle request
        // -----------------------------------------------

        String grantType = request.get("grant_type");

        switch (grantType) {
            case "authorization_code":
                jsonRes.set("refresh_token", "TODO"); // TODO: implement refresh logic
                break;
            default:
                return errorResponse(out, ERROR_UNSUPPORTED_GRANT_TYPE, request.get("state"), "Unsupported grant_type: " + request.containsKey("grant_type"));
        }

        String token = generateToken();
        long timeoutMillis = registry.registerAccessToken(clientId, token);

        jsonRes.set("access_token", token);
        jsonRes.set("token_type", "bearer");
        jsonRes.set("expires_in", timeoutMillis/1000);
        //jsonRes.set("scope", ?);
        if (request.containsKey("state")) jsonRes.set("state", request.get("state"));

        return jsonRes;
    }

    private String generateToken() {
        return String.valueOf(Math.abs(random.nextLong()));
    }

    // ------------------------------------------------------
    // Error handling
    // ------------------------------------------------------

    /**
     * @see <a href="https://tools.ietf.org/html/rfc6749#section-5.2">RFC 6749: Chapter 5.2</a>
     *
     * @param out
     * @param error
     * @param state
     * @param description
     * @return A DataNode containing the error response
     */
    private static DataNode errorResponse(HttpPrintStream out, String error, String state, String description) {
        logger.warning("OAuth2 Token Error(" + error + "): " + description);

        out.setResponseStatusCode(400);

        DataNode jsonErr = new DataNode(DataNode.DataType.Map);
        jsonErr.set("error", error);
        if (description != null) jsonErr.set("error_description", description);
        //if (uri != null)         jsonErr.set("error_uri", uri);
        if (state != null)       jsonErr.set("state", state);

        return jsonErr;
    }
}
