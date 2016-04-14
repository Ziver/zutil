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
