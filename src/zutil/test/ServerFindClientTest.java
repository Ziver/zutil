package zutil.test;

import java.io.IOException;

import zutil.network.ServerFindClient;

public class ServerFindClientTest {
	public static void main(String[] args){
		try {
			ServerFindClient client = new ServerFindClient(2000);
			System.out.println(client.find().getHostAddress());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
