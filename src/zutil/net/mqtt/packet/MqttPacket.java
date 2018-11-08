package zutil.net.mqtt.packet;


import zutil.parser.binary.BinaryStructInputStream;
import zutil.parser.binary.BinaryStructOutputStream;

import java.io.IOException;

import static zutil.net.mqtt.packet.MqttPacketHeader.*;

/**
 * A data class encapsulating a MQTT header and its controlHeader
 */
public class MqttPacket {


    public static MqttPacketHeader read(BinaryStructInputStream in) throws IOException {
        MqttPacketHeader packet = new MqttPacketHeader();

        // Peek into stream and find packet type
        in.mark(10);
        in.read(packet);
        in.reset();

        // Resolve the correct header class
        switch (packet.type){
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
                throw new IOException("Unknown header type: "+ packet.type);
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
