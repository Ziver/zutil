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

import org.junit.Test;
import zutil.converter.Converter;
import zutil.parser.binary.BinaryStructInputStream;
import zutil.parser.binary.BinaryStructOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;
import static zutil.net.dns.DNSPacketQuestion.*;
import static zutil.net.dns.DNSPacketResource.*;

/**
 * Created by Ziver
 */
public class DNSPacketTest {

    @Test
    public void writeRequestHeaderTest() throws IOException {
        DNSPacketHeader header = new DNSPacketHeader();
        header.setDefaultQueryData();
        header.countQuestion = 1;

        byte[] data = BinaryStructOutputStream.serialize(header);
        assertEquals("header length", 12, data.length);
        assertEquals("Flag byte1", 0x00, data[2]);
        assertEquals("Flag byte2", 0x00, data[3]);
        assertEquals("Question count byte1", 0x00, data[4]);
        assertEquals("Question count byte2", 0x01, data[5]);
    }

    @Test
    public void readResponseHeaderTest() throws IOException {
        DNSPacketHeader header = new DNSPacketHeader();
        header.setDefaultResponseData();
        header.countAnswerRecord = 1;

        byte[] data = BinaryStructOutputStream.serialize(header);
        assertEquals("header length", 12, data.length);
        assertEquals("Flag byte1", (byte) 0x84, data[2]);
        assertEquals("Flag byte2", (byte) 0x00, data[3]);
        assertEquals("Answer count byte1", 0x00, data[6]);
        assertEquals("Answer count byte2", 0x01, data[7]);
    }

    @Test
    public void writeRequestDnsPacketHeaderTest() throws IOException {
        DNSPacket packet = new DNSPacket();
        packet.getHeader().setDefaultQueryData();

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        BinaryStructOutputStream out = new BinaryStructOutputStream(buffer);
        packet.write(out);
        byte[] data = buffer.toByteArray();

        byte[] expected = {
                0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, // QDCOUNT
                0x00, 0x00, // ANCOUNT
                0x00, 0x00, // NSCOUNT
                0x00, 0x00, // ARCOUNT
        };
        assertArrayEquals(expected, data);
    }

    @Test
    public void writeRequestDnsPacketTest() throws IOException {
        DNSPacket packet = new DNSPacket();
        packet.getHeader().setDefaultQueryData();
        packet.addQuestion(new DNSPacketQuestion("appletv.local", QTYPE_A, QCLASS_IN));

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        BinaryStructOutputStream out = new BinaryStructOutputStream(buffer);
        packet.write(out);
        byte[] data = buffer.toByteArray();

        byte[] expected = {
                // HEADER
                0x00, 0x00, // ID
                0x00, 0x00, // FLAGS
                0x00, 0x01, // QDCOUNT
                0x00, 0x00, // ANCOUNT
                0x00, 0x00, // NSCOUNT
                0x00, 0x00, // ARCOUNT
                // QUESTION
                0x07, 0x61, 0x70, 0x70, 0x6c, 0x65, 0x74, 0x76, // "appletv"
                0x05, 0x6c, 0x6f, 0x63, 0x61, 0x6c, // "local"
                0x00, // NULL
                0x00, 0x01, // QTYPE
                0x00, 0x01  // QCLASS
        };
        assertArrayEquals(expected, data);
    }

    @Test
    public void readResponseDnsPacketTest() throws IOException {
        char[] input = {
                // HEADER
                0x00, 0x00, // ID
                0x84, 0x00, // FLAGS
                0x00, 0x00, // QDCOUNT
                0x00, 0x01, // ANCOUNT
                0x00, 0x00, // NSCOUNT
                0x00, 0x02, // ARCOUNT
                // ANSWER RECORD
                0x07, 0x61, 0x70, 0x70, 0x6c, 0x65, 0x74, 0x76, // FQDN: "appletv"
                0x05, 0x6c, 0x6f, 0x63, 0x61, 0x6c, // FQDN: "local"
                0x00, // FQDN: NULL
                0x00, 0x01, // TYPE: A/IPv4
                0x00, 0x01, // CLASS: IPv4
                0x00, 0x00, 0x78, 0x00, // TTL: 30720 seconds
                0x00, 0x04, // IPv4 length
                0x99, 0x6d, 0x07, 0x5a, // IPv4:  153.109.7.90
                // ADDITIONAL RECORD
                0xc0, 0x0c, // FQDN
                0x00, 0x1c, // TYPE: AAAA/IPv6
                0x80, 0x01, // CLASS: IPv6
                0x00, 0x00, 0x78, 0x00, // TTL: 30720 seconds
                0x00, 0x10, // IPv6 length
                0xfe, 0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // IPv6
                0x02, 0x23, 0x32, 0xff, 0xfe, 0xb1, 0x21, 0x52, // IPv6
                // ADDITIONAL RECORD
                0xc0, 0x0c, // FQDN
                0x00, 0x2f, // TYPE: NSEC
                0x80, 0x01, // CLASS: NSEC
                0x00, 0x00, 0x78, 0x00, // TTL
                0x00, 0x08, // NSEC length
                0xc0, 0x0c, 0x00, 0x04, 0x40, 0x00, 0x00, 0x08 // NSEC
        };
        ByteArrayInputStream buffer = new ByteArrayInputStream(Converter.toBytes(input));
        BinaryStructInputStream in = new BinaryStructInputStream(buffer);
        DNSPacket packet = DNSPacket.read(in);

        // Assert Header
        assertTrue("flagQueryResponse", packet.getHeader().flagQueryResponse);
        assertTrue("flagAuthoritativeAnswer", packet.getHeader().flagAuthoritativeAnswer);
        assertEquals("ANCOUNT", 1, packet.getHeader().countAnswerRecord);
        assertEquals("ARCOUNT", 2, packet.getHeader().countAdditionalRecord);
        // Assert Answer
        assertEquals("No Of Answer records", 1, packet.getAnswerRecords().size());
        DNSPacketResource answer = packet.getAnswerRecords().get(0);
        assertEquals("NAME", "appletv.local", answer.name);
        assertEquals("TYPE", TYPE_A, answer.type);
        assertEquals("CLASS", CLASS_IN, answer.clazz);
        assertEquals("TTL", 30720, answer.ttl);
        assertEquals("IPv4", new String(new char[]{0x99, 0x6d, 0x07, 0x5a}), answer.data);
    }


    @Test
    public void rawQuery() throws IOException {
/*
Source: http://serverfault.com/questions/173187/what-does-a-dns-request-look-like
The DNS part starts with 24 1a:
0000  00 00 00 00 00 00 00 00  00 00 00 00 08 00 45 00   ........ ......E.
0010  00 3c 51 e3 40 00 40 11  ea cb 7f 00 00 01 7f 00   .<Q.@.@. ........
0020  00 01 ec ed 00 35 00 28  fe 3b 24 1a 01 00 00 01   .....5.( .;$.....
0030  00 00 00 00 00 00 03 77  77 77 06 67 6f 6f 67 6c   .......w ww.googl
0040  65 03 63 6f 6d 00 00 01  00 01                     e.com... ..

Domain Name System (query)
    [Response In: 1852]
    Transaction ID: 0x241a
    Flags: 0x0100 (Standard query)
        0... .... .... .... = Response: Message is a query
        .000 0... .... .... = Opcode: Standard query (0)
        .... ..0. .... .... = Truncated: Message is not truncated
        .... ...1 .... .... = Recursion desired: Do query recursively
        .... .... .0.. .... = Z: reserved (0)
        .... .... ...0 .... = Non-authenticated data OK: Non-authenticated data is unacceptable
    Questions: 1
    Answer RRs: 0
    Authority RRs: 0
    Additional RRs: 0
    Queries
        www.google.com: type A, class IN
            Name: www.google.com
            Type: A (Host address)
            Class: IN (0x0001)
 */
        DNSPacket packet = new DNSPacket();
        DNSPacketHeader header = packet.getHeader();
        header.id = 0x241a;
        header.flagRecursionDesired = true;
        header.countQuestion = 1;

        DNSPacketQuestion question = new DNSPacketQuestion();
        question.qName = "www.google.com";
        question.qType = QTYPE_A;
        question.qClass = QCLASS_IN;
        packet.addQuestion(question);

        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        BinaryStructOutputStream out = new BinaryStructOutputStream(buff);
        packet.write(out);
        byte[] data = buff.toByteArray();
        assertEquals((
                "241a 01 00 00 01 00 00 00 00 00 00 " +
                "03 77  77 77 06 67 6f 6f 67 6c 65 03 63 6f 6d 00  0001  0001"
            ).replace(" ",""),
                Converter.toHexString(data));
    }

    @Test
    public void rawMultiAnswer() throws IOException {
/*
Source: http://serverfault.com/questions/173187/what-does-a-dns-request-look-like
the repsonse, again starting at 24 1a:
0000  00 00 00 00 00 00 00 00  00 00 00 00 08 00 45 00   ........ ......E.
0010  00 7a 00 00 40 00 40 11  3c 71 7f 00 00 01 7f 00   .z..@.@. <q......
0020  00 01 00 35 ec ed 00 66  fe 79 24 1a 81 80 00 01   ...5...f .y$.....
0030  00 03 00 00 00 00 03 77  77 77 06 67 6f 6f 67 6c   .......w ww.googl
0040  65 03 63 6f 6d 00 00 01  00 01 c0 0c 00 05 00 01   e.com... ........
0050  00 05 28 39 00 12 03 77  77 77 01 6c 06 67 6f 6f   ..(9...w ww.l.goo
0060  67 6c 65 03 63 6f 6d 00  c0 2c 00 01 00 01 00 00   gle.com. .,......
0070  00 e3 00 04 42 f9 59 63  c0 2c 00 01 00 01 00 00   ....B.Yc .,......
0080  00 e3 00 04 42 f9 59 68                            ....B.Yh

Domain Name System (response)
    [Request In: 1851]
    [Time: 0.000125000 seconds]
    Transaction ID: 0x241a
    Flags: 0x8180 (Standard query response, No error)
        1... .... .... .... = Response: Message is a response
        .000 0... .... .... = Opcode: Standard query (0)
        .... .0.. .... .... = Authoritative: Server is not an authority for domain
        .... ..0. .... .... = Truncated: Message is not truncated
        .... ...1 .... .... = Recursion desired: Do query recursively
        .... .... 1... .... = Recursion available: Server can do recursive queries
        .... .... .0.. .... = Z: reserved (0)
        .... .... ..0. .... = Answer authenticated: Answer/authority portion was not authenticated by the server
        .... .... .... 0000 = Reply code: No error (0)
    Questions: 1
    Answer RRs: 3
    Authority RRs: 0
    Additional RRs: 0
    Queries
        www.google.com: type A, class IN
            Name: www.google.com
            Type: A (Host address)
            Class: IN (0x0001)
    Answers
        www.google.com: type CNAME, class IN, cname www.l.google.com
            Name: www.google.com
            Type: CNAME (Canonical name for an alias)
            Class: IN (0x0001)
            Time to live: 3 days, 21 hours, 52 minutes, 57 seconds
            Data length: 18
            Primary name: www.l.google.com
        www.l.google.com: type A, class IN, addr 66.249.89.99
            Name: www.l.google.com
            Type: A (Host address)
            Class: IN (0x0001)
            Time to live: 3 minutes, 47 seconds
            Data length: 4
            Addr: 66.249.89.99
        www.l.google.com: type A, class IN, addr 66.249.89.104
            Name: www.l.google.com
            Type: A (Host address)
            Class: IN (0x0001)
            Time to live: 3 minutes, 47 seconds
            Data length: 4
            Addr: 66.249.89.104
         */
        byte[] input = Converter.hexToByte((
                    "24 1a 81 80 00 01 00 03 00 00 00 00" + // Header
                    "03 77  77 77 06 67 6f 6f 67 6c 65 03 63 6f 6d 00  0001  0001" + // Question
                    "c00c  0005  0001  00052839  0012  03 77 77 77  01 6c  06 67 6f 6f 67 6c 65  03 63 6f 6d 00" + // Answer1
                    "c02c  0001  0001  000000e3  0004  42 f9 59 63" + // Answer2
                    "c02c  0001  0001  000000e3  0004  42 f9 59 68"   // Answer3
                ).replace(" ", ""));
        ByteArrayInputStream buffer = new ByteArrayInputStream(input);
        BinaryStructInputStream in = new BinaryStructInputStream(buffer);
        DNSPacket packet = DNSPacket.read(in);

        assertEquals("id", 0x241a, packet.getHeader().id);
        assertTrue("flagQueryResponse", packet.getHeader().flagQueryResponse);
        assertEquals("No Of Question records", 1, packet.getHeader().countQuestion);
        assertEquals("No Of Answer records", 3, packet.getHeader().countAnswerRecord);
        assertEquals("No Of NameServer records", 0, packet.getHeader().countNameServer);
        assertEquals("No Of Additional records", 0, packet.getHeader().countAdditionalRecord);

        // Query
        DNSPacketQuestion question = packet.getQuestions().get(0);
        assertEquals("qNAME", "www.google.com", question.qName);
        assertEquals("qType", DNSPacketQuestion.QTYPE_A, question.qType);
        assertEquals("qClass", DNSPacketQuestion.QCLASS_IN, question.qClass);

        // Answer
        DNSPacketResource answer = packet.getAnswerRecords().get(0);
        assertEquals("NAME", "12", answer.name);
        assertEquals("TYPE", TYPE_CNAME, answer.type);
        assertEquals("CLASS", CLASS_IN, answer.clazz);
        assertEquals("TTL", 337977, answer.ttl);

        answer = packet.getAnswerRecords().get(1);
        assertEquals("NAME", "44", answer.name);
        assertEquals("TYPE", TYPE_A, answer.type);
        assertEquals("CLASS", CLASS_IN, answer.clazz);
        assertEquals("TTL", 227, answer.ttl);

        answer = packet.getAnswerRecords().get(2);
        assertEquals("NAME", "44", answer.name);
        assertEquals("TYPE", TYPE_A, answer.type);
        assertEquals("CLASS", CLASS_IN, answer.clazz);
        assertEquals("TTL", 227, answer.ttl);
    }
}
