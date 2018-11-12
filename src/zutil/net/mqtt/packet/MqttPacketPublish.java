package zutil.net.mqtt.packet;


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

/*
    @BinaryField(index = 2000, length = 1)
    private int flagDup;
    @BinaryField(index = 2001, length = 2)
    private int flagQoS;
    @BinaryField(index = 2002, length = 1)
    private int flagRetain;
*/
    // Variable Header

    @BinaryField(index = 2101, length = 16)
    private int topicNameLength;
    /**
     * The Topic Name identifies the information channel to which controlHeader data is published.
     */
    @VariableLengthBinaryField(index = 2102, lengthField = "topicNameLength")
    public String topicName;

    @BinaryField(index = 2102, length = 16)
    public int packetId;


    // Payload
    // - Application data

}
