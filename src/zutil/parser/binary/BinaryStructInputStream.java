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

package zutil.parser.binary;

import zutil.ByteUtil;
import zutil.io.PositionalInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A stream class that parses a byte stream into binary struct objects.
 * <p>
 * Limitations:<br>
 *  - Does not support sub binary objects.<br>
 *
 * @author Ziver
 */
public class BinaryStructInputStream extends InputStream{

    private InputStream in;
    private byte data;
    private int dataBitIndex = -1;

    private Map<Class, BinaryFieldSerializer> serializerCache;


    public BinaryStructInputStream(InputStream in) {
        this.in = in;

        enableSerializerCache(true);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return read;
    }

    /**
     * Reads the given struct from the input stream
     */
    public int read(BinaryStruct struct) throws IOException {
        List<BinaryFieldData> structDataList = BinaryFieldData.getStructFieldList(struct.getClass());
        PositionalInputStream positionalInputStream = (in instanceof PositionalInputStream ? (PositionalInputStream) in : new PositionalInputStream(in));
        long startPos = positionalInputStream.getPosition();

        for (BinaryFieldData field : structDataList) {
            if (field.hasSerializer()) {
                // Handle serializer cache

                BinaryFieldSerializer serializer = (serializerCache != null ? serializerCache.get(field.getSerializerClass()) : null);
                if (serializer == null) {
                    serializer = field.getSerializer();

                    if (serializerCache != null)
                        serializerCache.put(serializer.getClass(), serializer);
                }

                // Read in field through serializer

                Object value = serializer.read(positionalInputStream, field, struct);
                field.setValue(struct, value);
            } else {
                byte[] valueData = new byte[(int) Math.ceil(field.getBitLength(struct) / 8.0)];
                int fieldReadLength = 0; // How much we have read so far
                int shiftBy = shiftLeftBy(dataBitIndex, field.getBitLength(struct));

                // Parse value
                for (int valueDataIndex=valueData.length-1; valueDataIndex >= 0; --valueDataIndex) {
                    if (dataBitIndex < 0) { // Read new data?
                        data = (byte) positionalInputStream.read();
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
            }
        }

        return (int) (positionalInputStream.getPosition() - startPos);
    }

    @Override
    public int read() throws IOException {
        return in.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return in.read(b, off, len);
    }

    @Override
    public int available() throws IOException {
        return in.available();
    }

    /**
     * @see InputStream#markSupported()
     */
    @Override
    public boolean markSupported() {
        return in.markSupported();
    }

    /**
     * @see InputStream#mark(int)
     */
    @Override
    public void mark(int limit) {
        in.mark(limit);
    }
    /**
     * @see InputStream#reset()
     */
    @Override
    public void reset() throws IOException {
        in.reset();
    }


    /**
     * Enable or disable the caching of serializer objects. If disabled then
     * a new instance of the serializer will be created every time it is needed.
     * <p>
     * By default, caching is enabled.
     *
     * @param enabled set true to enable caching or false to disable.
     */
    public void enableSerializerCache(boolean enabled) {
        if (enabled) {
            serializerCache = new HashMap<>();
        } else {
            serializerCache = null;
        }
    }

    /**
     * Method will clear all cached instances of serializer objects.
     */
    public void clearSerializerCache() {
        if (serializerCache != null) {
            serializerCache.clear();
        }
    }

    protected static int shiftLeftBy(int bitIndex, int bitLength) {
        return (8 - ((7-bitIndex) + bitLength) % 8) % 8;
    }
}
