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
 * An Interface where custom field parser and writer can be implemented.
 * <p></p>
 * One instance of the serializer and will have the scope of the methods
 * {@link BinaryStructInputStream#read(BinaryStruct)} and {@link BinaryStructOutputStream#write(BinaryStruct)}
 * where as it will be deallocated after the methods have returned.
 * <p></p>
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
