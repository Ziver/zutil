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

import zutil.parser.binary.BinaryStruct;

/**
 * @see <a href="http://tools.ietf.org/html/rfc1035">DNS Spec (rfc1035)</a>
 * @author Ziver
 */
public class DnsPacketHeader implements BinaryStruct {

    /*
    Header section format

    The header contains the following fields:

                                        1  1  1  1  1  1
          0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
        |                      ID                       |
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
        |QR|   Opcode  |AA|TC|RD|RA|   Z    |   RCODE   |
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
        |                    QDCOUNT                    |
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
        |                    ANCOUNT                    |
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
        |                    NSCOUNT                    |
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
        |                    ARCOUNT                    |
        +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+

    where:
     */


    @BinaryField(index=0, length=16)
    public int id;

    //////////////// FLAGS
    //@BinaryField(index=10, length=16)
    //int flags;
    /**
     * A one bit field that specifies whether this message is a
     * query (0), or a response (1).
     */
    @BinaryField(index=10, length=1)
    public boolean flagQueryResponse;
    /**
     * A four bit field that specifies kind of query in this message.
     *
     * @see DnsConstants.OPCODE
     */
    @BinaryField(index=11, length=4)
    public int flagOperationCode;
    /**
     * This bit is valid in responses,
     * and specifies that the responding name server is an
     * authority for the domain name in question section.
     */
    @BinaryField(index=12, length=1)
    public boolean flagAuthoritativeAnswer;
    /**
     * specifies that this message was truncated
     * due to length greater than that permitted on the
     * transmission channel.
     */
    @BinaryField(index=13, length=1)
    public boolean flagTruncation;
    /**
     * this bit may be set in a query and
     * is copied into the response.  If RD is set, it directs
     * the name server to pursue the query recursively.
     * Recursive query support is optional.
     */
    @BinaryField(index=14, length=1)
    public boolean flagRecursionDesired;
    /**
     * this be is set or cleared in a
     * response, and denotes whether recursive query support is
     * available in the name server.
     */
    @BinaryField(index=15, length=1)
    public boolean flagRecursionAvailable;
    /**
     * Reserved for future use.  Must be zero in all queries
     * and responses.
     */
    @BinaryField(index=16, length=3)
    protected int z;
    /**
     * this field is set as part of responses.
     * The values have the following interpretation:
     *
     * @see DnsConstants.RCODE
     */
    @BinaryField(index=17, length=4)
    public int flagResponseCode;


    //////////////// COUNTS
    /**
     * Question Count.
     * Specifying the number of entries in the question section.
     */
    @BinaryField(index=20, length=16)
    public int countQuestion;
    /**
     * Answer Record Count.
     * specifying the number of resource records in
     * the answer section.
     */
    @BinaryField(index=21, length=16)
    public int countAnswerRecord;
    /**
     * Name Server (Authority Record) Count.
     * Specifying the number of name server resource records
     * in the authority records section.
     */
    @BinaryField(index=22, length=16)
    public int countNameServer;
    /**
     * Additional Record Count.
     * Specifying the number of resource records in the
     * additional records section
     */
    @BinaryField(index=23, length=16)
    public int countAdditionalRecord;






    public void setDefaultQueryData() {
        // Set all flags to zero
        flagQueryResponse = false;
        flagOperationCode = 0;
        flagAuthoritativeAnswer = false;
        flagTruncation = false;
        flagRecursionDesired = false;
        flagRecursionAvailable = false;
        z = 0;
        flagResponseCode = 0;
    }

    public void setDefaultResponseData() {
        flagQueryResponse = true;
        flagAuthoritativeAnswer = true;

        // Set the rest to zero
        // TODO: all flags should not be zeroed
        flagOperationCode = 0;
        flagTruncation = false;
        flagRecursionDesired = false;
        flagRecursionAvailable = false;
        z = 0;
        flagResponseCode = 0;
    }
}
