package zutil.io;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Ziver on 2016-07-12.
 */
public class IOUtilTest {

    @Test
    public void readLine() throws IOException {
        StringInputStream in = new StringInputStream("test\ntest2\ntest3");

        assertEquals("test", IOUtil.readLine(in));
        assertEquals("test2", IOUtil.readLine(in));
        assertEquals("test3", IOUtil.readLine(in));
        assertNull(IOUtil.readLine(in));
    }
    @Test
    public void readLineCarriageReturn() throws IOException {
        StringInputStream in = new StringInputStream("test\r\ntest2\r\n");

        assertEquals("test", IOUtil.readLine(in));
        assertEquals("test2", IOUtil.readLine(in));
        assertNull(IOUtil.readLine(in));
    }
}
