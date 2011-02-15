package zutil.test;

import zutil.net.UpdateServer;

public class UpdateServerTest {
	public static void main(String[] args){
		try {
			new UpdateServer(2000, "server");			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
