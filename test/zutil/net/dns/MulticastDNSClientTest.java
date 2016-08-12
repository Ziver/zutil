package zutil.net.dns;

import zutil.io.MultiPrintStream;

import java.io.IOException;

/**
 * Test class for MulticastDNSClient.
 *
 * Created by Ziver on 2016-04-26.
 */
public class MulticastDNSClientTest {

    public static void main(String[]  args) throws IOException {
        MulticastDNSClient mdns = new MulticastDNSClient();
        mdns.start();
        //mdns.sendProbe("appletv.local");
        mdns.sendProbe("_services._dns-sd._udp.local");
        //mdns.sendProbe("_googlecast._tcp.local");
        mdns.setListener(new DNSResolutionListener() {
            @Override
            public void receivedResponse(DNSPacket packet) {
                System.out.println("####### Received:");
                MultiPrintStream.out.dump(packet,3);
            }
        });
    }
}
