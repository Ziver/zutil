package zutil.net.mqtt;

import zutil.log.LogUtil;
import zutil.net.mqtt.packet.MqttPacket;
import zutil.net.mqtt.packet.MqttPacketConnect;
import zutil.net.mqtt.packet.MqttPacketConnectAck;
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
 */
public class MqttBroker extends ThreadedTCPNetworkServer{
    private static final Logger logger = LogUtil.getLogger();

    public static final int MQTT_PORT = 1883;
    public static final int MQTT_PORT_TLS = 8883;


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
                MqttPacket packet = MqttPacket.read(in);
                // Unexpected packet?
                if ( ! (packet.header instanceof MqttPacketConnect))
                    throw new IOException("Expected MqttPacketConnect but received "+packet.header.getClass());
                MqttPacketConnect conn = (MqttPacketConnect) packet.header;
                // Incorrect protocol version?
                if (conn.protocolLevel != 0x04){
                    MqttPacketConnectAck connack = new MqttPacketConnectAck();
                    connack.returnCode = MqttPacketConnectAck.RETCODE_PROT_VER_ERROR;
                    MqttPacket.write(out, connack);
                    return;
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
