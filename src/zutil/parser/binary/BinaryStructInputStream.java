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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * A stream class that parses a byte stream into
 * binary struct objects.
 *
 * @author Ziver
 */
public class BinaryStructInputStream {

    private InputStream in;
    private byte data;
    private int dataBitIndex = -1;

    public BinaryStructInputStream(InputStream in){
        this.in = in;
    }


    /**
     * Parses a byte array and assigns all fields in the struct
     */
    public static int read(BinaryStruct struct, byte[] data) {
        return read(struct, data, 0, data.length);
    }
    /**
     * Parses a byte array and assigns all fields in the struct
     */
    public static int read(BinaryStruct struct, byte[] data, int offset, int length) {
        int read = 0;
        try {
            ByteArrayInputStream buffer = new ByteArrayInputStream(data, offset, length);
            BinaryStructInputStream in = new BinaryStructInputStream(buffer);
            read = in.read(struct);
        } catch (Exception e){
            e.printStackTrace();
        }
        return read;
    }

    /**
     * Reads the given struct from the input stream
     */
    public int read(BinaryStruct struct) throws IOException {
        List<BinaryFieldData> structDataList = BinaryFieldData.getStructFieldList(struct.getClass());

        int totalReadLength = 0;
        for (BinaryFieldData field : structDataList){
            if (field.getSerializer() != null){
                Object value = field.getSerializer().read(in, field);
                field.setValue(struct, value);
            }
            else {
                byte[] valueData = new byte[(int) Math.ceil(field.getBitLength(struct) / 8.0)];
                int fieldReadLength = 0; // How much we have read so far
                int shiftBy = shiftLeftBy(dataBitIndex, field.getBitLength(struct));

                // Parse value
                for (int valueDataIndex=valueData.length-1; valueDataIndex >= 0 ; --valueDataIndex) {
                    if (dataBitIndex < 0) { // Read new data?
                        data = (byte) in.read();
                        dataBitIndex = 7;
                    }
                    int subBitLength = Math.min(dataBitIndex + 1, field.getBitLength(struct) - fieldReadLength);
                    valueData[valueDataIndex] = ByteUtil.getBits(data, dataBitIndex, subBitLength);
                    fieldReadLength += subBitLength;
                    dataBitIndex -= subBitLength;
                }
                // Set value
                ByteUtil.shiftLeft(valueData, shiftBy); // shift data so that LSB is at the beginning
                field.setByteValue(struct, valueData);
                totalReadLength += fieldReadLength;
            }
        }

        return totalReadLength;
    }

    /**
     * @see InputStream#markSupported()
     */
    public boolean markSupported(){
        return in.markSupported();
    }
    /**
     * @see InputStream#mark(int)
     */
    public void mark(int limit){
        in.mark(limit);
    }
    /**
     * @see InputStream#reset()
     */
    public void reset() throws IOException {
        in.reset();
    }



    protected static int shiftLeftBy(int bitIndex, int bitLength){
        int shiftBy = (8 - ((7-bitIndex) + bitLength) % 8) % 8;
        return shiftBy;
    }
}
