package zutil.log;

import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class StandardLogFormatter implements LogFormatter{
	// The split pattern where the 
	private static final Pattern splitter = Pattern.compile("\n");
	// the stream should print time stamp
	private boolean timeStamp = false;
	//The time stamp style
	private String timeStampString = "yyyy-MM-dd HH:mm:ss:SSS#";
	// If displaying class names are enabled
	private boolean className = true;
	// Specifies the max length of the longest class name
	private int max_class_name = 0;

	
	public String format(String source, Level level, String msg) {
		StringBuilder data = new StringBuilder();
		
		switch(level.intValue()){
		case /* SEVERE  */	1000: data.append("SEVERE : "); break;
		case /* WARNING */	900 : data.append("WARNING: "); break;
		case /* INFO    */	800 : data.append("INFO   : "); break;
		case /* CONFIG  */	700 : data.append("CONFIG : "); break;		
		case /* FINE    */	500 : data.append("FINE   : "); break;
		case /* FINER   */	400 : data.append("FINER  : "); break;
		case /* FINEST  */	300 : data.append("FINEST : "); break;
		}
		
		if( timeStamp && className ){
			data.append( getTime() );
			data.append( " " );
			data.append( paddSourceName(source) );
			data.append( ": " );
		}
		else if( timeStamp ){
			data.append( getTime() );
			data.append( " " );	
		}
		else if( className ){
			data.append( paddSourceName(source) );
			data.append( ": " );
		}
		
		StringBuilder ret = new StringBuilder();
		String[] array = splitter.split( msg );
		for( int i=0; i<array.length ;++i ){
			if( i!=0 )
				ret.append( '\n' );
			if( data.length() > 0 )
				ret.append( data );
			ret.append( array[i] );
		}
		return ret.toString();
	}
	
	/**
	 * If the formatter should add a time stamp in front of the log message
	 * 
	 * @param enable set to True to activate time stamp
	 */
	public void enableTimeStamp(boolean enable){
		timeStamp = enable;
	}

	/**
	 * The DateFormat to print in the time stamp
	 * 
	 * @param ts is the String to send to SimpleDateFormat
	 */
	public void setTimeStamp(String ts){
		timeStampString = ts;
	}

	/**
	 * @return the time stamp
	 */
	private String getTime(){
		return (new SimpleDateFormat(timeStampString)).format(new java.util.Date()).toString();
	}
	
	/**
	 * If the formatter should add the class/source name in front of the log message
	 * 
	 * @param enable set to True to activate class/source name
	 */
	public void enableSourceName(boolean enable){
		className = enable;
	}
	
	/**
	 * @return the Class name
	 */
	private String paddSourceName(String source){
		if( source.length() > max_class_name )
			max_class_name = source.length();
		
		StringBuilder sb = new StringBuilder( source );
	    for( int i=source.length(); i<max_class_name; ++i ) {
	        sb.append( ' ' ); 
	    }
	    return sb.toString();

	}

}
