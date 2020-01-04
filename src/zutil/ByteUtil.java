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

import zutil.converter.Converter;

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
     * Creates a new sub byte from index and with the given length length
     *
     * @param   data    is the byte data
     * @param   index   is the bit index, valid values 0-7
     * @param   length  is the length of bits to return, valid values 1-8
     * @return a new byte containing a sub byte defined by the index and length
     */
    public static byte getBits(byte data, int index, int length){
        return (byte) (data & getBitMask(index, length));
    }

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
     * Creates a new sub byte from LSB to the given length
     *
     * @param   data    is the byte data
     * @param   length  is the length of bits to return, valid values 1-8
     * @return a new byte containing a sub byte defined by the index and length
     */
    public static byte getBits(byte data, int length){
        return getBits(data, length-1, length);
    }

    /**
     * Creates a new sub byte array with only the given length of bits from the LSB.
     *
     * @param   data    is the byte data array
     * @param   length  is the length of bits to return
     * @return a new byte array of te given length containing the given data.
     */
    public static byte[] getBits(byte[] data, int length){
        byte[] dest = new byte[(int) Math.ceil(length/8.0)];
        System.arraycopy(data, 0, dest, 0, Math.min(data.length, dest.length));
        if(length % 8 != 0)
            dest[dest.length-1] = getBits(dest[dest.length-1], length % 8);
        return dest;
    }

    /**
     * Creates a new sub byte from MSB to the given length
     *
     * @param   data    is the byte data
     * @param   length  is the length of bits to return, valid values 1-8
     * @return a new byte containing a sub byte defined by the index and length
     */
    public static byte getBitsMSB(byte data, int length){
        return getShiftedBits(data, 7, length);
    }

    /**
     * Creates a new byte array with reversed byte ordering
     * (LittleEndian -&gt; BigEndian, BigEndian -&gt; LittleEndian)
     *
     * @param   data    is the byte array that will be reversed.
     * @return a new byte array that will have the same data but in reverse byte order
     */
    public static byte[] getReverseByteOrder(byte[] data){
        byte[] dest = new byte[data.length];
        if (data.length > 0)
            for (int i=0; i<data.length; ++i)
                dest[dest.length-1-i] = data[i];
        return dest;
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
            throw new IllegalArgumentException("Invalid index argument, allowed values: 0-7");
        if(length < 0 || index-length < 0)
            throw new IllegalArgumentException("Invalid length argument: "+length+", allowed values: 1 to "+(index+1)+" for index "+index);
        return (byte) BYTE_MASK[index][length];
    }


    /**
     * Shifts a whole byte array to the left by the specified amount.
     *
     * @param   data        the array to be shifted
     * @param   shiftBy     the amount to shift. Currently only supports maximum value of 8
     * @return same data reference as the data input
     */
    public static byte[] shiftLeft(byte[] data, int shiftBy) {
        if(0 > shiftBy || shiftBy > 8)
            throw new IllegalArgumentException("Invalid shiftBy("+shiftBy+") argument, allowed values: 0-8");
        if (shiftBy == 0)
            return data;

        byte rest;
        for (int i=0; i<data.length; ++i){
            rest = (byte)(getBits(data[i], shiftBy-1, shiftBy) << 8 - shiftBy);
            data[i] = (byte)((data[i]&0xFF) >>> shiftBy);
            if(i != 0)
                data[i-1] |= rest;
        }

        return data;
    }

    /**
     * Shifts a whole byte array to the right by the specified amount.
     *
     * @param   data        the array to be shifted
     * @param   shiftBy     the amount to shift. Currently only supports maximum value of 8
     * @return same data reference as the data input
     */
    public static byte[] shiftRight(byte[] data, int shiftBy) {
        if(0 > shiftBy || shiftBy > 8)
            throw new IllegalArgumentException("Invalid shiftBy("+shiftBy+") argument, allowed values: 0-8");
        if (shiftBy == 0)
            return data;

        byte rest = 0;
        for (int i=0; i<data.length; ++i){
            byte preRest = getBitsMSB(data[i], shiftBy);
            data[i] = (byte)(data[i] << shiftBy);
            if(i != 0)
                data[i] |= rest;
            rest = preRest;
        }

        return data;
    }


    /**
     * Presents a binary array in HEX and ASCII
     *
     * @param       data     The source binary data to format
     * @return A multiline String with human readable HEX and ASCII
     */
    public static String toFormattedString(byte[] data){
        return toFormattedString(data, 0, data.length);
    }

    /**
     * Presents a binary array in HEX and ASCII
     *
     * @param       data     The source binary data to format
     * @return A multiline String with human readable HEX and ASCII
     */
    public static String toFormattedString(byte[] data, int offset, int length){
        StringBuilder output = new StringBuilder();

        //000  XX XX XX XX XX XX XX XX  '........'
        int maxOffset = (""+length).length();
        for(; offset<length; offset+=8){
            if(offset != 0)
                output.append('\n');

            // Offset
            String offsetStr = ""+offset;
            for(int i=offsetStr.length(); i<3 || i<maxOffset; ++i){
                output.append('0');
            }
            output.append(offsetStr);
            output.append("  ");

            // HEX
            for(int i=0; i<8; ++i){
                if(offset+i < length)
                    output.append(Converter.toHexString(data[offset+i]));
                else
                    output.append("  ");
                output.append(' ');
            }
            output.append(' ');

            // ACII
            output.append('\'');
            for(int i=0; i<8; ++i){
                if(offset+i < length)
                    if( 32 <= data[offset+i] && data[offset+i] <= 126 )
                        output.append((char)data[offset+i]);
                    else
                        output.append('.');
                else
                    output.append(' ');
            }
            output.append('\'');
        }

        return output.toString();
    }
}
