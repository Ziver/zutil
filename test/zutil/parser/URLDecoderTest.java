/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Ziver Koc
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

package zutil.parser;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class URLDecoderTest {

    @Test
    public void simpleTest() {
        assertNull(URLDecoder.decode(null));
        assertEquals("", URLDecoder.decode(""));
        assertEquals("space space", URLDecoder.decode("space space"));
        assertEquals("space space", URLDecoder.decode("space+space"));
        assertEquals("space space", URLDecoder.decode("space%20space"));
    }

    @Test
    public void percentTest() {
        assertEquals("test+", URLDecoder.decode("test%2B"));
        assertEquals("test%2", URLDecoder.decode("test%2"));
        assertEquals("test+test", URLDecoder.decode("test%2Btest"));
        assertEquals("test+test", URLDecoder.decode("test%2btest"));
    }

    @Test
    public void percentMultibyteTest() throws UnsupportedEncodingException {
        assertEquals("?ngen", java.net.URLDecoder.decode("%3Fngen", "UTF-8"));
        assertEquals("?ngen", URLDecoder.decode("%3Fngen"));
    }
}
