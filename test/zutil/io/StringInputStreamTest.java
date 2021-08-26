package zutil.io;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;


public class StringInputStreamTest {

    @Test
    public void read() throws IOException {
        StringInputStream in = new StringInputStream("hello");

        assertEquals('h', (char) in.read());
        assertEquals('e', (char) in.read());
        assertEquals('l', (char) in.read());
        assertEquals('l', (char) in.read());
        assertEquals('o', (char) in.read());
        assertEquals(-1, in.read());
        assertEquals(-1, in.read());
    }

    @Test
    public void readArray() throws IOException {
        StringInputStream in = new StringInputStream("hello world");

        byte[] buffer = new byte[20];
        assertEquals(5, in.read(buffer, 0, 5));
        assertEquals("hello", new String(buffer, 0, 5));

        assertEquals(' ', (char) in.read());

        assertEquals(5, in.read(buffer));
        assertEquals("world", new String(buffer, 0, 5));

        assertEquals(-1, in.read());
    }

    @Test
    public void skip() {
        StringInputStream in = new StringInputStream("hello world");

        assertEquals(5, in.skip(5));
        assertEquals(6, in.skip(20));
    }

    @Test
    public void mark() throws IOException {
        StringInputStream in = new StringInputStream("hello world");

        in.mark(20);
        assertEquals(6, in.skip(6));
        in.reset();
        assertEquals('h', (char) in.read());
    }
}