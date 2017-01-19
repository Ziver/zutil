package zutil.net.mqtt.packet;

import zutil.parser.binary.BinaryStruct;

import java.util.List;

/**
 * A SUBACK Packet is sent by the Server to the Client to confirm receipt
 * and processing of a SUBSCRIBE Packet.
 *
 * @see <a href="http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/mqtt-v3.1.1.html">MQTT v3.1.1 Spec</a>
 */
public class MqttPacketSubscribeAck implements BinaryStruct{

    // Variable Header

    @BinaryField(index = 2000, length = 16)
    public int packetId;

    // Payload

    public List<MqttSubscribeAckPayload> payload;




    public static class MqttSubscribeAckPayload implements BinaryStruct{
        public static final int RETCODE_SUCESS_MAX_QOS_0 = 0;
        public static final int RETCODE_SUCESS_MAX_QOS_1 = 1;
        public static final int RETCODE_SUCESS_MAX_QOS_2 = 2;
        public static final int RETCODE_FAILURE          = 0x80;

        @BinaryField(index = 3001, length = 8)
        public int returnCode;
    }
}
