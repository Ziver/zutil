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
	/** Current virtual size of the stream */
	private int size;
	/** Points the current byte array index */
	private int arrayIndex;
	/** Points to a local index in the current byte array */
	private int arrayLocalIndex;
	/** Current virtual position of the stream */
	private int pos;

	/**
	 * Create a new instance of DynamicByteArrayStream
	 */
	public DynamicByteArrayStream(){
		bytes = new ArrayList<byte[]>();
		size = 0;
		arrayIndex = 0;
		arrayLocalIndex = 0;
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
	 * NOTE: This function will copy data.
	 * 
	 * @param 		b 			is the byte array to add
	 * @param 		offset		is the offset in the byte array
	 * @param 		length		is the amount of data to add
	 */
	public synchronized void append(byte[] b, int offset, int length){
		byte[] new_b = new byte[length];
		System.arraycopy(b, offset, new_b, 0, length);
		bytes.add(new_b);
		size += length;
	}

	@Override
	public synchronized int read() throws IOException {
		if(pos >= size)	return -1;
		
		int ret = bytes.get(arrayIndex)[arrayLocalIndex] & 0xff;
		pos++;
		arrayLocalIndex++;
		if(arrayLocalIndex >= bytes.get(arrayIndex).length){
			arrayIndex++;
			arrayLocalIndex = 0;
		}
		return ret;
	}

	public synchronized int read(byte b[], int off, int len) {
		if(len <= 0) return 0;
		if(pos >= size)	return -1;

        int bytes_read=0;
		if(pos+len >= size) len = size - pos;
		for(; bytes_read<len ;bytes_read++){
			byte[] src = bytes.get(arrayIndex);
            // Read length is LONGER than local array
			if(arrayLocalIndex +len-bytes_read >= src.length){
				int length = src.length- arrayLocalIndex;
				System.arraycopy(src, arrayLocalIndex, b, off+bytes_read, length);
				
				arrayLocalIndex = 0;
			    arrayIndex++;
				bytes_read += length;
			}
            // Read length is SHORTER than local array
			else{
				int length = len-bytes_read;
				System.arraycopy(src, arrayLocalIndex, b, off+bytes_read, length);
				
				arrayLocalIndex += length;
				bytes_read += length;
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
		arrayIndex = 0;
		arrayLocalIndex = 0;
		pos = 0;
	}

	public void close() throws IOException {
		clear();
	}
	
	/**
	 * @return 		all of the buffers content as a byte array.
	 */
	public byte[] getBytes(){
		byte[] data = new byte[size];
		this.read(data, 0, size);
		return data;
	}
	
	
	/**
	 * WARNING: This function might return a malformed String.
	 * 
	 * @return all the contents of the buffers as a String.
	 */
	public String toString(){
        return new String( this.getBytes() );
	}
}
