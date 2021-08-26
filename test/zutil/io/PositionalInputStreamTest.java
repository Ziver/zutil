package zutil.io;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;


public class PositionalInputStreamTest {

    @Test
    public void read() throws IOException {
        PositionalInputStream in = new PositionalInputStream(new StringInputStream("hello"));

        assertEquals(0, in.getPosition());
        assertEquals('h', (char) in.read());
        assertEquals(1, in.getPosition());
        assertEquals('e', (char) in.read());
        assertEquals(2, in.getPosition());
        assertEquals('l', (char) in.read());
        assertEquals(3, in.getPosition());
        assertEquals('l', (char) in.read());
        assertEquals(4, in.getPosition());
        assertEquals('o', (char) in.read());
        assertEquals(5, in.getPosition());
        assertEquals(-1, in.read());
        assertEquals(5, in.getPosition());
        assertEquals(-1, in.read());
        assertEquals(5, in.getPosition());
    }

    @Test
    public void readArray() throws IOException {
        PositionalInputStream in = new PositionalInputStream(new StringInputStream("hello world"));

        byte[] buffer = new byte[20];
        assertEquals(5, in.read(buffer, 0, 5));
        assertEquals("hello", new String(buffer, 0, 5));
        assertEquals(5, in.getPosition());

        assertEquals(' ', (char) in.read());
        assertEquals(6, in.getPosition());

        assertEquals(5, in.read(buffer));
        assertEquals("world", new String(buffer, 0, 5));
        assertEquals(11, in.getPosition());

        assertEquals(-1, in.read());
        assertEquals(11, in.getPosition());
    }

    @Test
    public void skip() throws IOException {
        PositionalInputStream in = new PositionalInputStream(new StringInputStream("hello world"));

        assertEquals(5, in.skip(5));
        assertEquals(5, in.getPosition());
        assertEquals(6, in.skip(20));
        assertEquals(11, in.getPosition());
    }

    @Test
    public void mark() throws IOException {
        PositionalInputStream in = new PositionalInputStream(new StringInputStream("hello world"));

        in.mark(20);
        assertEquals(6, in.skip(6));
        assertEquals(6, in.getPosition());
        in.reset();
        assertEquals(0, in.getPosition());
        assertEquals('h', (char) in.read());
        assertEquals(1, in.getPosition());
    }
}