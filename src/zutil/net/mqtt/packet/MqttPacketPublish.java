package zutil.net.mqtt.packet;


import zutil.ByteUtil;

/**
 * A PUBLISH Control Packet is sent from a Client to a Server
 *
 * @see <a href="http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/mqtt-v3.1.1.html">MQTT v3.1.1 Spec</a>
 */
public class MqttPacketPublish extends MqttPacketHeader {

    // Header

    {
        type = MqttPacketHeader.PACKET_TYPE_PUBLISH;
    }


    private byte flagDupBitmask = ByteUtil.getBitMask(3, 1);
    private byte flagQoSBitmask = ByteUtil.getBitMask(1, 2);
    private byte flagRetainBitmask = ByteUtil.getBitMask(0, 1);

    // Variable Header

    @BinaryField(index = 2001, length = 16)
    private int topicNameLength;
    /**
     * The Topic Name identifies the information channel to which controlHeader data is published.
     */
    @VariableLengthBinaryField(index = 2102, lengthField = "topicNameLength")
    public String topicName;

    @BinaryField(index = 2002, length = 16)
    public int packetId;


    // Payload
    // - Application data

    @BinaryField(index = 3001, length = 100000)
    public byte[] payload;


    // Util methods

    public boolean getFlagDup() {
        return (flags & flagDupBitmask) != 0;
    }

    public byte getFlagQoS() {
        return (byte) ((flags & flagQoSBitmask) >> 1);
    }

    public boolean getFlagRetain() {
        return (flags & flagRetainBitmask) != 0;
    }
}
