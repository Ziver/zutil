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

import zutil.ByteUtil;
import zutil.converter.Converter;
import zutil.parser.binary.BinaryFieldData;
import zutil.parser.binary.BinaryFieldSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.nio.charset.StandardCharsets;

/**
 * Serializer handles data that is prefixed by two byte length. Null objects will be prefixed by two zero bytes indicating length 0.
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
        int b1, b2;
        if ((b1 = in.read()) < 0)
            throw new StreamCorruptedException("Stream ended prematurely when reading first length byte.");
        if ((b2 = in.read()) < 0)
            throw new StreamCorruptedException("Stream ended prematurely when reading second length byte.");

        int length = Converter.toInt(new byte[]{
                (byte) (0XFF & b2),
                (byte) (0xFF & b1)
        });

        byte[] payload = new byte[length];
        in.read(payload);

        if (field.getType().isAssignableFrom(String.class))
            return new String(payload, StandardCharsets.UTF_8);
        return payload;
    }

    @Override
    public void write(OutputStream out, Object obj, BinaryFieldData field) throws IOException {
        if (obj == null) {
            out.write(0);
            out.write(0);
            return;
        }

        byte[] payload;
        if (obj instanceof String)
            payload = ((String) obj).getBytes(StandardCharsets.UTF_8);
        else if (obj instanceof byte[])
            payload = (byte[]) obj;
        else
            throw new UnsupportedOperationException("Class type not supported for serialization: " + obj.getClass().getSimpleName());

        int length = payload.length;

        out.write((length & 0xFF00) >> 8);
        out.write(length & 0xFF);
        out.write(payload);
    }
}
