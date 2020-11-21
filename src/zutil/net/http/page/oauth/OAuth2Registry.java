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

import zutil.Timer;

import java.util.HashMap;
import java.util.Map;

/**
 * A data class containing authentication information for individual
 * clients going through the OAuth 2 process.
 */
public class OAuth2Registry {
    private static final long DEFAULT_TIMEOUT = 24 * 60 * 60 * 1000; // 24h

    private Map<String, ClientRegister> clientRegistry = new HashMap<>();
    private boolean requireWhitelist = true;


    // ------------------------------------------------------
    // Whitelist methods
    // ------------------------------------------------------

    /**
     * Set the requirement or non-requirement of pre-registered client-ids.
     * If enabled then any clients starting a OAuth2 process needs to have a
     * preregistered client-id value in the registry object. (Default is set to true)
     *
     * @param enabled if true then all requests will be required to be in whitelist
     */
    public void requireWhitelisting(boolean enabled) {
        requireWhitelist = enabled;
    }

    /**
     * Register a client-id to be whitelisted. Note this function
     * has no impact if requireWhitelisting is set to false.
     *
     * @param clientId A String ID that should be whitelisted
     */
    public void addWhitelist(String clientId) {
        if (!clientRegistry.containsKey(clientId)) {
            clientRegistry.put(clientId, new ClientRegister());
        }
    }

    // ------------------------------------------------------
    // Validation methods
    // ------------------------------------------------------

    /**
     * Validates a client_id value is a valid value and is in the whitelist fro approved clients.
     *
     * @param clientId the client_id value to validate
     * @return true if the client_id is allowed to start the OAuth process.
     */
    public boolean isClientIdValid(String clientId) {
        if (clientId == null)
            return false;

        if (!requireWhitelist)
            return true;
        return clientRegistry.containsKey(clientId);
    }

    /**
     * Validates that a authorization code has valid format and has been authorized and not elapsed.
     *
     * @param clientId the id of the requesting client
     * @param code the code that should be validated
     * @return true if the given code is valid otherwise false.
     */
    public boolean isAuthorizationCodeValid(String clientId, String code) {
        if (clientId == null || code == null)
            return false;

        ClientRegister reg = getClientRegistry(clientId);

        if (reg != null) {
            return reg.authCodes.containsKey(code) &&
                    !reg.authCodes.get(code).hasTimedOut();
        }
        return false;
    }

    /**
     * Validates that a access token has valid format and has been authorized and not elapsed.
     *
     * @param clientId the id of the requesting client
     * @param token the token that should be validated
     * @return true if the given token is valid otherwise false.
     */
    public boolean isAccessTokenValid(String clientId, String token) {
        if (clientId == null || token == null)
            return false;

        ClientRegister reg = getClientRegistry(clientId);

        if (reg != null) {
            return reg.accessTokens.containsKey(token) &&
                    !reg.accessTokens.get(token).hasTimedOut();
        }
        return false;
    }

    // ------------------------------------------------------
    // OAuth2 process methods
    // ------------------------------------------------------

    protected long registerAuthorizationCode(String clientId, String code) {
        return registerAuthorizationCode(clientId, code, DEFAULT_TIMEOUT);
    }
    protected long registerAuthorizationCode(String clientId, String code, long timeoutMillis) {
        ClientRegister reg = getClientRegistry(clientId);

        if (reg != null) {
            reg.authCodes.put(code, new Timer(timeoutMillis));
            return timeoutMillis;
        }
        return -1;
    }

    protected long registerAccessToken(String clientId, String token) {
        return registerAccessToken(clientId, token, DEFAULT_TIMEOUT);
    }
    protected long registerAccessToken(String clientId, String token, long timeoutMillis) {
        ClientRegister reg = getClientRegistry(clientId);

        if (reg != null) {
            reg.accessTokens.put(token, new Timer(timeoutMillis));
            return timeoutMillis;
        }
        return -1;
    }

    // --------------------------------------------------------------------

    private ClientRegister getClientRegistry(String clientId) {
        if (!requireWhitelist && !clientRegistry.containsKey(clientId))
            clientRegistry.put(clientId, new ClientRegister());

        return clientRegistry.get(clientId);
    }

    private static class ClientRegister {
        Map<String, Timer> authCodes = new HashMap<>();
        Map<String, Timer> accessTokens = new HashMap<>();
    }
}
