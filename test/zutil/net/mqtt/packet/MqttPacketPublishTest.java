package zutil.net.mqtt.packet;

import org.junit.Test;
import zutil.converter.Converter;
import zutil.parser.binary.BinaryStructInputStream;
import zutil.parser.binary.BinaryStructOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class MqttPacketPublishTest {

    @Test
    public void decode() throws IOException {
        char[] data = new char[]{
                // Fixed Header
                0b0011_0000, // Packet Type(4) + Reserved(4)
                0xFF & 4, // Variable Header + Payload Length
                // Variable Header
                0b0000_0000, // length
                0xFF & 2,    // length
                'a',         // Topic Name
                'b',         // Topic Name
                // Payload
        };

        MqttPacketPublish obj = (MqttPacketPublish) MqttPacket.read(
                new BinaryStructInputStream(new ByteArrayInputStream(Converter.toBytes(data))));

        assertEquals(4, obj.variableHeaderAndPayloadLength);
        assertEquals("ab", obj.topicName);
        assertEquals(0, obj.packetId);
        assertArrayEquals(new byte[0], obj.payload);
    }

    @Test
    public void decodePayload() throws IOException {
        char[] data = new char[]{
                // Fixed Header
                0b0011_0000, // Packet Type(4) + Reserved(4)
                0xFF & 7, // Variable Header + Payload Length
                // Variable Header
                0b0000_0000, // length
                0xFF & 2,    // length
                'a',         // Topic Name
                'b',         // Topic Name
                // Payload
                0x00, 0x01, 0x02,
        };

        MqttPacketPublish obj = (MqttPacketPublish) MqttPacket.read(
                new BinaryStructInputStream(new ByteArrayInputStream(Converter.toBytes(data))));

        assertEquals(7, obj.variableHeaderAndPayloadLength);
        assertEquals("ab", obj.topicName);
        assertEquals(0, obj.packetId);
        assertArrayEquals(new byte[]{0x00, 0x01, 0x02}, obj.payload);
    }

    @Test
    public void encode() throws IOException {
        char[] data = new char[]{
                // Fixed Header
                0b0011_0000, // Packet Type(4) + Reserved(4)
                0xFF & 4, // Variable Header + Payload Length
                // Variable Header
                0b0000_0000, // length
                0xFF & 2,    // length
                'a',         // Topic Name
                'b',         // Topic Name
                // Payload
        };

        MqttPacketPublish obj = new MqttPacketPublish();
        obj.topicName = "ab";
        obj.packetId = 5;

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        BinaryStructOutputStream binOut = new BinaryStructOutputStream(buffer);
        MqttPacket.write(binOut, obj);
        assertArrayEquals(Converter.toBytes(data), buffer.toByteArray());
    }
}