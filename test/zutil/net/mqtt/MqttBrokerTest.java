package zutil.net.mqtt;

import org.junit.Test;
import zutil.net.mqtt.MqttBroker.MqttConnectionThread;
import zutil.net.mqtt.packet.*;
import zutil.net.mqtt.packet.MqttPacketSubscribe.MqttSubscribePayload;
import zutil.net.mqtt.packet.MqttPacketUnsubscribe.MqttUnsubscribePayload;

import java.io.IOException;
import java.util.LinkedList;

import static org.junit.Assert.*;

public class MqttBrokerTest {

    //**************** Mocks **************************

    public static class MqttConnectionThreadMock extends MqttConnectionThread {
        public LinkedList<MqttPacketHeader> sentPackets = new LinkedList<>();

        protected MqttConnectionThreadMock(MqttBroker b) {
            super(b);
        }

        @Override
        public void sendPacket(MqttPacketHeader packet) {
            sentPackets.add(packet);
        }
    }

    //**************** Test Cases **************************

    @Test
    public void subscribeEmpty() throws IOException {
        MqttConnectionThreadMock thread = new MqttConnectionThreadMock(new MqttBroker());
        MqttPacketSubscribe subscribePacket = new MqttPacketSubscribe();
        subscribePacket.packetId = (int)(Math.random()*1000);

        thread.handlePacket(subscribePacket);

        // Check response
        MqttPacketHeader responsePacket = thread.sentPackets.poll();
        assertEquals(MqttPacketSubscribeAck.class, responsePacket.getClass());
        assertEquals(subscribePacket.packetId, ((MqttPacketSubscribeAck)responsePacket).packetId);
        assertEquals(subscribePacket.payload.size(), ((MqttPacketSubscribeAck)responsePacket).payload.size());
    }

    @Test
    public void subscribe() throws IOException {
        MqttBroker broker = new MqttBroker();
        MqttConnectionThreadMock thread = new MqttConnectionThreadMock(broker);
        MqttPacketSubscribe subscribePacket = new MqttPacketSubscribe();
        subscribePacket.packetId = (int)(Math.random()*1000);

        subscribePacket.payload.add(new MqttSubscribePayload());
        subscribePacket.payload.get(0).topicFilter = "topic1";
        subscribePacket.payload.add(new MqttSubscribePayload());
        subscribePacket.payload.get(1).topicFilter = "topic2";

        thread.handlePacket(subscribePacket);

        // Check response
        MqttPacketHeader responsePacket = thread.sentPackets.poll();
        assertEquals(MqttPacketSubscribeAck.class, responsePacket.getClass());
        assertEquals(subscribePacket.packetId, ((MqttPacketSubscribeAck)responsePacket).packetId);
        assertEquals(subscribePacket.payload.size(), ((MqttPacketSubscribeAck)responsePacket).payload.size());
        // Check broker
        assertEquals(1, broker.getSubscriberCount("topic1"));
        assertEquals(1, broker.getSubscriberCount("topic2"));

        //************************ Duplicate subscribe packet

        subscribePacket.packetId = (int)(Math.random()*1000);
        subscribePacket.payload.clear();
        subscribePacket.payload.add(new MqttSubscribePayload());
        subscribePacket.payload.get(0).topicFilter = "topic1";

        thread.handlePacket(subscribePacket);

        // Check broker
        assertEquals(1, broker.getSubscriberCount("topic1"));

        //************************ New subscriber

        MqttConnectionThreadMock thread2 = new MqttConnectionThreadMock(broker);

        thread2.handlePacket(subscribePacket);

        // Check broker
        assertEquals(2, broker.getSubscriberCount("topic1"));
        assertEquals(1, broker.getSubscriberCount("topic2"));
    }

    @Test
    public void publish() throws IOException {
        // Setup subscriber
        final String[] recivedTopic = new String[1];
        final byte[] recievedPayload = new byte[1];
        MqttSubscriptionListener subscriber = new MqttSubscriptionListener() {
            public void dataPublished(String topic, byte[] data) {
                recivedTopic[0] = topic;
                recievedPayload[0] = data[0];
            }
        };

        // Setup broker
        MqttBroker broker = new MqttBroker();
        MqttConnectionThreadMock thread = new MqttConnectionThreadMock(broker);
        broker.subscribe("test/topic", subscriber);

        // Setup publish
        MqttPacketPublish publish = new MqttPacketPublish();
        publish.topicName = "test/topic";
        publish.payload = new byte[]{42};

        thread.handlePacket(publish);

        // Check response
        assertEquals("test/topic", recivedTopic[0]);
        assertEquals((byte) 42, recievedPayload[0]);
    }

    @Test
    public void unsubscribeEmpty() throws IOException {
        MqttBroker broker = new MqttBroker();
        MqttConnectionThreadMock thread = new MqttConnectionThreadMock(broker);
        MqttPacketUnsubscribe unsubscribePacket = new MqttPacketUnsubscribe();
        unsubscribePacket.packetId = (int)(Math.random()*1000);

        thread.handlePacket(unsubscribePacket);

        // Check response
        MqttPacketHeader responsePacket = thread.sentPackets.poll();
        assertEquals(MqttPacketUnsubscribeAck.class, responsePacket.getClass());
        assertEquals(unsubscribePacket.packetId, ((MqttPacketUnsubscribeAck)responsePacket).packetId);
    }

    @Test
    public void unsubscribe() throws IOException {
        MqttBroker broker = new MqttBroker();
        MqttConnectionThreadMock thread = new MqttConnectionThreadMock(broker);
        MqttPacketUnsubscribe unsubscribePacket = new MqttPacketUnsubscribe();
        unsubscribePacket.packetId = (int)(Math.random()*1000);

        unsubscribePacket.payload.add(new MqttUnsubscribePayload());
        unsubscribePacket.payload.get(0).topicFilter = "topic1";

        thread.handlePacket(unsubscribePacket);

        // Check response
        MqttPacketHeader responsePacket = thread.sentPackets.poll();
        assertEquals(MqttPacketUnsubscribeAck.class, responsePacket.getClass());
        assertEquals(unsubscribePacket.packetId, ((MqttPacketUnsubscribeAck)responsePacket).packetId);
        // Check broker
        assertEquals(-1, broker.getSubscriberCount("topic1"));

        //************************ New subscriber

        MqttPacketSubscribe subscribePacket = new MqttPacketSubscribe();
        subscribePacket.packetId = (int)(Math.random()*1000);

        subscribePacket.payload.add(new MqttSubscribePayload());
        subscribePacket.payload.get(0).topicFilter = "topic1";
        subscribePacket.payload.add(new MqttSubscribePayload());
        subscribePacket.payload.get(1).topicFilter = "topic2";

        thread.handlePacket(subscribePacket);

        // Check broker
        assertEquals(1, broker.getSubscriberCount("topic1"));

        //************************ Unsubscribe

        unsubscribePacket.packetId = (int)(Math.random()*1000);

        thread.handlePacket(unsubscribePacket);

        // Check broker
        assertEquals(-1, broker.getSubscriberCount("topic1"));
    }

    @Test
    public void ping() throws IOException {
        MqttConnectionThreadMock thread = new MqttConnectionThreadMock(new MqttBroker());
        MqttPacketPingReq pingPacket = new MqttPacketPingReq();

        thread.handlePacket(pingPacket);

        // Check response
        assertEquals(MqttPacketPingResp.class, thread.sentPackets.poll().getClass());
    }

    @Test
    public void disconnect() throws IOException {
        MqttConnectionThreadMock thread = new MqttConnectionThreadMock(new MqttBroker());
        MqttPacketDisconnect disconnectPacket = new MqttPacketDisconnect();

        thread.handlePacket(disconnectPacket);

        // Check response
        assertEquals(null, thread.sentPackets.poll());
        assertTrue(thread.isDisconnected());
    }
}