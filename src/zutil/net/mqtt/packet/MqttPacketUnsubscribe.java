package zutil.net.mqtt.packet;

import zutil.parser.binary.BinaryStruct;

import java.util.List;

/**
 * An UNSUBSCRIBE Packet is sent by the Client to the Server, to unsubscribe from topics.
 *
 * @see <a href="http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/mqtt-v3.1.1.html">MQTT v3.1.1 Spec</a>
 */
public class MqttPacketUnsubscribe extends MqttPacketHeader{

    // Header

    {
        type = MqttPacketHeader.PACKET_TYPE_UNSUBACK;
    }

    // Variable Header

    @BinaryField(index = 2000, length = 16)
    public int packetId;

    // Payload

    public List<MqttUnsubscribePayload> payload;



    public static class MqttUnsubscribePayload implements BinaryStruct{

        @BinaryField(index = 3001, length = 16)
        private int topicFilterLength;
        /** A filter indicating the Topic to which the Client wants to subscribe to*/
        @VariableLengthBinaryField(index = 3002, lengthField = "topicFilterLength")
        public String topicFilter;

    }
}
