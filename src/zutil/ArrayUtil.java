package zutil;

/**
 * A  utility class containing Array specific utility methods
 */
public class ArrayUtil {

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
