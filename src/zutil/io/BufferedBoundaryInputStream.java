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
 * A stream that is handled as a iterator. The stream will read
 * until it gets to a boundary and will return -1 (end of stream).
 * The stream will not return any data until the {@link #next()}
 * method is called.
 * 
 * @author Ziver
 *
 */
public class BufferedBoundaryInputStream extends FilterInputStream{
	/** The size of the buffer in bytes */
	protected static final int DEFAULT_BUF_SIZE = 8192;

	/** The raw buffer */
    private byte buffer[];
    /** The current position in the buffer */
    private int buf_pos = 0;
	/** The end position of the buffer */
    private int buf_end = 0;
    /** Boundary position, 0< means no boundary found */
    private int buf_bound_pos;
	/** The boundary (the delimiter)  */
    private byte[] boundary;


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
        buf_pos = 0;
		buf_end = 0;
        buf_bound_pos = -1;
		buffer = new byte[buf_size];
	}


	
	/**
     * @return 			the next byte from the stream or -1 if EOF or stream is on a boundary
	 */
	public final int read() throws IOException{
        if (fillBuffer() < 0)
            return -1;

        if(isOnBoundary())
			return -1; // boundary
		return buffer[buf_pos++];
	}

	/**
	 * Fills the given array with data from the buffer
	 * 
	 * @param 	b 		is the array that will be filled
	 * @return 			the amount of bytes read or -1 if EOF or stream is on a boundary
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
     * @return 			the amount of bytes read or -1 if EOF or stream is on a boundary
	 */
	public int read(byte b[], int off, int len) throws IOException {
        if (fillBuffer() < 0)
            return -1; // EOF
        if (isOnBoundary())
			return -1; // boundary

		// The request is larger then the buffer size
		int leftover = available();
		if (len > leftover)
			len = leftover;

		// the boundary is in the read range
		if (buf_pos < buf_bound_pos && buf_bound_pos < buf_pos+len)
			len = buf_bound_pos - buf_pos;
		
		System.arraycopy(buffer, buf_pos, b, off, len);
		buf_pos += len;
		return len;
	}


    /**
     * @return if the current position in the buffer is a boundary
     */
    private boolean isOnBoundary(){
        return buf_bound_pos == buf_pos;
    }



    /**
     * Checks if there is more data available after the boundary.
     * Note if the data between boundaries are longer than the buffer
     * size then this method will return true until the next boundary
     * is available in the buffer or the end of the stream.
     *
     * @return true if there is more data available after the closest boundary.
     */
    public boolean hasNext() throws IOException {
        if (buf_bound_pos < 0 && fillBuffer() >= 0)
            return true; // the boundary not in the buffer?
        if (buf_bound_pos >= 0 && buf_bound_pos + boundary.length < buf_end)
            return true; // there is more data beyond boundary in the buffer
        return false;
    }
    /**
     * Skips over the closest boundary
     */
	public void next() throws IOException {
		if (buf_bound_pos >= 0){ // is boundary in buffer?
			buf_pos += boundary.length;
			searchNextBoundary();
		}
        else { // read data until we find the next boundary or get to the end of the stream
            while (buf_bound_pos < 0 && fillBuffer() >= 0)
				buf_pos = buf_end;
        }
	}
	

	/**
	 * Skips a specific amounts of bytes in the buffer.
	 * Note that his method does not check for boundaries.
	 * 
     * @param	n 	the number of bytes to be skipped.
     * @return		the actual number of bytes skipped.
	 */
	public long skip(long n) throws IOException {
		int leftover = available();
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
        searchNextBoundary(); // redo the search with the new boundary
	}
	
	/**
	 * Sets the boundary for the stream
	 */
	public void setBoundary(byte[] b){
		boundary = new byte[b.length];
		System.arraycopy(b, 0, boundary, 0, b.length);
        searchNextBoundary(); // redo the search with the new boundary
	}

	/**
	 * @return     an estimate of the number of bytes that can be read (or skipped
	 *             over) from this buffered input stream without blocking.
	 * @exception  IOException  if an I/O error occurs.
	 */
	public int available() {
		return buf_end - buf_pos;
	}

	/**
     * Tests if this input stream supports the mark and 
     * reset methods.
     */
	public boolean markSupported(){
		return false;
	}




    /**
     * Checks if the buffer needs to be appended with data.
     * If so it moves the remaining data to the beginning of the
     * buffer and then fills the buffer with data from
     * the source stream
     *
     * @return 			the number of new bytes read from the source stream,
     *                  or -1 if the buffer is empty and it is the end of the stream
     */
    private int fillBuffer() throws IOException {
        int leftover = this.available();
        // Do we need to fill the buffer
        if(buf_pos < buf_end-boundary.length)
            return 0;
        // is there any data available
        if(leftover <= 0 && super.available() <= 0)
            return -1; // EOF

        // Move the end of the buffer to the start to not miss any split boundary
        if (leftover > 0 && buf_pos != 0)
            System.arraycopy(buffer, buf_pos, buffer, 0, leftover);
        buf_pos = 0;
        buf_end = leftover;

        // Copy in new data from the stream
        int n = super.read(buffer, buf_end, buffer.length-buf_end);
        if (n >= 0)
            buf_end = buf_end + n;

        searchNextBoundary();
        return ((n < 0 && this.available() > 0) ? 0 : n);
    }

    /**
     * Searches for the nearest boundary from the current buffer position
     */
    private void searchNextBoundary(){
        // No need to check for boundary if buffer is smaller than the boundary length
    	if (this.available() >= boundary.length) {
			for (int i = buf_pos; i < buf_end; i++) {
				for (int b = 0; b < boundary.length; b++) {
					if (buffer[i + b] != boundary[b])
						break;
					else if (b == boundary.length - 1) {
						buf_bound_pos = i;
						return;
					}
				}
			}
		}
        buf_bound_pos = -1;
    }
}
