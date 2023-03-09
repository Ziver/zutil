package zutil;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class ArrayUtilTest {

    @Test
    public void toIntArray() {
        assertArrayEquals(new int[]{}, ArrayUtil.toIntArray(Collections.emptyList()));
        assertArrayEquals(new int[]{1, 2, 3}, ArrayUtil.toIntArray(Arrays.asList(1, 2, 3)));
    }

    @Test
    public void contains() {
        assertFalse(ArrayUtil.contains(new Integer[]{}, 1));
        assertTrue(ArrayUtil.contains(new Integer[]{1}, 1));
        assertTrue(ArrayUtil.contains(new Integer[]{2, 1, 3}, 1));
    }

    @Test
    public void combine() {
        assertArrayEquals(new Integer[]{}, ArrayUtil.combine(new Integer[]{}, new Integer[]{}));
        assertArrayEquals(new Integer[]{1, 2}, ArrayUtil.combine(new Integer[]{1, 2}, new Integer[]{}));
        assertArrayEquals(new Integer[]{1, 2, 3, 4}, ArrayUtil.combine(new Integer[]{1, 2}, new Integer[]{3, 4}));

        assertArrayEquals(new int[]{}, ArrayUtil.combine(new int[]{}, new int[]{}));
        assertArrayEquals(new int[]{1, 2}, ArrayUtil.combine(new int[]{1, 2}, new int[]{}));
        assertArrayEquals(new int[]{1, 2, 3, 4}, ArrayUtil.combine(new int[]{1, 2}, new int[]{3, 4}));

        assertArrayEquals(new byte[]{}, ArrayUtil.combine(new byte[]{}, new byte[]{}));
        assertArrayEquals(new byte[]{1, 2}, ArrayUtil.combine(new byte[]{1, 2}, new byte[]{}));
        assertArrayEquals(new byte[]{1, 2, 3, 4}, ArrayUtil.combine(new byte[]{1, 2}, new byte[]{3, 4}));
    }
}