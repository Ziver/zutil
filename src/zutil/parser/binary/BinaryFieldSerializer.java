package zutil.parser.binary;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * An Interface where custom field parser and writer can be implemented.
 * NOTE: Partial octet serializing not supported.
 *
 * Created by Ziver on 2016-04-11.
 */
public interface BinaryFieldSerializer<T> {

    T read(InputStream in,
           BinaryFieldData field) throws IOException;

    void write(OutputStream out,
               T obj,
               BinaryFieldData field) throws IOException;
}
