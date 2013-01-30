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
package zutil.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import zutil.io.BoundaryBufferedInputStream;
import zutil.io.StringInputStream;

public class BoundaryBufferedInputStreamTest {

	@Test
	public void testReadB1() throws IOException {
		StringInputStream inin = new StringInputStream();
		BoundaryBufferedInputStream in = new BoundaryBufferedInputStream(inin);
		inin.add("aaa#aaaaaaaaaaaaaaaa#aaaaaaaaaaaaaaa#");
		
		in.setBoundary("#");
		
		int n = 0;
		for(n=0; in.read() != -1; n++);
		assertEquals(3, n);
		
		in.next();
		n = 0;
		for(n=0; in.read() != -1; n++);
		assertEquals(16, n);
		
		in.next();
		n = 0;
		for(n=0; in.read() != -1; n++);
		assertEquals(15, n);
		
		in.next();
		assertEquals(-1, in.read());
		
	}

	@Test
	public void testOnlyBoundaries() throws IOException {
		StringInputStream inin = new StringInputStream();
		BoundaryBufferedInputStream in = new BoundaryBufferedInputStream(inin);
		inin.add("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		
		in.setBoundary("a");
		
		int n;
		for(n=1; true; n++){
			assertEquals(-1, in.read());
			assertEquals(-1, in.read());
			in.next();
			if(!in.isBoundary())
				break;
		}
		assertEquals(35, n);
	}
	
	@Test
	public void testNoBounds() throws IOException {
		String data = "1234567891011121314151617181920";
		StringInputStream inin = new StringInputStream();
		BoundaryBufferedInputStream in = new BoundaryBufferedInputStream(inin);
		inin.add(data);		
		in.setBoundary("#");
		
		int out;
		StringBuilder output = new StringBuilder();
		while((out = in.read()) != -1){
			output.append((char)out);
		}
		assertEquals(data, output.toString());
	}

}
