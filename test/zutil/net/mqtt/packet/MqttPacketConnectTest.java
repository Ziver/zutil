package zutil.net.mqtt.packet;

import org.junit.Test;
import zutil.converter.Converter;
import zutil.net.mqtt.packet.MqttPacketConnect;
import zutil.parser.binary.BinaryStructInputStream;
import zutil.parser.binary.BinaryStructOutputStream;

import java.io.IOException;

import static org.junit.Assert.*;


public class MqttPacketConnectTest {

    char[] data = new char[]{
            0b0000_0000, // length
            0b0000_0100, // length
            0b0100_1101, // 'M'
            0b0101_0001, // 'Q'
            0b0101_0100, // 'T'
            0b0101_0100, // 'T'

            0b0000_0100, // Prot. Level

            0b1100_1110, // Flags

            0b0000_0000, // Keep alive
            0b0000_1010, // Keep alive
    };


    @Test
    public void decode(){
        MqttPacketConnect obj = new MqttPacketConnect();
        BinaryStructInputStream.read(obj, Converter.toBytes(data));

        assertEquals("MQTT", obj.protocolName);
        assertEquals(4, obj.protocolLevel);
        assertEquals(10, obj.keepAlive);

        assertTrue(obj.flagUsername);
        assertTrue(obj.flagPassword);
        assertFalse(obj.flagWillRetain);
        assertEquals(1, obj.flagWillQoS);
        assertTrue(obj.flagWillFlag);
        assertTrue(obj.flagCleanSession);
    }

    @Test
    public void encode() throws IOException {
        MqttPacketConnect obj = new MqttPacketConnect();
        obj.keepAlive = 10;

        obj.flagUsername = true;
        obj.flagPassword = true;
        obj.flagWillRetain = false;
        obj.flagWillQoS = 1;
        obj.flagWillFlag = true;
        obj.flagCleanSession = true;

        assertArrayEquals(Converter.toBytes(data),
                BinaryStructOutputStream.serialize(obj));
    }
}