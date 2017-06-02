package zutil.net.mqtt.packet;

import zutil.parser.binary.BinaryStruct;

/**
 * The CONNACK Packet is the packet sent by the Server in response to a
 * CONNECT Packet received from a Client.
 *
 * @see <a href="http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/mqtt-v3.1.1.html">MQTT v3.1.1 Spec</a>
 */
public class MqttPacketConnectAck extends MqttPacketHeader{
    public static final int RETCODE_OK                 = 0;
    public static final int RETCODE_PROT_VER_ERROR     = 1;
    public static final int RETCODE_IDENTIFIER_REJECT  = 2;
    public static final int RETCODE_SERVER_UNAVAILABLE = 3;
    public static final int RETCODE_BADD_USER_OR_PASS  = 4;
    public static final int RETCODE_NOT_AUTHORIZED     = 5;


    // Variable header

    @BinaryField(index = 2000, length = 7)
    private int flagReserved;
    /** Indicates that there is a valid Session available */
    @BinaryField(index = 2001, length = 1)
    public boolean flagSessionPresent;

    @BinaryField(index = 2002, length = 8)
    public int returnCode;

    // No payload
}
