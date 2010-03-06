package zutil.log;

import java.util.logging.Level;

public interface LogFormatter {
	
	/**
	 * This method formats a log message in a specific way
	 * 
	 * @param source is the class that sent the  log
	 * @param level is the severity of the log message
 	 * @param msg is the log message
	 */
	public String format( String source, Level level, String msg);
}
