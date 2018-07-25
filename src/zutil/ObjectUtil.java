package zutil;

import java.util.List;
import java.util.Map;

/**
 * A utility class containing general object functions
 */
public class ObjectUtil {

    /**
     * @return true if obj is null or empty String, List, Map
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null)
            return true;

        if (obj instanceof Map)
            return ((Map) obj).isEmpty();
        else if (obj instanceof List)
            return ((List) obj).isEmpty();
        else if (obj instanceof CharSequence)
            return ((CharSequence) obj).length() == 0;

        return false; // We don't know the type of class, but it is not null
    }
}
