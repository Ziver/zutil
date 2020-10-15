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

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SDPParserTest {

/*
"v=0\n" +
"o=mhandley 2890844526 2890842807 IN IP4 126.16.64.4\n" +
"s=SDP Seminar\n" +
"i=A Seminar on the session description protocol\n" +
"u=http://www.cs.ucl.ac.uk/staff/M.Handley/sdp.03.ps\n" +
"e=mjh@isi.edu (Mark Handley)\n" +
"c=IN IP4 224.2.17.12/127" +
"t=2873397496 2873404696\n" +
"a=recvonly\n" +
"m=audio 49170 RTP/AVP 0\n" +
"m=video 51372 RTP/AVP 31\n" +
"m=application 32416 udp wb\n" +
"a=orient:portrait";
*/

    @Test
    public void basicSessionInfo() throws IOException {
        String description =
                "v=0\n" +
                "o=mhandley 2890844526 2890842807 IN IP4 126.16.64.4\n" +
                "s=SDP Seminar\n" +
                "i=A Seminar on the session description protocol\n" +
                "u=http://www.cs.ucl.ac.uk/staff/M.Handley/sdp.03.ps\n" +
                "e=mjh@isi.edu (Mark Handley)\n" +
                "c=IN IP4 224.2.17.12/127";

        SDPParser parser = new SDPParser(description);
        List<SessionDescription> sessions = parser.parse();

        assertEquals(1, sessions.size());

        SessionDescription session = sessions.get(0);
        // v=
        assertEquals(0, session.getProtocolVersion());
        // o=
        assertEquals("mhandley", session.getSessionOwner());
        assertEquals(2890844526l, session.getSessionId());
        assertEquals(2890842807l, session.getAnnouncementVersion());
        assertEquals("IN", session.getOwnerNetworkType());
        assertEquals("IP4", session.getOwnerAddressType());
        assertEquals("126.16.64.4", session.getOwnerAddress());
        // s=
        assertEquals("SDP Seminar", session.getSessionTitle());
        // i=
        assertEquals("A Seminar on the session description protocol", session.getSessionDescription());
        // u=
        assertEquals("http://www.cs.ucl.ac.uk/staff/M.Handley/sdp.03.ps", session.getSessionURI());
        // e=
        assertEquals("mjh@isi.edu (Mark Handley)", session.getOrganizerEmail());
    }

    @Test
    public void basicTimingInfo() throws IOException {
        String description = "v=0\n" +
                "s=SDP Seminar\n" +
                "t=2873397496 2873404696";

        SDPParser parser = new SDPParser(description);
        List<SessionDescription> sessions = parser.parse();

        assertEquals(1, sessions.get(0).getTimings().size());
        TimingDescription timing = sessions.get(0).getTimings().get(0);

        assertEquals(timing.getStartTime(), 2873397496l);
        assertEquals(timing.getEndTime(), 2873404696l);
    }

    @Test
    public void basicMediaInfo() throws IOException {
        String description = "v=0\n" +
                "s=SDP Seminar\n" +
                "m=video 51372 RTP/AVP 31\n" +
                "i=main video feed";

        SDPParser parser = new SDPParser(description);
        List<SessionDescription> sessions = parser.parse();

        assertEquals(1, sessions.get(0).getMedia().size());
        MediaDescription media = sessions.get(0).getMedia().get(0);

        assertEquals(media.getType(), "video");
        assertEquals(media.getTransportPort(),  51372);
        assertEquals(media.getTransport(),  "RTP/AVP");
    }
}