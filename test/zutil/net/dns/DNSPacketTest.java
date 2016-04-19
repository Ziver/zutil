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
import zutil.parser.binary.BinaryStructOutputStream;

import static org.junit.Assert.assertTrue;
import static zutil.net.dns.DNSPacketQuestion.*;
import static zutil.net.dns.DNSPacketResource.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

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
        DNSPacket packet = new DNSPacket();
        packet.getHeader().setDefaultQueryData();
        packet.addQuestion(new DNSPacketQuestion("appletv.local", QTYPE_A, QCLASS_IN));

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
                0x07, 0x61, 0x70, 0x70, 0x6c, 0x65, 0x74, 0x76, // "apple"
                0x05, 0x6c, 0x6f, 0x63, 0x61, 0x6c, // "local"
                0x00, // NULL
                0x00, 0x01, // QTYPE
                0x00, 0x01  // QCLASS
        };
        assertArrayEquals(expected, data);
    }

    public void readRespnseDnsPacketTest() throws IOException {
        char[] input = {
                // HEADER
                0x00, 0x00, // ID
                0x84, 0x00, // FLAGS
                0x00, 0x00, // QDCOUNT
                0x00, 0x01, // ANCOUNT
                0x00, 0x00, // NSCOUNT
                0x00, 0x02, // ARCOUNT
                // ANSWER RECORD
                0x07, 0x61, 0x70, 0x70, 0x6c, 0x65, 0x74, 0x76, // FQDN: "apple"
                0x05, 0x6c, 0x6f, 0x63, 0x61, 0x6c, // FQDN: "local"
                0x00, // FQDN: NULL
                0x00, 0x01, // TYPE: A/IPv4
                0x80, 0x01, // CLASS: IPv4
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
        DNSPacket packet = DNSPacket.read(buffer);

        // Assert Header
        assertTrue("flagQueryResponse", packet.getHeader().flagQueryResponse);
        assertTrue("flagAuthoritativeAnswer", packet.getHeader().flagAuthoritativeAnswer);
        assertEquals("ANCOUNT", 1, packet.getHeader().countAnswerRecord);
        assertEquals("ARCOUNT", 2, packet.getHeader().countAdditionalRecord);
        // Assert Answer
        assertEquals("No Of Answer records", 1, packet.getAnswerRecords().size());
        DNSPacketResource answer = packet.getAnswerRecords().get(0);
        assertEquals("TYPE", TYPE_A, answer.type);
        assertEquals("CLASS", CLASS_IN, answer.clazz);
        assertEquals("TTL", 30720, answer.ttl);
        assertEquals("IPv4", new char[]{0x99, 0x6d, 0x07, 0x5a}, answer.data);
    }

}
