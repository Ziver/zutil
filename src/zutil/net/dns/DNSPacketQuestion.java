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
    /** a host address */
    public static final int QTYPE_A     = 1;
    /** an authoritative name server */
    public static final int QTYPE_NS    = 2;
    /** a mail destination (Obsolete - use MX) */
    public static final int QTYPE_MD    = 3;
    /** a mail forwarder (Obsolete - use MX) */
    public static final int QTYPE_MF    = 4;
    /** the canonical name for an alias */
    public static final int QTYPE_CNAME = 5;
    /** marks the start of a zone of authority */
    public static final int QTYPE_SOA   = 6;
    /** a mailbox domain name (EXPERIMENTAL) */
    public static final int QTYPE_MB    = 7;
    /** a mail group member (EXPERIMENTAL) */
    public static final int QTYPE_MG    = 8;
    /** a mail rename domain name (EXPERIMENTAL) */
    public static final int QTYPE_MR    = 9;
    /** a null RR (EXPERIMENTAL) */
    public static final int QTYPE_NULL  = 10;
    /** a well known service description */
    public static final int QTYPE_WKS   = 11;
    /** a domain name pointer */
    public static final int QTYPE_PTR   = 12;
    /** host information */
    public static final int QTYPE_HINFO = 13;
    /**  mailbox or mail list information */
    public static final int QTYPE_MINFO = 14;
    /** mail exchange */
    public static final int QTYPE_MX    = 15;
    /** text strings */
    public static final int QTYPE_TXT   = 16;
    /**  A request for a transfer of an entire zone */
    public static final int QTYPE_AXFR  = 252;
    /** A request for mailbox-related records (MB, MG or MR) */
    public static final int QTYPE_MAILB = 253;
    /** A request for mail agent RRs (Obsolete - see MX) */
    public static final int QTYPE_MAILA = 254;
    /** A request for all records */
    public static final int QTYPE_ANY   = 255;

    /**  the Internet */
    public static final int QCLASS_IN   = 1;
    /**  the CSNET class (Obsolete - used only for examples in some obsolete RFCs) */
    public static final int QCLASS_CS   = 2;
    /**  the CHAOS class */
    public static final int QCLASS_CH   = 3;
    /**  Hesiod [Dyer 87] */
    public static final int QCLASS_HS   = 4;
    /** any class */
    public static final int QCLASS_ANY  = 255;

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
    @CustomBinaryField(index=10, serializer=DomainStringSerializer.class)
    private String qName;

    /**
     * a two octet code which specifies the type of the query.
     * The values for this field include all codes valid for a
     * TYPE field, together with some more general codes which
     * can match more than one type of RR.
     */
    @BinaryField(index=10, length=16)
    private int qType;

    /**
     * a two octet code that specifies the class of the query.
     * For example, the QCLASS field is IN for the Internet.
     */
    @BinaryField(index=20, length=16)
    private int qClass;



    public DNSPacketQuestion() {}
    public DNSPacketQuestion(String qName, int qType, int qClass) {
        this.qName = qName;
        this.qType = qType;
        this.qClass = qClass;
    }





    public static class DomainStringSerializer implements BinaryFieldSerializer<String> {

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
