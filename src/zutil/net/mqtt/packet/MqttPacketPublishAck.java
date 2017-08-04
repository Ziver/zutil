package zutil.net.mqtt.packet;


/**
 * A PUBACK Packet is the response to a PUBLISH Packet with QoS level 1.
 *
 * @see <a href="http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/mqtt-v3.1.1.html">MQTT v3.1.1 Spec</a>
 */
public class MqttPacketPublishAck extends MqttPacketHeader{

    // Variable Header

    @BinaryField(index = 2000, length = 16)
    public int packetId;

    // No payload

}
