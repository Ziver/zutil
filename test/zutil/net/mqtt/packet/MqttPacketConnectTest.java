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

    char[] data = new char[]{
            // Fixed Header
            0b0001_0000, // Packet Type + Reserved
            0b0000_1010, // Variable Header + Payload Length
            // Variable Header
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
    public void decode() throws IOException {
        MqttPacketConnect obj = (MqttPacketConnect)MqttPacket.read(
                new BinaryStructInputStream(
                        new ByteArrayInputStream(
                                Converter.toBytes(data))));

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
        obj.payloadLength = 10;
        obj.keepAlive = 10;

        obj.flagUsername = true;
        obj.flagPassword = true;
        obj.flagWillRetain = false;
        obj.flagWillQoS = 1;
        obj.flagWillFlag = true;
        obj.flagCleanSession = true;

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        BinaryStructOutputStream binOut = new BinaryStructOutputStream(buffer);
        MqttPacket.write(binOut, obj);
        assertArrayEquals(Converter.toBytes(data), buffer.toByteArray());
    }
}