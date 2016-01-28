package zutil.parser.binary;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ezivkoc on 2016-01-28.
 */
public interface BinaryStruct {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface BinaryField{
        int index();
        int length();
    }
}
