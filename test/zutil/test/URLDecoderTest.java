package zutil.test;

import org.junit.Test;
import zutil.parser.URLDecoder;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;


/**
 * Created by ezivkoc on 2015-12-11.
 */
public class URLDecoderTest {

    @Test
    public void simpleTest(){
        assertEquals(null, URLDecoder.decode(null));
        assertEquals("", URLDecoder.decode(""));
        assertEquals("space space", URLDecoder.decode("space space"));
        assertEquals("space space", URLDecoder.decode("space+space"));
        assertEquals("space space", URLDecoder.decode("space%20space"));
    }

    @Test
    public void percentTest(){
        assertEquals("test+", URLDecoder.decode("test%2B"));
        assertEquals("test%2", URLDecoder.decode("test%2"));
        assertEquals("test+test", URLDecoder.decode("test%2Btest"));
        assertEquals("test+test", URLDecoder.decode("test%2btest"));
    }

    @Test
    public void percentMultibyteTest() throws UnsupportedEncodingException {
        assertEquals("Ängen", java.net.URLDecoder.decode("%C3%84ngen", "UTF-8"));
        assertEquals("Ängen", URLDecoder.decode("%C3%84ngen"));
    }
}
