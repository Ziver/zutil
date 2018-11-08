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
public class MqttBroker extends ThreadedTCPNetworkServer {
    private static final Logger logger = LogUtil.getLogger();

    public static final int MQTT_PORT = 1883;
    public static final int MQTT_PORT_TLS = 8883;
    public static final int MQTT_PROTOCOL_VERSION = 0x04; // MQTT 3.1.1


    public MqttBroker() {
        super(MQTT_PORT);
    }


    @Override
    protected ThreadedTCPNetworkServerThread getThreadInstance(Socket s) throws IOException {
        return new MqttConnectionThread(s);
    }


    protected static class MqttConnectionThread implements ThreadedTCPNetworkServerThread {
        private Socket socket;
        private BinaryStructInputStream in;
        private BinaryStructOutputStream out;

        private boolean shutdown = false;

        protected MqttConnectionThread() {} // Test constructor

        public MqttConnectionThread(Socket s) throws IOException {
            socket = s;
            in = new BinaryStructInputStream(socket.getInputStream());
            out = new BinaryStructOutputStream(socket.getOutputStream());
        }


        @Override
        public void run() {
            try {
                // Setup connection
                MqttPacketHeader connectPacket = MqttPacket.read(in);
                // Unexpected packet?
                if (!(connectPacket instanceof MqttPacketConnect))
                    throw new IOException("Expected MqttPacketConnect but received " + connectPacket.getClass());
                MqttPacketConnect conn = (MqttPacketConnect) connectPacket;

                // Reply
                MqttPacketConnectAck connectAck = new MqttPacketConnectAck();

                // Incorrect protocol version?
                if (conn.protocolLevel != MQTT_PROTOCOL_VERSION) {
                    connectAck.returnCode = MqttPacketConnectAck.RETCODE_PROT_VER_ERROR;
                    sendPacket(connectAck);
                    return;
                } else {
                    connectAck.returnCode = MqttPacketConnectAck.RETCODE_OK;
                }

                // TODO: authenticate
                // TODO: clean session
                sendPacket(connectAck);

                // Connected

                while (!shutdown) {
                    MqttPacketHeader packet = MqttPacket.read(in);
                    if (packet == null)
                        return;

                    MqttPacketHeader packetRsp = handlePacket(packet);

                    if (packetRsp != null)
                        sendPacket(packetRsp);
                }

                socket.close();
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

        public MqttPacketHeader handlePacket(MqttPacketHeader packet) throws IOException {
            switch (packet.type) {
                // TODO: Publish
                case MqttPacketHeader.PACKET_TYPE_PUBLISH:
                    break;
                // TODO: Subscribe
                case MqttPacketHeader.PACKET_TYPE_SUBSCRIBE:
                    break;
                // TODO: Unsubscribe
                case MqttPacketHeader.PACKET_TYPE_UNSUBSCRIBE:
                    break;
                // Ping
                case MqttPacketHeader.PACKET_TYPE_PINGREQ:
                    return new MqttPacketPingResp();
                // Close connection
                default:
                    logger.warning("Received unknown packet type: " + packet.type);
                case MqttPacketHeader.PACKET_TYPE_DISCONNECT:
                    shutdown = true;
            }

            return null;
        }

        public void sendPacket(MqttPacketHeader packet) throws IOException {
            MqttPacket.write(out, packet);
        }

        public boolean isShutdown() {
            return shutdown;
        }
    }
}
