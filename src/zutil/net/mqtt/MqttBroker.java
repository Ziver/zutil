package zutil.net.mqtt;

import zutil.log.LogUtil;
import zutil.net.mqtt.packet.*;
import zutil.net.mqtt.packet.MqttPacketSubscribe.MqttSubscribePayload;
import zutil.net.mqtt.packet.MqttPacketSubscribeAck.MqttSubscribeAckPayload;
import zutil.net.mqtt.packet.MqttPacketUnsubscribe.MqttUnsubscribePayload;
import zutil.net.threaded.ThreadedTCPNetworkServer;
import zutil.net.threaded.ThreadedTCPNetworkServerThread;
import zutil.parser.binary.BinaryStructInputStream;
import zutil.parser.binary.BinaryStructOutputStream;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
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

    private Map<String, List<MqttSubscriptionListener>> subscriptions;

    public MqttBroker() {
        super(MQTT_PORT);
        subscriptions = new HashMap<>();
    }

    @Override
    protected ThreadedTCPNetworkServerThread getThreadInstance(Socket s) throws IOException {
        return new MqttConnectionThread(s);
    }


    public synchronized void subscribe(String topic, MqttSubscriptionListener listener) {
        if (topic == null || topic.isEmpty() || listener == null)
            return;

        if (!subscriptions.containsKey(topic)) {
            logger.fine("Creating new topic: " + topic);
            subscriptions.put(topic, new ArrayList<>());
        }
        List topicSubscriptions = subscriptions.get(topic);

        if (topicSubscriptions.contains(listener)) {
            logger.finer("New subscriber on topic (" + topic + "), subscriber count: " + topicSubscriptions.size());
            topicSubscriptions.add(listener);
        }
    }

    public synchronized void unsubscribe(MqttSubscriptionListener listener) {
        if (listener == null)
            return;

        for (String topic : subscriptions.keySet()){
            unsubscribe(topic, listener);
        }
    }
    public synchronized void unsubscribe(String topic, MqttSubscriptionListener listener) {
        if (topic == null || topic.isEmpty() || listener == null)
            return;

        if (!subscriptions.containsKey(topic))
            return;
        List topicSubscriptions = subscriptions.get(topic);

        if (topicSubscriptions.remove(listener)){
            logger.finer("Subscriber unsubscribed from topic (" + topic + "), subscriber count: " + topicSubscriptions.size());

            if (topicSubscriptions.isEmpty()) {
                logger.fine("Removing empty topic: " + topic);
                subscriptions.remove(topic);
            }
        }
    }


    protected static class MqttConnectionThread implements ThreadedTCPNetworkServerThread, MqttSubscriptionListener {
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

                    handlePacket(packet);
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

        public void handlePacket(MqttPacketHeader packet) throws IOException {
            // TODO: QOS

            switch (packet.type) {
                // TODO: Publish
                case MqttPacketHeader.PACKET_TYPE_PUBLISH:
                    break;

                // TODO: Subscribe
                case MqttPacketHeader.PACKET_TYPE_SUBSCRIBE:
                    MqttPacketSubscribe subscribePacket = (MqttPacketSubscribe) packet;
                    MqttPacketSubscribeAck subscribeAckPacket = new MqttPacketSubscribeAck();
                    subscribeAckPacket.packetId = subscribePacket.packetId;

                    for (MqttSubscribePayload payload : subscribePacket.payload) {
                        // TODO: subscribe(payload.topicFilter, this)

                        MqttSubscribeAckPayload ackPayload = new MqttSubscribeAckPayload();
                        ackPayload.returnCode = MqttSubscribeAckPayload.RETCODE_SUCESS_MAX_QOS_0;
                        subscribeAckPacket.payload.add(ackPayload);
                    }
                    sendPacket(subscribeAckPacket);
                    break;

                // TODO: Unsubscribe
                case MqttPacketHeader.PACKET_TYPE_UNSUBSCRIBE:
                    MqttPacketUnsubscribe unsubscribePacket = (MqttPacketUnsubscribe) packet;

                    for (MqttUnsubscribePayload payload : unsubscribePacket.payload) {
                        // TODO: unsubscribe(payload.topicFilter, this)
                    }

                    MqttPacketUnsubscribeAck unsubscribeAckPacket = new MqttPacketUnsubscribeAck();
                    unsubscribeAckPacket.packetId = unsubscribePacket.packetId;
                    sendPacket(unsubscribeAckPacket);
                    break;

                // Ping
                case MqttPacketHeader.PACKET_TYPE_PINGREQ:
                    sendPacket(new MqttPacketPingResp());
                    break;

                // Close connection
                default:
                    logger.warning("Received unknown packet type: " + packet.type);
                case MqttPacketHeader.PACKET_TYPE_DISCONNECT:
                    shutdown = true;
                    break;
            }
        }

        @Override
        public void dataPublished(String topic, String data) {

        }

        public synchronized void sendPacket(MqttPacketHeader packet) throws IOException {
            MqttPacket.write(out, packet);
        }

        public boolean isShutdown() {
            return shutdown;
        }

    }
}
