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

package zutil.parser.binary.serializer;

import zutil.parser.binary.BinaryFieldData;
import zutil.parser.binary.BinaryFieldSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.nio.charset.StandardCharsets;

/**
 * Serializer handles data that is prefixed by two byte length.
 * <p>
 * Currently only these types are supported:
 * <ul>
 *     <li>byte[]</li>
 *     <li>String</li>
 * </ul>
 */
public class TwoByteLengthPrefixedDataSerializer implements BinaryFieldSerializer<Object> {

    @Override
    public Object read(InputStream in, BinaryFieldData field) throws IOException {
        int b = in.read();
        if (b < 0)
            throw new StreamCorruptedException("Stream ended prematurely when reading first length byte.");
        int length = (b & 0xFF) << 8;

        b = in.read();
        if (b < 0)
            throw new StreamCorruptedException("Stream ended prematurely when reading second length byte.");
        length |= b & 0xFF;

        byte[] payload = new byte[length];
        in.read(payload);

        if (field.getType().isAssignableFrom(String.class))
            return new String(payload, StandardCharsets.UTF_8);
        return payload;
    }

    @Override
    public void write(OutputStream out, Object obj, BinaryFieldData field) throws IOException {
        if (obj == null)
            return;

        byte[] payload;
        if (obj instanceof String)
            payload = ((String) obj).getBytes(StandardCharsets.UTF_8);
        else
            payload = (byte[]) obj;

        int length = payload.length;

        out.write((length & 0xFF00) >> 8);
        out.write(length & 0xFF);
        out.write(payload);
    }
}
