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

package zutil.parser.binary;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertArrayEquals;


/**
 * Created by Ziver
 */
public class BinaryStructOutputStreamTest {

    @Test
    public void basicIntTest() throws IOException {
        BinaryStruct struct = new BinaryStruct() {
            @BinaryField(index=1, length=32)
            public int i1 = 1;
            @BinaryField(index=2, length=32)
            public int i2 = 2;
        };

        byte[] data = BinaryStructOutputStream.serialize(struct);
        assertArrayEquals(new byte[]{0,0,0,1, 0,0,0,2}, data);
    }

    @Test
    public void basicBooleanTest() throws IOException {
        BinaryStruct struct = new BinaryStruct() {
            @BinaryField(index=1, length=1)
            public boolean b1 = false;
            @BinaryField(index=2, length=1)
            public boolean b2 = true;
        };

        byte[] data = BinaryStructOutputStream.serialize(struct);
        assertArrayEquals(new byte[]{(byte)0b0100_0000}, data);
    }
/*
    @Test
    public void basicStringTest(){
        BinaryTestStruct struct = new BinaryTestStruct() {
            @BinaryField(index=1, length=8*12)
            public String s1;

            public void assertObj(Object... expected){
                assertEquals(s1, "hello world!");
            }
        };

        BinaryStructParser.parse(struct, "hello world!".getBytes());
        struct.assertObj();
    }
    */
}
