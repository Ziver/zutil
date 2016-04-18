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

import zutil.parser.binary.BinaryStruct;


/**
 * Created by Ziver on 2016-02-09.
 * Reference: http://tools.ietf.org/html/rfc1035
 */
public class DNSPacketResource implements BinaryStruct {

    /** a host address */
    public static final int TYPE_A     = 1;
    /** an authoritative name server */
    public static final int TYPE_NS    = 2;
    /** a mail destination (Obsolete - use MX) */
    public static final int TYPE_MD    = 3;
    /** a mail forwarder (Obsolete - use MX) */
    public static final int TYPE_MF    = 4;
    /** the canonical name for an alias */
    public static final int TYPE_CNAME = 5;
    /** marks the start of a zone of authority */
    public static final int TYPE_SOA   = 6;
    /** a mailbox domain name (EXPERIMENTAL) */
    public static final int TYPE_MB    = 7;
    /** a mail group member (EXPERIMENTAL) */
    public static final int TYPE_MG    = 8;
    /** a mail rename domain name (EXPERIMENTAL) */
    public static final int TYPE_MR    = 9;
    /** a null RR (EXPERIMENTAL) */
    public static final int TYPE_NULL  = 10;
    /** a well known service description */
    public static final int TYPE_WKS   = 11;
    /** a domain name pointer */
    public static final int TYPE_PTR   = 12;
    /** host information */
    public static final int TYPE_HINFO = 13;
    /**  mailbox or mail list information */
    public static final int TYPE_MINFO = 14;
    /** mail exchange */
    public static final int TYPE_MX    = 15;
    /** text strings */
    public static final int TYPE_TXT   = 16;

    /**  the Internet */
    public static final int CLASS_IN   = 1;
    /**  the CSNET class (Obsolete - used only for examples in some obsolete RFCs) */
    public static final int CLASS_CS   = 2;
    /**  the CHAOS class */
    public static final int CLASS_CH   = 3;
    /**  Hesiod [Dyer 87] */
    public static final int CLASS_HS   = 4;




    /*
    The answer, authority, and additional sections all share the same
    format: a variable number of resource records, where the number of
    records is specified in the corresponding count field in the header.
    Each resource record has the following format:
                                        1  1  1  1  1  1
          0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
        |                                               |
        /                                               /
        /                      NAME                     /
        |                                               |
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
        |                      TYPE                     |
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
        |                     CLASS                     |
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
        |                      TTL                      |
        |                                               |
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
        |                   RDLENGTH                    |
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--|
        /                     RDATA                     /
        /                                               /
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+

    where:
    */

    /**
     * a domain name to which this resource record pertains.
     */
    @CustomBinaryField(index=10, serializer=DNSPacketQuestion.DomainStringSerializer.class)
    private String name;

    /**
     * two octets containing one of the RR type codes.  This
     * field specifies the meaning of the data in the RDATA
     * field.
     */
    @BinaryField(index=20, length=16)
    private int type;

    /**
     * two octets which specify the class of the data in the
     * RDATA field.
     */
    @BinaryField(index=30, length=16)
    private int clazz;

    /**
     * a 32 bit unsigned integer that specifies the time
     * interval (in seconds) that the resource record may be
     * cached before it should be discarded.  Zero values are
     * interpreted to mean that the RR can only be used for the
     * transaction in progress, and should not be cached.
     */
    @BinaryField(index=40, length=32)
    private int ttl;

    /**
     * an unsigned 16 bit integer that specifies the length in
     * octets of the RDATA field.
     */
    @BinaryField(index=50, length=16)
    private int length;

    /**
     * a variable length string of octets that describes the
     * resource.  The format of this information varies
     * according to the TYPE and CLASS of the resource record.
     * For example, if the TYPE is A and the CLASS is IN,
     * the RDATA field is a 4 octet ARPA Internet address.
     */
    @VariableLengthBinaryField(index=60, lengthField="length")
    private String data;


}
