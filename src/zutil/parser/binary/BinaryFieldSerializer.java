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

package zutil.parser.binary;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * An Interface defining a custom field parser and writer.
 * <p>
 * A new instance of the serializer will be instantiated for every time serialization is required.
 * {@link BinaryStructInputStream} and {@link BinaryStructOutputStream} objects.
 * <p>
 * NOTE: Partial octet serializing not supported.
 */
public interface BinaryFieldSerializer<T> {

    /**
     * Read the given field from the stream.
     *
     * @param in           the stream where the data should be read from.
     * @param field        meta-data about the target field that will be assigned.
     * @param parentObject the parent object that owns the field.
     * @return the value that should be assigned to the field.
     */
    default T read(InputStream in,
                   BinaryFieldData field,
                   Object parentObject) throws IOException {
        return read(in, field);
    }

    /**
     * Read the given field from the stream.
     *
     * @param in    the stream where the data should be read from.
     * @param field meta-data about the target field that will be assigned.
     * @return the value that should be assigned to the field.
     */
    T read(InputStream in,
           BinaryFieldData field) throws IOException;

    /**
     * Write the given field to the output stream.
     *
     * @param out          the stream where the field data should be written to.
     * @param obj          the object that should be serialized and written to the stream.
     * @param field        meta-data about the source field that will be serialized.
     * @param parentObject the parent object that owns the field.
     */
    default void write(OutputStream out,
                       T obj,
                       BinaryFieldData field,
                       Object parentObject) throws IOException {
        write(out, obj, field);
    }

    /**
     * Write the given field to the output stream.
     *
     * @param out   the stream where the field data should be written to.
     * @param obj   the object that should be serialized and written to the stream.
     * @param field meta-data about the source field that will be serialized.
     */
    void write(OutputStream out,
               T obj,
               BinaryFieldData field) throws IOException;
}
