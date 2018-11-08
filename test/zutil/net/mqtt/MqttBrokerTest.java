package zutil.net.mqtt;

import org.junit.Test;
import zutil.net.mqtt.MqttBroker.MqttConnectionThread;
import zutil.net.mqtt.packet.MqttPacketDisconnect;
import zutil.net.mqtt.packet.MqttPacketHeader;
import zutil.net.mqtt.packet.MqttPacketPingReq;
import zutil.net.mqtt.packet.MqttPacketPingResp;

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
    public void ping() throws IOException {
        MqttConnectionMockThread thread = new MqttConnectionMockThread();
        MqttPacketPingReq pingPacket = new MqttPacketPingReq();

        assertEquals(MqttPacketPingResp.class, thread.handlePacket(pingPacket).getClass());
    }

    @Test
    public void disconnect() throws IOException {
        MqttConnectionMockThread thread = new MqttConnectionMockThread();
        MqttPacketDisconnect disconnectPacket = new MqttPacketDisconnect();

        assertEquals(null, thread.handlePacket(disconnectPacket));
        assertTrue(thread.isShutdown());
    }
}