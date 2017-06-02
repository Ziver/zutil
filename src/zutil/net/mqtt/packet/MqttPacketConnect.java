package zutil.net.mqtt.packet;

import zutil.parser.binary.BinaryStruct;

/**
 * This packet is the first message sent from a Client when it
 * has established a connection to a Server. A Client can only
 * send the CONNECT Packet once over a Network Connection.
 *
 * @see <a href="http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/mqtt-v3.1.1.html">MQTT v3.1.1 Spec</a>
 */
public class MqttPacketConnect extends MqttPacketHeader {

    // Variable header

    @BinaryField(index = 2001, length = 16)
    private int protocolNameLength = 4;
    /**
     * The Protocol Name is a UTF-8 encoded string that represents
     * the protocol name “MQTT”, capitalized as shown. The string,
     * its offset and length will not be changed by future versions
     * of the MQTT specification.
     */
    @BinaryField(index = 2002, length = 8*4)
    public String protocolName = "MQTT";

    /**
     * The value represents the revision level of the protocol used by
     * the Client. The value of the Protocol Level field for the version
     * 3.1.1 of the protocol is 4 (0x04)
     */
    @BinaryField(index = 2003, length = 8)
    public int protocolLevel = 0x04;


    /** Indicates that the controlHeader contains a username */
    @BinaryField(index = 2010, length = 1)
    public boolean flagUsername;
    /** Indicates that the controlHeader contains a password */
    @BinaryField(index = 2011, length = 1)
    public boolean flagPassword;
    /** Specifies if the Will Message is to be Retained when it is published. */
    @BinaryField(index = 2012, length = 1)
    public boolean flagWillRetain;
    /** Specifies the QoS level to be used when publishing the Will Message. */
    @BinaryField(index = 2013, length = 2)
    public int flagWillQoS;
    @BinaryField(index = 2014, length = 1)
    public boolean flagWillFlag;
    @BinaryField(index = 2015, length = 1)
    /** This bit specifies the handling of the Session state. */
    public boolean flagCleanSession;
    @BinaryField(index = 2016, length = 1)
    private boolean reserved;


    /**
     * The Keep Alive is a time interval measured in seconds and
     * it is the maximum time interval that is permitted to elapse
     * between the point at which the Client finishes transmitting one
     * Control Packet and the point it starts sending the next.
     * A Keep Alive value of zero (0) has the effect of turning off
     * the keep alive mechanism.
     */
    @BinaryField(index = 2020, length = 16)
    public int keepAlive;


    // Payload:
    // - Client identifier
    // - Will Topic
    // - Will message
    // - User name
    // - Password

}
