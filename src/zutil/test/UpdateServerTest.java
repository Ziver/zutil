package zutil.test;

import zutil.network.UpdateServer;

public class UpdateServerTest {
	public static void main(String[] args){
		try {
			new UpdateServer(2000, "server");			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
