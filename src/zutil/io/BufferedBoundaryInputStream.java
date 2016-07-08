/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Ziver Koc
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

package zutil.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * TODO: boundry
 * 
 * @author Ziver
 *
 */
public class BufferedBoundaryInputStream extends FilterInputStream{
	/** The size of the buffer in bytes */
	public static final int DEFAULT_BUF_SIZE = 64*1024;

	/** The raw buffer */
	protected byte buffer[];
	/** The end position of the buffer */
	protected int buf_end = 0;
	/** The current position in the buffer */
	protected int buf_pos = 0;
	/** The boundary (the delimiter)  */
	protected byte[] boundary;
	/** Boundary position, 0< means no boundary found */
	protected int bound_pos;


	/**
	 * Creates a instance of this class with a default buffer size of 64K
	 * 
	 * @param 	in			is the InputStream that the buffer will use
	 */
	public BufferedBoundaryInputStream(InputStream in){
		this(in, DEFAULT_BUF_SIZE);
	}

	/**
	 * Creates a instance of this class
	 * 
	 * @param 	in			is the InputStream that the buffer will use
	 * @param	buf_size	speifies the buffer size
	 */
	public BufferedBoundaryInputStream(InputStream in, int buf_size){
		super(in);
		buf_end = 0;
		buf_pos = 0;
		buffer = new byte[buf_size];
		bound_pos = -1;
	}

	/**
	 * Moves the remaining data to the beginning of the 
	 * buffer and then fills the buffer with data from 
	 * the source stream to the buffer
	 * 
	 * @return 			the size of the buffer
	 */
	protected int fillBuffer() throws IOException {
		int leftover = buf_end - buf_pos;
		// is there any data available
		if(leftover < 0 && super.available() <= 0)
			return -1; // EOF
		
		// Move the end of the buffer to the start to not miss any split boundary
		System.arraycopy(buffer, buf_pos, buffer, 0, buf_end);
		// Copy in new data from the stream
		int n = super.read(buffer, leftover, buffer.length );
		// Reset positions
		if(n+leftover >= 0) {
			buf_end = leftover + n;
			buf_pos = 0;
		}
		searchNextBoundary();
		return n+leftover;
	}

	
	/**
	 * @return 			the next byte in the buffer
	 */
	public final int read() throws IOException{
		if(buf_pos >= buf_end-boundary.length) {
			if(fillBuffer() <= 0)
				return -1;  // EOF
		}
		
		if(bound_pos == buf_pos)
			return -1; // boundary
		return buffer[buf_pos++];
	}

	/**
	 * Fills the given array with data from the buffer
	 * 
	 * @param 	b 		is the array that will be filled
	 * @return 			the amount of bytes read or -1 if EOF
	 */	
	public int read(byte b[]) throws IOException {
		return read(b, 0, b.length);
	}

	/**
	 * Reads a given length of bytes from the buffer
	 * 
	 * @param 	b 		is the array that will be filled
	 * @param 	off 	is the offset in the array
	 * @param 	len 	is the amount to read
	 * @return 			the amount of bytes read or -1 if EOF
	 */
	public int read(byte b[], int off, int len) throws IOException {
		if(bound_pos == buf_pos)
			return -1; // boundary
			
		if(buf_pos >= buf_end-boundary.length) {
			if(fillBuffer() <= 0)
				return -1; // EOF
		}
		int leftover = buf_end - buf_pos;

		// The request is larger then the buffer size
		if(len > leftover){
			len = leftover;
		}
		// the boundary is in the read range
		if(buf_pos < bound_pos && bound_pos < buf_pos+len){
			len = buf_pos - bound_pos;
		}
		
		System.arraycopy(buffer, buf_pos, b, off, len);
		buf_pos += len;
		return len;
	}

	/**
	 * Skips over the boundary at the current position in the buffer
	 */
	public void next(){
		if(bound_pos == buf_pos){
			buf_pos += boundary.length;
			searchNextBoundary();
		}
	}
	
	/**
	 * Searches for the nearest boundary from the current position
	 */
	protected void searchNextBoundary(){
		for(int i=buf_pos; i<buf_end; i++){
			for(int b=0; b < boundary.length; b++){
				if(buffer[i] != boundary[b])
					break;
				else if(b == boundary.length-1){
					bound_pos = i;
					return;
				}
			}
		}
		bound_pos = -1;
	}
	
	/**
	 * Skips a specific amounts of bytes in the buffer.
	 * Note that his method does not check for boundaries.
	 * 
     * @param	n 	the number of bytes to be skipped.
     * @return		the actual number of bytes skipped.
	 */
	public long skip(long n) throws IOException {
		int leftover = buf_end - buf_pos;
		if(n > leftover){
			buf_pos = buf_end;
			return leftover;
		}
		return buf_pos += n;
	}
	
	
	/**
	 * Sets the boundary for the stream
	 */
	public void setBoundary(String b){
		this.boundary = b.getBytes();
	}
	
	/**
	 * Sets the boundary for the stream
	 */
	public void setBoundary(byte[] b){
		boundary = new byte[b.length];
		System.arraycopy(b, 0, boundary, 0, b.length);
	}

	/**
	 * @return if there is more data to read
	 */
	public boolean hasNext(){
		return bound_pos == buf_pos;
	}
	
	/**
	 * @return     an estimate of the number of bytes that can be read (or skipped
	 *             over) from this buffered input stream without blocking.
	 * @exception  IOException  if an I/O error occurs.
	 */
	public int available() throws IOException {
		return buf_end - buf_pos;
	}

	/**
     * Tests if this input stream supports the mark and 
     * reset methods.
     */
	public boolean markSupported(){
		return false;
	}
}
