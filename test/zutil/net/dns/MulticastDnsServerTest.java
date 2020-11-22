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