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
import zutil.parser.binary.BinaryFieldSerializer;
import zutil.parser.binary.serializer.TwoByteLengthPrefixedDataSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This packet is the first message sent from a Client when it
 * has established a connection to a Server. A Client can only
 * send the CONNECT Packet once over a Network Connection.
 *
 * @see <a href="http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/mqtt-v3.1.1.html">MQTT v3.1.1 Spec</a>
 */
public class MqttPacketConnect extends MqttPacketHeader {

    // Header

    {
        type = MqttPacketHeader.PACKET_TYPE_CONN;
    }

    // ------------------------------------------
    // Variable header
    // ------------------------------------------

    @BinaryField(index = 2001, length = 16)
    private int protocolNameLength = 4;
    /**
     * The Protocol Name is a UTF-8 encoded string that represents
     * the protocol name "MQTT", capitalized as shown. The string,
     * its offset and length will not be changed by future versions
     * of the MQTT specification.
     */
    @BinaryField(index = 2002, length = 8*4)
    public String protocolName = "MQTT";

    /**
     * The value represents the revision level of the protocol used by
     * the Client. The value of the Protocol Level field for the version
     * 3.1.1 of the protocol is 4 (0x04)
     */
    @BinaryField(index = 2003, length = 8)
    public int protocolLevel = 0x04;


    /** Indicates that the controlHeader contains a username */
    @BinaryField(index = 2010, length = 1)
    public boolean flagUsername;

    /** Indicates that the controlHeader contains a password */
    @BinaryField(index = 2011, length = 1)
    public boolean flagPassword;

    /** Specifies if the Will Message is to be Retained when it is published. */
    @BinaryField(index = 2012, length = 1)
    public boolean flagWillRetain;

    /** Specifies the QoS level to be used when publishing the Will Message. */
    @BinaryField(index = 2013, length = 2)
    public int flagWillQoS;

    @BinaryField(index = 2014, length = 1)
    public boolean flagWillFlag;

    /** This bit specifies the handling of the Session state. */
    @BinaryField(index = 2015, length = 1)

    public boolean flagCleanSession;

    @BinaryField(index = 2016, length = 1)
    public boolean flagReserved;


    /**
     * The Keep Alive is a time interval measured in seconds and
     * it is the maximum time interval that is permitted to elapse
     * between the point at which the Client finishes transmitting one
     * Control Packet and the point it starts sending the next.
     * A Keep Alive value of zero (0) has the effect of turning off
     * the keep alive mechanism.
     */
    @BinaryField(index = 2020, length = 16)
    public int keepAlive;


    @Override
    public int calculateVariableHeaderLength() {
        return 10;
    }

    // ------------------------------------------
    // Payload
    // ------------------------------------------

    @CustomBinaryField(index = 3000, serializer = MqttPacketConnectPayloadSerializer.class)
    public String clientIdentifier;

    @CustomBinaryField(index = 3001, serializer = MqttPacketConnectPayloadSerializer.class)
    public String willTopic;

    @CustomBinaryField(index = 3002, serializer = MqttPacketConnectPayloadSerializer.class)
    public byte[] willPayload;

    @CustomBinaryField(index = 3003, serializer = MqttPacketConnectPayloadSerializer.class)
    public String username;

    @CustomBinaryField(index = 3004, serializer = MqttPacketConnectPayloadSerializer.class)
    public String password;


    @Override
    public int calculatePayloadLength() {
        int length = 0;

        // Each String and byte[] is prefixed with a 2 byte length value in the payload

        if (!flagCleanSession)
            length += 2 + clientIdentifier.length();
        if (flagWillFlag)
            length += 2 + willTopic.length() + 2 + willPayload.length;
        if (flagUsername)
            length += 2 + username.length();
        if (flagPassword)
            length += 2 + password.length();

        return length;
    }

    // ------------------------------------------
    // Utilities
    // ------------------------------------------

    protected static class MqttPacketConnectPayloadSerializer implements BinaryFieldSerializer {

        @Override
        public Object read(InputStream in, BinaryFieldData field, Object parentObject) throws IOException {
            MqttPacketConnect packet = (MqttPacketConnect) parentObject;
            TwoByteLengthPrefixedDataSerializer serializer = new TwoByteLengthPrefixedDataSerializer();

            if ("clientIdentifier".equals(field.getName()) && !packet.flagCleanSession ||
                    "willTopic".equals(field.getName()) && packet.flagWillFlag ||
                    "willPayload".equals(field.getName()) && packet.flagWillFlag ||
                    "username".equals(field.getName()) && packet.flagUsername ||
                    "password".equals(field.getName()) && packet.flagPassword) {
                return serializer.read(in, field, parentObject);
            }

            return null;
        }

        @Override
        public void write(OutputStream out, Object obj, BinaryFieldData field, Object parentObject) throws IOException {
            MqttPacketConnect packet = (MqttPacketConnect) parentObject;
            TwoByteLengthPrefixedDataSerializer serializer = new TwoByteLengthPrefixedDataSerializer();

            if ("clientIdentifier".equals(field.getName()) && !packet.flagCleanSession ||
                    "willTopic".equals(field.getName()) && packet.flagWillFlag ||
                    "willPayload".equals(field.getName()) && packet.flagWillFlag ||
                    "username".equals(field.getName()) && packet.flagUsername ||
                    "password".equals(field.getName()) && packet.flagPassword) {
                serializer.write(out, obj, field, parentObject);
            }
        }


        @Override
        public Object read(InputStream in, BinaryFieldData field) throws IOException {
            return null;
        }
        @Override
        public void write(OutputStream out, Object obj, BinaryFieldData field) throws IOException {}
    }
}
