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


import zutil.parser.binary.BinaryStructInputStream;
import zutil.parser.binary.BinaryStructOutputStream;

import java.io.IOException;

import static zutil.net.mqtt.packet.MqttPacketHeader.*;

/**
 * A class for serializing and deserialize MQTT data packets
 */
public class MqttPacket {


    public static MqttPacketHeader read(BinaryStructInputStream in) throws IOException {
        MqttPacketHeader packet = new MqttPacketHeader();

        // Peek into stream and find packet type
        in.mark(10);
        in.read(packet);
        in.reset();

        // Resolve the correct header class
        switch (packet.type) {
            case PACKET_TYPE_CONN:        packet = new MqttPacketConnect(); break;
            case PACKET_TYPE_CONNACK:     packet = new MqttPacketConnectAck(); break;
            case PACKET_TYPE_PUBLISH:     packet = new MqttPacketPublish(); break;
            case PACKET_TYPE_PUBACK:      packet = new MqttPacketPublishAck(); break;
            case PACKET_TYPE_PUBREC:      packet = new MqttPacketPublishRec(); break;
            case PACKET_TYPE_PUBREL:      packet = new MqttPacketPublishRec(); break;
            case PACKET_TYPE_PUBCOMP:     packet = new MqttPacketPublishComp(); break;
            case PACKET_TYPE_SUBSCRIBE:   packet = new MqttPacketSubscribe(); break;
            case PACKET_TYPE_SUBACK:      packet = new MqttPacketSubscribeAck(); break;
            case PACKET_TYPE_UNSUBSCRIBE: packet = new MqttPacketUnsubscribe(); break;
            case PACKET_TYPE_UNSUBACK:    packet = new MqttPacketUnsubscribeAck(); break;
            case PACKET_TYPE_PINGREQ:     packet = new MqttPacketPingReq(); break;
            case PACKET_TYPE_PINGRESP:    packet = new MqttPacketPingResp(); break;
            case PACKET_TYPE_DISCONNECT:  packet = new MqttPacketDisconnect(); break;
            default:
                throw new IOException("Unknown header type: " + packet.type);
        }
        in.read(packet);
        // TODO: payload

        return packet;
    }

    public static void write(BinaryStructOutputStream out, MqttPacketHeader header) throws IOException{

        out.write(header);
        // TODO: payload
    }
}
