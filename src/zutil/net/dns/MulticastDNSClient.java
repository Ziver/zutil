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

import zutil.net.threaded.ThreadedUDPNetwork;
import zutil.net.threaded.ThreadedUDPNetworkThread;
import zutil.parser.binary.BinaryStructInputStream;
import zutil.parser.binary.BinaryStructOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Ziver
 */
public class MulticastDNSClient extends ThreadedUDPNetwork implements ThreadedUDPNetworkThread{
    private static final String MDNS_MULTICAST_ADDR = "224.0.0.251";
    private static final int    MDNS_MULTICAST_PORT = 5353;


    public MulticastDNSClient() throws IOException {
        super(null, MDNS_MULTICAST_ADDR, MDNS_MULTICAST_PORT);
        setThread( this );
    }


    public void sendProbe() {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            BinaryStructOutputStream out = new BinaryStructOutputStream(buffer);

            DNSPacket header = new DNSPacket();
            out.write(header);

            DatagramPacket packet = null;

            packet = new DatagramPacket(
                    buffer.toByteArray(), buffer.size(),
                    InetAddress.getByName( MDNS_MULTICAST_ADDR ),
                    MDNS_MULTICAST_PORT );
            send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receivedPacket(DatagramPacket packet, ThreadedUDPNetwork network) {
        DNSPacket header = new DNSPacket();
        BinaryStructInputStream.read(header, packet.getData());
    }
}
