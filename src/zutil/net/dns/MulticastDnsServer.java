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

import zutil.log.LogUtil;
import zutil.net.dns.packet.DnsConstants;
import zutil.net.dns.packet.DnsPacket;
import zutil.net.dns.packet.DnsPacketQuestion;
import zutil.net.dns.packet.DnsPacketResource;
import zutil.net.threaded.ThreadedUDPNetwork;
import zutil.net.threaded.ThreadedUDPNetworkThread;
import zutil.parser.binary.BinaryStructInputStream;
import zutil.parser.binary.BinaryStructOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements a MDNS Server. MDNS is a version
 * of the DNS protocol but supports Zeroconf.
 *
 * @see <a href="http://tools.ietf.org/html/rfc1035">DNS Spec (rfc1035)</a>
 * @see <a href="https://tools.ietf.org/html/rfc6763">DNS-SD Spec (rfc6763)</a>
 * @author Ziver
 */
public class MulticastDnsServer extends ThreadedUDPNetwork implements ThreadedUDPNetworkThread{
    private static final Logger logger = LogUtil.getLogger();

    protected static final String MDNS_MULTICAST_ADDR = "224.0.0.251";
    protected static final int    MDNS_MULTICAST_PORT = 5353;


    private HashMap<String,ArrayList<DnsPacketResource>> entries = new HashMap<>();



    public MulticastDnsServer() throws IOException {
        super(MDNS_MULTICAST_ADDR, MDNS_MULTICAST_PORT);
        setThread( this );
    }


    /**
     * Add a domain name specific data that will be returned to a requesting client
     *
     * @param   name    is the domain name to add the entry under
     * @param   ip      the IPv4 address to respond with
     */
    public void addEntry(String name, InetAddress ip){
        addEntry(name, DnsConstants.TYPE.A, DnsConstants.CLASS.IN, ip.getAddress());
    }
    /**
     * Add a domain name specific data that will be returned to a requesting client
     *
     * @param   name    is the domain name to add the entry under
     * @param   type    {@link zutil.net.dns.packet.DnsConstants.TYPE}
     * @param   clazz   {@link zutil.net.dns.packet.DnsConstants.CLASS}
     */
    public void addEntry(String name, int type, int clazz, byte[] data){
        DnsPacketResource resource = new DnsPacketResource();
        resource.name = name;
        resource.type = type;
        resource.clazz = clazz;
        //resource.ttl = 10; ???
        resource.length = data.length;
        resource.data = new String(data, StandardCharsets.ISO_8859_1);

        addEntry(resource);
    }

    private void addEntry(DnsPacketResource resource) {
        if ( ! entries.containsKey(resource.name))
            entries.put(resource.name, new ArrayList<>());
        entries.get(resource.name).add(resource);
    }



    @Override
    public void receivedPacket(DatagramPacket packet, ThreadedUDPNetwork network) {
        try {
            ByteArrayInputStream buffer = new ByteArrayInputStream(packet.getData(),
                    packet.getOffset(), packet.getLength());
            BinaryStructInputStream in = new BinaryStructInputStream(buffer);
            DnsPacket dnsPacket = DnsPacket.read(in);

            // Just handle queries and no responses
            if ( ! dnsPacket.getHeader().flagQueryResponse){
                DnsPacket response = handleReceivedPacket(dnsPacket);
                if (response != null){
                    ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
                    BinaryStructOutputStream out = new BinaryStructOutputStream(outBuffer);
                    response.write(out);
                    out.close();

                    DatagramPacket outPacket = new DatagramPacket(
                            outBuffer.toByteArray(), outBuffer.size(),
                            InetAddress.getByName( MDNS_MULTICAST_ADDR ),
                            MDNS_MULTICAST_PORT );
                    send(outPacket);
                }
            }
        } catch (IOException e){
            logger.log(Level.WARNING, null, e);
        }
    }

    protected DnsPacket handleReceivedPacket(DnsPacket request){
        DnsPacket response = new DnsPacket();
        response.getHeader().setDefaultResponseData();
        for (DnsPacketQuestion question : request.getQuestions()){
            if (question.name == null) continue;
            switch (question.type){

                // Normal Domain Name Resolution
                case DnsConstants.TYPE.A:
                    if (entries.containsKey(question.name)){
                        response.addAnswerRecord(entries.get(question.name));
                    }
                    break;

                // Service Name Resolution
                case DnsConstants.TYPE.PTR:
                    if (question.name.startsWith("_service.")){
                        String postFix = question.name.substring(9);
                        for (String domain : entries.keySet()){
                            if (domain.endsWith(postFix))
                                response.addAnswerRecord(entries.get(domain));
                        }
                    } else if (entries.containsKey(question.name)){
                        response.addAnswerRecord(entries.get(question.name));
                    }
                    break;
            }
        }
        if (response.getAnswerRecords().isEmpty() &&
                response.getNameServers().isEmpty() &&
                response.getAdditionalRecords().isEmpty())
            return null;
        return response;
    }
}
