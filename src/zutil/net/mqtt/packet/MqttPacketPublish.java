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


    private byte flagDupBitmask = ByteUtil.getBitMask(3, 1);
    private byte flagQoSBitmask = ByteUtil.getBitMask(1, 2);
    private byte flagRetainBitmask = ByteUtil.getBitMask(0, 1);

    // Variable Header

    @BinaryField(index = 2001, length = 16)
    private int topicNameLength;
    /**
     * The Topic Name identifies the information channel to which controlHeader data is published.
     */
    @VariableLengthBinaryField(index = 2102, lengthField = "topicNameLength")
    public String topicName;

    @BinaryField(index = 2002, length = 16)
    public int packetId;


    // Payload
    // - Application data

    @BinaryField(index = 3001, length = 100000)
    public byte[] payload;


    // Util methods

    public boolean getFlagDup() {
        return (flags & flagDupBitmask) != 0;
    }

    public byte getFlagQoS() {
        return (byte) ((flags & flagQoSBitmask) >> 1);
    }

    public boolean getFlagRetain() {
        return (flags & flagRetainBitmask) != 0;
    }
}
