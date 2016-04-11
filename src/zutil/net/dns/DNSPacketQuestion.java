/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Ziver Koc
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

package zutil.net.dns;

import zutil.parser.binary.BinaryFieldData;
import zutil.parser.binary.BinaryFieldSerializer;
import zutil.parser.binary.BinaryStruct;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Ziver on 2016-02-09.
 * Reference: http://tools.ietf.org/html/rfc1035
 */
public class DNSPacketQuestion implements BinaryStruct {

    /*
    Question section format
                                        1  1  1  1  1  1
          0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
        |                                               |
        /                     QNAME                     /
        /                                               /
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
        |                     QTYPE                     |
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
        |                     QCLASS                    |
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+

    where:

    QNAME           a domain name represented as a sequence of labels, where
                    each label consists of a length octet followed by that
                    number of octets.  The domain name terminates with the
                    zero length octet for the null label of the root.  Note
                    that this field may be an odd number of octets; no
                    padding is used.

    QTYPE           a two octet code which specifies the type of the query.
                    The values for this field include all codes valid for a
                    TYPE field, together with some more general codes which
                    can match more than one type of RR.

    QCLASS          a two octet code that specifies the class of the query.
                    For example, the QCLASS field is IN for the Internet.
    */

    @CustomBinaryField(index=10, serializer=QNameSerializer.class)
    private String qName;
    @BinaryField(index=10, length=1)
    private int qType;
    @BinaryField(index=20, length=1)
    private int qClass;




    private static class QNameSerializer implements BinaryFieldSerializer<String> {

        public String read(InputStream in, BinaryFieldData field) throws IOException {
            StringBuilder str = new StringBuilder();
            int c = in.read();
            while (c > 0){
                for (int i=0; i<c; ++i){
                    str.append((char)in.read());
                }
                c = in.read();
                if (c > 0)
                    str.append('.');
            }
            return toString();
        }

        public void write(OutputStream out, String domain, BinaryFieldData field) throws IOException {
            String[] labels = domain.split(".");
            for (String label : labels){
                out.write(label.length());
                out.write(label.getBytes());
            }
            out.write(0);
        }

    }
}
