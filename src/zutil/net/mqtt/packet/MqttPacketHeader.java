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

import zutil.parser.binary.BinaryStruct;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 */
public class MqttPacketHeader implements BinaryStruct {

    //                      RESERVED                = 0;
    public static final int PACKET_TYPE_CONN        = 1;
    public static final int PACKET_TYPE_CONNACK     = 2;
    public static final int PACKET_TYPE_PUBLISH     = 3;
    public static final int PACKET_TYPE_PUBACK      = 4;
    public static final int PACKET_TYPE_PUBREC      = 5;
    public static final int PACKET_TYPE_PUBREL      = 6;
    public static final int PACKET_TYPE_PUBCOMP     = 7;
    public static final int PACKET_TYPE_SUBSCRIBE   = 8;
    public static final int PACKET_TYPE_SUBACK      = 9;
    public static final int PACKET_TYPE_UNSUBSCRIBE = 10;
    public static final int PACKET_TYPE_UNSUBACK    = 11;
    public static final int PACKET_TYPE_PINGREQ     = 12;
    public static final int PACKET_TYPE_PINGRESP    = 13;
    public static final int PACKET_TYPE_DISCONNECT  = 14;
    //                      RESERVED                = 15


    // Fixed Header

    @BinaryField(index = 1, length = 4)
    public byte type;
    @BinaryField(index = 2, length = 4)
    public byte flags;

    @CustomBinaryField(index = 3, serializer = MqttVariableIntSerializer.class)
    public int variableHeaderAndPayloadLength;

    // ------------------------------------------
    // Variable Header
    // ------------------------------------------

    /**
     * @return the calculated length of the variable MQTT header in bytes
     */
    public int calculateVariableHeaderLength() {
        return 0;
    }

    // ------------------------------------------
    // Payload
    // ------------------------------------------

    /**
     * @return the calculated length of assigned payload in bytes
     */
    public int calculatePayloadLength() {
        return 0;
    }
}
