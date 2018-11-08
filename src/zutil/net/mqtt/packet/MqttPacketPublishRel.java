package zutil.net.mqtt.packet;


/**
 * Publish release.
 * A PUBREL Packet is the response to a PUBREC Packet. It is the third packet
 * of the QoS 2 protocol exchange.
 *
 * @see <a href="http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/mqtt-v3.1.1.html">MQTT v3.1.1 Spec</a>
 */
public class MqttPacketPublishRel extends MqttPacketHeader{

    // Header

    {
        type = MqttPacketHeader.PACKET_TYPE_PUBREL;
    }

    // Variable Header

    @BinaryField(index = 2000, length = 16)
    public int packetId;

    // No Payload

}
