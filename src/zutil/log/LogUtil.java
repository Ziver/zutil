package zutil.log;

import java.util.logging.ConsoleHandler;
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
	private static final Logger logger = Logger.getLogger( LogUtil.class.getName() );
	
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
			if( !name.equals( LogUtil.class.getName() ) ){
				//System.out.println("\""+name+"\"");
				logger.fine("Caling class: \""+name+"\"");
				return name;
			}
		}
		return null;
	}

	/**
	 * Sets the global log formatter to the specified one
	 * 
	 * @param 		f 		is the formatter class
	 */
	public static void setGlobalFormatter(Formatter f){
		Logger root = Logger.getLogger("");
		for (Handler handler : root.getHandlers()) {
			handler.setFormatter(f);
		}
	}
	
	/**
	 * Adds the log formatter
	 * 
	 * @param 		f 		is the formatter class
	 */
	public static void setFormatter(String name, Formatter f){
		Logger root = Logger.getLogger(name);
		for (Handler handler : root.getHandlers()) {
			handler.setFormatter(f);
		}
	}

	/**
	 * Sets the global log level
	 */
	public static void setGlobalLevel(Level level){
		setLevel("", level);
	}

	/**
	 * Sets the log level for a specified class
	 */
	public static void setLevel(Class<?> c, Level level){
		setLevel(c.getName(), level);
	}

	/**
	 * Sets the log level for a specified logger
	 */
	public static void setLevel(String name, Level level){
		logger.fine("Changing log level of \""+name+"\" to \""+level.getLocalizedName()+"\"");
		Logger logger = Logger.getLogger(name);
		logger.setLevel(level);
		// Check if the logger has a handler
		if( logger.getHandlers().length == 0 ){
			// Create a new console handler
			ConsoleHandler handler = new ConsoleHandler();
			handler.setLevel( level );
			logger.addHandler( handler );
			logger.setUseParentHandlers( false );
		}
		else{
			// Set the level on the handlers
			for (Handler handler : logger.getHandlers()) {
				handler.setLevel(level);
			}
		}
	}
}
