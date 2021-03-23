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

import zutil.io.MultiPrintStream;
import zutil.log.LogUtil;
import zutil.net.dns.packet.DnsConstants;
import zutil.net.dns.packet.DnsPacket;
import zutil.net.dns.packet.DnsPacketQuestion;
import zutil.net.threaded.ThreadedUDPNetwork;
import zutil.net.threaded.ThreadedUDPNetworkThread;
import zutil.parser.binary.BinaryStructInputStream;
import zutil.parser.binary.BinaryStructOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import static zutil.net.dns.MulticastDnsServer.MDNS_MULTICAST_ADDR;
import static zutil.net.dns.MulticastDnsServer.MDNS_MULTICAST_PORT;

/**
 * This class implements a MDNS Client. MDNS is a version
 * of the DNS protocol but supports Zeroconf.
 *
 * @see <a href="http://tools.ietf.org/html/rfc1035">DNS Spec (rfc1035)</a>
 * @see <a href="https://tools.ietf.org/html/rfc6763">DNS-SD Spec (rfc6763)</a>
 * @author Ziver
 */
public class MulticastDnsClient extends ThreadedUDPNetwork implements ThreadedUDPNetworkThread{
    private static final Logger logger = LogUtil.getLogger();


    private HashSet<Integer> activeProbes;
    private DnsResolutionListener listener;


    public MulticastDnsClient() throws IOException {
        super(MDNS_MULTICAST_ADDR, MDNS_MULTICAST_PORT);
        setThread(this);

        this.activeProbes = new HashSet<>();
    }

    public void setListener(DnsResolutionListener listener) {
        this.listener = listener;
    }


    public void sendProbe(String domain) throws IOException {
        int id = 0; // Needs to be zero when doing multicast
        activeProbes.add(id);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        BinaryStructOutputStream out = new BinaryStructOutputStream(buffer);

        DnsPacket dnsPacket = new DnsPacket();
        dnsPacket.getHeader().id = id;
        dnsPacket.getHeader().setDefaultQueryData();
        dnsPacket.addQuestion(new DnsPacketQuestion(
                domain,
                DnsConstants.TYPE.SRV,
                DnsConstants.CLASS.IN));
        dnsPacket.write(out);

        DatagramPacket udpPacket = new DatagramPacket(
                buffer.toByteArray(), buffer.size(),
                InetAddress.getByName(MDNS_MULTICAST_ADDR),
                MDNS_MULTICAST_PORT);

        logger.fine("Sending MDSN probe id: " + id + ", for domain: " + domain);
        //System.out.println("Sending:\n" +ByteUtil.toFormattedString(udpPacket.getData(), udpPacket.getOffset(), udpPacket.getLength()));
        //MultiPrintStream.out.dump(dnsPacket,3);

        send(udpPacket);
    }

    @Override
    public void receivedPacket(DatagramPacket packet, ThreadedUDPNetwork network) {
        try {
            ByteArrayInputStream buffer = new ByteArrayInputStream(packet.getData(),
                    packet.getOffset(), packet.getLength());
            BinaryStructInputStream in = new BinaryStructInputStream(buffer);
            DnsPacket dnsPacket = DnsPacket.read(in);

            //System.out.println("Received:\n" +ByteUtil.toFormattedString(packet.getData(), packet.getOffset(), packet.getLength()));
            MultiPrintStream.out.dump(dnsPacket,3);

            if (dnsPacket.getHeader().flagQueryResponse) {
                if (activeProbes.contains(dnsPacket.getHeader().id)) {
                    logger.fine("Received MDNS response from: " + packet.getAddress() + ", msg id: " + dnsPacket.getHeader().id);
                    if (listener != null)
                        listener.receivedResponse(dnsPacket);
                } else {
                    logger.fine("Received MDNS packet: " + packet.getAddress() + ", msg id: " + dnsPacket.getHeader().id);
                }
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, null, e);
        }
    }

}
