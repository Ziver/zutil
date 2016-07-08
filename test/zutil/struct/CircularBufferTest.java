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

package zutil.struct;

import org.junit.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


/**
 * Created by Ziver on 2015-09-22.
 */
public class CircularBufferTest {

    @Test
    public void addToEmpty() {
        CircularBuffer<Integer> buff = new CircularBuffer<Integer>(0);
        try {
            buff.add(10);
            fail("IndexOutOfBoundsException was not thrown");
        } catch (IndexOutOfBoundsException e) {}
    }

    @Test
    public void addOneElement() {
        CircularBuffer<Integer> buff = new CircularBuffer<Integer>(1);
        assertEquals(0, buff.size());
        buff.add(10);
        assertEquals(1, buff.size());
        assertEquals((Integer) 10, buff.get(0));
    }

    @Test
    public void addThreeElements() {
        CircularBuffer<Integer> buff = new CircularBuffer<Integer>(10);
        buff.add(10);
        buff.add(11);
        buff.add(12);
        assertEquals(3, buff.size());
        assertEquals((Integer) 12, buff.get(0));
        assertEquals((Integer) 11, buff.get(1));
        assertEquals((Integer) 10, buff.get(2));
    }

    @Test
    public void addOutOfRange() {
        CircularBuffer<Integer> buff = new CircularBuffer<Integer>(2);
        buff.add(10);
        buff.add(11);
        buff.add(12);
        assertEquals(2, buff.size());
        assertEquals((Integer) 12, buff.get(0));
        assertEquals((Integer) 11, buff.get(1));
        try {
            buff.get(2);
            fail("IndexOutOfBoundsException was not thrown");
        } catch (IndexOutOfBoundsException e) {}
    }

    @Test(expected = NoSuchElementException.class)
    public void iteratorEmpty() {
        CircularBuffer<Integer> buff = new CircularBuffer<Integer>(10);
        Iterator<Integer> it = buff.iterator();

        assert (!it.hasNext());
        it.next(); // Exception
    }

    @Test(expected = NoSuchElementException.class)
    public void iteratorThreeElements() {
        CircularBuffer<Integer> buff = new CircularBuffer<Integer>(10);
        buff.add(10);
        buff.add(11);
        buff.add(12);

        Iterator<Integer> it = buff.iterator();
        assert (it.hasNext());
        assertEquals((Integer) 12, it.next());
        assert (it.hasNext());
        assertEquals((Integer) 11, it.next());
        assert (it.hasNext());
        assertEquals((Integer) 10, it.next());
        assert (!it.hasNext());

        it.next(); // Exception
    }
}
