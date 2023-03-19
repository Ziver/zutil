/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Ziver Koc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package zutil.net.mqtt.packet;

import org.junit.Test;
import zutil.converter.Converter;
import zutil.parser.binary.BinaryStructInputStream;
import zutil.parser.binary.BinaryStructOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;


public class MqttPacketConnectTest {

    @Test
    public void decode() throws IOException {
        char[] data = new char[]{
                // Fixed Header
                0b0001_0000, // Packet Type(4) + Reserved(4)
                0b0000_1010, // Variable Header + Payload Length
                // Variable Header
                0b0000_0000, // length
                0b0000_0100, // length
                0b0100_1101, // 'M'
                0b0101_0001, // 'Q'
                0b0101_0100, // 'T'
                0b0101_0100, // 'T'

                0b0000_0100, // Prot. Level

                0b0001_1010, // Flags

                0b0000_0000, // Keep alive
                0b0000_1010, // Keep alive
                // Payload
                0x00, 0x01, '1', // Client Identifier
        };

        MqttPacketConnect obj = (MqttPacketConnect) MqttPacket.read(
                new BinaryStructInputStream(new ByteArrayInputStream(Converter.toBytes(data))));

        assertEquals("MQTT", obj.protocolName);
        assertEquals(4, obj.protocolLevel);
        assertEquals(10, obj.keepAlive);

        assertFalse(obj.flagUsername);
        assertFalse(obj.flagPassword);
        assertFalse(obj.flagWillRetain);
        assertEquals(3, obj.flagWillQoS);
        assertFalse(obj.flagWillFlag);
        assertTrue(obj.flagCleanSession);

        assertEquals("1", obj.clientIdentifier);
        assertNull(obj.willTopic);
        assertNull(null, obj.willPayload);
        assertNull(obj.username);
        assertNull(obj.password);
    }

    @Test
    public void decodePayload() throws IOException {
        char[] data = new char[]{
                // Fixed Header
                0b0001_0000, // Packet Type + Reserved
                0xFF & 25,   // Variable Header + Payload Length
                // Variable Header
                0b0000_0000, // length
                0b0000_0100, // length
                0b0100_1101, // 'M'
                0b0101_0001, // 'Q'
                0b0101_0100, // 'T'
                0b0101_0100, // 'T'

                0b0000_0100, // Prot. Level

                0b1100_1100, // Flags

                0b0000_0000, // Keep alive
                0b0000_1010, // Keep alive
                // Payload
                0x00, 0x01, '1', // Client Identifier
                0x00, 0x01, '2', // Will Topic
                0x00, 0x01, 0x03, // Will payload: 3
                0x00, 0x01, '4', // Username
                0x00, 0x01, '5', // password
        };

        MqttPacketConnect obj = (MqttPacketConnect) MqttPacket.read(
                new BinaryStructInputStream(new ByteArrayInputStream(Converter.toBytes(data))));

        assertEquals("MQTT", obj.protocolName);
        assertEquals(4, obj.protocolLevel);
        assertEquals(10, obj.keepAlive);

        assertTrue(obj.flagUsername);
        assertTrue(obj.flagPassword);
        assertFalse(obj.flagWillRetain);
        assertEquals(1, obj.flagWillQoS);
        assertTrue(obj.flagWillFlag);
        assertFalse(obj.flagCleanSession);

        assertEquals("1", obj.clientIdentifier);
        assertEquals("2", obj.willTopic);
        assertArrayEquals(new byte[]{3}, obj.willPayload);
        assertEquals("4", obj.username);
        assertEquals("5", obj.password);
    }

    @Test
    public void encode() throws IOException {
        char[] data = new char[]{
                // Fixed Header
                0b0001_0000, // Packet Type + Reserved
                0xFF & 13,   // Variable Header + Payload Length
                // Variable Header
                0b0000_0000, // length
                0b0000_0100, // length
                0b0100_1101, // 'M'
                0b0101_0001, // 'Q'
                0b0101_0100, // 'T'
                0b0101_0100, // 'T'

                0b0000_0100, // Prot. Level

                0b0000_1010, // Flags

                0b0000_0000, // Keep alive
                0b0000_1010, // Keep alive
                // Payload
                0x00, 0x01, '1', // Client Identifier
        };

        MqttPacketConnect obj = new MqttPacketConnect();
        obj.keepAlive = 10;

        obj.flagUsername = false;
        obj.flagPassword = false;
        obj.flagWillRetain = false;
        obj.flagWillQoS = 1;
        obj.flagWillFlag = false;
        obj.flagCleanSession = true;

        obj.clientIdentifier = "1";

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        BinaryStructOutputStream binOut = new BinaryStructOutputStream(buffer);
        MqttPacket.write(binOut, obj);
        assertArrayEquals(Converter.toBytes(data), buffer.toByteArray());
    }

    @Test
    public void encodePayload() throws IOException {
        char[] data = new char[]{
                // Fixed Header
                0b0001_0000, // Packet Type(4) + Reserved(4)
                0xFF & 25,   // Variable Header + Payload Length
                // Variable Header
                0b0000_0000, // length
                0b0000_0100, // length
                0b0100_1101, // 'M'
                0b0101_0001, // 'Q'
                0b0101_0100, // 'T'
                0b0101_0100, // 'T'

                0b0000_0100, // Prot. Level

                0b1101_0100, // Flags

                0b0000_0000, // Keep alive
                0b0000_1010, // Keep alive
                // Payload
                0x00, 0x01, '1', // Client Identifier: 1
                0x00, 0x01, '2', // Will Topic: 2
                0x00, 0x01, 0x03, // Will payload: 3
                0x00, 0x01, '4', // Username: 4
                0x00, 0x01, '5', // password: 5
        };

        MqttPacketConnect obj = new MqttPacketConnect();
        obj.keepAlive = 10;

        obj.flagUsername = true;
        obj.flagPassword = true;
        obj.flagWillRetain = false;
        obj.flagWillQoS = 2;
        obj.flagWillFlag = true;
        obj.flagCleanSession = false;

        obj.clientIdentifier = "1";
        obj.willTopic = "2";
        obj.willPayload = new byte[]{3};
        obj.username = "4";
        obj.password = "5";

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        BinaryStructOutputStream binOut = new BinaryStructOutputStream(buffer);
        MqttPacket.write(binOut, obj);
        assertArrayEquals(Converter.toBytes(data), buffer.toByteArray());
    }
}