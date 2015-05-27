/*
 * Copyright (c) 2015 ezivkoc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package zutil.log;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;

import zutil.io.StringOutputStream;

public class CompactLogFormatter extends Formatter{
	// The split pattern where the 
	private static final Pattern splitter = Pattern.compile("\n");
	// the stream should print time stamp
	private boolean timeStamp = true;
	//The time stamp style
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	// If displaying class names are enabled
	private boolean className = true;
	// If displaying method names are enabled
	private boolean methodName = false;
	// Specifies the max length of the longest class name
	private int max_class_name = 0;
	// Cache for the class padding
	private static HashMap<String,String> padd_cache = new HashMap<String,String>();
	// Date temp file
	private Date date = new Date();

	@Override 
	public String format(LogRecord record) {
		StringBuilder data = new StringBuilder();

		if( timeStamp ){
			date.setTime( record.getMillis() );
			data.append( dateFormatter.format(date) );
			data.append(' ');
		}

		switch( record.getLevel().intValue() ){
		case /* SEVERE  */	1000: data.append("[SEVERE] "); break;
		case /* WARNING */	900 : data.append("[WARNING]"); break;
		case /* INFO    */	800 : data.append("[INFO]   "); break;
		case /* CONFIG  */	700 : data.append("[CONFIG] "); break;		
		case /* FINE    */	500 : data.append("[FINE]   "); break;
		case /* FINER   */	400 : data.append("[FINER]  "); break;
		case /* FINEST  */	300 : data.append("[FINEST] "); break;
		}
		data.append(' ');

		if( className ){
			data.append( paddClassName(record.getSourceClassName()) );
		}
		if(methodName){
			data.append( record.getSourceMethodName() );
		}
		data.append( ": " );

		StringBuffer ret = new StringBuffer();
		if( record.getMessage() != null ){
			String[] array = splitter.split( record.getMessage() );
			for( int i=0; i<array.length ;++i ){
				if( i!=0 )
					ret.append( '\n' );
				if( data.length() > 0 )
					ret.append( data );
				ret.append( array[i] );
			}
			ret.append( '\n' );
		}

		if( record.getThrown() != null ){
			StringOutputStream out = new StringOutputStream();
			record.getThrown().printStackTrace(new PrintStream(out));
			String[] array = splitter.split( out.toString() );
			for( int i=0; i<array.length ;++i ){
				if( i!=0 )
					ret.append( '\n' );
				if( data.length() > 0 )
					ret.append( data );
				ret.append( array[i] );
			}
			ret.append( '\n' );
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
		dateFormatter = new SimpleDateFormat(ts);
	}

	/**
	 * If the formatter should add the class/source name in front of the log message
	 * 
	 * @param enable set to True to activate class/source name
	 */
	public void enableClassName(boolean enable){
		className = enable;
	}

	/**
	 * If the formatter should add the class/source name in front of the log message
	 * 
	 * @param enable set to True to activate class/source name
	 */
	public void enableMethodName(boolean enable){
		methodName = enable;
	}

	/**
	 * @return the Class name
	 */
	private String paddClassName(String source){
		String tmp = padd_cache.get(source);
		if( tmp != null )
			return tmp;

		String c_name = source.substring( source.lastIndexOf('.')+1 );
		if( c_name.length() > max_class_name )
			max_class_name = c_name.length();

		StringBuilder sb = new StringBuilder( c_name );
		for( int i=c_name.length(); i<max_class_name; ++i ) {
			sb.append( ' ' ); 
		}
		tmp = sb.toString();
		padd_cache.put(source, tmp);
		return tmp;

	}

}
