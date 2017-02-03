package zutil.net.dns;

import java.net.InetAddress;
import java.util.HashMap;

/**
 * This class contains data about a service that
 * can be looked up through DNS and MDNS.
 */
public class DnsService {

    private String name;
    private String dns;
    private InetAddress host;
    private HashMap properties;
}
