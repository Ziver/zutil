package zutil;

import junit.framework.TestCase;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class ClassUtilTest extends TestCase {

    public void testGetAllDeclaredFields() {
        List<Field> fields = ClassUtil.getAllDeclaredFields(TestClass.class);
        List<String> fieldNames = getFieldNames(fields);

        assertEquals(4, fields.size());
        assertTrue(fieldNames.contains("superPrivateInt"));
        assertTrue(fieldNames.contains("superPublicInt"));
        assertTrue(fieldNames.contains("protectedInt"));
        assertTrue(fieldNames.contains("publicInt"));

        fields = ClassUtil.getAllDeclaredFields(TestClass.class, TestSuperClass.class);
        fieldNames = getFieldNames(fields);

        assertEquals(2, fields.size());
        assertFalse(fieldNames.contains("superPrivateInt"));
        assertFalse(fieldNames.contains("superPublicInt"));
        assertTrue(fieldNames.contains("protectedInt"));
        assertTrue(fieldNames.contains("publicInt"));
    }

    // ----------------------------------------------------
    // Utilities
    // ----------------------------------------------------

    private List<String> getFieldNames(List<Field> fields) {
        List<String> names = new ArrayList<>();

        for (Field field : fields)
            names.add(field.getName());
        return names;
    }

    // ----------------------------------------------------
    // Test Classes
    // ----------------------------------------------------

    public static class TestSuperClass {
        private int superPrivateInt = 10;
        public int superPublicInt = 11;
    }

    public static class TestClass extends TestSuperClass {
        protected int protectedInt = 20;
        public int publicInt = 21;
    }
}