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
