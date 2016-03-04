package zutil.parser.binary;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import zutil.converter.Converter;
import zutil.parser.binary.BinaryStruct.BinaryField;

/**
 * A class representing each field in a BinaryStruct.
 */
public class BinaryFieldData implements Comparable<BinaryFieldData> {
    private static final HashMap<Class, List<BinaryFieldData>> cache = new HashMap<>();

    private int index;
    private int length;
    private Field field;


    protected static List<BinaryFieldData> getStructFieldList(Class<? extends BinaryStruct> clazz){
        if (!cache.containsKey(clazz)) {
            ArrayList<BinaryFieldData> list = new ArrayList<>();
            for (Field field : clazz.getFields()) {
                if (field.isAnnotationPresent(BinaryField.class))
                    list.add(new BinaryFieldData(field));
            }
            Collections.sort(list);
            cache.put(clazz, list);
        }
        return cache.get(clazz);
    }


    private BinaryFieldData(Field f){
        field = f;
        BinaryField fieldData = field.getAnnotation(BinaryField.class);
        index = fieldData.index();
        length = fieldData.length();
    }

    protected void setValue(Object obj, byte[] data){
        try {
            field.setAccessible(true);
            if (field.getType() == Boolean.class || field.getType() == boolean.class)
                field.set(obj, data[0] != 0);
            else if (field.getType() == Integer.class || field.getType() == int.class)
                field.set(obj, Converter.toInt(data));
            else if (field.getType() == String.class)
                field.set(obj, new String(data));
        } catch (IllegalAccessException e){
            e.printStackTrace();
        }
    }

    // TODO: variable length support
    protected byte[] getValue(Object obj){
        try {
            if (field.getType() == Boolean.class || field.getType() == boolean.class)
                return new byte[]{ (byte)(field.getBoolean(obj) ? 0x01 : 0x00) };
            else if (field.getType() == Integer.class || field.getType() == int.class)
                return Converter.toBytes(field.getInt(obj));
            else if (field.getType() == String.class)
                return ((String)(field.get(obj))).getBytes();
        } catch (IllegalAccessException e){
            e.printStackTrace();
        }
        return null;
    }


    public int getBitLength(){
        return length;
    }

    @Override
    public int compareTo(BinaryFieldData o) {
        return this.index - o.index;
    }
}