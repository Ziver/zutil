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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A data class containing information about a Media session
 */
public class SessionDescription {
    protected int protocolVersion;

    protected String sessionOwner;
    protected long sessionId;
    protected long sessionAnnouncementVersion;
    protected String ownerNetworkType = "IN";
    protected String ownerAddressType = "IP4"; // IP4 or IP6
    protected String ownerAddress;

    protected String sessionTitle;
    protected String sessionDescription;
    protected String sessionURI;

    protected String organizerEmail;
    protected String organizerPhoneNumber;

    protected List<TimingDescription> timings = new ArrayList<>();

    protected List<MediaDescription> media = new ArrayList<>();

    protected Map<String,String> attributes = new HashMap<>();


    public int getProtocolVersion() {
        return protocolVersion;
    }

    public String getSessionOwner() {
        return sessionOwner;
    }

    public long getSessionId() {
        return sessionId;
    }

    public long getAnnouncementVersion() {
        return sessionAnnouncementVersion;
    }

    public String getOwnerNetworkType() {
        return ownerNetworkType;
    }

    public String getOwnerAddressType() {
        return ownerAddressType;
    }

    public String getOwnerAddress() {
        return ownerAddress;
    }

    public String getSessionTitle() {
        return sessionTitle;
    }

    public String getSessionDescription() {
        return sessionDescription;
    }

    public String getSessionURI() {
        return sessionURI;
    }

    public String getOrganizerEmail() {
        return organizerEmail;
    }

    public String getOrganizerPhoneNumber() {
        return organizerPhoneNumber;
    }

    public List<TimingDescription> getTimings() {
        return timings;
    }

    public List<MediaDescription> getMedia() {
        return media;
    }


    public String toString() {
        StringBuffer output = new StringBuffer();

        output.append("v=").append(protocolVersion).append('\n');
        output.append("o=").append(sessionOwner).append(' ').append(sessionId).append(' ').append(sessionAnnouncementVersion)
                .append(' ').append(ownerNetworkType).append(' ').append(ownerAddressType).append(' ').append(ownerAddress).append('\n');
        output.append("s=").append(sessionTitle).append('\n');

        if (sessionDescription != null)   output.append("i=").append(sessionDescription).append('\n');
        if (sessionURI != null)           output.append("u=").append(sessionURI).append('\n');
        if (organizerEmail != null)       output.append("e=").append(organizerEmail).append('\n');
        if (organizerPhoneNumber != null) output.append("p=").append(organizerPhoneNumber).append('\n');
        // TODO: [optional] c=<network type> <address type> <connection address> // Session overall connection information

        // Time description
        for (TimingDescription t : timings) output.append(t.toString()).append('\n');

        // TODO: [optional] b=<modifier>:<bandwidth kilobits per second>
        // TODO: [optional] z=<adjustment time> <offset> <adjustment time> <offset> .... // Time zone adjustments
        // TODO: [optional] k=<method=clear|base64|uri|prompt>:<encryption key> // Encryption information
        // TODO: [optional] a=<session attribute>:<value>

        // Media description
        for (MediaDescription m : media) output.append(m.toString()).append('\n');

        return output.toString().trim();
    }
}



