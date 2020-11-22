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
import zutil.ClassUtil;
import zutil.converter.Converter;
import zutil.parser.binary.BinaryStruct.BinaryField;
import zutil.parser.binary.BinaryStruct.CustomBinaryField;
import zutil.parser.binary.BinaryStruct.VariableLengthBinaryField;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * A class representing each field in a BinaryStruct.
 */
public class BinaryFieldData {
    private static final HashMap<Class, List<BinaryFieldData>> cache = new HashMap<>();

    private int index;
    private int length;
    private Field field;
    /* @VariableLengthBinaryField */
    private BinaryFieldData lengthField;
    private int lengthMultiplier;
    /* @CustomBinaryField */
    private BinaryFieldSerializer serializer;


    protected static List<BinaryFieldData> getStructFieldList(Class<? extends BinaryStruct> clazz){
        if (!cache.containsKey(clazz)) {
            try {
                ArrayList<BinaryFieldData> list = new ArrayList<>();
                for(Class<?> cc = clazz; cc != Object.class ;cc = cc.getSuperclass()) { // iterate through all super classes
                    for (Field field : cc.getDeclaredFields()) {
                        if (field.isAnnotationPresent(BinaryField.class) ||
                                field.isAnnotationPresent(CustomBinaryField.class) ||
                                field.isAnnotationPresent(VariableLengthBinaryField.class))

                            list.add(new BinaryFieldData(field));
                    }
                }
                list.sort(new Comparator<BinaryFieldData>() {
                    @Override
                    public int compare(BinaryFieldData o1, BinaryFieldData o2) {
                        return o1.index - o2.index;
                    }
                });
                cache.put(clazz, list);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return cache.get(clazz);
    }


    private BinaryFieldData(Field f) throws IllegalAccessException, InstantiationException, NoSuchFieldException {
        field = f;
        this.length = -1;
        this.lengthField = null;
        this.lengthMultiplier = 1;
        this.serializer = null;
        if (field.isAnnotationPresent(CustomBinaryField.class)){
            CustomBinaryField fieldData = field.getAnnotation(CustomBinaryField.class);
            this.index = fieldData.index();
            this.serializer = fieldData.serializer().newInstance();
        }
        else if (field.isAnnotationPresent(VariableLengthBinaryField.class)) {
            VariableLengthBinaryField fieldData = field.getAnnotation(VariableLengthBinaryField.class);
            this.index = fieldData.index();
            this.lengthMultiplier = fieldData.multiplier();
            this.lengthField = new BinaryFieldData(
                    field.getDeclaringClass().getDeclaredField(fieldData.lengthField()));
            if ( !ClassUtil.isNumber(lengthField.getType()))
                throw new IllegalArgumentException("Length variable for VariableLengthBinaryStruct needs to be of a number type.");
        }
        else {
            BinaryField fieldData = field.getAnnotation(BinaryField.class);
            this.index = fieldData.index();
            this.length = fieldData.length();
        }
    }


    public String getName(){
        return field.getName();
    }
    public Class<?> getType(){
        return field.getType();
    }

    public void setByteValue(Object obj, byte[] data){
        try {
            field.setAccessible(true);
            if (field.getType() == Boolean.class || field.getType() == boolean.class)
                field.setBoolean(obj, data[0] != 0);
            else if (field.getType() == Byte.class || field.getType() == byte.class)
                field.setByte(obj, data[0]);
            else if (field.getType() == Integer.class || field.getType() == int.class)
                field.setInt(obj, Converter.toInt(data));
            else if (field.getType() == String.class)
                field.set(obj, new String(ByteUtil.getReverseByteOrder(data), StandardCharsets.ISO_8859_1));
            else
                throw new UnsupportedOperationException("Unsupported BinaryStruct field class: "+ field.getType());
        } catch (IllegalAccessException e){
            e.printStackTrace();
        }
    }
    public void setValue(Object obj, Object value){
        try {
            field.setAccessible(true);
            field.set(obj, value);
        } catch (IllegalAccessException e){
            e.printStackTrace();
        }
    }

    public byte[] getByteValue(Object obj){
        try {
            field.setAccessible(true);
            if (field.getType() == Boolean.class || field.getType() == boolean.class)
                return ByteUtil.getBits(
                        new byte[]{ (byte)(field.getBoolean(obj) ? 0x01 : 0x00)},
                        getBitLength(obj));
            else if (field.getType() == Byte.class || field.getType() == byte.class)
                return ByteUtil.getBits(
                        new byte[]{field.getByte(obj)},
                        getBitLength(obj));
            else if (field.getType() == Integer.class || field.getType() == int.class)
                return ByteUtil.getBits(
                        Converter.toBytes(field.getInt(obj)),
                        getBitLength(obj));
            else if (field.getType() == String.class)
                return ByteUtil.getReverseByteOrder(
                        ByteUtil.getBits(
                                ((String)(field.get(obj))).getBytes(StandardCharsets.ISO_8859_1),
                                getBitLength(obj)));
            else
                throw new UnsupportedOperationException("Unsupported BinaryStruct field type: "+ getType());
        } catch (IllegalAccessException e){
            e.printStackTrace();
        }
        return null;
    }
    public Object getValue(Object obj){
        try {
            field.setAccessible(true);
            return field.get(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    public int getBitLength(Object obj){
        if(lengthField != null)
            return (int) lengthField.getValue(obj) * lengthMultiplier;
        return length;
    }

    public BinaryFieldSerializer getSerializer(){
        return serializer;
    }


    @Override
    public String toString(){
        return field.getDeclaringClass().getSimpleName() + "::" + field.getName() +
                " (" +
                (lengthField != null ?
                    "LengthField: " + lengthField +", LengthMultiplier: "+lengthMultiplier :
                    length+" bits") +
                (serializer != null ?
                    ", Serializer: " + serializer.getClass().getName() : "") +
                ")";

    }
}