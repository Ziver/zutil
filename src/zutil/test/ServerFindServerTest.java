package zutil.test;

import java.io.IOException;

import zutil.net.ServerFind;

public class ServerFindServerTest {
	public static void main(String[] args){
		try {
			new ServerFind(2000);			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
