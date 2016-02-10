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

package zutil.test;

import org.junit.Test;
import zutil.ByteUtil;

import static org.junit.Assert.assertEquals;

/**
 * Created by Ziver on 2016-01-31.
 */
public class ByteUtilTest {


    @Test
    public void getShiftedBits(){
        assertEquals(1, ByteUtil.getShiftedBits((byte)0b1000_0000, 7, 1));
        assertEquals(1, ByteUtil.getShiftedBits((byte)0b0001_0000, 4, 1));
        assertEquals(1, ByteUtil.getShiftedBits((byte)0b0000_0001, 0, 1));

        assertEquals(3, ByteUtil.getShiftedBits((byte)0b0110_0000, 6, 2));

        assertEquals((byte)0xFF, ByteUtil.getShiftedBits((byte)0b1111_1111, 7, 8));
    }
}
