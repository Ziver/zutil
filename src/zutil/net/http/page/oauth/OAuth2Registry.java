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

import zutil.Hasher;
import zutil.Timer;
import zutil.net.http.page.oauth.OAuth2RegistryStore.OAuth2ClientRegister;

import java.io.Serializable;
import java.util.*;

/**
 * A data class containing authentication information for individual
 * clients going through the OAuth 2 process.
 */
public class OAuth2Registry implements Serializable {
    private static final long DEFAULT_CODE_TIMEOUT  =      10 * 60 * 1000; // 10min
    private static final long DEFAULT_TOKEN_TIMEOUT = 24 * 60 * 60 * 1000; // 24h

    private Map<String, OAuth2ClientRegister> clientRegisters = new HashMap<>();
    private boolean requireWhitelist = true;

    transient private OAuth2RegistryStore registryStore;
    transient private List<OAuth2TokenRegistrationListener> tokenListeners = new ArrayList<>();
    transient private Random random = new Random();


    /**
     * Create an in memory only OAuth2 registry.
     */
    public OAuth2Registry() {}
    /**
     * Create new OAuth2 registry with an offline backup store.
     */
    public OAuth2Registry(OAuth2RegistryStore store) {
        this.registryStore = store;

        for (OAuth2ClientRegister register : store.getClientRegistries()) {
            clientRegisters.put(register.clientId, register);
        }
    }

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
        if (!clientRegisters.containsKey(clientId)) {
            OAuth2ClientRegister clientRegister = new OAuth2ClientRegister(clientId);

            clientRegisters.put(clientId, clientRegister);
            if (registryStore != null) registryStore.storeClientRegister(clientRegister);
        }
    }

    // ------------------------------------------------------
    // Validation methods
    // ------------------------------------------------------

    /**
     * Validates a client_id value is a valid value and is in the whitelist from approved clients.
     *
     * @param clientId the client_id value to validate
     * @return true if the client_id is allowed to start the OAuth process.
     */
    public boolean isClientIdValid(String clientId) {
        if (clientId == null)
            return false;

        if (!requireWhitelist)
            return true;
        return clientRegisters.containsKey(clientId);
    }

    /**
     * Validates that an authorization code has valid format and has been authorized and not elapsed.
     *
     * @param code the code that should be validated
     * @return true if the given code is valid otherwise false.
     */
    public boolean isAuthorizationCodeValid(String code) {
        OAuth2ClientRegister clientRegister = getClientRegisterForAuthCode(code);

        if (clientRegister != null) {
            return clientRegister.authCodes.containsKey(code) &&
                    !clientRegister.authCodes.get(code).hasTimedOut();
        }
        return false;
    }

    /**
     * Validates that an access token has valid format and has been authorized and not elapsed.
     *
     * @param token the token that should be validated
     * @return true if the given token is valid otherwise false.
     */
    public boolean isAccessTokenValid(String token) {
        OAuth2ClientRegister clientRegister = getClientRegisterForToken(token);

        if (clientRegister != null) {
            return clientRegister.accessTokens.containsKey(token) &&
                    !clientRegister.accessTokens.get(token).hasTimedOut();
        }
        return false;
    }

    // ------------------------------------------------------
    // Revocation
    // ------------------------------------------------------

    public void revokeAuthorizationCode(String code) {
        OAuth2ClientRegister clientRegister = getClientRegisterForAuthCode(code);

        if (clientRegister != null) {
            clientRegister.authCodes.remove(code);
            if (registryStore != null) registryStore.storeClientRegister(clientRegister);
        }
    }

    // ------------------------------------------------------
    // OAuth2 process methods
    // ------------------------------------------------------

    protected long registerAuthorizationCode(String clientId, String code) {
        return registerAuthorizationCode(clientId, code, DEFAULT_CODE_TIMEOUT);
    }
    protected long registerAuthorizationCode(String clientId, String code, long timeoutMillis) {
        OAuth2ClientRegister clientRegister = getClientRegister(clientId);

        if (clientRegister != null) {
            clientRegister.authCodes.put(code, new Timer(timeoutMillis).start());

            if (registryStore != null) registryStore.storeClientRegister(clientRegister);
            return timeoutMillis;
        }
        return -1;
    }

    protected long registerAccessToken(String clientId, String token) {
        return registerAccessToken(clientId, token, DEFAULT_TOKEN_TIMEOUT);
    }
    protected long registerAccessToken(String clientId, String token, long timeoutMillis) {
        OAuth2ClientRegister clientRegister = getClientRegister(clientId);

        if (clientRegister != null) {
            long absoluteTimeout = System.currentTimeMillis() + timeoutMillis;
            clientRegister.accessTokens.put(token, new Timer(timeoutMillis).start());

            if (registryStore != null) registryStore.storeClientRegister(clientRegister);

            if (tokenListeners != null) {
                for (OAuth2TokenRegistrationListener listener : tokenListeners) {
                    listener.onTokenRegistration(clientId, token, absoluteTimeout);
                }
            }
            return timeoutMillis;
        }
        return -1;
    }

    protected String generateCode() {
        return generateToken();
    }

    protected String generateToken() {
        return Hasher.SHA1(Math.abs(random.nextLong()));
    }

    // ------------------------------------------------------
    // Data methods
    // ------------------------------------------------------

    /**
     * @param code is the authentication code given to the client.
     * @return The client_id registered for the given code
     */
    public String getClientIdForAuthenticationCode(String code) {
        for (String clientId : clientRegisters.keySet()) {
            if (clientRegisters.get(clientId).authCodes.containsKey(code))
                return clientId;
        }

        return null;
    }

    /**
     * @param token is the access token given to the client.
     * @return The client_id registered for the given token
     */
    public String getClientIdForAccessToken(String token) {
        for (String clientId : clientRegisters.keySet()) {
            if (clientRegisters.get(clientId).accessTokens.containsKey(token))
                return clientId;
        }

        return null;
    }

    /**
     * @param clientId is the client_id string
     * @return all the authorization codes assigned to the given clientID or empty list if client does not exist
     */
    public List<String> getAuthenticationCodes(String clientId) {
        OAuth2ClientRegister clientRegister = getClientRegister(clientId);

        if (clientRegister != null)
            return new ArrayList<>(clientRegister.authCodes.keySet());
        return Collections.EMPTY_LIST;
    }

    /**
     * @param clientId is the client_id string
     * @return all the access tokens assigned to the given clientID or empty list if client does not exist
     */
    public List<String> getAccessTokens(String clientId) {
        OAuth2ClientRegister clientRegister = getClientRegister(clientId);

        if (clientRegister != null)
            return new ArrayList<>(clientRegister.accessTokens.keySet());
        return Collections.EMPTY_LIST;
    }

    /**
     * @param token is the access token to get timeout value for.
     * @return an epic based value in milliseconds where the token stops being valid or -1 if token is already invalid or does not exist.
     */
    public long getAccessTokenTimeout(String token) {
        OAuth2ClientRegister clientRegister = getClientRegisterForToken(token);

        if (clientRegister != null)
            return clientRegister.accessTokens.get(token).getTimeoutTimeMillis();
        return -1;
    }

    // ------------------------------------------------------
    // Listeners
    // ------------------------------------------------------

    public void addTokenListener(OAuth2TokenRegistrationListener listener) {
        if (!this.tokenListeners.contains(listener))
            this.tokenListeners.add(listener);
    }

    public interface OAuth2TokenRegistrationListener {
        /**
         * Method will be called when a new token is successfully registered on a client
         *
         * @param clientId          the client ID that got the specified token
         * @param token             a String token that has been generated and provided to the client
         * @param timeoutMillis     the expiration epoc time of the token
         */
        void onTokenRegistration(String clientId, String token, long timeoutMillis);
    }

    // ------------------------------------------------------
    // Client register logic
    // ------------------------------------------------------

    private OAuth2ClientRegister getClientRegister(String clientId) {
        if (!requireWhitelist && !clientRegisters.containsKey(clientId)) {
            OAuth2ClientRegister clientRegister = new OAuth2ClientRegister(clientId);

            clientRegisters.put(clientId, clientRegister);
            if (registryStore != null) registryStore.storeClientRegister(clientRegister);
        }

        return clientRegisters.get(clientId);
    }

    private OAuth2ClientRegister getClientRegisterForAuthCode(String code) {
        String clientId = getClientIdForAuthenticationCode(code);

        return (clientId == null ? null : clientRegisters.get(clientId));
    }

    private OAuth2ClientRegister getClientRegisterForToken(String token) {
        String clientId = getClientIdForAccessToken(token);

        return (clientId == null ? null : clientRegisters.get(clientId));
    }
}
