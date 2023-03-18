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

import zutil.parser.binary.*;
import zutil.parser.binary.serializer.BinaryStructListSerializer;
import zutil.parser.binary.serializer.TwoByteLengthPrefixedDataSerializer;

import java.util.ArrayList;
import java.util.List;

/**
 * The SUBSCRIBE Packet is sent from the Client to the Server to create one or more Subscriptions.
 *
 * @see <a href="http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/mqtt-v3.1.1.html">MQTT v3.1.1 Spec</a>
 */
public class MqttPacketSubscribe extends MqttPacketHeader {

    // Header

    {
        type = MqttPacketHeader.PACKET_TYPE_SUBSCRIBE;
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

    @CustomBinaryField(index = 3000, serializer = MqttSubscribePayloadSerializer.class)
    public List<MqttSubscribePayload> payloads = new ArrayList<>();


    @Override
    public int calculatePayloadLength() {
        int length = 0;
        for (MqttSubscribePayload p : payloads) {
            length += p.calculatePayloadLength();
        }
        return length;
    }

    public static class MqttSubscribePayload implements BinaryStruct {
        //@BinaryField(index = 3001, length = 16)
        //private int topicFilterLength;
        /** A filter indicating the Topic to which the Client wants to subscribe to **/
        @CustomBinaryField(index = 3002, serializer = TwoByteLengthPrefixedDataSerializer.class)
        public String topicFilter;

        @BinaryField(index = 3003, length = 6)
        private int reserved;
        /** the maximum QoS level at which the Server can send Application Messages to the Client **/
        @BinaryField(index = 3004, length = 2)
        public int qos;


        protected int calculatePayloadLength() {
            return 2 + (topicFilter != null ? topicFilter.length() : 0) + 1;
        }
    }


    private static class MqttSubscribePayloadSerializer extends BinaryStructListSerializer<MqttSubscribePayload> {

        protected MqttSubscribePayloadSerializer() {
            super(MqttSubscribePayload.class);
        }

        @Override
        protected boolean readNext(int objIndex, int bytesRead, BinaryFieldData field, Object parentObject) {
            MqttPacketSubscribe packetSubscribe = ((MqttPacketSubscribe) parentObject);
            return bytesRead < packetSubscribe.variableHeaderAndPayloadLength - packetSubscribe.calculateVariableHeaderLength();
        }
    }
}
