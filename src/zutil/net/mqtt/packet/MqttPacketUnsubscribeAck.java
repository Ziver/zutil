package zutil.net.mqtt.packet;

import zutil.parser.binary.BinaryStruct;

import java.util.List;

/**
 * The UNSUBACK Packet is sent by the Server to the Client to confirm receipt
 * of an UNSUBSCRIBE Packet.
 *
 * @see <a href="http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/mqtt-v3.1.1.html">MQTT v3.1.1 Spec</a>
 */
public class MqttPacketUnsubscribeAck extends MqttPacketHeader{

    // Variable Header

    @BinaryField(index = 2000, length = 16)
    public int packetId;

    // No payload
}
