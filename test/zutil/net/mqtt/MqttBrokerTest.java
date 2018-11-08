package zutil.net.mqtt;

import org.junit.Test;
import zutil.net.mqtt.MqttBroker.MqttConnectionThread;
import zutil.net.mqtt.packet.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import static org.junit.Assert.*;

public class MqttBrokerTest {

    public static class MqttConnectionMockThread extends MqttConnectionThread {
        public LinkedList<MqttPacketHeader> sentPackets = new LinkedList<>();

        @Override
        public void sendPacket(MqttPacketHeader packet){
            sentPackets.add(packet);
        }
    }

    @Test
    public void subscribeEmpty() throws IOException {
        MqttConnectionMockThread thread = new MqttConnectionMockThread();
        MqttPacketSubscribe subscribePacket = new MqttPacketSubscribe();
        subscribePacket.packetId = (int)(Math.random()*1000);

        thread.handlePacket(subscribePacket);

        MqttPacketHeader responsePacket = thread.sentPackets.poll();
        assertEquals(MqttPacketSubscribeAck.class, responsePacket.getClass());
        assertEquals(subscribePacket.packetId, ((MqttPacketSubscribeAck)responsePacket).packetId);
    }

    @Test
    public void unsubscribe() throws IOException {
        MqttConnectionMockThread thread = new MqttConnectionMockThread();
        MqttPacketUnsubscribe unsubscribePacket = new MqttPacketUnsubscribe();
        unsubscribePacket.packetId = (int)(Math.random()*1000);

        thread.handlePacket(unsubscribePacket);

        MqttPacketHeader responsePacket = thread.sentPackets.poll();
        assertEquals(MqttPacketUnsubscribeAck.class, responsePacket.getClass());
        assertEquals(unsubscribePacket.packetId, ((MqttPacketUnsubscribeAck)responsePacket).packetId);
    }

    @Test
    public void ping() throws IOException {
        MqttConnectionMockThread thread = new MqttConnectionMockThread();
        MqttPacketPingReq pingPacket = new MqttPacketPingReq();

        thread.handlePacket(pingPacket);

        assertEquals(MqttPacketPingResp.class, thread.sentPackets.poll().getClass());
    }

    @Test
    public void disconnect() throws IOException {
        MqttConnectionMockThread thread = new MqttConnectionMockThread();
        MqttPacketDisconnect disconnectPacket = new MqttPacketDisconnect();

        thread.handlePacket(disconnectPacket);

        assertEquals(null, thread.sentPackets.poll());
        assertTrue(thread.isShutdown());
    }
}