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

package zutil.net;

import zutil.osal.MultiCommandExecutor;
import zutil.osal.OSAbstractionLayer;

import java.io.IOException;
import java.net.InetAddress;

/**
 * This class is a IPv4 scanner, it will scan a
 * range of IPs to check if they are available.
 * Note that this class uses the platform specific
 * ping executable to check for availability.
 */
public class InetScanner {
    private static final int TIMEOUT_SEC = 1; // 1 second

    private InetScanListener listener;
    private boolean canceled;


    public void setListener(InetScanListener listener) {
        this.listener = listener;
    }


    /**
     * Starts scanning a /24 ip range. This method will block until the scan is finished
     *
     * @param   ip      the network ip address
     */
    public synchronized void scan(InetAddress ip) {
        canceled = false;
        String netAddr = ip.getHostAddress().substring(0, ip.getHostAddress().lastIndexOf('.')+1);

        try (MultiCommandExecutor exec = new MultiCommandExecutor()) {
            for (int i = 1; i < 255 && !canceled; i++) {
                try {
                    String targetIp = netAddr + i;
                    boolean online = isReachable(targetIp, exec);
                    if (online && listener != null)
                        listener.foundInetAddress(InetAddress.getByName(targetIp));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Cancels the ongoing ip scan
     */
    public void cancel() {
        canceled = true;
    }


    /**
     * Will check if the given IP is reachable (Pingable)
     */
    public static boolean isReachable(String host) {
        String[] output = OSAbstractionLayer.exec(platformPingCmd(host));

        for (String line : output) {
            if (platformPingCheck(line))
                return true;
        }
        return false;
    }
    /**
     * Will check if the given IP is reachable (Pingable).
     * This method is faster if multiple pings are needed as a MultiCommandExecutor can be provided.
     */
    public static boolean isReachable(String host, MultiCommandExecutor exec) throws IOException {
        exec.exec(platformPingCmd(host));

        for (String line; (line=exec.readLine()) != null;) {
            if (platformPingCheck(line))
                return true;
        }
        return false;
    }


    private static String platformPingCmd(String ip) {
        switch (OSAbstractionLayer.getInstance().getOSType()) {
            case Windows:
                return "ping -n 1 -w " + (TIMEOUT_SEC*1000) + " " + ip;
            case Linux:
            case MacOS:
                return "ping -c 1 -W " + TIMEOUT_SEC + " " + ip;
            default:
                return null;
        }
    }
    private static boolean platformPingCheck(String line) {
        return line.contains("TTL=") || line.contains("ttl=");
    }


    public interface InetScanListener {
        void foundInetAddress(InetAddress ip);
    }

}
