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
    protected String sessionId;
    protected String sessionVersion;
    protected String networkType;
    protected String addressType;
    protected String address;

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

    public String getSessionId() {
        return sessionId;
    }

    public String getSessionVersion() {
        return sessionVersion;
    }

    public String getNetworkType() {
        return networkType;
    }

    public String getAddressType() {
        return addressType;
    }

    public String getAddress() {
        return address;
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
        // TODO: o=<owner username> <session id> <session version> <network type> <address type> <address>
        output.append("s=").append(sessionTitle).append('\n');;

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



