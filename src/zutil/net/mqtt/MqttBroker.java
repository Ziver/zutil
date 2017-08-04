package zutil.net.mqtt;

import zutil.log.LogUtil;
import zutil.net.mqtt.packet.*;
import zutil.net.threaded.ThreadedTCPNetworkServer;
import zutil.net.threaded.ThreadedTCPNetworkServerThread;
import zutil.parser.binary.BinaryStructInputStream;
import zutil.parser.binary.BinaryStructOutputStream;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TODO:
 *
 * @see <a href="http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/mqtt-v3.1.1.html">MQTT v3.1.1 Spec</a>
 */
public class MqttBroker extends ThreadedTCPNetworkServer{
    private static final Logger logger = LogUtil.getLogger();

    public static final int MQTT_PORT = 1883;
    public static final int MQTT_PORT_TLS = 8883;
    public static final int MQTT_PROTOCOL_VERSION = 0x04; // MQTT 3.1.1


    public MqttBroker(){
        super(MQTT_PORT);
    }


    @Override
    protected ThreadedTCPNetworkServerThread getThreadInstance(Socket s) throws IOException {
        return new MQTTConnectionThread(s);
    }


    private static class MQTTConnectionThread implements ThreadedTCPNetworkServerThread {
        private Socket socket;
        private BinaryStructInputStream in;
        private BinaryStructOutputStream out;


        private MQTTConnectionThread(Socket s) throws IOException {
            socket = s;
            in = new BinaryStructInputStream(socket.getInputStream());
            out = new BinaryStructOutputStream(socket.getOutputStream());
        }


        @Override
        public void run() {
            try {
                // Setup connection
                MqttPacketHeader packet = MqttPacket.read(in);
                // Unexpected packet?
                if ( ! (packet instanceof MqttPacketConnect))
                    throw new IOException("Expected MqttPacketConnect but received "+packet.getClass());
                MqttPacketConnect conn = (MqttPacketConnect) packet;

                // Reply
                MqttPacketConnectAck connack = new MqttPacketConnectAck();
                connack.returnCode = MqttPacketConnectAck.RETCODE_OK;
                // Incorrect protocol version?
                if (conn.protocolLevel != MQTT_PROTOCOL_VERSION){
                    connack.returnCode = MqttPacketConnectAck.RETCODE_PROT_VER_ERROR;
                    MqttPacket.write(out, connack);
                    return;
                }
                // TODO: authenticate
                // TODO: clean session
                MqttPacket.write(out, connack);

                // Connected
                while (true) {
                    packet = MqttPacket.read(in);
                    if (packet == null)
                        return;

                    switch (packet.type){
                        // Publish
                        case MqttPacketHeader.PACKET_TYPE_PUBLISH:
                            break;
                        // Subscribe
                        case MqttPacketHeader.PACKET_TYPE_SUBSCRIBE:
                            break;
                        // Unsubscribe
                        case MqttPacketHeader.PACKET_TYPE_UNSUBSCRIBE:
                            break;
                        // Ping
                        case MqttPacketHeader.PACKET_TYPE_PINGREQ:
                            MqttPacket.write(out, new MqttPacketPingResp());
                            break;
                        // Close connection
                        default:
                            logger.warning("Received unknown packet type: "+packet.type);
                        case MqttPacketHeader.PACKET_TYPE_DISCONNECT:
                            return;
                    }
                }

            } catch (IOException e) {
                logger.log(Level.SEVERE, null, e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.log(Level.SEVERE, null, e);
                }
            }
        }
    }
}
