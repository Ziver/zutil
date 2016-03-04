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
import zutil.converter.Converter;
import zutil.parser.binary.BinaryStruct.BinaryField;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * A stream class that parses a byte stream into
 * binary struct objects.
 *
 * @author Ziver
 */
public class BinaryStructInputStream {

    public static int parse(BinaryStruct struct, byte[] data) {
        List<BinaryFieldData> structDataList = BinaryFieldData.getStructFieldList(struct.getClass());
        int bitOffset = 0;
        for (BinaryFieldData field : structDataList){

            int byteIndex = bitOffset / 8;
            int bitIndex = 7 - bitOffset % 8;
            int bitLength = Math.min(bitIndex+1, field.getBitLength());

            int readLength = 0;
            byte[] valueData = new byte[(int) Math.ceil(field.getBitLength() / 8.0)];
            for (int index = 0; index < valueData.length; ++index) {
                valueData[index] = ByteUtil.getShiftedBits(data[byteIndex], bitIndex, bitLength);
                readLength += bitLength;
                byteIndex++;
                bitIndex = 7;
                bitLength = Math.min(bitIndex+1, field.getBitLength() - readLength);
            }
            field.setValue(struct, valueData);
            bitOffset += field.getBitLength();
        }
        return bitOffset;
    }




}
