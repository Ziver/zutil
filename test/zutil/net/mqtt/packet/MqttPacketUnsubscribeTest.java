package zutil.net.mqtt.packet;

import org.junit.Test;
import zutil.converter.Converter;
import zutil.net.mqtt.packet.MqttPacketUnsubscribe.MqttUnsubscribePayload;
import zutil.parser.binary.BinaryStructInputStream;
import zutil.parser.binary.BinaryStructOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;


public class MqttPacketUnsubscribeTest {

    @Test
    public void decode() throws IOException {
        char[] data = new char[]{
                // Fixed Header
                0b1010_0000, // Packet Type(4) + Reserved(4)
                0xFF & 10,    // Variable Header + Payload Length
                // Variable Header
                0b0000_0000, // Packet Identifier
                0xFF & 8,    // Packet Identifier
                // Payload
                // -- Item 1
                0b0000_0000, // length
                0xFF & 2,    // length
                'a',         // Topic Name
                'b',         // Topic Name
                // -- Item 2
                0b0000_0000, // length
                0xFF & 2,    // length
                'c',         // Topic Name
                'd',         // Topic Name
        };

        MqttPacketUnsubscribe obj = (MqttPacketUnsubscribe) MqttPacket.read(
                new BinaryStructInputStream(new ByteArrayInputStream(Converter.toBytes(data))));

        assertEquals(10, obj.variableHeaderAndPayloadLength);
        assertEquals(8, obj.packetId);
        assertEquals(2, obj.payloads.size());

        assertEquals("ab", obj.payloads.get(0).topicFilter);

        assertEquals("cd", obj.payloads.get(1).topicFilter);
    }

    @Test
    public void encode() throws IOException {
        char[] data = new char[]{
                // Fixed Header
                0b1010_0000, // Packet Type(4) + Reserved(4)
                0xFF & 10,    // Variable Header + Payload Length
                // Variable Header
                0b0000_0000, // Packet Identifier
                0xFF & 8,    // Packet Identifier
                // Payload
                // -- Item 1
                0b0000_0000, // length
                0xFF & 2,    // length
                'a',         // Topic Name
                'b',         // Topic Name
                // -- Item 2
                0b0000_0000, // length
                0xFF & 2,    // length
                'c',         // Topic Name
                'd',         // Topic Name
        };

        MqttPacketUnsubscribe obj = new MqttPacketUnsubscribe();
        obj.variableHeaderAndPayloadLength = 10;
        obj.packetId = 8;

        MqttUnsubscribePayload p1 = new MqttUnsubscribePayload();
        p1.topicFilter = "ab";
        obj.payloads.add(p1);

        MqttUnsubscribePayload p2 = new MqttUnsubscribePayload();
        p2.topicFilter = "cd";
        obj.payloads.add(p2);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        BinaryStructOutputStream binOut = new BinaryStructOutputStream(buffer);
        MqttPacket.write(binOut, obj);
        assertArrayEquals(Converter.toBytes(data), buffer.toByteArray());
    }
}