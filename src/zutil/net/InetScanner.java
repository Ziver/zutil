package zutil.net;

import zutil.osal.MultiCommandExecutor;
import zutil.osal.OSAbstractionLayer;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;

/**
 * This class is a IPv4 scanner, it will scan a
 * range of IPs to check if they are available.
 * Note that this class uses the platform specific
 * ping executable to check for availability.
 */
public class InetScanner {
    private static final int TIMEOUT_MS = 50;

    private InetScanListener listener;
    private boolean canceled;


    public void setListener(InetScanListener listener){
        this.listener = listener;
    }


    /**
     * Starts scanning a /24 ip range. This method will block until the scan is finished
     *
     * @param   ip      the network ip address
     */
    public synchronized void scan(InetAddress ip){
        canceled = false;
        MultiCommandExecutor exec = new MultiCommandExecutor();
        String netAddr = ip.getHostAddress().substring(0, ip.getHostAddress().lastIndexOf('.')+1);

        try{
            for (int i = 1; i < 255 && !canceled; i++) {
                try {
                    String targetIp = netAddr+i;
                    exec.exec(platformPingCmd(targetIp));

                    boolean online = false;
                    for (String line; (line=exec.readLine()) != null;) {
                        if (platformPingCheck(line))
                            online = true;
                    }
                    if (online && listener != null)
                        listener.foundInetAddress(InetAddress.getByName(targetIp));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            exec.close();
        }
    }

    /**
     * Cancels the ongoing ip scan
     */
    public void cancel(){
        canceled = true;
    }


    /**
     * Will check if the given IP is reachable (Pingable)
     */
    public static boolean isReachable(InetAddress ip){
        String[] output = OSAbstractionLayer.exec(platformPingCmd(ip.getHostAddress()));

        boolean online = false;
        for (String line : output) {
            if (platformPingCheck(line))
                online = true;
        }
        return online;
    }


    private static String platformPingCmd(String ip){
        switch (OSAbstractionLayer.getInstance().getOSType()){
            case Windows:
                return "ping -n 1 -w "+ TIMEOUT_MS +" " + ip;
            case Linux:
            case MacOS:
                return "ping -c 1 -W "+ TIMEOUT_MS +" " + ip;
            default:
                return null;
        }
    }
    private static boolean platformPingCheck(String line){
        return line.contains("TTL=") || line.contains("ttl=");
    }



    public interface InetScanListener {
        void foundInetAddress(InetAddress ip);
    }
}
