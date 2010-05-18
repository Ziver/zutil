package zutil.log;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility functions for the standard Java Logger
 * 
 * @author Ziver
 */
public class LogUtil {
	private LogUtil(){}
	
	/**
	 * @return a new Logger for the calling class
	 */
	public static Logger getLogger(){
		return Logger.getLogger(getCalingClass());
	}
	
	/**
	 * @return the parent class other than Logger in the stack
	 */
	public static String getCalingClass(){
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		for(int i=1; i<stackTraceElements.length ;++i){
			String name = stackTraceElements[i].getClassName();
			//name = name.substring( name.lastIndexOf('.')+1 );
			if( !name.equals( LogUtil.class.getName() ) )
				return name;
		}
		return null;
	}
	
	/**
	 * Sets the global log formatter to the specified one
	 * 
	 * @param f is the formatter class
	 */
	public static void setGlobalFormatter(Formatter f){
		Logger root = Logger.getLogger("");
		for (Handler handler : root.getHandlers()) {
			handler.setFormatter(f);
	    }
	}
	
	/**
	 * Sets the global log level
	 */
	public static void setGlobalLogLevel(Level level){
		setLevel("", level);
	}
	
	/**
	 * Sets the log level for a specified logger
	 */
	public static void setLevel(String name, Level level){
		Logger root = Logger.getLogger("");
		root.setLevel(level);
		for (Handler handler : root.getHandlers()) {
			handler.setLevel(level);
	    }
	}
}
