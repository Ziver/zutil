package zutil.net.mqtt.packet;

import zutil.parser.binary.BinaryStruct;

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



    @BinaryField(index = 1, length = 4)
    public byte type;
    @BinaryField(index = 2, length = 4)
    public byte flags;

    @CustomBinaryField(index = 3, serializer = MqttVariableIntSerializer.class)
    public int payloadLength;

}
