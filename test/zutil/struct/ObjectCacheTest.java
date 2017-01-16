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
public class ObjectCacheTest {
    public static final String KEY = "key";
    public static final String OBJECT = "object";
    public static final String KEY2 = "key2";
    public static final String OBJECT2 = "object2";


    @Test
    public void emptyCache() throws InterruptedException {
        ObjectCache cache = new ObjectCache(10);
        assertFalse(cache.containsKey(KEY));
        assertEquals(0, cache.size());
    }

    @Test
    public void zeroTTL() throws InterruptedException {
        ObjectCache cache = new ObjectCache(0);
        cache.put(KEY, OBJECT);
        assertEquals(1, cache.size());
        Thread.sleep(1);
        assertFalse(cache.containsKey(KEY));
    }

    @Test
    public void tenMsTTL() throws InterruptedException {
        ObjectCache cache = new ObjectCache(10);
        cache.put(KEY, OBJECT);
        assertEquals(OBJECT, cache.get(KEY));
        Thread.sleep(1);
        assertTrue(cache.containsKey(KEY));
        Thread.sleep(10);
        assertFalse(cache.containsKey(KEY));
        assertEquals(0, cache.size());
    }

    @Test
    public void oneSecTTL() throws InterruptedException {
        ObjectCache cache = new ObjectCache(1000);
        cache.put(KEY, OBJECT);
        Thread.sleep(1);
        assertTrue(cache.containsKey(KEY));
    }

    //@Test
    // This TC does not work
    public void javaGRC() throws InterruptedException {
        ObjectCache cache = new ObjectCache(10000);
        {
            String tmp = new String("temporary obj");
            cache.put(KEY, tmp);
            assertEquals(1, cache.size());
        }
        System.gc(); // will probably not do anything
        Thread.sleep(10);
        assertEquals(1, cache.garbageCollect());
    }

    @Test
    public void grc() throws InterruptedException {
        ObjectCache cache = new ObjectCache(10);
        cache.put(KEY, OBJECT);
        cache.put(KEY2, OBJECT2);
        assertEquals(2, cache.size());
        Thread.sleep(20);
        assertEquals(2, cache.size());
        assertEquals(2, cache.garbageCollect());
        assertEquals(0, cache.size());
    }
}