package zutil.test;

import java.io.IOException;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;

import zutil.Encrypter;
import zutil.net.nio.NioClient;
import zutil.net.nio.message.StringMessage;
import zutil.net.nio.response.PrintRsp;


@SuppressWarnings("unused")
public class NetworkClientTest {
	public static void main(String[] args) throws NoSuchAlgorithmException {
		try {
			int count = 0;
			long time = System.currentTimeMillis()+1000*60;
			NioClient client = new NioClient(InetAddress.getByName("localhost"), 6056);
			//client.setEncrypter(new Encrypter("lol", Encrypter.PASSPHRASE_DES_ALGO));
			while(time > System.currentTimeMillis()){
				PrintRsp handler = new PrintRsp();			
				client.send(handler, new StringMessage("StringMessage: "+count));
				handler.waitForResponse();
				//try {Thread.sleep(100);} catch (InterruptedException e) {}
				//System.out.println("sending..");
				count++;
			}
			
			System.out.println("Message Count 1m: "+count);
			System.out.println("Message Count 1s: "+count/60);
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
