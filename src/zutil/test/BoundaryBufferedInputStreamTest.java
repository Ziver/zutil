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
		for(n=0; in.read() != -1 ;n++);
		assertEquals(3, n);
		
		in.next();
		n = 0;
		for(n=0; in.read() != -1 ;n++);
		assertEquals(16, n);
		
		in.next();
		n = 0;
		for(n=0; in.read() != -1 ;n++);
		assertEquals(15, n);
		
		in.next();
		assertEquals(-1, in.read());
		
	}

	@Test
	public void testReadByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testReadByteArrayIntInt() {
		fail("Not yet implemented");
	}

}
