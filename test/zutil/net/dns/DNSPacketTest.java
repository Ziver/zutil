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
import zutil.parser.binary.BinaryStructOutputStream;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by Ziver
 */
public class DNSPacketTest {

    @Test
    public void headerQueryTest() throws IOException {
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
    public void headerResponseTest() throws IOException {
        DNSPacketHeader header = new DNSPacketHeader();
        header.setDefaultResponseData();
        header.countAnswerRecord = 1;

        byte[] data = BinaryStructOutputStream.serialize(header);
        assertEquals("header length", 12, data.length);
        assertEquals("Flag byte1", (byte)0x84, data[2]);
        assertEquals("Flag byte2", (byte)0x00, data[3]);
        assertEquals("Answer count byte1", 0x00, data[6]);
        assertEquals("Answer count byte2", 0x01, data[7]);
    }
}
