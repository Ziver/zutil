package zutil.net.mqtt.packet;

import zutil.parser.binary.BinaryStruct;

/**
 * Publish received.
 * A PUBREC Packet is the response to a PUBLISH Packet with QoS 2. It is the
 * second packet of the QoS 2 protocol exchange.
 *
 * @see <a href="http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/mqtt-v3.1.1.html">MQTT v3.1.1 Spec</a>
 */
public class MqttPacketPublishRec extends MqttPacketHeader{

    // Variable Header

    @BinaryField(index = 2000, length = 16)
    public int packetId;

    // No payload
}
