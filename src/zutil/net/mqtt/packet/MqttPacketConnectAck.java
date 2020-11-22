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

/**
 * The CONNACK Packet is the packet sent by the Server in response to a
 * CONNECT Packet received from a Client.
 *
 * @see <a href="http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/mqtt-v3.1.1.html">MQTT v3.1.1 Spec</a>
 */
public class MqttPacketConnectAck extends MqttPacketHeader{
    public static final int RETCODE_OK                 = 0;
    public static final int RETCODE_PROT_VER_ERROR     = 1;
    public static final int RETCODE_IDENTIFIER_REJECT  = 2;
    public static final int RETCODE_SERVER_UNAVAILABLE = 3;
    public static final int RETCODE_BADD_USER_OR_PASS  = 4;
    public static final int RETCODE_NOT_AUTHORIZED     = 5;

    // Header

    {
        type = MqttPacketHeader.PACKET_TYPE_CONNACK;
    }

    // Variable header

    @BinaryField(index = 2000, length = 7)
    private int flagReserved;
    /** Indicates that there is a valid Session available */
    @BinaryField(index = 2001, length = 1)
    public boolean flagSessionPresent;

    @BinaryField(index = 2002, length = 8)
    public int returnCode;

    // No payload

}
