package zutil.test;

import java.awt.EventQueue;
import java.util.logging.Level;

import zutil.ProgressListener;
import zutil.log.CompactLogFormatter;
import zutil.log.LogUtil;
import zutil.net.update.FileInfo;
import zutil.net.update.UpdateClient;
import zutil.net.update.Zupdater;

public class UpdateClientTest implements ProgressListener<UpdateClient, FileInfo>{
	public static void main(String[] args){
		LogUtil.setLevel("zutil", Level.FINEST);
		LogUtil.setFormatter("zutil", new CompactLogFormatter());
		
		UpdateClientTest client = new UpdateClientTest();
		client.start();
	}
	
	public void start(){
		try {
			final UpdateClient client = new UpdateClient("localhost", 2000, "C:\\Users\\Ziver\\Desktop\\client");
			client.setProgressListener(new Zupdater());
			
			//client.setProgressListener(this);
						
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						Zupdater gui = new Zupdater();
						client.setProgressListener(gui);
						gui.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			client.update();
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void progressUpdate(UpdateClient source, FileInfo info, double percent) {
		System.out.println(info+": "+percent+"%");		
	}
}
