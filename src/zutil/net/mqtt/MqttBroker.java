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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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

    private List<MqttSubscriptionListener> globalListeners = new ArrayList<>();
    private Map<String, List<MqttSubscriptionListener>> subscriptionListeners = new HashMap<>();


    public MqttBroker() throws IOException {
        super(MQTT_PORT);
    }

    public MqttBroker(int port) throws IOException {
        super(port);
    }


    @Override
    protected ThreadedTCPNetworkServerThread getThreadInstance(Socket s) throws IOException {
        return new MqttConnectionThread(this, s);
    }


    /**
     * @return the subscriber count for the specific topic, -1 if
     *         topic does not exist or has not been created yet.
     */
    public int getSubscriberCount(String topic) {
        List<MqttSubscriptionListener> topicSubscriptions = subscriptionListeners.get(topic);

        if (topicSubscriptions != null)
            return topicSubscriptions.size();
        return -1;
    }

    /**
     * Assign a listener that will be called for any topic.
     *
     * @param listener the listener that will be called.
     */
    public void addGlobalSubscriber(MqttSubscriptionListener listener) {
        globalListeners.add(listener);
    }

    /**
     * Remove the given listener from global subscribers.
     *
     * @param listener the listener that should not be called anymore.
     */
    public void removeGlobalSubscriber(MqttSubscriptionListener listener) {
        globalListeners.remove(listener);
    }

    /**
     * Add the listener as a subscriber of the specific topic.
     *
     * @param topic    the topic that will be subscribed to.
     * @param listener the listener that will be called.
     */
    public synchronized void subscribe(String topic, MqttSubscriptionListener listener) {
        if (topic == null || topic.isEmpty() || listener == null)
            return;

        if (!subscriptionListeners.containsKey(topic)) {
            logger.finest("Creating new topic: " + topic);
            subscriptionListeners.put(topic, new ArrayList<>());
        }

        List<MqttSubscriptionListener> topicSubscriptions = subscriptionListeners.get(topic);

        if (!topicSubscriptions.contains(listener)) {
            topicSubscriptions.add(listener);
            logger.finer("New subscriber on topic: " + topic + " (subscriber count: " + topicSubscriptions.size() + ")");
        }
    }

    /**
     * Publish data to the specific topic.
     *
     * @param topic the topic where the data should be published to.
     * @param data  the data that should be published. String will be converted to UTF-8 byte array.
     */
    public void publish(String topic, String data) {
        publish(topic, data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Publish data to the specific topic.
     *
     * @param topic the topic where the data should be published to.
     * @param data  the data that should be published.
     */
    public void publish(String topic, byte[] data) {
        logger.finer("Data has been published to topic: " + topic);

        if (globalListeners != null)
            globalListeners.forEach(listener -> listener.dataPublished(topic, data));

        List<MqttSubscriptionListener> topicSubscriptions = subscriptionListeners.get(topic);
        if (topicSubscriptions != null) {
            topicSubscriptions.forEach(subscriber -> subscriber.dataPublished(topic, data));
        }
    }

    /**
     * Unsubscribe the listener from all available MQTT topics.
     *
     * @param listener the listener that should be unsubscribed.
     */
    public synchronized void unsubscribe(MqttSubscriptionListener listener) {
        if (listener == null)
            return;

        for (String topic : subscriptionListeners.keySet()) {
            unsubscribe(topic, listener);
        }
    }

    /**
     * Unsubscribe the listener from the specific MQTT topic.
     *
     * @param topic    the specific topic that should be unsubscribed from.
     * @param listener the target listener that should be unsubscribed.
     */
    public synchronized void unsubscribe(String topic, MqttSubscriptionListener listener) {
        if (topic == null || topic.isEmpty() || listener == null)
            return;

        if (!subscriptionListeners.containsKey(topic))
            return;

        List<MqttSubscriptionListener> topicSubscriptions = subscriptionListeners.get(topic);

        if (topicSubscriptions.remove(listener)) {
            logger.finer("Subscriber unsubscribed from topic " + topic + " (subscriber count: " + topicSubscriptions.size() + ")");

            if (topicSubscriptions.isEmpty()) {
                logger.finest("Removing empty topic: " + topic);
                subscriptionListeners.remove(topic);
            }
        }
    }


    protected static class MqttConnectionThread implements ThreadedTCPNetworkServerThread, MqttSubscriptionListener {
        private final MqttBroker broker;
        private Socket socket;
        private BinaryStructInputStream in;
        private BinaryStructOutputStream out;

        private boolean disconnected = false;
        /** A message that should be sent in case the connection to client is abnormally disconnected */
        private MqttPacketPublish willPacket = null;
        /** The maximum amount of time(seconds) to wait for activity from client, 0 means no timeout */
        private int connectionTimeoutTime = 0;

        /**
         * Test constructor
         */
        protected MqttConnectionThread(MqttBroker b) {
            broker = b;
        }

        public MqttConnectionThread(MqttBroker b, Socket s) throws IOException {
            this(b);
            socket = s;

            InputStream baseInputstream = socket.getInputStream();
            if (!baseInputstream.markSupported())
                baseInputstream = new BufferedInputStream(baseInputstream);

            in = new BinaryStructInputStream(baseInputstream);
            out = new BinaryStructOutputStream(socket.getOutputStream());
        }


        @Override
        public void run() {
            try {
                // Setup connection

                logger.fine("[" + socket.getInetAddress() + "] New MQTT client connected.");
                MqttPacketHeader connectPacket = MqttPacket.read(in);
                handleConnect(connectPacket);

                // Connected

                while (!disconnected) {
                    MqttPacketHeader packet = MqttPacket.read(in);
                    if (packet == null)
                        return;

                    handlePacket(packet);
                }

            } catch (IOException e) {
                logger.log(Level.WARNING, "[" + socket.getInetAddress() + "] There was a issue with the client connection.", e);
            } finally {
                try {
                    sendWillPacket();

                    logger.fine("[" + socket.getInetAddress() + "] MQTT client disconnected.");
                    socket.close();
                    broker.unsubscribe(this);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, null, e);
                }
            }
        }

        private void handleConnect(MqttPacketHeader connectPacket) throws IOException {
            // Unexpected packet?
            if (!(connectPacket instanceof MqttPacketConnect))
                throw new IOException("Expected MqttPacketConnect but received " + connectPacket.getClass());
            MqttPacketConnect conn = (MqttPacketConnect) connectPacket;

            // Reply
            MqttPacketConnectAck connectAck = new MqttPacketConnectAck();

            // ----------------------------------
            // Handling Header
            // ----------------------------------

            // Incorrect protocol version?
            if (conn.protocolLevel != MQTT_PROTOCOL_VERSION) {
                connectAck.returnCode = MqttPacketConnectAck.RETCODE_PROT_VER_ERROR;
                sendPacket(connectAck);
                return;
            } else {
                connectAck.returnCode = MqttPacketConnectAck.RETCODE_OK;
            }

            // Is reserved field properly set? should be false
            if (conn.flagReserved) {
                disconnected = true;
                return;
            }

            // Handle Session
            if (conn.flagCleanSession) {
                // TODO: Remove session
                connectAck.flagSessionPresent = false;
            } else {
                // TODO: Restore or create new session
                throw new UnsupportedOperationException("Sessions currently not supported.");
            }

            // Handle will message
            if (conn.flagWillFlag) {
                logger.fine("[" + socket.getInetAddress() + "] Registered will packet for topic: " + conn.willTopic);
                willPacket = new MqttPacketPublish();
                willPacket.topicName = conn.willTopic;
                willPacket.payload = conn.willPayload;
            } else {
                willPacket = null;
            }

            // TODO: authenticate
            if (conn.flagUsername) {
                String username;

                if (conn.flagPassword) {
                    String password;
                }
            }

            connectionTimeoutTime = conn.keepAlive;

            // ----------------------------------
            // Handling Payload
            // ----------------------------------


            sendPacket(connectAck);
        }

        protected void handlePacket(MqttPacketHeader packet) throws IOException {
            // TODO: QOS 1
            // TODO: QOS 2
            // TODO: handle connection timeout

            switch (packet.type) {
                case MqttPacketHeader.PACKET_TYPE_PUBLISH:
                    handlePublish((MqttPacketPublish) packet);
                    break;

                case MqttPacketHeader.PACKET_TYPE_SUBSCRIBE:
                    handleSubscribe((MqttPacketSubscribe) packet);
                    break;

                case MqttPacketHeader.PACKET_TYPE_UNSUBSCRIBE:
                    handleUnsubscribe((MqttPacketUnsubscribe) packet);
                    break;

                // Ping
                case MqttPacketHeader.PACKET_TYPE_PINGREQ:
                    sendPacket(new MqttPacketPingResp());
                    break;

                // Close connection
                default:
                    logger.warning("[" + socket.getInetAddress() + "] Received unknown packet type: " + packet.type + " (" + packet.getClass() + ")");
                    sendWillPacket();
                    /* FALLTHROUGH */
                case MqttPacketHeader.PACKET_TYPE_DISCONNECT:
                    willPacket = null;
                    disconnected = true;
                    break;
            }
        }


        private void handlePublish(MqttPacketPublish publishPacket) {
            if (publishPacket.getFlagQoS() != 0)
                throw new UnsupportedOperationException("QoS larger then 0 not supported.");

            logger.finer("[" + socket.getInetAddress() + "] Publishing to topic: " + publishPacket.topicName);
            broker.publish(publishPacket.topicName, publishPacket.payload);
        }

        private void handleSubscribe(MqttPacketSubscribe subscribePacket) throws IOException {
            MqttPacketSubscribeAck subscribeAckPacket = new MqttPacketSubscribeAck();
            subscribeAckPacket.packetId = subscribePacket.packetId;

            for (MqttSubscribePayload payload : subscribePacket.payloads) {
                logger.finer("[" + socket.getInetAddress() + "] Subscribing to topic: " + payload.topicFilter);
                broker.subscribe(payload.topicFilter, this);

                // Prepare response
                MqttSubscribeAckPayload ackPayload = new MqttSubscribeAckPayload();
                ackPayload.returnCode = MqttSubscribeAckPayload.RETCODE_SUCESS_MAX_QOS_0;
                subscribeAckPacket.payloads.add(ackPayload);
            }

            sendPacket(subscribeAckPacket);
        }

        private void handleUnsubscribe(MqttPacketUnsubscribe unsubscribePacket) throws IOException {
            for (MqttUnsubscribePayload payload : unsubscribePacket.payloads) {
                logger.finer("[" + socket.getInetAddress() + "] Unsubscribing from topic: " + payload.topicFilter);
                broker.unsubscribe(payload.topicFilter, this);
            }

            // Prepare response
            MqttPacketUnsubscribeAck unsubscribeAckPacket = new MqttPacketUnsubscribeAck();
            unsubscribeAckPacket.packetId = unsubscribePacket.packetId;
            sendPacket(unsubscribeAckPacket);
        }


        @Override
        public void dataPublished(String topic, byte[] data) {
            // Data has been published to a subscribed topic.
        }

        private void sendWillPacket() throws IOException {
            if (willPacket != null) {
                logger.fine("[" + socket.getInetAddress() + "] Publishing will packet.");
                sendPacket(willPacket);
                willPacket = null;
            }
        }

        public synchronized void sendPacket(MqttPacketHeader packet) throws IOException {
            MqttPacket.write(out, packet);
        }


        public boolean isDisconnected() {
            return disconnected;
        }
    }
}
