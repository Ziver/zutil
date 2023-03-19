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

package zutil.net.dns;

import org.junit.Test;
import zutil.converter.Converter;
import zutil.net.dns.packet.*;
import zutil.parser.binary.BinaryStructInputStream;
import zutil.parser.binary.BinaryStructOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by Ziver
 */
public class DnsPacketTest {

    @Test
    public void writeRequestHeaderTest() throws IOException {
        DnsPacketHeader header = new DnsPacketHeader();
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
        DnsPacketHeader header = new DnsPacketHeader();
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
        DnsPacket packet = new DnsPacket();
        packet.getHeader().setDefaultQueryData();

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        packet.write(buffer);
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
        DnsPacket packet = new DnsPacket();
        packet.getHeader().setDefaultQueryData();
        packet.addQuestion(new DnsPacketQuestion(
                "appletv.local", DnsConstants.TYPE.A, DnsConstants.CLASS.IN));

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        packet.write(buffer);
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
        DnsPacket packet = DnsPacket.read(buffer);

        // Assert Header
        assertTrue("flagQueryResponse", packet.getHeader().flagQueryResponse);
        assertTrue("flagAuthoritativeAnswer", packet.getHeader().flagAuthoritativeAnswer);
        assertEquals("ANCOUNT", 1, packet.getHeader().countAnswerRecord);
        assertEquals("ARCOUNT", 2, packet.getHeader().countAdditionalRecord);
        // Assert Answer
        assertEquals("No Of Answer records", 1, packet.getAnswerRecords().size());
        DnsPacketResource answer = packet.getAnswerRecords().get(0);
        assertEquals("NAME", "appletv.local", answer.name);
        assertEquals("TYPE", DnsConstants.TYPE.A, answer.type);
        assertEquals("CLASS", DnsConstants.CLASS.IN, answer.clazz);
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
        DnsPacket packet = new DnsPacket();
        DnsPacketHeader header = packet.getHeader();
        header.id = 0x241a;
        header.flagRecursionDesired = true;
        header.countQuestion = 1;

        DnsPacketQuestion question = new DnsPacketQuestion();
        question.name = "www.google.com";
        question.type = DnsConstants.TYPE.A;
        question.clazz = DnsConstants.CLASS.IN;
        packet.addQuestion(question);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        packet.write(buffer);
        byte[] data = buffer.toByteArray();
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
        DnsPacket packet = DnsPacket.read(buffer);

        assertEquals("id", 0x241a, packet.getHeader().id);
        assertTrue("flagQueryResponse", packet.getHeader().flagQueryResponse);
        assertEquals("No Of Question records", 1, packet.getHeader().countQuestion);
        assertEquals("No Of Answer records", 3, packet.getHeader().countAnswerRecord);
        assertEquals("No Of NameServer records", 0, packet.getHeader().countNameServer);
        assertEquals("No Of Additional records", 0, packet.getHeader().countAdditionalRecord);

        // Query
        DnsPacketQuestion question = packet.getQuestions().get(0);
        assertEquals("qNAME", "www.google.com", question.name);
        assertEquals("type", DnsConstants.TYPE.A, question.type);
        assertEquals("clazz", DnsConstants.CLASS.IN, question.clazz);

        // Answer
        DnsPacketResource answer = packet.getAnswerRecords().get(0);
        assertEquals("NAME", "www.google.com", answer.name);
        //assertEquals("NAME", "<12>", answer.name);
        assertEquals("TYPE", DnsConstants.TYPE.CNAME, answer.type);
        assertEquals("CLASS", DnsConstants.CLASS.IN, answer.clazz);
        assertEquals("TTL", 337977, answer.ttl);

        answer = packet.getAnswerRecords().get(1);
        assertEquals("NAME", "<44>", answer.name);
        assertEquals("TYPE", DnsConstants.TYPE.A, answer.type);
        assertEquals("CLASS", DnsConstants.CLASS.IN, answer.clazz);
        assertEquals("TTL", 227, answer.ttl);

        answer = packet.getAnswerRecords().get(2);
        assertEquals("NAME", "<44>", answer.name);
        assertEquals("TYPE", DnsConstants.TYPE.A, answer.type);
        assertEquals("CLASS", DnsConstants.CLASS.IN, answer.clazz);
        assertEquals("TTL", 227, answer.ttl);
    }

    @Test
    public void rawIphoneQuery() throws IOException {
/*
0000   00 00 00 00 00 06 00 00 00 00 00 01 0f 5f 63 6f   ............._co
0010   6d 70 61 6e 69 6f 6e 2d 6c 69 6e 6b 04 5f 74 63   mpanion-link._tc
0020   70 05 6c 6f 63 61 6c 00 00 0c 00 01 08 5f 68 6f   p.local......_ho
0030   6d 65 6b 69 74 c0 1c 00 0c 00 01 03 68 61 6c c0   mekit.......hal.
0040   21 00 41 00 01 c0 3b 00 1c 00 01 c0 3b 00 01 00   !.A...;.....;...
0050   01 0c 5f 73 6c 65 65 70 2d 70 72 6f 78 79 04 5f   .._sleep-proxy._
0060   75 64 70 c0 21 00 0c 00 01 00 00 29 05 a0 00 00   udp.!......)....
0070   11 94 00 12 00 04 00 0e 00 65 7a e6 ba 29 34 00   .........ez..)4.
0080   d6 07 3a f2 2e e7                                 ..:...



Multicast Domain Name System (query)
    Transaction ID: 0x0000
    Flags: 0x0000 Standard query
    Questions: 6
    Answer RRs: 0
    Authority RRs: 0
    Additional RRs: 1
    Queries
        _companion-link._tcp.local: type PTR, class IN, "QM" question
            Name: _companion-link._tcp.local
            [Name Length: 26]
            [Label Count: 3]
            Type: PTR (domain name PoinTeR) (12)
            .000 0000 0000 0001 = Class: IN (0x0001)
            0... .... .... .... = "QU" question: False
        _homekit._tcp.local: type PTR, class IN, "QM" question
            Name: _homekit._tcp.local
            [Name Length: 19]
            [Label Count: 3]
            Type: PTR (domain name PoinTeR) (12)
            .000 0000 0000 0001 = Class: IN (0x0001)
            0... .... .... .... = "QU" question: False
        hal.local: type Unknown (65), class IN, "QM" question
            Name: hal.local
            [Name Length: 9]
            [Label Count: 2]
            Type: Unknown (65)
            .000 0000 0000 0001 = Class: IN (0x0001)
            0... .... .... .... = "QU" question: False
        hal.local: type AAAA, class IN, "QM" question
            Name: hal.local
            [Name Length: 9]
            [Label Count: 2]
            Type: AAAA (IPv6 Address) (28)
            .000 0000 0000 0001 = Class: IN (0x0001)
            0... .... .... .... = "QU" question: False
        hal.local: type A, class IN, "QM" question
            Name: hal.local
            [Name Length: 9]
            [Label Count: 2]
            Type: A (Host Address) (1)
            .000 0000 0000 0001 = Class: IN (0x0001)
            0... .... .... .... = "QU" question: False
        _sleep-proxy._udp.local: type PTR, class IN, "QM" question
            Name: _sleep-proxy._udp.local
            [Name Length: 23]
            [Label Count: 3]
            Type: PTR (domain name PoinTeR) (12)
            .000 0000 0000 0001 = Class: IN (0x0001)
            0... .... .... .... = "QU" question: False
    Additional records
        <Root>: type OPT
            Name: <Root>
            Type: OPT (41)
            .000 0101 1010 0000 = UDP payload size: 0x05a0
            0... .... .... .... = Cache flush: False
            Higher bits in extended RCODE: 0x00
            EDNS0 version: 0
            Z: 0x1194
                0... .... .... .... = DO bit: Cannot handle DNSSEC security RRs
                .001 0001 1001 0100 = Reserved: 0x1194
            Data length: 18
            Option: Owner (reserved)
         */

        byte[] input = Converter.hexToByte((
                "00 00 00 00 00 06 00 00 00 00 00 01" + // Header
                "0f 5f 63 6f 6d 70 61 6e 69 6f 6e 2d 6c 69 6e 6b 04 5f 74 63 70 05 6c 6f 63 61 6c 00 00 0c 00 01" + // (offset 12) Query PTR: _companion-link._tcp.local
                "08 5f 68 6f 6d 65 6b 69 74 c0 1c 00 0c 00 01" + // (offset 44) Query PTR: _homekit._tcp.local
                "03 68 61 6c c0 21 00 41 00 01" + // (offset 59) Query Unknown: hal.local
                "c0 3b 00 1c 00 01" + // (offset 69) Query IPv6: hal.local
                "c0 3b 00 01 00 01" + // (offset 75) Query IPv4: hal.local
                "0c 5f 73 6c 65 65 70 2d 70 72 6f 78 79 04 5f 75 64 70 c0 21 00 0c 00 01" + // Query PTR: _sleep-proxy._udp.local
                "00 00 29 05 a0 00 00 11 94 00 12 00 04 00 0e 00 65 7a e6 ba 29 34 00 d6 07 3a f2 2e e7" // Additional records
        ).replace(" ", ""));
        ByteArrayInputStream buffer = new ByteArrayInputStream(input);
        DnsPacket packet = DnsPacket.read(buffer);

        assertEquals("id", 0x00, packet.getHeader().id);
        assertFalse("flagQueryResponse", packet.getHeader().flagQueryResponse);
        assertEquals("No Of Question records", 6, packet.getHeader().countQuestion);
        assertEquals("No Of Answer records", 0, packet.getHeader().countAnswerRecord);
        assertEquals("No Of NameServer records", 0, packet.getHeader().countNameServer);
        assertEquals("No Of Additional records", 1, packet.getHeader().countAdditionalRecord);

        // Query
        DnsPacketQuestion question1 = packet.getQuestions().get(0);
        assertEquals("qNAME", "_companion-link._tcp.local", question1.name);
        assertEquals("type", DnsConstants.TYPE.PTR, question1.type);
        assertEquals("clazz", DnsConstants.CLASS.IN, question1.clazz);

        DnsPacketQuestion question2 = packet.getQuestions().get(1);
        assertEquals("qNAME", "_homekit._tcp.local", question2.name);
        //assertEquals("qNAME", "_homekit.<28>", question2.name);
        assertEquals("type", DnsConstants.TYPE.PTR, question2.type);
        assertEquals("clazz", DnsConstants.CLASS.IN, question2.clazz);

        DnsPacketQuestion question3 = packet.getQuestions().get(2);
        assertEquals("qNAME", "hal.local", question3.name);
        //assertEquals("qNAME", "hal.<33>", question3.name);
        assertEquals("clazz", DnsConstants.CLASS.IN, question3.clazz);

        DnsPacketQuestion question4 = packet.getQuestions().get(3);
        assertEquals("qNAME", "hal.local", question4.name);
        //assertEquals("qNAME", "<59>", question4.name);
        assertEquals("type", DnsConstants.TYPE.AAAA, question4.type);
        assertEquals("clazz", DnsConstants.CLASS.IN, question4.clazz);

        DnsPacketQuestion question5 = packet.getQuestions().get(4);
        assertEquals("qNAME", "hal.local", question5.name);
        //assertEquals("qNAME", "<59>", question5.name);
        assertEquals("type", DnsConstants.TYPE.A, question5.type);
        assertEquals("clazz", DnsConstants.CLASS.IN, question5.clazz);

        DnsPacketQuestion question6 = packet.getQuestions().get(5);
        assertEquals("qNAME", "_sleep-proxy._udp.local", question6.name);
        //assertEquals("qNAME", "_sleep-proxy._udp.<33>", question6.name);
        assertEquals("type", DnsConstants.TYPE.PTR, question6.type);
        assertEquals("clazz", DnsConstants.CLASS.IN, question6.clazz);
    }
}
