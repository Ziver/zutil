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

package zutil.log;

import org.junit.Test;
import zutil.log.CounterManager.Counter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class CounterManagerTest {

    @Test
    public void singletonTest(){
        Counter c1 = CounterManager.getCounter("TestCounter");
        Counter c2 = CounterManager.getCounter("TestCounter");
        assertSame(c1, c2);

        c1 = CounterManager.getCounter("TestCounter");
        c2 = CounterManager.getCounter(CounterManagerTest.class, "TestCounter");
        assertSame(c1, c2);

        c1 = CounterManager.getCounter(CounterManagerTest.class, "TestCounter");
        c2 = CounterManager.getCounter(CounterManagerTest.class, "TestCounter");
        assertSame(c1, c2);
    }

    @Test
    public void addTest(){
        Counter c1 = CounterManager.getCounter("AddTestCounter");
        assertEquals(0, c1.getValue());

        c1.add(10);
        assertEquals(10, c1.getValue());

        c1.add(20);
        c1.add(30);
        assertEquals(60, c1.getValue());
    }

    @Test
    public void setTest(){
        Counter c1 = CounterManager.getCounter("SetTestCounter");
        assertEquals(0, c1.getValue());

        c1.set(10);
        assertEquals(10, c1.getValue());

        c1.set(20);
        c1.set(30);
        assertEquals(30, c1.getValue());
    }

    @Test
    public void incrementTest(){
        Counter c1 = CounterManager.getCounter("IncrementTestCounter");
        assertEquals(0, c1.getValue());

        c1.increment();
        assertEquals(1, c1.getValue());

        c1.increment();
        c1.increment();
        c1.increment();
        assertEquals(4, c1.getValue());
    }

    @Test
    public void decrementTest(){
        Counter c1 = CounterManager.getCounter("DecrementTestCounter");
        assertEquals(0, c1.getValue());

        c1.decrement();
        assertEquals(-1, c1.getValue());

        c1.decrement();
        c1.decrement();
        c1.decrement();
        assertEquals(-4, c1.getValue());
    }

    @Test
    public void maxTest(){
        Counter c1 = CounterManager.getCounter("MaxTestCounter");
        assertEquals(0, c1.getMax());

        c1.set(10);
        assertEquals(10, c1.getMax());

        c1.set(50);
        assertEquals(50, c1.getMax());

        c1.set(0);
        assertEquals(50, c1.getMax());
    }

    @Test
    public void minTest(){
        Counter c1 = CounterManager.getCounter("MinTestCounter");
        assertEquals(0, c1.getMin());

        c1.set(10);
        assertEquals(0, c1.getMin());

        c1.set(-50);
        assertEquals(-50, c1.getMin());

        c1.set(0);
        assertEquals(-50, c1.getMin());
    }

    @Test
    public void averageTest(){
        Counter c1 = CounterManager.getCounter("MinTestCounter");
        assertEquals(0, (int)c1.getAverage());

        c1.set(10);
        assertEquals(10, (int)c1.getAverage());

        c1.set(10);
        assertEquals(10, (int)c1.getAverage());

        c1.set(70);
        assertEquals(30, (int)c1.getAverage());
    }

}