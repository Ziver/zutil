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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@SuppressWarnings("resource")
public class BufferedBoundaryInputStreamTest {

	@Test
	public void read_normal() throws IOException {
		StringInputStream inin = new StringInputStream("aaa#a##aaaaaaa#");
		BufferedBoundaryInputStream in = new BufferedBoundaryInputStream(inin);
		in.setBoundary("#");

		assertTrue(in.hasNext());
        assertEquals('a', in.read());
        assertEquals('a', in.read());
        assertEquals('a', in.read());
        assertEquals(-1, in.read());

        assertTrue(in.hasNext());
		in.next();
        assertEquals('a', in.read());
        assertEquals(-1, in.read());

        assertTrue(in.hasNext());
        in.next();
        assertEquals(-1, in.read());

        assertTrue(in.hasNext());
        in.next();
        assertEquals('a', in.read());
        assertEquals('a', in.read());
        assertEquals('a', in.read());
        assertEquals('a', in.read());
        assertEquals('a', in.read());
        assertEquals('a', in.read());
        assertEquals('a', in.read());
        assertEquals(-1, in.read());

        assertFalse(in.hasNext());
		in.next();
		assertEquals(-1, in.read());
        assertFalse(in.hasNext());
	}
	@Test
	public void readArr_normal() throws IOException {
		StringInputStream inin = new StringInputStream("aaa#aaaaaaaaaaaaaaaa#aaaaaaaaaaaaaaa#");
		BufferedBoundaryInputStream in = new BufferedBoundaryInputStream(inin);
		in.setBoundary("#");

		byte[] buff = new byte[100];
		int n = in.read(buff);
		assertEquals(3, n);

		assertTrue(in.hasNext());
		in.next();
		n = in.read(buff);
		assertEquals(16, n);

		assertTrue(in.hasNext());
		in.next();
		n = in.read(buff);
		assertEquals(15, n);

		assertFalse(in.hasNext());
		in.next();
		n = in.read(buff);
		assertEquals(-1, n);
	}

    @Test
    public void read_multiCharBoundary() throws IOException {
        StringInputStream inin = new StringInputStream("aaa1234");
        BufferedBoundaryInputStream in = new BufferedBoundaryInputStream(inin);
        in.setBoundary("1234");

        byte[] buff = new byte[100];
        assertEquals(3, in.read(buff));
        assertEquals(-1, in.read());

        assertFalse(in.hasNext());
        in.next();
        assertEquals(-1, in.read(buff));
        assertFalse(in.hasNext());
    }
    @Test
    public void readArr_multiCharBoundary() throws IOException {
        StringInputStream inin = new StringInputStream("aaa1234");
        BufferedBoundaryInputStream in = new BufferedBoundaryInputStream(inin);
        in.setBoundary("1234");

        assertEquals('a', in.read());
        assertEquals('a', in.read());
        assertEquals('a', in.read());
        assertEquals(-1, in.read());

        assertFalse(in.hasNext());
        in.next();
        assertEquals(-1, in.read());
        assertFalse(in.hasNext());
    }

	@Test
	public void read_startWithBound() throws IOException {
		StringInputStream inin = new StringInputStream("#aaa");
		BufferedBoundaryInputStream in = new BufferedBoundaryInputStream(inin);
		in.setBoundary("#");

		assertEquals(-1, in.read());
	}
	@Test
	public void readArr_startWithBound() throws IOException {
		StringInputStream inin = new StringInputStream("#aaa");
		BufferedBoundaryInputStream in = new BufferedBoundaryInputStream(inin);
		in.setBoundary("#");

        assertEquals(-1, in.read(new byte[10], 0, 10));
	}

	@Test
	public void read_onlyBoundaries() throws IOException {
		StringInputStream inin = new StringInputStream("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		BufferedBoundaryInputStream in = new BufferedBoundaryInputStream(inin);
		in.setBoundary("a");
		
		int n;
		for(n=1; in.hasNext(); n++){
			assertEquals(-1, in.read());
			assertEquals(-1, in.read());
			in.next();
		}
		assertEquals(35, n);
	}
	@Test
	public void readArr_onlyBoundaries() throws IOException {
		StringInputStream inin = new StringInputStream("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		BufferedBoundaryInputStream in = new BufferedBoundaryInputStream(inin);
		in.setBoundary("a");

        byte[] buff = new byte[100];
		int n;
		for(n=1; in.hasNext(); n++){
			assertEquals(-1, in.read(buff));
			assertEquals(-1, in.read(buff));
			in.next();
		}
		assertEquals(35, n);
	}
	
	@Test
	public void read_noBounds() throws IOException {
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
    public void readArr_noBounds() throws IOException {
        String data = "1234567891011121314151617181920";
        StringInputStream inin = new StringInputStream(data);
        BufferedBoundaryInputStream in = new BufferedBoundaryInputStream(inin);
        in.setBoundary("#");

        byte[] buff = new byte[100];
        assertEquals(data.length(), in.read(buff));
        assertEquals(data, new String(buff, 0, data.length()));
    }


    @Test
    public void read_largeData() throws IOException {
        String data = "aaaaaaaaaaaa#aa#aaaaaaaaaaaaaaa#";
        StringInputStream inin = new StringInputStream(data);
        BufferedBoundaryInputStream in = new BufferedBoundaryInputStream(inin, 10);
        in.setBoundary("#");

        assertTrue(in.hasNext());
        for (int i=0; i<12; ++i)
            assertEquals('a', in.read());
        assertEquals(-1, in.read());

        assertTrue(in.hasNext());
        in.next();
        assertEquals('a', in.read());
        assertEquals('a', in.read());

        assertTrue(in.hasNext());
        in.next();
        for (int i=0; i<15; ++i)
            assertEquals('a', in.read());
        assertEquals(-1, in.read());
        assertFalse(in.hasNext());
    }
    @Test
    public void readArr_largeData() throws IOException {
        String data = "aaaaaaaaaaaa#aa#aaaaaaaaaaaaaaa#";
        StringInputStream inin = new StringInputStream(data);
        BufferedBoundaryInputStream in = new BufferedBoundaryInputStream(inin, 10);
        in.setBoundary("#");

        byte[] buff = new byte[100];
        assertTrue(in.hasNext());
        assertEquals(10, in.read(buff));
        assertEquals(2, in.read(buff));
        assertEquals(-1, in.read());

        assertTrue(in.hasNext());
        in.next();
        assertEquals(2, in.read(buff));

        assertTrue(in.hasNext());
        in.next();
        assertEquals(10, in.read(buff));
        assertEquals(5, in.read(buff));
        assertEquals(-1, in.read());
        assertFalse(in.hasNext());
    }
}
