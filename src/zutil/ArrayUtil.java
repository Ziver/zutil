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
