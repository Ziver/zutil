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

package zutil;

import java.util.List;

/**
 * A  utility class containing Array specific utility methods
 */
public class ArrayUtil {

    /**
     * Converts a List with Integer objects to a primary type int array
     */
    public static int[] toIntArray(List<Integer> list){
        if (list == null)
            return null;
        int[] arr = new int[list.size()];
        int i = 0;
        for (Integer v : list)
            arr[i++] = v;
        return arr;
    }

    /**
     * Searches for a given object inside of an array.
     * The method uses reference comparison or {@link #equals(Object)} to check for equality.
     *
     * @return True if the given Object is found inside the array, false otherwise.
     */
    public static <T> boolean contains(T[] array, T obj) {
        for (final T element : array)
            if (element == obj || obj != null && obj.equals(element))
                return true;
        return false;
    }
}
