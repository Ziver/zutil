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

import org.junit.Test;
import zutil.io.StringInputStream;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;

import static org.junit.Assert.assertEquals;

/**
 * Created by ezivkoc on 2016-09-30.
 */
public class ProcNetArpTest {
    @Test
    public void parse() throws Exception {
        StringInputStream buff = new StringInputStream(
                "IP address       HW type     Flags       HW address            Mask     Device\n" +
                "192.168.1.140    0x1         0x0         fc:f8:ae:3f:66:64     *        eth0\n" +
                "169.254.67.249   0x1         0x0         78:ab:bb:c4:05:b3     *        eth0\n" +
                "192.168.1.127    0x1         0x2         00:05:cd:39:aa:07     *        eth0\n" +
                "192.168.1.1      0x1         0x2         c0:56:27:c2:5e:82     *        eth0\n" +
                "192.168.1.190    0x1         0x0         fc:f8:ae:3f:66:64     *        eth0\n" +
                "192.168.1.253    0x1         0x0         00:00:00:00:00:00     *        eth0\n" +
                "192.168.1.105    0x1         0x2         24:0a:64:49:c9:7b     *        eth0\n" +
                "192.168.1.110    0x1         0x0         00:00:00:00:00:00     *        eth0\n" +
                "192.168.1.2      0x1         0x0         00:00:00:00:00:00     *        eth0\n" +
                "192.168.1.137    0x1         0x2         78:ab:bb:c4:05:b3     *        eth0\n" +
                "192.168.1.128    0x1         0x2         6c:ad:f8:be:ef:ba     *        eth0\n" +
                "192.168.1.30     0x1         0x0         00:00:00:00:00:00     *        eth0\n" +
                "192.168.1.12     0x1         0x0         00:00:00:00:00:00     *        eth0\n" +
                "192.168.1.178    0x1         0x2         6c:ad:f8:be:ef:ba     *        eth0\n");
        ProcNetArp.parse(new BufferedReader(new InputStreamReader(buff)));

        ProcNetArp.ArpEntry entry = ProcNetArp.getArpEntry(InetAddress.getByName("192.168.1.140"));
        assertEquals("192.168.1.140", entry.getIp().getHostAddress());
        assertEquals("fc:f8:ae:3f:66:64", entry.getHwAddressString());
        assertEquals("eth0", entry.getNetworkInterface());

        entry = ProcNetArp.getArpEntry(InetAddress.getByName("192.168.1.1"));
        assertEquals("192.168.1.1", entry.getIp().getHostAddress());
        assertEquals("c0:56:27:c2:5e:82", entry.getHwAddressString());
        assertEquals("eth0", entry.getNetworkInterface());

        entry = ProcNetArp.getArpEntry(InetAddress.getByName("192.168.1.178"));
        assertEquals("192.168.1.178", entry.getIp().getHostAddress());
        assertEquals("6c:ad:f8:be:ef:ba", entry.getHwAddressString());
        assertEquals("eth0", entry.getNetworkInterface());
    }

    @Test
    public void parseTwoTimes() throws Exception {
        parse();
        parse();
    }
}