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
