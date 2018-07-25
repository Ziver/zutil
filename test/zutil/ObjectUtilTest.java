package zutil;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class ObjectUtilTest {

    @Test
    public void isEmpty() {
        assertTrue(ObjectUtil.isEmpty(null));
        assertTrue(ObjectUtil.isEmpty(""));
        assertTrue(ObjectUtil.isEmpty(new StringBuffer()));
        assertTrue(ObjectUtil.isEmpty(new StringBuilder()));
        assertTrue(ObjectUtil.isEmpty(new ArrayList<>()));
        assertTrue(ObjectUtil.isEmpty(new LinkedList<>()));
        assertTrue(ObjectUtil.isEmpty(new HashMap<>()));
        assertTrue(ObjectUtil.isEmpty(new Hashtable<>()));


        assertFalse(ObjectUtil.isEmpty(" "));
        assertFalse(ObjectUtil.isEmpty("a"));
        assertFalse(ObjectUtil.isEmpty(new StringBuilder("a")));
        assertFalse(ObjectUtil.isEmpty(new StringBuffer("a")));
        assertFalse(ObjectUtil.isEmpty(Arrays.asList(1, 2, 3)));
    }
}