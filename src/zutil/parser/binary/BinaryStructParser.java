package zutil.parser.binary;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import zutil.parser.binary.BinaryStruct.*;

/**
 * Created by ezivkoc on 2016-01-28.
 */
public class BinaryStructParser {

    public static void parse(BinaryStruct struct, byte[] bytes) {
        List<BinaryFieldData> structDataList = getStructDataList(struct.getClass());
        int bitIndex;
        for(BinaryFieldData field : structDataList){

        }
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
            length = fieldData.index();
        }


        @Override
        public int compareTo(BinaryFieldData o) {
            return this.index - o.index;
        }
    }
}
