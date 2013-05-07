package zutil.test;

import java.util.logging.Level;
import java.util.logging.Logger;

import zutil.log.LogUtil;
import zutil.log.net.NetLogServer;

public class NetLogServerTest {
	public static final Logger logger = LogUtil.getLogger();

	public static void main(String[] args){
		LogUtil.setGlobalLevel(Level.FINEST);
		LogUtil.addGlobalHandler(new NetLogServer(5050));
		
		while(true){
			logger.log(Level.SEVERE,  "Test Severe");
			logger.log(Level.WARNING, "Test Warning");
			logger.log(Level.INFO,    "Test Info");
			logger.log(Level.FINE,    "Test Fine");
			logger.log(Level.FINER,   "Test Finer");
			logger.log(Level.FINEST,  "Test Finest");
			
			logger.log(Level.SEVERE,  "Test Exception", new Exception("Test"));
			
			try{Thread.sleep(3000);}catch(Exception e){}
		}
	}
}
