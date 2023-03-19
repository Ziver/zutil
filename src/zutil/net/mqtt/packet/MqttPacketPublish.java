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


import zutil.ByteUtil;
import zutil.converter.Converter;
import zutil.parser.binary.BinaryFieldData;
import zutil.parser.binary.BinaryFieldSerializer;
import zutil.parser.binary.serializer.TwoByteLengthPrefixedDataSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A PUBLISH Control Packet is sent from a Client to a Server
 *
 * @see <a href="http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/mqtt-v3.1.1.html">MQTT v3.1.1 Spec</a>
 */
public class MqttPacketPublish extends MqttPacketHeader {

    // Header

    {
        type = MqttPacketHeader.PACKET_TYPE_PUBLISH;
    }


    private static final byte FLAG_DUP_BITMASK = ByteUtil.getBitMask(3, 1);
    private static final byte FLAG_QOS_BITMASK = ByteUtil.getBitMask(1, 2);
    private static final byte FLAG_RETAIN_BITMASK = ByteUtil.getBitMask(0, 1);

    // ------------------------------------------
    // Variable Header
    // ------------------------------------------

    //@BinaryField(index = 2001, length = 16)
    //private int topicNameLength;
    /**
     * The Topic Name identifies the information channel to which controlHeader data is published.
     */
    @CustomBinaryField(index = 2002, serializer = TwoByteLengthPrefixedDataSerializer.class)
    public String topicName;

    /**
     * A unique identity of this packet. Only available if QOS is above 0.
     */
    @CustomBinaryField(index = 2003, serializer = MqttPacketPublishPacketIdSerializer.class)
    public int packetId;


    @Override
    public int calculateVariableHeaderLength() {
        return 2 + (topicName != null ? topicName.length() : 0) + (getFlagQoS() > 0 ? 2 : 0);
    }

    // ------------------------------------------
    // Payload
    // ------------------------------------------
    // - Application data

    @CustomBinaryField(index = 3000, serializer = MqttPacketPublishPayloadSerializer.class)
    public byte[] payload;


    @Override
    public int calculatePayloadLength() {
        return payload == null ? 0 : payload.length;
    }

    // ------------------------------------------
    // Util methods
    // ------------------------------------------

    public boolean getFlagDup() {
        return (flags & FLAG_DUP_BITMASK) != 0;
    }

    public byte getFlagQoS() {
        return (byte) ((flags & FLAG_QOS_BITMASK) >> 1);
    }

    public boolean getFlagRetain() {
        return (flags & FLAG_RETAIN_BITMASK) != 0;
    }


    private static class MqttPacketPublishPayloadSerializer implements BinaryFieldSerializer<byte[]> {

        @Override
        public byte[] read(InputStream in, BinaryFieldData field) throws IOException {
            return new byte[0];
        }

        @Override
        public byte[] read(InputStream in, BinaryFieldData field, Object parentObject) throws IOException {
            MqttPacketPublish publishPacket = (MqttPacketPublish) parentObject;
            int variableLength = publishPacket.calculateVariableHeaderLength();
            int payloadLength = Math.max(0, publishPacket.variableHeaderAndPayloadLength - variableLength);

            byte[] payload = new byte[payloadLength];
            in.read(payload);
            return payload;
        }

        @Override
        public void write(OutputStream out, byte[] obj, BinaryFieldData field) throws IOException {
            if (obj != null)
                out.write(obj);
        }
    }

    /**
     * Only read and write Packet Identifier if QOS is above 0
     */
    private static class MqttPacketPublishPacketIdSerializer implements BinaryFieldSerializer {
        @Override
        public Object read(InputStream in, BinaryFieldData field, Object parentObject) throws IOException {
            MqttPacketPublish publish = (MqttPacketPublish) parentObject;

            if (0 < publish.getFlagQoS()) {
                byte[] b = new byte[2];
                in.read(b);
                return Converter.toInt(b);
            }

            return 0;
        }
        @Override
        public Object read(InputStream in, BinaryFieldData field) throws IOException {return null;}

        @Override
        public void write(OutputStream out, Object obj, BinaryFieldData field, Object parentObject) throws IOException {
            MqttPacketPublish publish = (MqttPacketPublish) parentObject;

            if (0 < publish.getFlagQoS()) {
                byte[] b = Converter.toBytes((int) obj);
                out.write(b[1]);
                out.write(b[0]);
            }
        }
        @Override
        public void write(OutputStream out, Object obj, BinaryFieldData field) throws IOException {}
    }
}
