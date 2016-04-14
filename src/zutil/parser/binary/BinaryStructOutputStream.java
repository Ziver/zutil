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

package zutil.parser.binary;

import zutil.ByteUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


/**
 * A stream class that generates a byte stream from
 * binary struct objects.
 *
 * @author Ziver
 */
public class BinaryStructOutputStream {

    private OutputStream out;
    private byte rest;
    private int restBitLength; // length from Most Significant Bit


    public BinaryStructOutputStream(OutputStream out){
        this.out = out;

        rest = 0;
        restBitLength = 0;
    }


    /**
     * Generate a binary byte array from the provided struct.
     * The byte array will be left
     */
    public static byte[] serialize(BinaryStruct struct) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        BinaryStructOutputStream out = new BinaryStructOutputStream(buffer);
        out.write(struct);
        out.flush();
        return buffer.toByteArray();
    }

    /**
     * Generate a binary stream from the provided struct and
     * write the data to the underlying stream.
     */
    public void write(BinaryStruct struct) throws IOException {
        List<BinaryFieldData> structDataList = BinaryFieldData.getStructFieldList(struct.getClass());

        for (BinaryFieldData field : structDataList){
            if (field.getSerializer() != null){
                localFlush();
                field.getSerializer().write(out, field.getValue(struct), field);
            }
            else{
                byte[] data = field.getByteValue(struct);

                int fieldBitLength = field.getBitLength(struct);
                for (int i = (int) Math.ceil(fieldBitLength / 8.0) - 1; fieldBitLength > 0; fieldBitLength -= 8, --i) {
                    byte b = data[i];
                    if (restBitLength == 0 && fieldBitLength >= 8)
                        out.write(0xFF & b);
                    else {
                        b <<= 8 - restBitLength - fieldBitLength;
                        b &= ByteUtil.getBitMask(7 - restBitLength, fieldBitLength);
                        rest |= b;
                        restBitLength += fieldBitLength;
                        if (restBitLength >= 8)
                            localFlush();
                    }
                }
            }
        }
    }


    /**
     * Writes any outstanding data to the stream
     */
    public void flush() throws IOException {
        localFlush();
        out.flush();
    }
    private void localFlush() throws IOException {
        if(restBitLength > 0){
            out.write(0xFF & rest);
            rest = 0;
            restBitLength = 0;
        }
    }

    /**
     * Flushes and closes the underlying stream
     */
    public void close() throws IOException {
        flush();
        out.close();
    }
}
