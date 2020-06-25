package zutil.net.dns;

import org.junit.Test;
import zutil.net.dns.packet.DnsConstants;
import zutil.net.dns.packet.DnsPacket;
import zutil.net.dns.packet.DnsPacketQuestion;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.*;

/**
 *
 */
public class MulticastDnsServerTest {

    private MulticastDnsServer server = new MulticastDnsServer();
    public MulticastDnsServerTest() throws IOException {}


    @Test
    public void domainLookupNoEntries() throws UnknownHostException {
        DnsPacket request = creatRequestDnsPacket(
                "example.com",
                DnsConstants.TYPE.A);

        DnsPacket response = server.handleReceivedPacket(InetAddress.getLocalHost(), request);
        assertNull(response);
    }


    @Test
    public void domainLookup() throws UnknownHostException {
        DnsPacket request = creatRequestDnsPacket(
                "example.com",
                DnsConstants.TYPE.A);

        server.addEntry("example.com", InetAddress.getLocalHost());
        DnsPacket response = server.handleReceivedPacket(InetAddress.getLocalHost(), request);
        assertNotNull(response);
        assertEquals("example.com", response.getAnswerRecords().get(0).name);
    }


    @Test
    public void serviceDiscoveryNoEntries() throws UnknownHostException {
        DnsPacket request = creatRequestDnsPacket(
                "_service._tcp.local",
                DnsConstants.TYPE.PTR);

        DnsPacket response = server.handleReceivedPacket(InetAddress.getLocalHost(), request);
        assertNull(response);
    }


    @Test
    public void serviceDiscovery() throws UnknownHostException {
        DnsPacket request = creatRequestDnsPacket(
                "_service._tcp.local",
                DnsConstants.TYPE.PTR);

        server.addEntry("_http._tcp.local", InetAddress.getLocalHost());
        DnsPacket response = server.handleReceivedPacket(InetAddress.getLocalHost(), request);
        assertNotNull(response);
    }


    private static DnsPacket creatRequestDnsPacket(String domain, int type){
        DnsPacket request = new DnsPacket();
        request.getHeader().setDefaultQueryData();
        DnsPacketQuestion question = new DnsPacketQuestion();
        question.name = domain;
        question.type = type;
        question.clazz = DnsConstants.CLASS.IN;
        request.addQuestion(question);
        return request;
    }
}