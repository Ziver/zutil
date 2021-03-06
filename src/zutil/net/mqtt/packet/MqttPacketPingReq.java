package zutil.net.mqtt.packet;

/**
 * The PINGREQ Packet is used in Keep Alive processing
 *
 * @see <a href="http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/mqtt-v3.1.1.html">MQTT v3.1.1 Spec</a>
 */
public class MqttPacketPingReq extends MqttPacketHeader{

    // Header

    {
        type = MqttPacketHeader.PACKET_TYPE_PINGREQ;
    }

    // No variable header

    // No payload

}
