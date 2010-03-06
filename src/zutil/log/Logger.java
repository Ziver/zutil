package zutil.log;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.logging.Level;

import zutil.MultiPrintStream;

/**
 * This is a logger class
 * 
 * @author Ziver
 *
 */
public class Logger {	
	// This is the global log level
	protected static Level global_log_level;
	// The OutputStream
	protected static PrintStream out;
	// The formatter class that formats the output
	protected static LogFormatter formatter;
	// Class Specific log level
	protected static HashMap<String, Level> class_log_level;

	// The class that is logging
	private String source;

	/**
	 * Creates an Logger instance that is tied to the calling class
	 */
	public Logger(){
		this( getCalingClass() );
	}

	/**
	 * Creates an Logger instance that is tied to the a specific class
	 * 
	 * @param logging_class is the class that will be displayed as the logger
	 */
	public Logger(Class<?> logging_class){
		this(logging_class.getSimpleName());
	}
	
	/**
	 * Creates an Logger instance that is tied to the a specific source
	 * 
	 * @param source is the string that will be displayed as the logger
	 */
	public Logger(String source){
		this.source = source;

		if(global_log_level == null)
			global_log_level = Level.ALL;
		if(out == null)
			out = MultiPrintStream.out;
		if(formatter == null)
			formatter = new StandardLogFormatter();
		if(class_log_level == null)
			class_log_level = new HashMap<String, Level>();
	}

	/**
	 * @return the parent class other than Logger in the stack
	 */
	protected static String getCalingClass(){
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		for(int i=1; i<stackTraceElements.length ;++i){
			String name = stackTraceElements[i].getClassName();
			name = name.substring( name.lastIndexOf('.')+1 );
			if( !name.equals( Logger.class.getSimpleName() ) )
				return name;
		}
		return null;
	}

	/**
	 * @param formater is the LogFormater that will be used
	 */
	public void setFormater(LogFormatter formater){
		Logger.formatter = formater;
	}

	/**
	 * @param out is the PrintStream that the logs will be sent to
	 */
	public void setPrintStream(PrintStream out){
		Logger.out = out;
	}

	/**
	 * Logs an message based on level
	 * 
	 * @param level is the level of the message
	 * @param msg is the message to log
	 */
	public void log(Level level, String msg){
		log(level, source, msg);
	}

	/**
	 * Logs an message based on level
	 * 
	 * @param level is the level of the message
	 * @param source is the source class
	 * @param msg is the message to log
	 */
	public static void log(Level level, Class<?> source, String msg){
		log(level, source.getSimpleName(), msg);
	}
	
	/**
	 * Logs an message based on level
	 * 
	 * @param level is the level of the message
	 * @param source is the source
	 * @param msg is the message to log
	 */
	public static void log(Level level, String source, String msg){
		// Check if message should be logged
		if( class_log_level.containsKey(source) ){
			if( class_log_level.get(source).intValue() > level.intValue() )
				return;
		}
		else if( global_log_level.intValue() > level.intValue() )
			return;

		// This message should be logged
		if( formatter != null )
			msg = formatter.format(source, level, msg);

		if(out != null)
			out.println(msg);
		else
			MultiPrintStream.out.println(msg);
	}
}
