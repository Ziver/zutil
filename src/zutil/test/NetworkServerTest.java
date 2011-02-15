package zutil.test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import zutil.Encrypter;
import zutil.net.nio.NioServer;


@SuppressWarnings("unused")
public class NetworkServerTest {
	public static void main(String[] args) throws NoSuchAlgorithmException {
		try {
			NioServer server = new NioServer(6056);
			//server.setEncrypter(new Encrypter("lol", Encrypter.PASSPHRASE_DES_ALGO));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
