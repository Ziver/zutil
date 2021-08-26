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

package zutil.net.dns.packet;

import zutil.io.PositionalInputStream;
import zutil.parser.binary.BinaryFieldData;
import zutil.parser.binary.BinaryFieldSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * A serializer class that can read and write a DNS FQDN in binary format.
 */
public class FQDNStringSerializer implements BinaryFieldSerializer<String> {
    private HashMap<Integer, String> stringCache = new HashMap<>();


    public String read(InputStream in, BinaryFieldData field) throws IOException {
        StringBuilder buffer = new StringBuilder();
        int pos = (int) ((PositionalInputStream) in).getPosition();
        int c;

        while ((c=in.read()) > 0) {
            if (buffer.length() > 0) // Don't add dot to first loop
                buffer.append('.');

            if ((c & 0b1100_0000) == 0b1100_0000) {
                // This is an offset pointer to the String data
                int offset = (c & 0b0011_1111) << 8;
                offset |= in.read() & 0b1111_1111;

                if (stringCache.containsKey(offset))
                    buffer.append(stringCache.get(offset));
                else
                    buffer.append('<').append(offset).append('>');
                break; // PTR is always the last part of the FQDN
            } else {
                // Read normal String data
                for (int i = 0; i < c; ++i) {
                    buffer.append((char) in.read());
                }
            }
        }

        String output = buffer.toString();

        // Populate cache
        if (in instanceof PositionalInputStream) {
            stringCache.put(pos, output);
            for (int index = 0; index >= 0;) {
                index = buffer.indexOf(".", index);
                if (index >= 0) {
                    ++index;
                    stringCache.put(pos + index, buffer.substring(index));
                }
            }
        }

        return output;
    }

    public void write(OutputStream out, String domain, BinaryFieldData field) throws IOException {
        if (domain != null) {
            String[] labels = domain.split("\\.");
            for (String label : labels) {
                out.write(label.length());
                out.write(label.getBytes());
            }
        }
        out.write(0);
    }
}
