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
package zutil.net.media;

import zutil.io.IOUtil;
import zutil.net.http.HttpClient;
import zutil.net.http.HttpHeader;
import zutil.parser.sdp.SessionDescription;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static zutil.net.media.RTSPClient.RTSPRequestType.*;

/**
 * A client class for controlling a media stream through the RTSP protocol.
 *
 * @see <a href="https://tools.ietf.org/html/rfc2326">RFC2326</a>
 */
public class RTSPClient {
    public enum RTSPRequestType {
        DESCRIBE,      // RFC Section 10.2
        ANNOUNCE,      // RFC Section 10.3
        GET_PARAMETER, // RFC Section 10.8
        OPTIONS,       // RFC Section 10.1
        PAUSE,         // RFC Section 10.6
        PLAY,          // RFC Section 10.5
        RECORD,        // RFC Section 10.11
        REDIRECT,      // RFC Section 10.10
        SETUP,         // RFC Section 10.4
        SET_PARAMETER, // RFC Section 10.9
        TEARDOWN,      // RFC Section 10.7
    }

    // Constants

    private static final String PARAMETER_SESSION = "Session";

    // Current media variables

    private final URL url;

    private String sessionId;
    private String transport;


    public RTSPClient(String strUrl) throws MalformedURLException {
        if (strUrl.startsWith("rtsp://"))
            strUrl = strUrl.substring(7);
        this.url = new URL("http://" + strUrl);
    }

    // -----------------------------
    // Actions
    // -----------------------------

    /**
     * Start playing the media
     */
    public void play() throws IOException {
        setup();
        // TODO: header Range: npt=10-15
    }

    /**
     * Request server to record the media, might not be supported on some servers.
     */
    public void record() throws IOException {
        setup();
    }

    public void pause() throws IOException {
        setup();
    }

    // -----------------------------
    // Data methods
    // -----------------------------

    /**
     * Will request a description of the media from server.
     *
     * @return a human readable description of the media
     */
    public String getDescription() throws IOException {
        HttpClient http = getHttpClient(DESCRIBE);
        HttpHeader resp = http.send();
        // TODO: parse response

        http.close();
        return null;
    }

    public void setDescription(SessionDescription description) throws IOException {
        HttpClient http = getHttpClient(ANNOUNCE);
        http.setContent(description.toString());
        http.send();

        http.close();
    }


    public String getParameter(String key) throws IOException {
        HttpClient http = getHttpClient(GET_PARAMETER);
        http.setHeader(HttpHeader.HEADER_CONTENT_TYPE, "text/parameters");
        http.setContent(key);
        http.send();

        String response = IOUtil.readContentAsString(http.getResponseInputStream());
        // TODO: parse response

        http.close();
        return null;
    }

    public void setParameter(String key, String value) throws IOException {
        HttpClient http = getHttpClient(SET_PARAMETER);
        http.setHeader("Content-Type", "text/parameters");
        http.setContent(key + ": " + value);
        http.send();

        http.close();
    }


    public String getSessionID() {
        return sessionId;
    }

    public String getTransport() {
        return transport;
    }

    // -----------------------------
    // Other methods
    // -----------------------------

    /**
     * Initiates the session by allocating resources on the server.
     * This function will only initiate the resources once any other call
     * will only return without any action.
     */
    private void setup() throws IOException {
        if (sessionId == null) {
            HttpClient http = getHttpClient(SETUP);
            http.setHeader("Transport", "RTP/AVP;unicast;client_port=4588-4589");
            HttpHeader resp = http.send();

            sessionId = resp.getHeader(PARAMETER_SESSION);
            transport = resp.getHeader("Transport");
            http.close();
        }
    }

    /**
     * Stop the stream and release any associated resources on the server.
     */
    public void close() throws IOException {
        HttpClient http = getHttpClient(TEARDOWN);
        http.send();
    }


    private HttpClient getHttpClient(RTSPRequestType type) {
        HttpClient http = new HttpClient(type.toString());
        http.setURL(url); // TODO: Request line is required to contain the whole URL and not tha path
        http.setHeader("Accept", "application/sdp");
        http.setHeader(PARAMETER_SESSION, sessionId);

        return http;
    }
}
