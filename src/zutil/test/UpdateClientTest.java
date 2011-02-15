package zutil.test;

import zutil.ProgressListener;
import zutil.net.UpdateClient;
import zutil.net.Zupdater;

public class UpdateClientTest implements ProgressListener{
	public static void main(String[] args){
		UpdateClientTest client = new UpdateClientTest();
		client.start();
	}
	
	public void start(){
		try {
			UpdateClient client = new UpdateClient("localhost", 2000, "client");
			client.setProgressListener(new Zupdater());
			//client.setProgressListener(this);
			client.update();
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void progressUpdate(Object source, Object info, double percent) {
		System.out.println(info+": "+percent+"%");		
	}
}
