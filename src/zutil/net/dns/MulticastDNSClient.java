/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Ziver Koc
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

import zutil.ByteUtil;
import zutil.converter.Converter;
import zutil.io.MultiPrintStream;
import zutil.log.LogUtil;
import zutil.net.threaded.ThreadedUDPNetwork;
import zutil.net.threaded.ThreadedUDPNetworkThread;
import zutil.parser.binary.BinaryStructInputStream;
import zutil.parser.binary.BinaryStructOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements a MDNS Client. MDNS is a version
 * of the DNS protocol but used a Zeroconf application.
 *
 * Created by Ziver
 */
public class MulticastDNSClient extends ThreadedUDPNetwork implements ThreadedUDPNetworkThread{
    private static final Logger logger = LogUtil.getLogger();

    private static final String MDNS_MULTICAST_ADDR = "224.0.0.251";
    private static final int    MDNS_MULTICAST_PORT = 5353;


    public MulticastDNSClient() throws IOException {
        super(null, MDNS_MULTICAST_ADDR, MDNS_MULTICAST_PORT);
        setThread( this );
    }


    public void sendProbe(String service) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        BinaryStructOutputStream out = new BinaryStructOutputStream(buffer);

        DNSPacket dnsPacket = new DNSPacket();
        dnsPacket.getHeader().setDefaultQueryData();
        dnsPacket.addQuestion(new DNSPacketQuestion(
                service,
                DNSPacketQuestion.QTYPE_A,
                DNSPacketQuestion.QCLASS_IN));
        dnsPacket.write(out);

        DatagramPacket udpPacket = new DatagramPacket(
                buffer.toByteArray(), buffer.size(),
                InetAddress.getByName( MDNS_MULTICAST_ADDR ),
                MDNS_MULTICAST_PORT );

        System.out.println("#### Sending Request");
        System.out.println(ByteUtil.toFormattedString(udpPacket.getData()));
        MultiPrintStream.out.dump(dnsPacket,3);

        //send(udpPacket);
    }

    @Override
    public void receivedPacket(DatagramPacket packet, ThreadedUDPNetwork network) {
        try {
            ByteArrayInputStream buffer = new ByteArrayInputStream(packet.getData(),
                    packet.getOffset(), packet.getLength());
            BinaryStructInputStream in = new BinaryStructInputStream(buffer);
            DNSPacket dnsPacket = DNSPacket.read(in);
            System.out.println("#### Received response from "+packet.getAddress());
            System.out.println(ByteUtil.toFormattedString(packet.getData()));
            MultiPrintStream.out.dump(dnsPacket,3);
        } catch (IOException e){
            logger.log(Level.WARNING, null, e);
        }
    }
}
