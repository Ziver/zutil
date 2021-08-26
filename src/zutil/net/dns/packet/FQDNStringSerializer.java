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

import zutil.parser.binary.BinaryFieldData;
import zutil.parser.binary.BinaryFieldSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A serializer class that can read and write a DNS FQDN in binary format.
 */
public class FQDNStringSerializer implements BinaryFieldSerializer<String> {

    public String read(InputStream in, BinaryFieldData field) throws IOException {
        StringBuilder str = new StringBuilder();
        int c;

        while ((c=in.read()) > 0) {
            if (str.length() > 0) // Don't add dot to first loop
                str.append('.');

            if ((c & 0b1100_0000) == 0b1100_0000) {
                // This a offset pointer to the String data
                int offset = (c & 0b0011_1111) << 8;
                offset |= in.read() & 0b1111_1111;
                str.append(offset);
                break; // PTR is always the last part of the FQDN
            } else {
                // Normal String data
                for (int i = 0; i < c; ++i) {
                    str.append((char) in.read());
                }
            }
        }
        return str.toString();
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
