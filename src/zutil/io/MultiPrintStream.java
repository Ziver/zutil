/*******************************************************************************
 * Copyright (c) 2011 Ziver Koc
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
 ******************************************************************************/
package zutil.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import zutil.Dumpable;

/**
 * @author Ziver
 * this class can print strings to multiple PrintStreams
 */
public class MultiPrintStream extends PrintStream {
	//the print streams that will print
	private ArrayList<PrintStream> streams;
	//a instance of this class
	public static MultiPrintStream out = new MultiPrintStream();

	public MultiPrintStream(){
		super(new PrintStream(System.out));
		streams = new ArrayList<PrintStream>();
		streams.add(new PrintStream(System.out)); 
	}

	/**
	 * This constructor makes a simple PrintStream that prints to the console and to a file
	 * @param file is the file name to output to
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
	 * This constructor takes a array of PrintStreams to be used
	 * @param streams is a array of the streams that will be used
	 */
	public MultiPrintStream(PrintStream[] streams){
		super(streams[0]);
		this.streams = new ArrayList<PrintStream>();
		for(int i=0; i<streams.length ;i++){
			this.streams.add(streams[i]);
		}
	}

	/**
	 * This constructor takes a array of PrintStreams to be used
	 * @param streams is a array of the streams that will be used
	 */
	public static void makeInstance(MultiPrintStream instanceStream){
		out = instanceStream;
	}

	/**
	 * Adds a PrintStream to the list of streams
	 * @param p is the PrintStream to add
	 */
	public void addPrintStream(PrintStream p){
		streams.add(p);
	}

	/**
	 * Remove a PrintStream from the list
	 * @param p is the PrintStream to remove
	 */
	public void removePrintStream(PrintStream p){
		streams.remove(p);
	}

	/**
	 * Remove a PrintStream from the list
	 * @param p is the index of the PrintStream to remove
	 */
	public void removePrintStream(int p){
		streams.remove(p);
	}

	/**
	 * writes to all the PrintStreams
	 */
	public void write(int b) {
		for(int i=0; i<streams.size() ;i++)
			streams.get(i).write(b);
	}
	
	/**
	 * writes to all the PrintStreams
	 */
	 public void write(byte buf[], int off, int len){
		for(int i=0; i<streams.size() ;i++)
			streams.get(i).write(buf, off, len);
	}
	
	/**
	 * Prints with a new line to all the PrintStreams
	 */
	public void println(String s){
		for(int i=0; i<streams.size() ;i++)
			streams.get(i).println(s);
	}

	/**
	 * Prints to all the PrintStreams
	 */
	public void print(String s){
		for(int i=0; i<streams.size() ;i++)
			streams.get(i).print(s);
	}

	public void println(){			println("");}
	public void println(boolean x){	println(String.valueOf(x));}
	public void println(char x){	println(String.valueOf(x));}
	public void println(char[] x){	println(new String(x));}
	public void println(double x){	println(String.valueOf(x));}
	public void println(float x){	println(String.valueOf(x));}
	public void println(int x){		println(String.valueOf(x));}
	public void println(long x){	println(String.valueOf(x));}
	public void println(Object x){	println(String.valueOf(x));}

	public void print(boolean x){	print(String.valueOf(x));}
	public void print(char x){		print(String.valueOf(x));}
	public void print(char[] x){	print(new String(x));}
	public void print(double x){	print(String.valueOf(x));}
	public void print(float x){		print(String.valueOf(x));}
	public void print(int x){		print(String.valueOf(x));}
	public void print(long x){		print(String.valueOf(x));}
	public void print(Object x){	print(String.valueOf(x));}



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
	 * <br>- Array content
	 * <br>- Map content (HashMap etc.)
	 * <br>- List content (ArrayList, LinkedList etc.)
	 * <br>- InputStream content (Prints out until the end of the stream)
	 * <br>- Reader content (Prints out until the end of the reader)
	 * <br>- Instance variables of a Object
	 * 
	 * @param o is the Object to dump
	 */
	public void dump( Object o ){
		println(dumpToString( o ));
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
	 * @param o is the Object to dump
	 * @return A String with all the printed data
	 */
	public String dumpToString( Object o) {
		return dumpToString(o, "");
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
	 * @param o is the Object to dump
	 * @param head is the string that will be put in front of every line
	 * @return A String with all the printed data
	 */

	private String dumpToString( Object o , String head) {
		if(o == null) 
			return "NULL";
		StringBuffer buffer = new StringBuffer();
		Class<?> oClass = o.getClass();
		buffer.append( oClass.getName() );
		String nextHead = head + "\t";
		// Prints out Arrays
		if ( oClass.isArray() ) {			
			buffer.append( "[" );
			for ( int i=0; i<Array.getLength(o) ;i++ ) {
				Object value = Array.get(o,i);
				buffer.append("\n");
				buffer.append(nextHead);
				buffer.append( (dumbCapable(value) ? dumpToString(value, nextHead) : value) );
				if ( i+1<Array.getLength(o) )
					buffer.append( "," );
			}
			buffer.append( "\n" );
			buffer.append(head);
			buffer.append( "]" );
		}
		// Prints out a list
		else if(o instanceof Collection){
			Iterator<?> it = ((Collection<?>)o).iterator();
			buffer.append( "{" );
			while(it.hasNext()){
				Object value = it.next();
				buffer.append("\n");
				buffer.append(nextHead);
				buffer.append( (dumbCapable(value) ? dumpToString(value, nextHead) : value) );
				if(it.hasNext())
					buffer.append( "," );
			}
			buffer.append( "\n" );
			buffer.append(head);
			buffer.append( "}" );
		}
		// Prints out a Map
		else if(o instanceof Map){
			Iterator<?> it = ((Map<?,?>)o).keySet().iterator();
			buffer.append( "{" );
			while(it.hasNext()){
				Object key = it.next();
				Object value = ((Map<?,?>)o).get(key);
				buffer.append("\n");
				buffer.append(nextHead);
				buffer.append( key );
				buffer.append( "=>" );
				buffer.append( (dumbCapable(value) ? dumpToString(value, nextHead) : value) );
				if(it.hasNext())
					buffer.append( "," );
			}
			buffer.append( "\n" );
			buffer.append(head);
			buffer.append( "}" );
		}
		// Prints out data from InputStream
		else if(o instanceof InputStream){
			buffer.append( " =>{\n" );
			try {				
				InputStream in = (InputStream)o;
				int tmp;
				while((tmp = in.read()) != -1){
					buffer.append(nextHead);
					buffer.append( (char)tmp );
				}
				in.close();
			} catch (IOException e) {
				e.printStackTrace(this);
			}
			buffer.append( "\n" );
			buffer.append(head);
			buffer.append( "}" );
		}
		// Prints out data from InputStream
		else if(o instanceof Reader){
			buffer.append( " =>{\n" );
			try {
				Reader in = (Reader)o;
				int tmp;
				while((tmp = in.read()) != -1){
					buffer.append(nextHead);
					buffer.append( (char)tmp );
				}
				in.close();
			} catch (IOException e) {
				e.printStackTrace(this);
			}
			buffer.append( "\n" );
			buffer.append(head);
			buffer.append( "}" );
		}
		// Prints out Object properties
		else{
			buffer.append( "{" );
			while ( oClass != null ) {
				Field[] fields = oClass.getDeclaredFields();
				for ( int i=0; i<fields.length; i++ ) {
					fields[i].setAccessible( true );
					buffer.append("\n");
					buffer.append(nextHead);
					buffer.append( fields[i].getType().getSimpleName() );
					buffer.append( " " );
					buffer.append( fields[i].getName() );
					buffer.append( " = " );
					try {
						Object value = fields[i].get(o);
						if (value != null) {
							buffer.append( (dumbCapable(value) ? dumpToString(value, nextHead) : value) );
						}
					} catch ( IllegalAccessException e ) {}
					if ( i+1<fields.length )
						buffer.append( "," );
				}
				oClass = oClass.getSuperclass();
			}
			buffer.append( "\n" );
			buffer.append(head);
			buffer.append( "}" );
		}

		return buffer.toString();
	}

	/**
	 * An helper function for the dump function.
	 */
	private boolean dumbCapable(Object o){
		if(o != null){		
			if(o.getClass().isArray()) return true;
			else if(o instanceof Collection)return true;
			else if(o instanceof Map)return true;
			else if(o instanceof InputStream)return true;
			else if(o instanceof Reader)return true;
			else if(o instanceof Dumpable)return true;
		}
		return false;
	}
}
