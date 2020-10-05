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

    protected List<TimingDescription> timing = new ArrayList<>();

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

    public List<TimingDescription> getTiming() {
        return timing;
    }

    public List<MediaDescription> getMedia() {
        return media;
    }
}



