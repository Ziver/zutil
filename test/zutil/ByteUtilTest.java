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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;


/**
 * Created by Ziver on 2016-01-31.
 */
public class ByteUtilTest {


    @Test
    public void getShiftedBits() {
        assertEquals(1, ByteUtil.getShiftedBits((byte)0b1000_0000, 7, 1));
        assertEquals(1, ByteUtil.getShiftedBits((byte)0b0001_0000, 4, 1));
        assertEquals(1, ByteUtil.getShiftedBits((byte)0b0000_0001, 0, 1));

        assertEquals(3, ByteUtil.getShiftedBits((byte)0b0110_0000, 6, 2));

        assertEquals((byte)0xFF, ByteUtil.getShiftedBits((byte)0b1111_1111, 7, 8));
    }


    @Test
    public void getBits() {
        assertEquals(0x01, ByteUtil.getBits((byte)0x11, 1));
        assertEquals(0x03, ByteUtil.getBits((byte)0x13, 4));
        assertEquals((byte)0x55, ByteUtil.getBits((byte)0x55, 8));
    }

    @Test
    public void getBitsMSB() {
        assertEquals(0x01, ByteUtil.getBitsMSB((byte)0x80, 1));
        assertEquals(0x05, ByteUtil.getBitsMSB((byte)0x52, 4));
        assertEquals((byte)0x55, ByteUtil.getBitsMSB((byte)0x55, 8));
        assertEquals((byte)0x03, ByteUtil.getBitsMSB((byte)0xFF, 2));
        assertEquals((byte)0x0F, ByteUtil.getBitsMSB((byte)0xFF, 4));
    }

    @Test
    public void getBitsArray() {
        assertArrayEquals(new byte[]{}, ByteUtil.getBits(new byte[]{0x00}, 0));
        assertArrayEquals(new byte[]{0x00}, ByteUtil.getBits(new byte[]{}, 1));
        assertArrayEquals(new byte[]{0x00,0x00,0x00,0x00}, ByteUtil.getBits(new byte[]{0x00}, 32));
        assertArrayEquals(new byte[]{0x00}, ByteUtil.getBits(new byte[]{0x00,0x10}, 1));
        assertArrayEquals(new byte[]{0x00}, ByteUtil.getBits(new byte[]{0x00,0x10}, 8));
        assertArrayEquals(new byte[]{0x00,0x01}, ByteUtil.getBits(new byte[]{0x00,0x01}, 9));
        assertArrayEquals(new byte[]{0x00,0x01}, ByteUtil.getBits(new byte[]{0x00,0x11}, 9));
    }

    @Test
    public void getReverseByteOrder() {
        assertArrayEquals(new byte[]{}, ByteUtil.getReverseByteOrder(new byte[]{}));
        assertArrayEquals(new byte[]{0x11}, ByteUtil.getReverseByteOrder(new byte[]{0x11}));
        assertArrayEquals(new byte[]{0x22,0x11}, ByteUtil.getReverseByteOrder(new byte[]{0x11,0x22}));
        assertArrayEquals(new byte[]{0x44,0x33,0x22,0x11}, ByteUtil.getReverseByteOrder(new byte[]{0x11,0x22,0x33,0x44}));
    }


    @Test
    public void toFormattedString() {
        byte[] data = new byte[1];
        assertEquals("000  00                       '.       '",
                ByteUtil.toFormattedString(data));

        data[0] = 65;
        assertEquals("000  41                       'A       '",
                ByteUtil.toFormattedString(data));

        byte[] data2 = new byte[8];
        data2[4] = 65;
        assertEquals("000  00 00 00 00 41 00 00 00  '....A...'",
                ByteUtil.toFormattedString(data2));

        byte[] data3 = new byte[32];
        data3[4] = 65;
        assertEquals("000  00 00 00 00 41 00 00 00  '....A...'\n"+
                        "008  00 00 00 00 00 00 00 00  '........'\n"+
                        "016  00 00 00 00 00 00 00 00  '........'\n"+
                        "024  00 00 00 00 00 00 00 00  '........'",
                ByteUtil.toFormattedString(data3));
    }


    @Test
    public void shiftLeft() {
        assertArrayEquals(         new byte[]{},
                ByteUtil.shiftLeft(new byte[]{}, 4));
        assertArrayEquals(         new byte[]{0b0000_0001},
                ByteUtil.shiftLeft(new byte[]{0b0000_0001}, 0));
        assertArrayEquals(         new byte[]{0b0000_0001},
                ByteUtil.shiftLeft(new byte[]{0b0001_0000}, 4));
        assertArrayEquals(         new byte[]{0b0000_1000},
                ByteUtil.shiftLeft(new byte[]{(byte)0b1000_0000}, 4));
        assertArrayEquals(         new byte[]{0b0001_0001, 0b0000_0000},
                ByteUtil.shiftLeft(new byte[]{0b0001_0000, 0b0000_0001}, 4));
        assertArrayEquals(         new byte[]{0b0100_1001, 0b0000_0001},
                ByteUtil.shiftLeft(new byte[]{0b0111_1111, 0b0101_0010}, 6));
        assertArrayEquals(         new byte[]{0b0000_0001,0b0000_0001,0b0000_0001,0b0000_0001},
                ByteUtil.shiftLeft(new byte[]{0b0001_0000,0b0001_0000,0b0001_0000,0b0001_0000}, 4));
    }

    @Test
    public void shiftRight() {
        assertArrayEquals(         new byte[]{},
                ByteUtil.shiftRight(new byte[]{}, 4));
        assertArrayEquals(         new byte[]{0b0000_0001},
                ByteUtil.shiftRight(new byte[]{0b0000_0001}, 0));
        assertArrayEquals(         new byte[]{(byte)0b0001_0000},
                ByteUtil.shiftRight(new byte[]{0b0000_0001}, 4));
        assertArrayEquals(         new byte[]{(byte)0b1000_0000},
                ByteUtil.shiftRight(new byte[]{0b0000_1000}, 4));
        assertArrayEquals(         new byte[]{0b0000_0000},
                ByteUtil.shiftRight(new byte[]{(byte)0b0001_0000}, 4));
        assertArrayEquals(         new byte[]{0b0001_0000, 0b0001_0000},
                ByteUtil.shiftRight(new byte[]{0b0000_0001, 0b0000_0001}, 4));
        assertArrayEquals(         new byte[]{(byte)0b1100_0000, (byte)0b1001_1111},
                ByteUtil.shiftRight(new byte[]{0b0111_1111, 0b0101_0010}, 6));
        assertArrayEquals(         new byte[]{0b0000_0000,0b0000_0001,0b0000_0001,0b0000_0001},
                ByteUtil.shiftRight(new byte[]{0b0001_0000,0b0001_0000,0b0001_0000,0b0001_0000}, 4));
    }
}
