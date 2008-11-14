package zutil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Ziver
 * this class can print strings to multiple PrintStreams
 */
public class MultiPrintStream extends PrintStream {
	//the print streams that will print
	private ArrayList<PrintStream> streams;
	// the stream should print time stamp
	private boolean timeStamp = false;
	//The timestamp style
	private String timeStampString = "yyyy-MM-dd HH:mm:ss:SSS# ";
	//a instance of this class
	public static MultiPrintStream out = new MultiPrintStream();

	public MultiPrintStream(){
		super(new PrintStream(System.out));
		streams = new ArrayList<PrintStream>();
		streams.add(new PrintStream(System.out)); 
	}

	/**
	 * this constructor makes a simple PrintStream that prints to the console and to a file
	 * @param file the file name to output to
	 */
	public MultiPrintStream(String file){
		super(new PrintStream(System.out));
		try {
			streams = new ArrayList<PrintStream>();
			streams.add(new PrintStream(System.out));
			streams.add(new PrintStream(new File(file)));
		} catch (FileNotFoundException e) {
			System.out.println("Error when declaring PrintStream!!");
			e.printStackTrace();
		}	    
	}

	/**
	 * this constructor takes a array of PrintStreams to be used
	 * @param streams a array of the streams that will be used
	 */
	public MultiPrintStream(PrintStream[] streams){
		super(streams[0]);
		this.streams = new ArrayList<PrintStream>();
		for(int i=0; i<streams.length ;i++){
			this.streams.add(streams[i]);
		}
	}

	/**
	 * this constructor takes a array of PrintStreams to be used
	 * @param streams a array of the streams that will be used
	 */
	public static void makeInstance(MultiPrintStream instanceStream){
		out = instanceStream;
	}

	/**
	 * Adds a printstream to the list pf streams
	 * @param p The Printstream to add
	 */
	public void addPrintStream(PrintStream p){
		streams.add(p);
	}

	/**
	 * Remove a printstream from the list
	 * @param p The PrintStream to remove
	 */
	public void removePrintStream(PrintStream p){
		streams.remove(p);
	}

	/**
	 * Remove a printstream from the list
	 * @param p The index of the PrintStream to remove
	 */
	public void removePrintStream(int p){
		streams.remove(p);
	}

	/**
	 * prints whit a new line to all the PrintStreams
	 */
	public void println(String s){
		if(!s.equals(""))s = getTime() + s;
		for(int i=0; i<streams.size() ;i++)
			streams.get(i).println(s);
	}

	/**
	 * prints to all the PrintStreams
	 */
	public void print(String s){
		for(int i=0; i<streams.size() ;i++)
			streams.get(i).print(s);
	}

	public void println(){			println("");}
	public void println(boolean x){	println(""+x);}
	public void println(char x){	println(""+x);}
	public void println(char[] x){	println(new String(x));}
	public void println(double x){	println(""+x);}
	public void println(float x){	println(""+x);}
	public void println(int x){		println(""+x);}
	public void println(long x){	println(""+x);}
	public void println(Object x){	println(""+x);}

	public void print(boolean x){	print(""+x);}
	public void print(char x){		print(""+x);}
	public void print(char[] x){	print(new String(x));}
	public void print(double x){	print(""+x);}
	public void print(float x){		print(""+x);}
	public void print(int x){		print(""+x);}
	public void print(long x){		print(""+x);}
	public void print(Object x){	print(""+x);}


	/**
	 * If the streams should print timestamp in front
	 * of the msgs
	 * @param enable True to activate
	 */
	public void printTimeStamp(boolean enable){
		timeStamp = enable;
	}

	/**
	 * The DateFormat to print in the time stamp
	 * @param ts String to send to SimpleDateFormat
	 */
	public void setTimeStamp(String ts){
		timeStampString = ts;
	}

	private String getTime(){
		if(timeStamp)
			return "" + (new SimpleDateFormat(timeStampString)).format(new java.util.Date());
		else 
			return "";
	}

	public boolean checkError(){
		for(int i=0; i<streams.size() ;i++)
			if(streams.get(i).checkError())
				return true;
		return false;
	}


	/**
	 * closes all the PrintStreams
	 */
	public void close(){
		for(int i=0; i<streams.size() ;i++)
			streams.get(i).close();
	}

	/**
	 * Dumps the content of:
	 * - Array content
	 * - Map content (HashMap etc.)
	 * - List content (ArrayList, LinkedList etc.)
	 * - InputStream content (Prints out until the end of the stream)
	 * - Reader content (Prints out until the end of the reader)
	 * - Instance variables of a Object
	 * 
	 * @param o The Object to dump
	 * @return A String with all the printed data
	 */
	public String dump( Object o ){
		return dump( o , true);
	}

	/**
	 * Dumps the content of:
	 * <br>- Array content
	 * <br>- Map content (HashMap etc.)
	 * <br>- List content (ArrayList, LinkedList etc.)
	 * <br>- InputStream content (Prints out until the end of the stream)
	 * <br>- Reader content (Prints out until the end of the reader)
	 * <br>- Instance variables of a Object
	 * 
	 * @param o The Object to dump
	 * @param print If the method should print the data or just return it
	 * @return A String with all the printed data
	 */
	@SuppressWarnings("unchecked")
	private String dump( Object o , boolean print) {
		StringBuffer buffer = new StringBuffer();
		Class oClass = o.getClass();
		buffer.append( oClass.getName() );
		// Prints out Arrays
		if ( oClass.isArray() ) {
			buffer.append( "[" );
			for ( int i=0; i<Array.getLength(o) ;i++ ) {
				if ( i > 0 )
					buffer.append( ", " );
				Object value = Array.get(o,i);
				buffer.append( (dumbCapable(value) ? dump(value,false) : value) );
			}
			buffer.append( "]" );
		}
		// Prints out a list
		else if(o instanceof Collection){
			Iterator it = ((Collection)o).iterator();
			buffer.append( "{" );
			while(it.hasNext()){
				Object value = it.next();
				buffer.append( (dumbCapable(value) ? dump(value,false) : value) );
				if(it.hasNext())
					buffer.append( ", " );
			}
			buffer.append( "}" );
		}
		// Prints out a Map
		else if(o instanceof Map){
			Iterator it = ((Map)o).keySet().iterator();
			buffer.append( "{" );
			while(it.hasNext()){
				Object key = it.next();
				Object value = ((Map)o).get(key);
				buffer.append( key );
				buffer.append( "=>" );
				buffer.append( (dumbCapable(value) ? dump(value,false) : value) );
				if(it.hasNext())
					buffer.append( ", " );
			}
			buffer.append( "}" );
		}
		// Prints out data from InputStream
		else if(o instanceof InputStream){
			buffer.append( " =>{ \n" );
			try {				
				InputStream in = (InputStream)o;
				int tmp;
				while((tmp = in.read()) != -1){
					buffer.append( (char)tmp );
				}
				in.close();
			} catch (IOException e) {
				e.printStackTrace(this);
			}
			buffer.append( "\n}" );
		}
		// Prints out data from InputStream
		else if(o instanceof Reader){
			buffer.append( " =>{ \n" );
			try {
				Reader in = (Reader)o;
				int tmp;
				while((tmp = in.read()) != -1){
					buffer.append( (char)tmp );
				}
				in.close();
			} catch (IOException e) {
				e.printStackTrace(this);
			}
			buffer.append( "\n}" );
		}
		// Prints out Object properties
		else{
			buffer.append( "{" );
			while ( oClass != null ) {
				Field[] fields = oClass.getDeclaredFields();
				for ( int i=0; i<fields.length; i++ ) {
					if ( buffer.length() > 1 )
						buffer.append( ", " );
					fields[i].setAccessible( true );
					buffer.append( fields[i].getName() );
					buffer.append( "=" );
					try {
						Object value = fields[i].get(o);
						if (value != null) {
							buffer.append( (dumbCapable(value) ? dump(value,false) : value) );
						}
					} catch ( IllegalAccessException e ) {}
				}
				oClass = oClass.getSuperclass();
			}
			buffer.append( "}" );
		}

		if(print) out.println(buffer.toString());
		return buffer.toString();
	}

	private boolean dumbCapable(Object o){
		if(o.getClass().isArray()) return true;
		else if(o instanceof Collection)return true;
		else if(o instanceof Map)return true;
		return false;
	}
}
