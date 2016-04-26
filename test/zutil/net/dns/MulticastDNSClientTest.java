package zutil.net.dns;

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
        mdns.sendProbe("apple.local");
    }
}
