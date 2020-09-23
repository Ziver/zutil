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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class MultiMapTest {

    @Test
    public void size() {
        MultiMap<String,String> map = new MultiMap<>();
        assertEquals(0, map.size());

        map.put("test", "test");
        assertEquals(1, map.size());

        map.put("test", "test");
        assertEquals(1, map.size());
    }

    @Test
    public void isEmpty() {
        MultiMap<String,String> map = new MultiMap<>();
        assertTrue(map.isEmpty());

        map.put("test", "test");
        assertFalse(map.isEmpty());
    }

    @Test
    public void containsKey() {
        MultiMap<String,String> map = new MultiMap<>();
        assertEquals(false, map.containsKey("test"));

        map.put("test", "test");
        assertEquals(true, map.containsKey("test"));

        map.put("test", "test2");
        assertEquals(true, map.containsKey("test"));

        map.remove("test");
        assertEquals(false, map.containsKey("test"));
    }

    @Test
    public void containsValue() {
        MultiMap<String,String> map = new MultiMap<>();
        assertEquals(false, map.containsValue("value"));
        assertEquals(false, map.containsValue("value2"));

        map.put("test", "value");
        assertEquals(true, map.containsValue("value"));
        assertEquals(false, map.containsValue("value2"));

        map.put("test", "value2");
        assertEquals(true, map.containsValue("value2"));
    }

    @Test
    public void put() {
        MultiMap<String,String> map = new MultiMap<>();
        assertNull(map.put("test", "value"));
        assertEquals("value", map.put("test", "value2"));
    }

    @Test
    public void putAll() {
        Map<String,String> sourceMap = new HashMap<>();
        sourceMap.put("test", "value");
        sourceMap.put("test2", "value2");

        MultiMap<String,String> map = new MultiMap<>();
        map.putAll(sourceMap);

        assertEquals("value", map.get("test"));
        assertEquals("value2", map.get("test2"));

        MultiMap<String,String> sourceMap2 = new MultiMap<>();
        sourceMap2.put("test", "value");
        sourceMap2.put("test2", "value2");
        sourceMap2.put("test2", "value3");

        MultiMap<String,String> map2 = new MultiMap<>();
        map2.putAll(sourceMap2);

        assertEquals("value", map2.get("test"));
        assertEquals(Arrays.asList("value"), map2.getAll("test"));
        assertEquals(Arrays.asList("value2", "value3"), map2.getAll("test2"));
    }

    @Test
    public void remove() {
        MultiMap<String,String> map = new MultiMap<>();
        assertEquals(null, map.remove("test"));

        map.put("test", "value");
        assertEquals("value", map.remove("test"));
        assertEquals(null, map.get("test"));

        map.put("test", "value");
        map.put("test", "value2");
        assertEquals("value", map.remove("test"));
        assertEquals(null, map.get("test"));
    }

    @Test
    public void clear() {
        MultiMap<String,String> map = new MultiMap<>();
        map.put("test", "value");
        map.put("test", "value2");
        map.put("test3", "value3");

        assertEquals(false, map.isEmpty());

        map.clear();

        assertEquals(true, map.isEmpty());
    }

    @Test
    public void get() {
        MultiMap<String,String> map = new MultiMap<>();
        assertEquals(null, map.get("test"));

        assertEquals(null, map.put("test", "value"));
        assertEquals("value", map.get("test"));

        assertEquals("value", map.put("test", "value2"));
        assertEquals("value2", map.get("test"));
    }

    @Test
    public void getAll() {
        MultiMap<String,String> map = new MultiMap<>();
        assertEquals(Arrays.asList(), map.getAll("test"));

        map.put("test", "value");
        assertEquals(Arrays.asList("value"), map.getAll("test"));

        map.put("test", "value2");
        assertEquals(Arrays.asList("value", "value2"), map.getAll("test"));
    }

    @Test
    public void keySet() {
        MultiMap<String,String> map = new MultiMap<>();
        assertEquals(true, map.keySet().isEmpty());

        map.put("test", "value");
        assertEquals(1, map.keySet().size());
        assertEquals(true, map.keySet().contains("test"));

        map.put("test", "value2");
        assertEquals(1, map.keySet().size());
        assertEquals(true, map.keySet().contains("test"));
    }

    @Test
    public void values() {
        MultiMap<String,String> map = new MultiMap<>();
        assertEquals(Arrays.asList(), map.values());

        map.put("test", "value");
        assertEquals(Arrays.asList("value"), map.values());

        map.put("test", "value2");
        assertEquals(Arrays.asList("value", "value2"), map.values());
    }
}