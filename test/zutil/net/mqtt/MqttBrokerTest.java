package zutil.net.mqtt;

import org.junit.Test;
import zutil.net.mqtt.MqttBroker.MqttConnectionThread;
import zutil.net.mqtt.packet.MqttPacketDisconnect;
import zutil.net.mqtt.packet.MqttPacketPingReq;
import zutil.net.mqtt.packet.MqttPacketPingResp;

import java.io.IOException;

import static org.junit.Assert.*;

public class MqttBrokerTest {


    @Test
    public void ping() throws IOException {
        MqttConnectionThread thread = new MqttConnectionThread();
        MqttPacketPingReq pingPacket = new MqttPacketPingReq();

        assertEquals(MqttPacketPingResp.class, thread.handleMqttPacket(pingPacket).getClass());
    }

    @Test
    public void disconnect() throws IOException {
        MqttConnectionThread thread = new MqttConnectionThread();
        MqttPacketDisconnect disconnectPacket = new MqttPacketDisconnect();

        assertEquals(null, thread.handleMqttPacket(disconnectPacket));
        assertTrue(thread.isShutdown());
    }
}