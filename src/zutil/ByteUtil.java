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

package zutil;

/**
 * Utility functions for byte primitive type
 *
 * Created by Ziver on 2016-01-30.
 */
public class ByteUtil {
    /** Bitmask array used by utility functions **/
    private static final int[][] BYTE_MASK = new int[][]{
            {0b0000_0001},
            {0b0000_0010, 0b0000_0011},
            {0b0000_0100, 0b0000_0110, 0b0000_0111},
            {0b0000_1000, 0b0000_1100, 0b0000_1110, 0b0000_1111},
            {0b0001_0000, 0b0001_1000, 0b0001_1100, 0b0001_1110, 0b0001_1111},
            {0b0010_0000, 0b0011_0000, 0b0011_1000, 0b0011_1100, 0b0011_1110, 0b0011_1111},
            {0b0100_0000, 0b0110_0000, 0b0111_0000, 0b0111_1000, 0b0111_1100, 0b0111_1110, 0b0111_1111},
            {0b1000_0000, 0b1100_0000, 0b1110_0000, 0b1111_0000, 0b1111_1000, 0b1111_1100, 0b1111_1110, 0b1111_1111}
    };

    /**
     * Creates a new sub byte from index and with a length and shifts the data to the left
     *
     * @param   data    is the byte data
     * @param   index   is the bit index, valid values 0-7
     * @param   length  is the length of bits to return, valid values 1-8
     * @return a new byte containing a sub byte defined by the index and length
     */
    public static byte getShiftedBits(byte data, int index, int length){
        int ret = 0xFF & getBits(data, index, length);
        ret = ret >>> index+1-length;
        return (byte) ret;
    }

    /**
     * Creates a new sub byte from index and with a length
     *
     * @param   data    is the byte data
     * @param   index   is the bit index, valid values 0-7
     * @param   length  is the length of bits to return, valid values 1-8
     * @return a new byte containing a sub byte defined by the index and length
     */
    public static byte getBits(byte data, int index, int length){
        byte ret = (byte) (data & getBitMask(index, length));
        return  ret;
    }

    /**
     * Returns a byte bitmask
     *
     * @param   index   start index of the mask, valid values 0-7
     * @param   length  length of mask from index, valid values 1-8 depending on index
     */
    public static byte getBitMask(int index, int length) {
        --length;
        if(0 > index || index > 7)
            throw new IllegalArgumentException("Invalid index argument, allowed value is 0-7");
        if(length < 0 || 7-index-length < 0)
            throw new IllegalArgumentException("Invalid length argument: "+length+", allowed values 1-8 depending on index");
        return (byte) BYTE_MASK[index][length];
    }
}
