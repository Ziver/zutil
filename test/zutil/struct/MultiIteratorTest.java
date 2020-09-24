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

package zutil.struct;

import org.junit.Test;

import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class MultiIteratorTest {

    @Test(expected = NoSuchElementException.class)
    public void emptyTest() {
        MultiIterator<String> it = new MultiIterator<>();
        assertEquals(false, it.hasNext());
        it.next(); // NoSuchElementException
    }

    @Test
    public void oneIteratorTest() {
        MultiIterator<String> it = new MultiIterator<>();
        it.addIterator(Arrays.asList("value1", "value2").iterator());

        assertEquals(true, it.hasNext());
        assertEquals("value1", it.next());

        assertEquals(true, it.hasNext());
        assertEquals("value2", it.next());

        assertEquals(false, it.hasNext());
    }

    @Test
    public void multiIteratorTest() {
        MultiIterator<String> it = new MultiIterator<>();
        it.addIterator(Arrays.asList("value1", "value2").iterator());
        it.addIterator(Arrays.asList("value3", "value4").iterator());
        it.addIterator(Arrays.asList("value5", "value6").iterator());

        assertEquals("value1", it.next());
        assertEquals("value2", it.next());

        assertEquals(true, it.hasNext());
        assertEquals("value3", it.next());
        assertEquals("value4", it.next());

        assertEquals("value5", it.next());
        assertEquals("value6", it.next());

        assertEquals(false, it.hasNext());
    }
}