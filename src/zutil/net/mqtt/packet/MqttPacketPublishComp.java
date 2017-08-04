package zutil.net.mqtt.packet;


/**
 * Publish complete.
 * The PUBCOMP Packet is the response to a PUBREL Packet. It is the fourth and
 * final packet of the QoS 2 protocol exchange.
 *
 * @see <a href="http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/mqtt-v3.1.1.html">MQTT v3.1.1 Spec</a>
 */
public class MqttPacketPublishComp extends MqttPacketHeader{

    // Variable Header

    @BinaryField(index = 2000, length = 16)
    public int packetId;

    // No payload

}
