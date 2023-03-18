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

import zutil.parser.binary.BinaryFieldData;
import zutil.parser.binary.BinaryStruct;
import zutil.parser.binary.serializer.BinaryStructListSerializer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * A SUBACK Packet is sent by the Server to the Client to confirm receipt
 * and processing of a SUBSCRIBE Packet.
 *
 * @see <a href="http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/mqtt-v3.1.1.html">MQTT v3.1.1 Spec</a>
 */
public class MqttPacketSubscribeAck extends MqttPacketHeader {

    // Header

    {
        type = MqttPacketHeader.PACKET_TYPE_SUBACK;
    }

    // ------------------------------------------
    // Variable Header
    // ------------------------------------------

    @BinaryField(index = 2000, length = 16)
    public int packetId;


    @Override
    public int calculateVariableHeaderLength() {
        return 2;
    }

    // ------------------------------------------
    // Payload
    // ------------------------------------------

    @CustomBinaryField(index = 3000, serializer = MqttSubscribeAckPayloadSerializer.class)
    public List<MqttSubscribeAckPayload> payloads = new LinkedList<>();


    @Override
    public int calculatePayloadLength() {
        int length = 0;
        for (MqttSubscribeAckPayload p : payloads) {
            length += p.calculatePayloadLength();
        }
        return length;
    }

    public static class MqttSubscribeAckPayload implements BinaryStruct{
        public static final int RETCODE_SUCESS_MAX_QOS_0 = 0;
        public static final int RETCODE_SUCESS_MAX_QOS_1 = 1;
        public static final int RETCODE_SUCESS_MAX_QOS_2 = 2;
        public static final int RETCODE_FAILURE          = 0x80;

        @BinaryField(index = 3001, length = 8)
        public int returnCode;


        protected int calculatePayloadLength() {
            return 1;
        }
    }


    private static class MqttSubscribeAckPayloadSerializer extends BinaryStructListSerializer<MqttSubscribeAckPayload> {

        protected MqttSubscribeAckPayloadSerializer() {
            super(MqttSubscribeAckPayload.class);
        }

        @Override
        protected boolean readNext(int objIndex, int bytesRead, BinaryFieldData field, Object parentObject) {
            MqttPacketSubscribeAck packetSubscribe = ((MqttPacketSubscribeAck) parentObject);
            return bytesRead < packetSubscribe.variableHeaderAndPayloadLength - packetSubscribe.calculateVariableHeaderLength();
        }
    }
}
