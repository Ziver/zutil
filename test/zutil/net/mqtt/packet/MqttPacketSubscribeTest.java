package zutil.net.mqtt.packet;

import org.junit.Test;
import zutil.converter.Converter;
import zutil.net.mqtt.packet.MqttPacketSubscribe.MqttSubscribePayload;
import zutil.parser.binary.BinaryStructInputStream;
import zutil.parser.binary.BinaryStructOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class MqttPacketSubscribeTest {

    @Test
    public void decode() throws IOException {
        char[] data = new char[]{
                // Fixed Header
                0b1000_0000, // Packet Type(4) + Reserved(4)
                0xFF & 12,    // Variable Header + Payload Length
                // Variable Header
                0b0000_0000, // Packet Identifier
                0xFF & 8,    // Packet Identifier
                // Payload
                // -- Item 1
                0b0000_0000, // length
                0xFF & 2,    // length
                'a',         // Topic Name
                'b',         // Topic Name
                0b000000_00, // Reserved(6) + QoS(2)
                // -- Item 2
                0b0000_0000, // length
                0xFF & 2,    // length
                'c',         // Topic Name
                'd',         // Topic Name
                0b000000_01, // Reserved(6) + QoS(2)
        };

        MqttPacketSubscribe obj = (MqttPacketSubscribe) MqttPacket.read(
                new BinaryStructInputStream(new ByteArrayInputStream(Converter.toBytes(data))));

        assertEquals(12, obj.variableHeaderAndPayloadLength);
        assertEquals(8, obj.packetId);
        assertEquals(2, obj.payloads.size());

        assertEquals("ab", obj.payloads.get(0).topicFilter);
        assertEquals(0,    obj.payloads.get(0).qos);

        assertEquals("cd", obj.payloads.get(1).topicFilter);
        assertEquals(1,    obj.payloads.get(1).qos);
    }

    @Test
    public void encode() throws IOException {
        char[] data = new char[]{
                // Fixed Header
                0b1000_0000, // Packet Type(4) + Reserved(4)
                0xFF & 12,    // Variable Header + Payload Length
                // Variable Header
                0b0000_0000, // Packet Identifier
                0xFF & 8,    // Packet Identifier
                // Payload
                // -- Item 1
                0b0000_0000, // length
                0xFF & 2,    // length
                'a',         // Topic Name
                'b',         // Topic Name
                0b000000_00, // Reserved(6) + QoS(2)
                // -- Item 2
                0b0000_0000, // length
                0xFF & 2,    // length
                'c',         // Topic Name
                'd',         // Topic Name
                0b000000_01, // Reserved(6) + QoS(2)
        };

        MqttPacketSubscribe obj = new MqttPacketSubscribe();
        obj.variableHeaderAndPayloadLength = 12;
        obj.packetId = 8;

        MqttSubscribePayload p1 = new MqttSubscribePayload();
        p1.topicFilter = "ab";
        p1.qos = 0;
        obj.payloads.add(p1);

        MqttSubscribePayload p2 = new MqttSubscribePayload();
        p2.topicFilter = "cd";
        p2.qos = 1;
        obj.payloads.add(p2);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        BinaryStructOutputStream binOut = new BinaryStructOutputStream(buffer);
        MqttPacket.write(binOut, obj);
        assertArrayEquals(Converter.toBytes(data), buffer.toByteArray());
    }
}