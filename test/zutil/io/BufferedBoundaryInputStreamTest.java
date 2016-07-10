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

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@SuppressWarnings("resource")
public class BufferedBoundaryInputStreamTest {

	@Test
	public void normal() throws IOException {
		StringInputStream inin = new StringInputStream("aaa#aaaaaaaaaaaaaaaa#aaaaaaaaaaaaaaa#");
		BufferedBoundaryInputStream in = new BufferedBoundaryInputStream(inin);
		
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
	public void startWithBound() throws IOException {
		StringInputStream inin = new StringInputStream("#aaa");
		BufferedBoundaryInputStream in = new BufferedBoundaryInputStream(inin);
		in.setBoundary("#");

		assertEquals(-1, in.read());

        inin = new StringInputStream("#aaa");
        in = new BufferedBoundaryInputStream(inin);
        in.setBoundary("#");

        assertEquals(-1, in.read(new byte[10], 0, 10));
	}

	@Test
	public void onlyBoundaries() throws IOException {
		StringInputStream inin = new StringInputStream("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		BufferedBoundaryInputStream in = new BufferedBoundaryInputStream(inin);

		in.setBoundary("a");
		
		int n;
		for(n=1; true; n++){
			assertEquals(-1, in.read());
			assertEquals(-1, in.read());
			in.next();
			if(!in.isOnBoundary())
				break;
		}
		assertEquals(35, n);
	}
	
	@Test
	public void noBounds() throws IOException {
		String data = "1234567891011121314151617181920";
		StringInputStream inin = new StringInputStream(data);
		BufferedBoundaryInputStream in = new BufferedBoundaryInputStream(inin);
		in.setBoundary("#");
		
		int out;
		StringBuilder output = new StringBuilder();
		while((out = in.read()) != -1){
			output.append((char)out);
		}
		assertEquals(data, output.toString());
	}

    @Test
    public void next() throws IOException {
        StringInputStream inin = new StringInputStream("a#a#");
        BufferedBoundaryInputStream in = new BufferedBoundaryInputStream(inin);
        in.setBoundary("#");

        assertEquals('a', in.read());
        assertEquals(-1, in.read());
        assertEquals(true, in.next());
        assertEquals('a', in.read());
        assertEquals(-1, in.read());
        assertEquals(false, in.next());
    }
}
