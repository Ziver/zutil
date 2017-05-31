package zutil.net.mqtt.packet;


import zutil.parser.binary.BinaryStruct;
import zutil.parser.binary.BinaryStructInputStream;
import zutil.parser.binary.BinaryStructOutputStream;

import java.io.IOException;

import static zutil.net.mqtt.packet.MqttPacketHeader.*;

/**
 * A data class encapsulating a MQTT header and its controlHeader
 */
public class MqttPacket {

    public MqttPacketHeader header;


    private MqttPacket() {}

    public static MqttPacket read(BinaryStructInputStream in) throws IOException {
        MqttPacket packet = new MqttPacket();

        // Peek into stream and find packet type
        in.mark(10);
        packet.header = new MqttPacketHeader();
        in.read(packet.header);
        in.reset();

        // Resolve the correct header class
        switch (packet.header.type){
            case PACKET_TYPE_CONN:        packet.header = new MqttPacketConnect(); break;
            case PACKET_TYPE_CONNACK:     packet.header = new MqttPacketConnectAck(); break;
            case PACKET_TYPE_PUBLISH:     packet.header = new MqttPacketPublish(); break;
            case PACKET_TYPE_PUBACK:      packet.header = new MqttPacketPublishAck(); break;
            case PACKET_TYPE_PUBREC:      packet.header = new MqttPacketPublishRec(); break;
            case PACKET_TYPE_PUBREL:      packet.header = new MqttPacketPublishRec(); break;
            case PACKET_TYPE_PUBCOMP:     packet.header = new MqttPacketPublishComp(); break;
            case PACKET_TYPE_SUBSCRIBE:   packet.header = new MqttPacketSubscribe(); break;
            case PACKET_TYPE_SUBACK:      packet.header = new MqttPacketSubscribeAck(); break;
            case PACKET_TYPE_UNSUBSCRIBE: packet.header = new MqttPacketUnsubscribe(); break;
            case PACKET_TYPE_UNSUBACK:    packet.header = new MqttPacketUnsubscribeAck(); break;
            case PACKET_TYPE_PINGREQ:     packet.header = new MqttPacketPingReq(); break;
            case PACKET_TYPE_PINGRESP:    packet.header = new MqttPacketPingResp(); break;
            case PACKET_TYPE_DISCONNECT:  packet.header = new MqttPacketDisconnect(); break;
            default:
                throw new IOException("Unknown header type: "+ packet.header.type);
        }
        in.read(packet.header);
        // TODO: payload

        return packet;
    }

    public static void write(BinaryStructOutputStream out, MqttPacketHeader header) throws IOException{
        if      (header instanceof MqttPacketConnect)        header.type = PACKET_TYPE_CONN;
        else if (header instanceof MqttPacketConnectAck)     header.type = PACKET_TYPE_CONNACK;
        else if (header instanceof MqttPacketPublishAck)     header.type = PACKET_TYPE_PUBLISH;
        else if (header instanceof MqttPacketPublishRec)     header.type = PACKET_TYPE_PUBACK;
        else if (header instanceof MqttPacketPublishComp)    header.type = PACKET_TYPE_PUBREL;
        else if (header instanceof MqttPacketSubscribe)      header.type = PACKET_TYPE_PUBCOMP;
        else if (header instanceof MqttPacketSubscribeAck)   header.type = PACKET_TYPE_SUBSCRIBE;
        else if (header instanceof MqttPacketUnsubscribe)    header.type = PACKET_TYPE_UNSUBSCRIBE;
        else if (header instanceof MqttPacketUnsubscribeAck) header.type = PACKET_TYPE_UNSUBACK;
        else if (header instanceof MqttPacketPingReq)        header.type = PACKET_TYPE_PINGREQ;
        else if (header instanceof MqttPacketPingResp)       header.type = PACKET_TYPE_PINGRESP;
        else if (header instanceof MqttPacketDisconnect)     header.type = PACKET_TYPE_DISCONNECT;
        else
            throw new IOException("Unknown header class: "+ header.getClass());

        out.write(header);
        // TODO: payload
    }
}
