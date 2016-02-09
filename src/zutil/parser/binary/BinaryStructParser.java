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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import zutil.ByteUtil;
import zutil.converters.Converter;
import zutil.parser.binary.BinaryStruct.*;

/**
 * Created by Ziver on 2016-01-28.
 */
public class BinaryStructParser {

    public static int parse(BinaryStruct struct, byte[] data) {
        List<BinaryFieldData> structDataList = getStructDataList(struct.getClass());
        int bitOffset = 0;
        for (BinaryFieldData field : structDataList){
            bitOffset += field.setValue(struct, data, bitOffset);
        }
        return bitOffset;
    }


    private static List<BinaryFieldData> getStructDataList(Class<? extends BinaryStruct> clazz){
        ArrayList<BinaryFieldData> list = new ArrayList<>();
        for (Field field : clazz.getFields()){
            if (field.isAnnotationPresent(BinaryField.class))
                list.add(new BinaryFieldData(field));
        }
        Collections.sort(list);
        return list;
    }



    public static class BinaryFieldData implements Comparable<BinaryFieldData> {
        private int index;
        private int length;
        private Field field;

        protected BinaryFieldData(Field f){
            field = f;
            BinaryField fieldData = field.getAnnotation(BinaryField.class);
            index = fieldData.index();
            length = fieldData.length();
        }

        protected int setValue(Object obj, byte[] data, int bitOffset){
            try {
                int byteIndex = bitOffset / 8;
                int bitIndex = 7 - bitOffset % 8;
                int bitLength = Math.min(bitIndex+1, length);

                int readLength = 0;
                byte[] valueData = new byte[(int) Math.ceil(length / 8.0)];
                for (int index = 0; index < valueData.length; ++index) {
                    valueData[index] = ByteUtil.getShiftedBits(data[byteIndex], bitIndex, bitLength);
                    readLength += bitLength;
                    byteIndex++;
                    bitIndex = 7;
                    bitLength = Math.min(bitIndex+1, length - readLength);
                }

                field.setAccessible(true);
                if (field.getType() == Boolean.class || field.getType() == boolean.class)
                    field.set(obj, valueData[0] != 0);
                else if (field.getType() == Integer.class || field.getType() == int.class)
                    field.set(obj, Converter.toInt(valueData));
                else if (field.getType() == String.class)
                    field.set(obj, new String(valueData));
                return readLength;
            } catch (IllegalAccessException e){
                e.printStackTrace();
            }
            return length; // we return the configured length to not shift the data
        }

        @Override
        public int compareTo(BinaryFieldData o) {
            return this.index - o.index;
        }
    }
}
