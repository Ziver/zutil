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
 */
public class DNSPacket implements BinaryStruct {
    public static final int OPCODE_QUERY = 0;
    public static final int OPCODE_IQUERY = 1;
    public static final int OPCODE_STATUS = 2;
    public static final int OPCODE_NOTIFY = 4;
    public static final int OPCODE_UPDATE = 5;

    public static final int RCODE_NO_ERROR = 0;
    public static final int RCODE_FORMAT_ERROR = 1;
    public static final int RCODE_SERVER_FAILURE = 2;
    public static final int RCODE_NAME_ERROR = 3;
    public static final int RCODE_NOT_IMPLEMENTED = 4;
    public static final int RCODE_REFUSED = 5;
    public static final int RCODE_YX_DOMAIN = 6; // A name exists where it should not
    public static final int RCODE_YX_RR_SET = 7; // A resource record set exists where it should not
    public static final int RCODE_NX_RR_SET = 8; // A resource record set should exist but does not
    public static final int RCODE_NOT_AUTH = 9; // Server is not authoritative for the zone
    public static final int RCODE_NOT_ZONE = 10; // The name is not in the zone specified in the message


    @BinaryField(index=0, length=16)
    int id;

    //////////////// FLAGS
    //@BinaryField(index=10, length=16)
    //int flags;
    /**
     * Query/Response Flag.
     * Differentiates between queries and responses.
     * Set to 0 when the query is generated; changed to 1 when that
     * query is changed to a response by a replying server.
     */
    @BinaryField(index=10, length=1)
    boolean qr;
    /**
     * Operation Code.
     * Specifies the type of query in the message. This field is
     * copied unchanged in the response
     */
    @BinaryField(index=11, length=4)
    int opcode;
    /**
     * Authoritative Answer Flag
     * This bit is set to true in a response to indicate that the server
     * that created the response is authoritative for the zone in
     * which the domain name specified in the Question section is located
     */
    @BinaryField(index=12, length=1)
    boolean aa;
    /**
     * Truncation Flag.
     * When set to true, indicates that the message was truncated
     * due to its length
     */
    @BinaryField(index=13, length=1)
    boolean tc;
    /**
     * Recursion Desired.
     * When set in a query, requests that the server receiving the
     * query attempt to answer the query recursively, if the server
     * supports recursive resolution. The value of this bit is not
     * changed in the response.
     */
    @BinaryField(index=14, length=1)
    boolean rd;
    /**
     * Recursion Available.
     * Set to true to indicate whether the server creating the response
     * supports recursive queries.
     */
    @BinaryField(index=15, length=1)
    boolean ra;
    @BinaryField(index=16, length=3)
    int reserved;
    /**
     * Response Code.
     */
    @BinaryField(index=17, length=4)
    int rCode;



    //////////////// COUNTS
    /**
     * Question Count.
     * Specifies the number of questions in the
     * Question section of the message.
     */
    @BinaryField(index=20, length=16)
    int qdCount;
    /**
     * Answer Record Count.
     * Specifies the number of resource records
     * in the Answer section of the message.
     */
    @BinaryField(index=21, length=16)
    int anCount;
    /**
     * Name Server (Authority Record) Count.
     * Specifies the number of resource records in the
     * Authority section of the message.
     */
    @BinaryField(index=22, length=16)
    int nsCount;
    /**
     * Additional Record Count.
     * Specifies the number of resource records in the
     * Additional section of the message.
     */
    @BinaryField(index=23, length=16)
    int arCount;



    //////////////// REQUEST DATA
    // char data[?]
    // char flag[2] => QTYPE=0x00_01 host address query
    //                 QCLASS=0x00_01 internet query
}
