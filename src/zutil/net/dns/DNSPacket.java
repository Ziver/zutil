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
    public static final int FLAG_QUERY = 0x00_00;
    public static final int FLAG_RESPONSE = 0x84_00;


    @BinaryField(index=1, length=16)
    int id;
    @BinaryField(index=2, length=16)
    int flags;
    @BinaryField(index=3, length=16)
    int qdCount;
    @BinaryField(index=4, length=16)
    int anCount;
    @BinaryField(index=5, length=16)
    int nsCount;
    @BinaryField(index=6, length=16)
    int arCount;


    // REQUEST
    // char data[?]
    // char flag[2] => QTYPE=0x00_01 host address query
    //                 QCLASS=0x00_01 internet query
}
