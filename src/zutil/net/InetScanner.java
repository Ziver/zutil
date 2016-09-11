package zutil.net;

import zutil.osal.MultiCommandExecutor;
import zutil.osal.OSAbstractionLayer;

import java.io.*;

/**
 * Created by Ziver on 2016-09-11.
 */
public class InetScanner {
    private static final int TIMEOUT_MS = 50;


    public static void main(String[] args){
        //scan();
        scan2();
    }


    public static void scan(){
        for (int i = 1; i < 255; i++) {
            String ip = "192.168.1."+i;
            System.out.println(ip+": "+isReachableByPing(ip));
        }
    }
    public static boolean isReachableByPing(String host) {
        try{
            String[] output = OSAbstractionLayer.exec(getPlatformPingCmd(host));
            if (output[2].contains("TTL=") || output[2].contains("ttl="))
                return true;

        } catch( Exception e ) {
            e.printStackTrace();
        }
        return false;
    }


    public static void scan2(){
        try{
            MultiCommandExecutor exec = new MultiCommandExecutor();
            // execute the desired command (here: ls) n times
            for (int i = 1; i < 255; i++) {
                try {
                    String ip = "192.168.1."+i;
                    exec.exec(getPlatformPingCmd(ip));

                    System.out.print(ip+": ");
                    boolean online = false;
                    for (String line; (line=exec.readLine()) != null;) {
                        if (line.contains("TTL=") || line.contains("ttl="))
                            online = true;
                    }
                    System.out.println(online);
                }
                catch (IOException e) {
                    System.out.println(e);
                }
            }

            exec.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getPlatformPingCmd(String ip){
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
}
