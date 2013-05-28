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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class DynamicByteArrayStream extends InputStream{
	/** The byte array container */
	private ArrayList<byte[]> bytes;
	/** The current size of the stream */
	private int size;
	/** points to the current index in the ArrayList */
	private int byteArrayIndex;
	/** points locally in the current index in the ArrayList */
	private int localPointer;
	/** The current position */
	private int pos;

	/**
	 * Create a new instance of DynamicByteArrayStream
	 */
	public DynamicByteArrayStream(){
		bytes = new ArrayList<byte[]>();
		size = 0;
		byteArrayIndex = 0;
		localPointer = 0;
		pos = 0;
	}

	/**
	 * Append an byte array to the stream
	 * 
	 * @param 		b 			is the byte array to add.
	 */
	public synchronized void append(byte[] b){
		bytes.add(b);
		size += b.length;
	}
	
	/**
	 * Append an byte array to the stream.
	 * WARNING: This function will copy data.
	 * 
	 * @param 		b 			is the byte array to add
	 * @param 		offset		is the offset in the byte array
	 * @param 		length		is the amount of data to add
	 */
	public synchronized void append(byte[] b, int offset, int length){
		byte[] new_b = new byte[length];
		System.arraycopy(b, offset, new_b, 0, length);
		bytes.add(b);
		size += length;
	}

	@Override
	public synchronized int read() throws IOException {
		if(pos >= size)	return -1;
		
		int ret = bytes.get(byteArrayIndex)[localPointer] & 0xff;
		pos++;
		localPointer++;
		if(localPointer >= bytes.get(byteArrayIndex).length){
			byteArrayIndex++;
			localPointer = 0;
		}
		return ret;
	}

	public synchronized int read(byte b[], int off, int len) {
		if(len <= 0) return 0;
		if(pos >= size)	return -1;
		
		int bytes_read = 0;
		if(pos+len >= size) len = size - pos;
		for(int i=0; i<len ;i++){
			byte[] src = bytes.get(byteArrayIndex);
			if(localPointer+len-i >= src.length){
				int length = src.length-localPointer;
				System.arraycopy(src, localPointer, b, off+i, length);
				
				localPointer = 0;
				byteArrayIndex++;
				bytes_read += length;
				i += length;
			}
			else{
				int length = len-i;
				System.arraycopy(src, localPointer, b, off+i, length);
				
				localPointer += length;
				bytes_read += length;
				i += length;
			}
		}
		pos += len;
		return bytes_read;
	}

	public synchronized int available() {
		return size - pos;
	}

	/**
	 * Clears this stream from the byte arrays
	 */
	public synchronized void clear(){
		size = 0;
		reset();
		bytes.clear();
	}
	
	public synchronized void reset() {
		byteArrayIndex = 0;
		localPointer = 0;
		pos = 0;
	}

	public void close() throws IOException {
		clear();
	}
	
	/**
	 * @return 		all of the buffers content as a byte array.
	 */
	public byte[] getByte(){
		byte[] data = new byte[size];
		this.read(data, 0, size);
		return data;
	}
	
	
	/**
	 * WARNING: This function might return a malformed String.
	 * 
	 * @return 		all of the buffers content as a String.
	 */
	public String getString(){
		String data = new String( this.getByte() );
		return data;
	}
}
