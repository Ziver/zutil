package zutil.net;


import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Ziver on 2016-09-19.
 */
public class InetScannerTest {

    public static void main(String[] args) throws UnknownHostException {
        InetScanner scanner = new InetScanner();
        scanner.setListener(new InetScanner.InetScanListener() {
            @Override
            public void foundInetAddress(InetAddress ip) {
                System.out.println(ip+": online");
            }
        });
        scanner.scan(InetAddress.getLocalHost());
        System.out.println("Done");
    }
}