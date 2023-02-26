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

package zutil;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class ObjectUtilTest {

    @Test
    public void isEmptyArr() {
        assertTrue(ObjectUtil.isEmpty());
        assertTrue(ObjectUtil.isEmpty(null, null));
        assertTrue(ObjectUtil.isEmpty("", "", ""));
        assertTrue(ObjectUtil.isEmpty(new StringBuffer(), ""));
        assertTrue(ObjectUtil.isEmpty("", new StringBuilder()));
        assertTrue(ObjectUtil.isEmpty(new ArrayList<>(), ""));
        assertTrue(ObjectUtil.isEmpty(new LinkedList<>(), ""));
        assertTrue(ObjectUtil.isEmpty(new HashMap<>(), ""));
        assertTrue(ObjectUtil.isEmpty(new Hashtable<>(), ""));
        assertTrue(ObjectUtil.isEmpty((Object) new String[0]));


        assertFalse(ObjectUtil.isEmpty(" ", ""));
        assertFalse(ObjectUtil.isEmpty("a", ""));
        assertFalse(ObjectUtil.isEmpty("", new StringBuilder("a")));
        assertFalse(ObjectUtil.isEmpty("", new StringBuffer("a")));
        assertFalse(ObjectUtil.isEmpty("", Arrays.asList(1, 2, 3)));
        assertFalse(ObjectUtil.isEmpty((Object) new String[]{"a"}));
    }

    @Test
    public void isEmpty() {
        assertTrue(ObjectUtil.isEmpty((String) null));
        assertTrue(ObjectUtil.isEmpty(""));
        assertTrue(ObjectUtil.isEmpty(new StringBuffer()));
        assertTrue(ObjectUtil.isEmpty(new StringBuilder()));
        assertTrue(ObjectUtil.isEmpty(new ArrayList<>()));
        assertTrue(ObjectUtil.isEmpty(new LinkedList<>()));
        assertTrue(ObjectUtil.isEmpty(new HashMap<>()));
        assertTrue(ObjectUtil.isEmpty(new Hashtable<>()));


        assertFalse(ObjectUtil.isEmpty(" "));
        assertFalse(ObjectUtil.isEmpty("a"));
        assertFalse(ObjectUtil.isEmpty(new StringBuilder("a")));
        assertFalse(ObjectUtil.isEmpty(new StringBuffer("a")));
        assertFalse(ObjectUtil.isEmpty(Arrays.asList(1, 2, 3)));
    }
}