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
 * @see <a href="http://tools.ietf.org/html/rfc1035">DNS Spec (rfc1035)</a>
 * @author Ziver
 */
public class DnsPacketQuestion implements BinaryStruct {

    /*
    Question section format
                                        1  1  1  1  1  1
          0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
        |                                               |
        /                      NAME                     /
        /                                               /
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
        |                      TYPE                     |
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
        |                      CLASS                    |
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+

    where:
    */

    /**
     * a domain name represented as a sequence of labels, where
     * each label consists of a length octet followed by that
     * number of octets.  The domain name terminates with the
     * zero length octet for the null label of the root.  Note
     * that this field may be an odd number of octets; no
     * padding is used.
     */
    @CustomBinaryField(index=10, serializer=FQDNStringSerializer.class)
    public String name;

    /**
     * a two octet code which specifies the type of the query.
     * The values for this field include all codes valid for a
     * TYPE field, together with some more general codes which
     * can match more than one type of RR.
     *
     * @see DnsConstants.TYPE
     */
    @BinaryField(index=10, length=16)
    public int type;

    /**
     * a two octet code that specifies the class of the query.
     * For example, the QCLASS field is IN for the Internet.
     *
     * @see DnsConstants.CLASS
     */
    @BinaryField(index=20, length=16)
    public int clazz;



    public DnsPacketQuestion() {}
    public DnsPacketQuestion(String name, int type, int clazz) {
        this.name = name;
        this.type = type;
        this.clazz = clazz;
    }





    public static class FQDNStringSerializer implements BinaryFieldSerializer<String> {

        public String read(InputStream in, BinaryFieldData field) throws IOException {
            StringBuilder str = new StringBuilder();
            int c = in.read();
            // Is this a pointer
            if ((c & 0b1100_0000) == 0b1100_0000 ){
                int offset = (c & 0b0011_1111) << 8;
                offset |= in.read() & 0b1111_1111;
                str.append(offset);
            }
            // Normal Domain String
            else {
                while (c > 0) {
                    for (int i = 0; i < c; ++i) {
                        str.append((char) in.read());
                    }
                    c = in.read();
                    if (c > 0)
                        str.append('.');
                }
            }
            return str.toString();
        }

        public void write(OutputStream out, String domain, BinaryFieldData field) throws IOException {
            if (domain != null){
                String[] labels = domain.split("\\.");
                for (String label : labels) {
                    out.write(label.length());
                    out.write(label.getBytes());
                }
            }
            out.write(0);
        }

    }
}
