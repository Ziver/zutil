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

package zutil.io;

import java.io.OutputStream;

/**
 * This class saves all the input data in to an StringBuffer
 * 
 * @author Ziver
 *
 */
public class StringOutputStream extends OutputStream{
	// The buffer
	protected StringBuilder buffer;
	
	/**
	 * Creates an new instance of this class
	 */
	public StringOutputStream(){
		clear();
	}
	
	@Override
	public void write(int b) {
		buffer.append( b );
	}

	@Override
	public void write(byte[] b) {
		buffer.append( new String(b) );
	}
	
	@Override
	public void write(byte[] b, int off, int len) {
		buffer.append( new String(b, off, len) );
	}

	/**
	 * Same as {@link OutputStream:clear()}
	 */
	@Override
	public void close() {
		clear();
	}
	
	/**
	 * Clears the String buffer
	 */
	public void clear(){
		buffer = new StringBuilder();
	}
	
	/**
	 * @return the String with the data
	 */
	@Override
	public String toString() {
		return buffer.toString();
	}
}
