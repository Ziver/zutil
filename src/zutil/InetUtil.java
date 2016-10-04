package zutil;

import zutil.converter.Converter;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * This class contains IP and MAC utility methods.
 *
 * Created by Ziver on 2016-10-04.
 */
public class InetUtil {

    /**
     * @return a list of IPv4 addresses for the all local network cards
     */
    public static List<InetAddress> getLocalInet4Address(){
        ArrayList<InetAddress> ips = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> netIntf = NetworkInterface.getNetworkInterfaces();
            while(netIntf.hasMoreElements()){
                Enumeration<InetAddress> addresses = netIntf.nextElement().getInetAddresses();
                while (addresses.hasMoreElements()){
                    InetAddress ip = addresses.nextElement();
                    if (ip instanceof Inet4Address && ip.isSiteLocalAddress())
                        ips.add(ip);
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ips;
    }


    /**
     * @return the String representation of the given MAC address
     */
    public static String getHardwareAddressInString(byte[] address) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < address.length; i++) {
            if (i != 0)
                str.append(':');
            str.append(Converter.toHexString(address[i]));
        }
        return str.toString();
    }
}
