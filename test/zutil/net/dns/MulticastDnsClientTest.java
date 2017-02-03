package zutil.net.dns;

import zutil.io.MultiPrintStream;
import zutil.log.CompactLogFormatter;
import zutil.log.LogUtil;
import zutil.net.dns.packet.DnsPacket;

import java.io.IOException;
import java.util.logging.Level;

/**
 * Test class for MulticastDnsClient.
 *
 * Created by Ziver on 2016-04-26.
 */
public class MulticastDnsClientTest {

    public static void main(String[]  args) throws IOException {
        LogUtil.setGlobalLevel(Level.ALL);
        LogUtil.setGlobalFormatter(new CompactLogFormatter());

        MulticastDnsClient mdns = new MulticastDnsClient();
        mdns.start();
        //mdns.sendProbe("appletv.local");
        //mdns.sendProbe("_services._dns-sd._udp.local");
        mdns.sendProbe("ziver-VirtualBox._afpovertcp._tcp.local");
        mdns.setListener(new DnsResolutionListener() {
            @Override
            public void receivedResponse(DnsPacket packet) {
                System.out.println("####### Received:");
                MultiPrintStream.out.dump(packet,3);
            }
        });
    }
}
