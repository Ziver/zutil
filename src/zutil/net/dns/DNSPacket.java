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
import zutil.parser.binary.BinaryStruct.*;

/**
 * Created by Ziver on 2016-02-09.
 * Reference: http://tools.ietf.org/html/rfc1035
 */
public class DNSPacket implements BinaryStruct {
    public static final int OPCODE_QUERY = 0;
    public static final int OPCODE_IQUERY = 1;
    public static final int OPCODE_STATUS = 2;

    public static final int RCODE_NO_ERROR = 0;
    public static final int RCODE_FORMAT_ERROR = 1;
    public static final int RCODE_SERVER_FAILURE = 2;
    public static final int RCODE_NAME_ERROR = 3;
    public static final int RCODE_NOT_IMPLEMENTED = 4;
    public static final int RCODE_REFUSED = 5;


    @BinaryField(index=0, length=16)
    int id;

    //////////////// FLAGS
    //@BinaryField(index=10, length=16)
    //int flags;
    /**
     * A one bit field that specifies whether this message is a
     * query (0), or a response (1).
     */
    @BinaryField(index=10, length=1)
    boolean flagQueryResponse;
    /**
     * A four bit field that specifies kind of query in this message.
     * <pre>
     * 0    a standard query (QUERY)
     * 1    an inverse query (IQUERY)
     * 2    a server status request (STATUS)
     * </pre>
     */
    @BinaryField(index=11, length=4)
    int flagOperationCode;
    /**
     * This bit is valid in responses,
     * and specifies that the responding name server is an
     * authority for the domain name in question section.
     */
    @BinaryField(index=12, length=1)
    boolean flagAuthoritativeAnswer;
    /**
     * specifies that this message was truncated
     * due to length greater than that permitted on the
     * transmission channel.
     */
    @BinaryField(index=13, length=1)
    boolean flagTruncation;
    /**
     * this bit may be set in a query and
     * is copied into the response.  If RD is set, it directs
     * the name server to pursue the query recursively.
     * Recursive query support is optional.
     */
    @BinaryField(index=14, length=1)
    boolean flagRecursionDesired;
    /**
     * this be is set or cleared in a
     * response, and denotes whether recursive query support is
     * available in the name server.
     */
    @BinaryField(index=15, length=1)
    boolean flagRecursionAvailable;
    /**
     * Reserved for future use.  Must be zero in all queries
     * and responses.
     */
    @BinaryField(index=16, length=3)
    private int z;
    /**
     * this field is set as part of responses.
     * The values have the following interpretation:
     * <pre>
     * 0    No error condition
     * 1    Format error - The name server was
     *      unable to interpret the query.
     * 2    Server failure - The name server was
     *      unable to process this query due to a
     *      problem with the name server.
     * 3    Name Error - Meaningful only for
     *      responses from an authoritative name
     *      server, this code signifies that the
     *      domain name referenced in the query does
     *      not exist.
     * 4    Not Implemented - The name server does
     *      not support the requested kind of query.
     * 5    Refused - The name server refuses to
     *      perform the specified operation for
     *      policy reasons.
     *</pre>
     */
    @BinaryField(index=17, length=4)
    int flagResponseCode;


    //////////////// COUNTS
    /**
     * Question Count.
     * Specifying the number of entries in the question section.
     */
    @BinaryField(index=20, length=16)
    int countQuestion;
    /**
     * Answer Record Count.
     * specifying the number of resource records in
     * the answer section.
     */
    @BinaryField(index=21, length=16)
    int countAnswerRecord;
    /**
     * Name Server (Authority Record) Count.
     * Specifying the number of name server resource records
     * in the authority records section.
     */
    @BinaryField(index=22, length=16)
    int countNameServer;
    /**
     * Additional Record Count.
     * Specifying the number of resource records in the
     * additional records section
     */
    @BinaryField(index=23, length=16)
    int countAdditionalRecord;



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

    /*
    Resource record format

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

    NAME            a domain name to which this resource record pertains.

    TYPE            two octets containing one of the RR type codes.  This
                    field specifies the meaning of the data in the RDATA
                    field.

    CLASS           two octets which specify the class of the data in the
                    RDATA field.

    TTL             a 32 bit unsigned integer that specifies the time
                    interval (in seconds) that the resource record may be
                    cached before it should be discarded.  Zero values are
                    interpreted to mean that the RR can only be used for the
                    transaction in progress, and should not be cached.

    RDLENGTH        an unsigned 16 bit integer that specifies the length in
                    octets of the RDATA field.

    RDATA           a variable length string of octets that describes the
                    resource.  The format of this information varies
                    according to the TYPE and CLASS of the resource record.
                    For example, the if the TYPE is A and the CLASS is IN,
                    the RDATA field is a 4 octet ARPA Internet address.
     */
}
