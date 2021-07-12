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

package zutil.parser.json;

import org.junit.Assert;
import org.junit.Test;
import zutil.io.StringOutputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


public class JSONSerializerTest {

    @Test
    public void testOutputSerializerWithPrimitives() throws IOException {
        TestClass sourceObj = new TestClass().init();

        String data = writeObjectToJson(sourceObj);
        data = data.replace("\"", "'");

        assertThat(data, containsString("'str': 'abcd'"));
        assertThat(data, containsString("'value': 42"));
        assertThat(data, containsString("'decimal': 1.1"));
        assertThat(data, containsString("'testEnum': 'ENUM2'"));
    }

    @Test
    public void testInputSerializerWithPrimitives() throws IOException {
        TestClass sourceObj = new TestClass().init();

        TestClass targetObj = sendReceiveObject(sourceObj);

        TestClass.assertEquals(sourceObj, targetObj);
    }

    @Test
    public void testOutputSerializerWithClones() throws IOException {
        TestClassObjClone sourceObj = new TestClassObjClone().init();

        String data = writeObjectToJson(sourceObj);
        data = data.replace("\"", "'");

        assertThat(data, containsString("'@class': 'zutil.parser.json.JSONSerializerTest$TestClassObjClone'"));
        assertThat(data, containsString("'obj1': {'@class': 'zutil.parser.json.JSONSerializerTest$TestObj', '@object_id': 2, 'value': 42}"));
        assertThat(data, containsString("'obj2': {'@object_id': 2}"));
    }

    @Test
    public void testInputSerializerWithClones() throws IOException {
        TestClassObjClone sourceObj = new TestClassObjClone().init();

        TestClassObjClone targetObj = sendReceiveObject(sourceObj);

        assertEquals( sourceObj, targetObj );
    }

    @Test
    public void testOutputSerializerWithArrays() throws InterruptedException, IOException, ClassNotFoundException{
        TestClassArray sourceObj = new TestClassArray().init();

        String data = writeObjectToJson(sourceObj);
        data = data.replace("\"", "'");

        assertEquals(
                "{'@class': 'zutil.parser.json.JSONSerializerTest$TestClassArray', 'array': [1, 2, 3, 4], '@object_id': 1}",
                data);
    }

    @Test
    public void testInputSerializerWithArrays() throws IOException {
        TestClassArray sourceObj = new TestClassArray().init();

        TestClassArray targetObj = sendReceiveObject(sourceObj);
        assertEquals( sourceObj, targetObj );
    }

    @Test
    public void testInputSerializerWithStringArrays() throws IOException {
        TestClassStringArray sourceObj = new TestClassStringArray().init();

        TestClassStringArray targetObj = sendReceiveObject(sourceObj);
        assertEquals( sourceObj, targetObj );
    }

    @Test
    public void testSerializerWithNullFieldsHidden() throws IOException {
        TestClass sourceObj = new TestClass();

        String data = writeObjectToJson(sourceObj, false);
        data = data.replace("\"", "'");
        assertEquals(
                "{'decimal': 0.0}",
                data);

        TestClass targetObj = sendReceiveObject(sourceObj);
        TestClass.assertEquals(sourceObj, targetObj);
    }

    @Test
    public void testSerializerWithMapField() throws IOException {
        TestClassMap sourceObj = new TestClassMap().init();

        TestClassMap targetObj = sendReceiveObject(sourceObj);
        TestClassMap.assertEquals(sourceObj, targetObj);
    }

    @Test
    public void testSerializerWithMapFieldWithNullEntry() throws IOException {
        TestClassMap sourceObj = new TestClassMap().init();
        sourceObj.map.put("key1", null);

        TestClassMap targetObj = sendReceiveObject(sourceObj);
        sourceObj.map.remove("key1"); // key1 should not be set in destination
        TestClassMap.assertEquals(sourceObj, targetObj);
    }

    @Test
    public void testSerializerWithListField() throws IOException {
        TestClassList sourceObj = new TestClassList().init();

        String data = writeObjectToJson(sourceObj);
        data = data.replace("\"", "'");
        assertThat(data, containsString("'list': ['value1', 'value2']"));

        TestClassList targetObj = sendReceiveObject(sourceObj);
        TestClassList.assertEquals(sourceObj, targetObj);
    }

    @Test
    public void testSerializerWithMultipleObj() throws IOException {
        TestClass sourceObj1 = new TestClass().init();
        TestClass sourceObj2 = new TestClass().init();

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        JSONObjectOutputStream out = new JSONObjectOutputStream(buffer);
        out.enableMetaData(false);
        out.writeObject(sourceObj1);
        out.writeObject(sourceObj2);
        out.flush();

        JSONObjectInputStream in = new JSONObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
        TestClass targetObj1 = in.readObject(TestClass.class);
        TestClass.assertEquals(sourceObj1, targetObj1);
        TestClass targetObj2 = in.readObject(TestClass.class);
        TestClass.assertEquals(sourceObj2, targetObj2);
    }

    /******************* Utility Functions ************************************/

    static <T> T sendReceiveObject(T sourceObj) throws IOException {
        return readObjectFromJson(
                writeObjectToJson(sourceObj));
    }

    @SuppressWarnings("unchecked")
    static <T> T readObjectFromJson(String json) throws IOException {
        StringReader bin = new StringReader(json);
        JSONObjectInputStream in = new JSONObjectInputStream(bin);
        T targetObj = (T) in.readObject();
        in.close();

        return targetObj;
    }

    static <T> String writeObjectToJson(T sourceObj) throws IOException {
        return writeObjectToJson(sourceObj, true);
    }
    private static <T> String writeObjectToJson(T sourceObj, boolean metadata) throws IOException {
        StringOutputStream bout = new StringOutputStream();
        JSONObjectOutputStream out = new JSONObjectOutputStream(bout);
        out.enableMetaData(metadata);

        out.writeObject(sourceObj);
        out.flush();
        out.close();

        return bout.toString();
    }

    /******************** Test Classes ************************************/

    public enum TestEnum {
        ENUM1, ENUM2, ENUM3
    }

    public static class TestClass implements Serializable {
        private static final long serialVersionUID = 1L;
        String str;
        double decimal;
        TestObj obj1;
        TestObj obj2;
        TestEnum testEnum;

        public TestClass init() {
            this.str = "abcd";
            this.decimal = 1.1;
            this.obj1 = new TestObj().init();
            this.obj2 = new TestObj().init();
            this.testEnum = TestEnum.ENUM2;
            return this;
        }

        public static void assertEquals(TestClass obj1, TestClass obj2) {
            Assert.assertEquals(obj1.str, obj2.str);
            Assert.assertEquals(obj1.decimal, obj2.decimal, 0.001);
            Assert.assertEquals(obj1.obj1, obj2.obj1);
            Assert.assertEquals(obj1.obj2, obj2.obj2);
            Assert.assertEquals(obj1.testEnum, obj2.testEnum);
        }
    }

    public static class TestClassObjClone {
        TestObj obj1;
        TestObj obj2;

        public TestClassObjClone init() {
            this.obj1 = this.obj2 = new TestObj().init();
            return this;
        }

        public boolean equals(Object obj) {
            return obj instanceof TestClassObjClone &&
                    this.obj1.equals(((TestClassObjClone)obj).obj1) &&
                    this.obj1 == this.obj2 &&
                    ((TestClassObjClone)obj).obj1 == ((TestClassObjClone)obj).obj2;
        }
    }

    public static class TestObj implements Serializable {
        private static final long serialVersionUID = 1L;
        int value;

        public TestObj init() {
            this.value = 42;
            return this;
        }

        public boolean equals(Object obj) {
            return obj instanceof TestObj &&
                    this.value == ((TestObj)obj).value;
        }
    }

    public static class TestClassArray {
        private int[] array;

        public TestClassArray init() {
            array = new int[]{1,2,3,4};
            return this;
        }

        public boolean equals(Object obj) {
            return obj instanceof TestClassArray &&
                    Arrays.equals(this.array ,((TestClassArray)obj).array);
        }
    }

    public static class TestClassStringArray {
        private String[] array;

        public TestClassStringArray init() {
            array = new String[]{"test","string","array"};
            return this;
        }

        public boolean equals(Object obj) {
            return obj instanceof TestClassStringArray &&
                    Arrays.equals(this.array ,((TestClassStringArray)obj).array);
        }
    }

    public static class TestClassMap {
        private HashMap<String,String> map;

        public TestClassMap init(){
            map = new HashMap<>();
            map.put("key1", "value1");
            map.put("key2", "value2");
            return this;
        }

        public static void assertEquals(TestClassMap obj1, TestClassMap obj2) {
            Assert.assertEquals(obj1.map, obj2.map);
        }
    }

    public static class TestClassList {
        private ArrayList<String> list;

        public TestClassList init() {
            list = new ArrayList<>();
            list.add("value1");
            list.add("value2");
            return this;
        }

        public static void assertEquals(TestClassList obj1, TestClassList obj2) {
            Assert.assertEquals(obj1.list, obj2.list);
        }
    }
}
