package zutil.net.mqtt.packet;

import org.junit.Test;
import zutil.converter.Converter;
import zutil.net.mqtt.packet.MqttPacketSubscribeAck.MqttSubscribeAckPayload;
import zutil.parser.binary.BinaryStructInputStream;
import zutil.parser.binary.BinaryStructOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;


public class MqttPacketSubscribeAckTest {

    @Test
    public void decode() throws IOException {
        char[] data = new char[]{
                // Fixed Header
                0b1001_0000, // Packet Type(4) + Reserved(4)
                0xFF & 4,   // Variable Header + Payload Length
                // Variable Header
                0b0000_0000, // Packet Identifier
                0xFF & 8,    // Packet Identifier
                // Payload
                // -- Item 1
                0b0000_0001, // Return code
                // -- Item 2
                0b0000_0010, // Return code
        };

        MqttPacketSubscribeAck obj = (MqttPacketSubscribeAck) MqttPacket.read(
                new BinaryStructInputStream(new ByteArrayInputStream(Converter.toBytes(data))));

        assertEquals(4, obj.variableHeaderAndPayloadLength);
        assertEquals(8, obj.packetId);
        assertEquals(2, obj.payloads.size());

        assertEquals(MqttSubscribeAckPayload.RETCODE_SUCESS_MAX_QOS_1, obj.payloads.get(0).returnCode);

        assertEquals(MqttSubscribeAckPayload.RETCODE_SUCESS_MAX_QOS_2, obj.payloads.get(1).returnCode);
    }

    @Test
    public void encode() throws IOException {
        char[] data = new char[]{
                // Fixed Header
                0b1001_0000, // Packet Type(4) + Reserved(4)
                0xFF & 4,   // Variable Header + Payload Length
                // Variable Header
                0b0000_0000, // Packet Identifier
                0xFF & 8,    // Packet Identifier
                // Payload
                // -- Item 1
                0b0000_0001, // Return code
                // -- Item 2
                0b0000_0010, // Return code
        };

        MqttPacketSubscribeAck obj = new MqttPacketSubscribeAck();
        obj.variableHeaderAndPayloadLength = 4;
        obj.packetId = 8;

        MqttSubscribeAckPayload p1 = new MqttSubscribeAckPayload();
        p1.returnCode = MqttSubscribeAckPayload.RETCODE_SUCESS_MAX_QOS_1;
        obj.payloads.add(p1);

        MqttSubscribeAckPayload p2 = new MqttSubscribeAckPayload();
        p2.returnCode = MqttSubscribeAckPayload.RETCODE_SUCESS_MAX_QOS_2;
        obj.payloads.add(p2);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        BinaryStructOutputStream binOut = new BinaryStructOutputStream(buffer);
        MqttPacket.write(binOut, obj);
        assertArrayEquals(Converter.toBytes(data), buffer.toByteArray());
    }

}