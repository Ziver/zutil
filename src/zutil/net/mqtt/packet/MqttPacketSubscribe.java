package zutil.net.mqtt.packet;

import zutil.parser.binary.BinaryStruct;

import java.util.List;

/**
 * The SUBSCRIBE Packet is sent from the Client to the Server to create one or more Subscriptions.
 *
 * @see <a href="http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/mqtt-v3.1.1.html">MQTT v3.1.1 Spec</a>
 */
public class MqttPacketSubscribe extends MqttPacketHeader{

    // Header

    {
        type = MqttPacketHeader.PACKET_TYPE_SUBSCRIBE;
    }

    // Variable Header

    @BinaryField(index = 2000, length = 16)
    public int packetId;

    // Payload

    public List<MqttSubscribePayload> payload;



    public static class MqttSubscribePayload implements BinaryStruct{

        @BinaryField(index = 3001, length = 16)
        private int topicFilterLength;
        /** A filter indicating the Topic to which the Client wants to subscribe to*/
        @VariableLengthBinaryField(index = 3002, lengthField = "topicFilterLength")
        public String topicFilter;

        @BinaryField(index = 3003, length = 6)
        private int reserved;
        @BinaryField(index = 3004, length = 2)
        private int qos;
    }
}
