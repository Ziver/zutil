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

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * Handles URLs in the HTTP protocol
 *
 * @author Ziver
 */
public class HttpURL {
    public static final String PROTOCOL_SEPARATOR = "://";
    public static final String PORT_SEPARATOR = ":";
    public static final String PATH_SEPARATOR = "/";
    public static final String PARAMETER_SEPARATOR = "?";
    public static final String ANCHOR_SEPARATOR = "#";

    private String protocol = "";
    private String host = "127.0.0.1";
    private int port = -1;
    private String path;
    private String anchor;

    private HashMap<String, String> parameters = new HashMap<>();


    public HttpURL() {}

    public HttpURL(String urlStr) throws MalformedURLException {
        this(new URL(urlStr));
    }

    public HttpURL(URL url) {
        this.setProtocol(url.getProtocol());
        this.setHost(url.getHost());
        this.setPort(url.getPort());
        this.setPath(url.getPath());
        this.setParameters(url.getQuery());
    }


    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getPath() {
        return path;
    }

    public String getAnchor() {
        return anchor;
    }

    public void setProtocol(String prot) {
        this.protocol = prot;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setPath(String path) {
        if (path.length() >= 1 && !path.startsWith(PATH_SEPARATOR))
            path = PATH_SEPARATOR + path;
        this.path = path;
    }

    public void setAnchor(String anch) {
        this.anchor = anch;
    }

    public void setParameter(String key, String value) {
        this.parameters.put(key, value);
    }

    protected void setParameters(HashMap<String, String> pars) {
        this.parameters = pars;
    }

    protected void setParameters(String query) {
        if (query == null)
            return;
        HttpHeaderParser.parseURLParameters(parameters, query);
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    /**
     * Generates the parameter string in a URL.
     * <p>
     * e.g.
     * "key=value&amp;key2=value&amp;..."
     */
    public String getParameterString() {
        StringBuilder param = new StringBuilder();
        for (String key : parameters.keySet()) {
            if (param.length() > 0)
                param.append('&');
            param.append(key);
            param.append('=');
            param.append(parameters.get(key));
        }
        return param.toString();
    }

    /**
     * Generates a path that are used in the HTTP header
     */
    public String getHttpURL() {
        StringBuilder url = new StringBuilder();
        url.append(path);
        if (!parameters.isEmpty())
            url.append(PARAMETER_SEPARATOR).append(getParameterString());

        return url.toString();
    }

    /**
     * Generates a full URL
     */
    public String getURL() {
        return toString();
    }

    /**
     * Generates the whole URL
     */
    public String toString() {
        StringBuilder url = new StringBuilder();
        url.append(protocol);
        url.append(PROTOCOL_SEPARATOR);
        url.append(host);
        if (port > 0)
            url.append(PORT_SEPARATOR).append(port);

        if (path != null)
            url.append(path);
        else
            url.append(PATH_SEPARATOR);

        if (!parameters.isEmpty())
            url.append(PARAMETER_SEPARATOR).append(getParameterString());
        if (anchor != null)
            url.append(ANCHOR_SEPARATOR).append(anchor);

        return url.toString();
    }
}
