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

package zutil.parser.sdp;

import zutil.io.IOUtil;
import zutil.io.StringInputStream;
import zutil.log.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * A parser class for reading SDP Media Session Description.
 * The protocol describes a
 *
 * @see <a href="https://tools.ietf.org/html/rfc2327">RFC2327</a>
 */
public class SDPParser {
    public static final Logger logger = LogUtil.getLogger();

    private InputStream in;


    public SDPParser(InputStream in) {
        this.in = in;
    }

    public SDPParser(String input) {
        this.in = new StringInputStream(input);
    }

    /**
     * Will read the stream until its end and create SessionDescription objects
     *
     * @return a List of sessions parsed from the InputStream. Empty list if no sessions where defined.
     * @throws IOException if ther eis any IO related issues.
     * @throws RuntimeException if there is any parsing issue.
     */
    public List<SessionDescription> parse() throws IOException {
        List<SessionDescription> sessions = new ArrayList<>();

        String line;
        SessionDescription currentSession = null;
        TimingDescription currentTiming = null;
        MediaDescription currentMedia = null;
        String[] tmpArr;

        while ((line=IOUtil.readLine(in)) != null) {
            if (line.trim().isEmpty())
                continue;

            if (line.length() > 2 && line.charAt(1) != '=') {
                logger.warning("Payload contains invalid SDP format: '" + line + "'");
                continue;
            }

            switch (line.charAt(0)) {
                // --------------------------------------
                // Session description
                // --------------------------------------

                // v=<protocol version> // Start of a new session
                case 'v':
                    currentSession = new SessionDescription();
                    sessions.add(currentSession);
                    currentSession.protocolVersion = Integer.parseInt(getValue(line));
                    break;

                // TODO: o=<owner username> <session id> <session version> <network type> <address type> <address>

                // s=<session title>
                case 's':
                    if (currentSession == null) throw new RuntimeException("Received session title before session definition: '" + line + "'");

                    currentSession.sessionTitle = getValue(line);
                    break;

                // [optional] u=<Session description URI>
                case 'u':
                    if (currentSession == null) throw new RuntimeException("Received session URI before session definition: '" + line + "'");

                    currentSession.sessionURI = getValue(line);
                    break;

                // [optional] e=<email address>
                case 'e':
                    if (currentSession == null) throw new RuntimeException("Received organizer email before session definition: '" + line + "'");

                    currentSession.organizerEmail = getValue(line);
                    break;

                // [optional] p=<phone number>
                case 'p':
                    if (currentSession == null) throw new RuntimeException("Received organizer phone before session definition: '" + line + "'");

                    currentSession.organizerPhoneNumber = getValue(line);
                    break;

                // TODO: [optional] c=<network type> <address type> <connection address> // Session overall connection information

                // TODO: [optional] b=<modifier>:<bandwidth kilobits per second>

                // TODO: [optional] z=<adjustment time> <offset> <adjustment time> <offset> .... // Time zone adjustments

                // TODO: [optional] k=<method=clear|base64|uri|prompt>:<encryption key> // Encryption information

                // --------------------------------------
                // Time description
                // --------------------------------------

                // t=<start time> <stop time> // Time the session is active
                case 't':
                    if (currentSession == null) throw new RuntimeException("Time description received before a session has been defined: '" + line + "'");

                    currentTiming = new TimingDescription();
                    currentSession.timings.add(currentTiming);

                    tmpArr = getValueArray(line);
                    if (tmpArr.length != 2) throw new RuntimeException("Incorrect time definition found: '" + line + "'");

                    currentTiming.startTime = Long.parseLong(tmpArr[0]);
                    currentTiming.endTime = Long.parseLong(tmpArr[1]);
                    break;

                // TODO: [optional] r=<repeat interval> <active duration> <list of offsets from start-time> // Repeat information

                // --------------------------------------
                // Media description
                // --------------------------------------

                // m=<media=audio|video|application|data|control> <transport port> <transport=RTP/AVP|udp> <media format list> // Media name and transport address
                case 'm':
                    if (currentSession == null) throw new RuntimeException("Media description received before a session has been defined");

                    currentMedia = new MediaDescription();
                    currentSession.media.add(currentMedia);

                    tmpArr = getValueArray(line);
                    if (tmpArr.length < 3) throw new RuntimeException("Incorrect media definition found: '" + line + "'");

                    currentMedia.type = tmpArr[0];
                    currentMedia.transportPort = Integer.parseInt(tmpArr[1]); // TODO: Support "<port>/<number of ports>" notation
                    currentMedia.transport = tmpArr[2];
                    // TODO: Support media formats
                    break;

                // [optional] i=<session description>
                // [optional] i=<media label>
                case 'i':
                    if (currentSession == null) throw new RuntimeException("Received session title before session definition: '" + line + "'");

                    if (currentMedia != null)
                        currentMedia.label = getValue(line);
                    else
                        currentSession.sessionDescription = getValue(line);
                    break;

                // TODO: [optional] c=<network type> <address type> <connection address> // Media specific connection information

                // TODO: [optional] b=(bandwidth information)

                // TODO: [optional] k=(encryption key)

                // Zero or more session attribute lines
                // TODO: [optional] a=<session attribute>:<value>
                // TODO: [optional] a=<media attribute>:<value>
                // TODO: [optional] a=rtpmap:<attribute>:<value>

                default:
                    logger.fine("Unknown announcement type '" + line + "'");
            }
        }

        return sessions;
    }

    private String getValue(String line) {
        return line.substring(2);
    }

    private String[] getValueArray(String line) {
        return getValue(line).split(" ");
    }
}
