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

package zutil.osal.linux.app;

import zutil.Timer;
import zutil.converter.Converter;
import zutil.log.LogUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Makes the Linux ARP cache available as Java api
 * <p>
 * Created by Ziver on 2015-05-19.
 */
public class ProcNetArp {
    private static final Logger logger = LogUtil.getLogger();
    private static final String PROC_PATH = "/proc/net/arp";
    private static final int TTL = 500; // update stats every 0.5 second

    private static HashMap<InetAddress, ArpEntry> arpTable = new HashMap<>();
    private static Timer updateTimer = new Timer(TTL);


    private synchronized static void update() {
        if (!updateTimer.hasTimedOut())
            return;

        try {
            BufferedReader in = new BufferedReader(new FileReader(PROC_PATH));
            parse(in);
            in.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, null, e);
        }
    }
    protected static void parse(BufferedReader in) throws IOException {
        updateTimer.start();
        String line;
        in.readLine(); // Skipp headers
        while ((line = in.readLine()) != null) {
            String[] str = line.split("\\s+");

            if (str.length >= 6) {
                InetAddress ip = InetAddress.getByName(str[0]);
                if (!arpTable.containsKey(ip))
                    arpTable.put(ip, new ArpEntry());
                ArpEntry entry = arpTable.get(ip);
                entry.ip = ip;
                entry.hwAddress = Converter.hexToByte(str[3].replaceAll(":", ""));
                entry.netIf = str[5];
            } else
                logger.warning(PROC_PATH + " contains unrecognized format");
        }
    }


    public static Collection<ArpEntry> getAllArpEntries() {
        update();
        return arpTable.values();
    }

    public static ArpEntry getArpEntry(InetAddress ip) {
        update();
        return arpTable.get(ip);
    }


    public static class ArpEntry {
        private InetAddress ip;
        private byte[] hwAddress;
        private String netIf;


        public InetAddress getIp() {
            return ip;
        }

        public byte[] getHwAddress() {
            return hwAddress;
        }

        public String getHwAddressString() {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < hwAddress.length; i++) {
                if (i != 0)
                    str.append(':');
                str.append(Converter.toHexString(hwAddress[i]));
            }
            return str.toString();
        }

        public String getNetworkInterface() {
            return netIf;
        }
    }


    public static void main(String[] args) {
        for (ArpEntry entry : getAllArpEntries()) {
            System.out.println(entry.getIp().getHostAddress() + " => " + entry.getHwAddressString());
        }
    }
}
