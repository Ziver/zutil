/*******************************************************************************
 * Copyright (c) 2013 Ziver
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

import java.io.InputStream;

/**
 * This class saves all the input data in to an StringBuffer
 * 
 * @author Ziver
 *
 */
public class StringInputStream extends InputStream{
	// The buffer
	protected StringBuilder buffer;
	
	/**
	 * Creates an new instance of this class
	 */
	public StringInputStream(){
		clear();
	}
	
	public StringInputStream(String data) {
		clear();
		add(data);
	}

	/**
	 * Returns an estimate of the number of bytes 
	 * that can be read (or skipped over) from this 
	 * input stream without blocking by the next 
	 * invocation of a method for this input stream.
	 */
	public int available(){
		return buffer.length();
	}	


    /**
     * Reads the next byte of data from the input stream.
     */
    public int read(){
        if(buffer.length() > 0){
            int ret = Character.getNumericValue( buffer.charAt( 0 ));
            buffer.deleteCharAt( 0 );
            return ret;
        }
        return -1;
    }
	
    /**
     * Reads some number of bytes from the input stream 
     * and stores them into the buffer array b.
     */
    public int read(byte[] b){
    	return read( b, 0, b.length );
    }
	
    /**
     * Reads up to len bytes of data from the input stream 
     * into an array of bytes.
     */
    public int read(byte[] b, int off, int len){
    	if( buffer.length() < len ){
    		len = buffer.length();
    	}
    	char[] ctmp = new char[len];
    	buffer.getChars(0, len, ctmp, 0);
    	byte[] btmp = new String( ctmp ).getBytes();
    	System.arraycopy(btmp, 0, b, off, len);
    	buffer.delete(0, len);
		return len;
    }
    
    
    /**
     * Skips over and discards n bytes of data from this 
     * input stream.
     * 
     * @param	n		is the amount characters to skip
     */
    public long skip(long n){
    	if( buffer.length() < n ){
    		int len = buffer.length();
    		buffer.delete(0, len);
    		return len;
    	}    		
    	else{
    		buffer.delete(0, (int) n);
    		return n;
    	}
    }
    
    /**
     * Tests if this input stream supports the mark and 
     * reset methods.
     */
	public boolean markSupported(){
		return false;
	}
	

    /**
     * Closes this input stream and releases any system 
     * resources associated with the stream.
     */
	public void close(){
		clear();
	}	
	
	public void clear(){
		buffer = new StringBuilder();
	}
	
	public void add( String data ){
		buffer.append( data );
	}
}
