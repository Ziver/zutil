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
import java.io.InputStream;

import static org.junit.Assert.*;


@SuppressWarnings("resource")
public class BufferedBoundaryInputStreamTest {

    private void readAndAssertArray(byte[] expected, InputStream in, int readcount) throws IOException {
        byte[] buff = new byte[readcount];
        int n = in.read(buff, 0, readcount);
        if (n < readcount)
            n += in.read(buff, n, readcount-n);
        assertEquals(readcount, n);
        assertArrayEquals(expected, buff);
    }

    private static BufferedBoundaryInputStream getBufferedBoundaryInputStream(String data, String boundary) {
        return getBufferedBoundaryInputStream(data, boundary, -1);
    }
    private static BufferedBoundaryInputStream getBufferedBoundaryInputStream(String data, String boundary, int buffLimit) {
        StringInputStream inin = new StringInputStream(data);
        BufferedBoundaryInputStream in = buffLimit >= 0 ?
                new BufferedBoundaryInputStream(inin, buffLimit) :
                new BufferedBoundaryInputStream(inin);
        in.setBoundary(boundary);
        return in;
    }



	@Test
	public void read_normal() throws IOException {
        BufferedBoundaryInputStream in = getBufferedBoundaryInputStream("aaa#a##aaaaaaa#", "#");

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
        BufferedBoundaryInputStream in = getBufferedBoundaryInputStream("aaa#aaaaaaaaaaaaaaaa#aaaaaaaaaaaaaaa#", "#");

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
        BufferedBoundaryInputStream in = getBufferedBoundaryInputStream("aaa1234", "1234");

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
        BufferedBoundaryInputStream in = getBufferedBoundaryInputStream(
                "------------------------------83ff53821b7chello------------------------------83ff53821b7c--",
                "------------------------------83ff53821b7c");

        assertEquals(-1, in.read());
        assertTrue(in.hasNext()); in.next();
        assertEquals("hello", IOUtil.readContentAsString(in));
        assertEquals(-1, in.read());
        assertTrue(in.hasNext()); in.next();
        assertEquals('-', in.read());
        assertEquals('-', in.read());
        assertEquals(-1, in.read());

        assertFalse(in.hasNext());
        in.next();
        assertEquals(-1, in.read());
        assertFalse(in.hasNext());
    }

	@Test
	public void read_startWithBound() throws IOException {
        BufferedBoundaryInputStream in = getBufferedBoundaryInputStream("#aaa", "#");

		assertEquals(-1, in.read());
	}
	@Test
	public void readArr_startWithBound() throws IOException {
        BufferedBoundaryInputStream in = getBufferedBoundaryInputStream("#aaa", "#");

        assertEquals(-1, in.read(new byte[10], 0, 10));
	}

	@Test
	public void read_onlyBoundaries() throws IOException {
        BufferedBoundaryInputStream in = getBufferedBoundaryInputStream("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "a");
		
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
        BufferedBoundaryInputStream in = getBufferedBoundaryInputStream("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "a");

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
        BufferedBoundaryInputStream in = getBufferedBoundaryInputStream(data, "#");
		
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
        BufferedBoundaryInputStream in = getBufferedBoundaryInputStream(data, "#");

        byte[] buff = new byte[100];
        assertEquals(data.length(), in.read(buff));
        assertEquals(data, new String(buff, 0, data.length()));
    }


    @Test
    public void read_largeData() throws IOException {
        BufferedBoundaryInputStream in = getBufferedBoundaryInputStream(
                "#aaaaaaaaaaaa#aa#aaaaaaaaaaaaaaa#", "#", 10);

        assertEquals(-1, in.read());
        assertTrue(in.hasNext());
        in.next();
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
    public void read_largeDataOnlyNext() throws IOException {
        BufferedBoundaryInputStream in = getBufferedBoundaryInputStream(
                "#aaaaaaaaaaaa#aa#aaaaaaaaaaaaaaa#", "#", 10);

        in.next();
        for (int i=0; i<12; ++i)
            assertEquals('a', in.read());
        assertEquals(-1, in.read());

        in.next();
        assertEquals('a', in.read());
        assertEquals('a', in.read());

        in.next();
        for (int i=0; i<15; ++i)
            assertEquals('a', in.read());
        assertEquals(-1, in.read());
    }
    @Test
    public void readArr_largeData() throws IOException {
        BufferedBoundaryInputStream in = getBufferedBoundaryInputStream(
                "aaaaaaaaaaaa#aa#aaaaaaaaaaaaaaa#", "#", 10);

        byte[] buff = new byte[100];
        int n = 0;
        assertTrue(in.hasNext());
        n = in.read(buff) + in.read(buff);
        assertEquals(12, n);
        assertEquals(-1, in.read(buff));

        assertTrue(in.hasNext());
        in.next();
        assertEquals(2, in.read(buff));

        assertTrue(in.hasNext());
        in.next();
        n = in.read(buff) + in.read(buff) + in.read(buff);
        assertEquals(15, n);
        assertEquals(-1, in.read());
        assertFalse(in.hasNext());
    }

    @Test
    public void readArr_splitBoundary() throws IOException {
        BufferedBoundaryInputStream in = getBufferedBoundaryInputStream(
                "aaaaaaaa####aaaaaaaa", "####", 10);

        byte[] buff = new byte[10];
        assertEquals(4, in.read(buff, 0 , 4));
        int n = 0;
        n += in.read(buff, 0 , 10);
        n += in.read(buff, 0 , 10);
        assertEquals(4, n);
        assertEquals(-1, in.read(buff, 0 , 6));
        in.next();
        assertEquals(6, in.read(buff, 0 , 6));
    }


    @Test
    public void read_mark() throws IOException {
        BufferedBoundaryInputStream in = getBufferedBoundaryInputStream(
                "0123456789#abcdefghijklmn#opqrstuvwxyz", "#");

        in.read();in.read();in.read();
        assertEquals('3', in.read());
        in.mark(10); // mark '4'
        assertEquals('4', in.read());
        in.read();in.read();in.read();
        assertEquals('8', in.read());
        in.reset(); // go back to '4'
        assertEquals('4', in.read());
    }
    @Test
    public void readArr_mark() throws IOException {
        BufferedBoundaryInputStream in = getBufferedBoundaryInputStream(
                "0123456789#abcdefghijklmn#opqrstuvwxyz", "#");

        readAndAssertArray(new byte[]{'0','1','2','3'}, in, 4);
        in.mark(10); // mark '4'
        readAndAssertArray(new byte[]{'4','5','6','7'}, in, 4);
        readAndAssertArray(new byte[]{'8','9'}, in, 2);
        in.reset(); // go back to '4'
        readAndAssertArray(new byte[]{'4','5','6','7'}, in, 4);
    }

    @Test
    public void readArr_markBoundary() throws IOException {
        BufferedBoundaryInputStream in = getBufferedBoundaryInputStream(
                "0123456789#abcdefghijklmn#opqrstuvwxyz", "#");

        readAndAssertArray(new byte[]{'0','1','2','3'}, in, 4);
        in.mark(30); // mark '4'
        readAndAssertArray(new byte[]{'4','5','6','7','8','9'}, in, 6);
        assertEquals(-1, in.read()); // boundary
        in.next();
        readAndAssertArray(new byte[]{'a','b'}, in, 2);
        in.reset(); // go back to '4'
        readAndAssertArray(new byte[]{'4','5','6','7','8','9'}, in, 6);
        assertEquals(-1, in.read()); // is boundary still there
    }

    @Test
    public void readArr_markLargeData() throws IOException {
        BufferedBoundaryInputStream in = getBufferedBoundaryInputStream(
                "0123456789#abcdefghijklmn#opqrstuvwxyz", "#", 10);

        readAndAssertArray(new byte[]{'0','1','2','3'}, in, 4);
        in.mark(30); // mark '4'
        readAndAssertArray(new byte[]{'4','5','6','7','8','9'}, in, 6);
        assertEquals(-1, in.read()); // boundary
        in.next();
        readAndAssertArray(new byte[]{'a','b','c','d','e','f'}, in, 6);
        try{
            in.reset(); // Exception we have passed buffer limit
            fail();
        } catch (IOException e){}
        readAndAssertArray(new byte[]{'g','h'}, in, 2);
    }
}
