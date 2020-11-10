/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Ziver Koc
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

import zutil.StringUtil;
import zutil.io.IOUtil;
import zutil.io.StringInputStream;
import zutil.parser.URLDecoder;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class HttpHeaderParser {
    private static final Pattern PATTERN_COLON = Pattern.compile(":");
    private static final Pattern PATTERN_EQUAL = Pattern.compile("=");
    private static final Pattern PATTERN_AND = Pattern.compile("&");


    private InputStream in;
    private boolean skipStatusLine;


    /**
     * Parses the HTTP header information from a stream
     *
     * @param in is the stream
     */
    public HttpHeaderParser(InputStream in) {
        this.in = in;
        this.skipStatusLine = false;
    }

    /**
     * Parses the HTTP header information from a String
     *
     * @param in is the String
     */
    public HttpHeaderParser(String in) {
        this(new StringInputStream(in));
    }


    public HttpHeader read() throws IOException {
        HttpHeader header = new HttpHeader();
        String line;

        // First line
        if (!skipStatusLine) {
            if ((line = IOUtil.readLine(in)) != null && !line.isEmpty())
                parseStatusLine(header, line);
            else
                return null;
        }
        // Read header body
        while ((line = IOUtil.readLine(in)) != null && !line.isEmpty()) {
            parseHeaderLine(header.getHeaderMap(), line);
        }

        // Post processing
        parseCookieValues(header.getCookieMap(), header.getHeader(HttpHeader.HEADER_COOKIE));
        header.setInputStream(in);
        return header;
    }

    /**
     * @param skipStatusLine indicates if the parser should expect a  http status lines. (default: true)
     */
    public void skipStatusLine(boolean skipStatusLine) {
        this.skipStatusLine = skipStatusLine;
    }

    /**
     * Parses the first line of a http request/response and stores the values in a HttpHeader object
     *
     * @param header     the header object where the cookies will be stored.
     * @param statusLine the status line String
     */
    private static void parseStatusLine(HttpHeader header, String statusLine) {
        String[] lineArr = statusLine.split(" ", 3);

        if (lineArr.length != 3)
            return;

        // Server Response
        if (lineArr[0].contains("/")) {
            header.setIsRequest(false);
            header.setProtocol(lineArr[0].substring(0, lineArr[0].indexOf('/')));
            header.setProtocolVersion(Float.parseFloat(lineArr[0].substring(lineArr[0].indexOf('/')+1)));
            header.setResponseStatusCode(Integer.parseInt(lineArr[1]));
            header.getResponseStatusString(lineArr[2]);
        }
        // Client Request
        else {
            header.setIsRequest(true);
            header.setRequestType(lineArr[0]);
            header.setProtocol(lineArr[2].substring(0, lineArr[2].indexOf('/')));
            header.setProtocolVersion(Float.parseFloat(lineArr[2].substring(lineArr[2].indexOf('/')+1)));
            String url = lineArr[1];

            // parse URL and attributes
            int paramStartIndex = url.indexOf('?');
            if (paramStartIndex >= 0) {
                url = statusLine.substring(0, paramStartIndex);
                parseURLParameters(header.getURLAttributeMap(), statusLine.substring(paramStartIndex + 1));
            }
            header.setRequestURL(url.trim());
        }
    }

    /**
     * Parses a http key value paired header line.
     * Note that all header keys will be stored with
     * uppercase notation to make them case insensitive.
     *
     * @param map  a map where the header key(Uppercase) and value will be stored.
     * @param line is the next line in the header
     */
    public static void parseHeaderLine(Map<String, String> map, String line) {
        String[] data = PATTERN_COLON.split(line, 2);
        map.put(
                data[0].trim().toUpperCase(),                    // Key
                (data.length > 1 ? data[1] : "").trim());        //Value
    }

    /**
     * Parses a raw cookie header into key and value pairs
     *
     * @param map         the Map where the cookies will be stored.
     * @param cookieValue the raw cookie header value String that will be parsed
     */
    public static void parseCookieValues(Map<String, String> map, String cookieValue) {
        parseHeaderValues(map, cookieValue, ";");
    }


    /**
     * Parses a header value string that contains key and value paired data and
     * stores them in a HashMap. If a pair only contain a key then the value
     * will be set as a empty string.
     * <p>
     * TODO: method is not quote aware
     *
     * @param headerValue the raw header value String that will be parsed.
     * @param delimiter   the delimiter that separates key and value pairs (e.g. ';' for Cookies or ',' for Cache-Control)
     */
    public static HashMap<String, String> parseHeaderValues(String headerValue, String delimiter) {
        HashMap<String, String> map = new HashMap<>();
        parseHeaderValues(map, headerValue, delimiter);
        return map;
    }

    /**
     * Parses a header value string that contains key and value paired data and
     * stores them in a HashMap. If a pair only contain a key then the value
     * will be set as a empty string.
     * <p>
     * TODO: method is not quote aware
     *
     * @param map         the Map where key and values will be stored.
     * @param headerValue the raw header value String that will be parsed.
     * @param delimiter   the delimiter that separates key and value pairs (e.g. ';' for Cookies or ',' for Cache-Control)
     */
    public static void parseHeaderValues(Map<String, String> map, String headerValue, String delimiter) {
        if (headerValue != null && !headerValue.isEmpty()) {
            String[] tmpArr = headerValue.split(delimiter);
            for (String cookie : tmpArr) {
                String[] tmpStr = PATTERN_EQUAL.split(cookie, 2);
                map.put(
                        tmpStr[0].trim(), // Key
                        StringUtil.trim((tmpStr.length > 1 ? tmpStr[1] : "").trim(), '\"')); //Value
            }
        }
    }


    /**
     * Parses a string with variables from a get or post request that was sent from a client
     *
     * @param header        the header object where the url attributes key and value will be stored.
     * @param urlAttributes is the String containing all the attributes
     */
    public static void parseURLParameters(HttpHeader header, String urlAttributes) {
        parseURLParameters(header.getURLAttributeMap(), urlAttributes);
    }

    /**
     * Parses a string with variables from a get or post request that was sent from a client
     *
     * @param map           a map where the url attributes key and value will be stored.
     * @param urlAttributes is the String containing all the attributes
     */
    public static void parseURLParameters(Map<String, String> map, String urlAttributes) {
        String[] tmp;
        urlAttributes = URLDecoder.decode(urlAttributes);
        // get the variables
        String[] data = PATTERN_AND.split(urlAttributes);
        for (String element : data) {
            tmp = PATTERN_EQUAL.split(element, 2);
            map.put(
                    tmp[0].trim(),                                // Key
                    (tmp.length > 1 ? tmp[1] : "").trim());       //Value
        }
    }
}
