package zutil.test;

import java.util.logging.Level;

import zutil.log.CompactLogFormatter;
import zutil.log.LogUtil;
import zutil.net.update.UpdateServer;

public class UpdateServerTest {
	public static void main(String[] args){
		try {
			LogUtil.setGlobalLevel(Level.FINEST);
			LogUtil.setGlobalFormatter(new CompactLogFormatter());
			
			new UpdateServer(2000, "C:\\Users\\Ziver\\Desktop\\server");			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
