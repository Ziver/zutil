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

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


/**
 * Created by Ziver on 2015-11-20.
 */
public class TimedHashSetTest {
    public static final String ENTRY = "key";
    public static final String ENTRY2 = "key2";

    @Test
    public void zeroTTL() throws InterruptedException {
        TimedHashSet set = new TimedHashSet(0);
        set.add(ENTRY);
        Thread.sleep(1);
        assertFalse(set.contains(ENTRY));
    }

    @Test
    public void tenMsTTL() throws InterruptedException {
        TimedHashSet set = new TimedHashSet(10);
        set.add(ENTRY);
        assertTrue(set.contains(ENTRY));
        Thread.sleep(15);
        assertFalse(set.contains(ENTRY));
    }

    @Test
    public void oneSecTTL() throws InterruptedException {
        TimedHashSet set = new TimedHashSet(1000);
        set.add(ENTRY);
        Thread.sleep(1);
        assertTrue(set.contains(ENTRY));
    }

    @Test
    public void grc() throws InterruptedException {
        TimedHashSet set = new TimedHashSet(10);
        set.add(ENTRY);
        set.add(ENTRY2);
        assertEquals(2, set.size());
        Thread.sleep(20);
        assertEquals(2, set.size());
        assertEquals(2, set.garbageCollect());
        assertEquals(0, set.size());
    }
}