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

import zutil.ObjectUtil;
import zutil.converter.Converter;
import zutil.io.IOUtil;
import zutil.log.LogUtil;
import zutil.net.http.HttpClient;
import zutil.net.http.HttpHeader;
import zutil.net.http.HttpURL;
import zutil.parser.sdp.SessionDescription;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Logger;

import static zutil.net.media.RTSPClient.RTSPRequestType.*;

/**
 * A client class for controlling a media stream through the RTSP protocol.
 *
 * @see <a href="https://tools.ietf.org/html/rfc2326">RFC2326</a>
 */
public class RTSPClient {
    private static final Logger logger = LogUtil.getLogger();

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
    private static final String PARAMETER_PUBLIC  = "Public";

    // Current media variables

    private final HttpURL url;

    private RTSPRequestType[] supportedOptions;
    private int sequenceId;
    private String sessionId;
    private String transport;


    public RTSPClient(String strUrl) throws MalformedURLException {
        if (strUrl.startsWith("rtsp://"))
            strUrl = strUrl.substring(7);
        this.url = new HttpURL(new URL("http://" + strUrl));
        url.setProtocol("rtsp");
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

        ResponseData resp = sendRequest(http);
        // TODO: parse response

        return null;
    }

    public void setDescription(SessionDescription description) throws IOException {
        HttpClient http = getHttpClient(ANNOUNCE);
        http.setContent(description.toString());

        sendRequest(http);
    }


    public String getParameter(String key) throws IOException {
        HttpClient http = getHttpClient(GET_PARAMETER);
        http.setHeader(HttpHeader.HEADER_CONTENT_TYPE, "text/parameters");
        http.setContent(key);

        ResponseData resp = sendRequest(http);
        // TODO: parse response

        http.close();
        return null;
    }

    public void setParameter(String key, String value) throws IOException {
        HttpClient http = getHttpClient(SET_PARAMETER);
        http.setHeader("Content-Type", "text/parameters");
        http.setContent(key + ": " + value);

        ResponseData resp = sendRequest(http);
        // TODO: parse response
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
    protected void setup() throws IOException {
        if (sessionId == null) {
            supportedOptions = getOptions();

            HttpClient http = getHttpClient(SETUP);
            http.setHeader("Transport", "RTP/AVP;unicast;client_port=4588-4589");

            ResponseData resp = sendRequest(http);
            sessionId = resp.header.getHeader(PARAMETER_SESSION);
            transport = resp.header.getHeader("Transport");
        }
    }

    /**
     * Will check what request types are supported by the server
     *
     * @return an array of RTSP request types that are supported by the server.
     */
    protected RTSPRequestType[] getOptions() throws IOException {
        HttpClient http = getHttpClient(OPTIONS);
        ResponseData resp = sendRequest(http);

        String options = resp.header.getHeader(PARAMETER_PUBLIC);
        if (!ObjectUtil.isEmpty(options)) {
            String[] arr = options.split(",");
            RTSPRequestType[] ret = new RTSPRequestType[arr.length];

            // Convert received options to the enum type
            for (int i=0; i<arr.length; i++) {
                ret[i] = RTSPRequestType.valueOf(arr[i].trim());
            }

            logger.fine("Received supported options: " + Arrays.toString(ret));
            return ret;
        }

        return new RTSPRequestType[]{};
    }

    /**
     * Stop the stream and release any associated resources on the server.
     */
    public void close() throws IOException {
        HttpClient http = getHttpClient(TEARDOWN);
        sendRequest(http);
    }


    private HttpClient getHttpClient(RTSPRequestType type) {
        HttpClient http = new HttpClient(type.toString());
        http.setProtocol("RTSP");
        http.setURL(url);
        http.setAbsoluteURL(true);
        http.setHeader("Accept", "application/sdp");
        http.setHeader("Accept", "application/sdp");
        http.setHeader("CSeq", "" + sequenceId++);
        if (sessionId != null)
            http.setHeader(PARAMETER_SESSION, sessionId);

        return http;
    }

    private ResponseData sendRequest(HttpClient http) throws IOException {
        ResponseData resp = new ResponseData();

        logger.finest("RTSP Request: " + http);
        resp.header = http.send();
        logger.finest("RTSP Response: " + resp.header);

        //resp.content = IOUtil.readContentAsString(http.getResponseInputStream());
        http.close();
        return resp;
    }

    private static class ResponseData {
        HttpHeader header;
        String content;
    }
}
