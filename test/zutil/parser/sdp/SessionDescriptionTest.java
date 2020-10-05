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

import org.junit.Test;

import static org.junit.Assert.*;

public class SessionDescriptionTest {

    // TODO: o=<owner username> <session id> <session version> <network type> <address type> <address>
    // TODO: [optional] i=<session description>
    // TODO: [optional] u=<Session description URI>
    // TODO: [optional] e=<email address>
    // TODO: [optional] p=<phone number>
    // TODO: [optional] c=<network type> <address type> <connection address> // Session overall connection information

    // --------------------------------------
    // Time description
    // --------------------------------------

    // TODO: [optional] b=<modifier>:<bandwidth kilobits per second>
    // TODO: [optional] z=<adjustment time> <offset> <adjustment time> <offset> .... // Time zone adjustments
    // TODO: [optional] k=<method=clear|base64|uri|prompt>:<encryption key> // Encryption information
    // TODO: [optional] a=<session attribute>:<value>

    // --------------------------------------
    // Media description
    // --------------------------------------

    @Test
    public void basicSession() {
        SessionDescription session = new SessionDescription();
        session.protocolVersion = 0;
        session.sessionTitle = "SDP Seminar";

        assertEquals(session.toString(), "v=0\n" +
                "s=SDP Seminar"
        );
    }

    @Test
    public void basicTiming() {
        TimingDescription timing = new TimingDescription();
        timing.startTime = 2873397496l;
        timing.endTime = 2873404696l;

        SessionDescription session = new SessionDescription();
        session.protocolVersion = 0;
        session.sessionTitle = "SDP Seminar";
        session.timings.add(timing);

        assertEquals(session.toString(), "v=0\n" +
                        "s=SDP Seminar\n" +
                        "t=2873397496 2873404696"
        );
    }

    @Test
    public void basicMedia() {
        MediaDescription media = new MediaDescription();
        media.type = "video";
        media.transportPort = 51372;
        media.transport = "RTP/AVP";
        media.label = "main video feed";

        SessionDescription session = new SessionDescription();
        session.protocolVersion = 0;
        session.sessionTitle = "SDP Seminar";
        session.media.add(media);

        assertEquals(session.toString(), "v=0\n" +
                "s=SDP Seminar\n" +
                "m=video 51372 RTP/AVP\n" +
                "i=main video feed"
        );
    }

}