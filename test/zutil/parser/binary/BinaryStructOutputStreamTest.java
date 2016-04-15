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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.Assert.assertArrayEquals;


/**
 * Created by Ziver
 */
public class BinaryStructOutputStreamTest {

    @Test
    public void basicIntTest() throws IOException {
        BinaryStruct struct = new BinaryStruct() {
            @BinaryField(index=1, length=32)
            int i1 = 1;
            @BinaryField(index=2, length=32)
            public int i2 = 2;
        };

        byte[] data = BinaryStructOutputStream.serialize(struct);
        assertArrayEquals(new byte[]{0,0,0,1, 0,0,0,2}, data);
    }

    @Test
    public void shortIntTest() throws IOException {
        BinaryStruct struct = new BinaryStruct() {
            @BinaryField(index=1, length=16)
            int i1 = 1;
            @BinaryField(index=2, length=16)
            int i2 = 2;
        };

        byte[] data = BinaryStructOutputStream.serialize(struct);
        assertArrayEquals(new byte[]{0,1, 0,2}, data);
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

    @Test
    public void basicStringTest() throws IOException {
        BinaryStruct struct = new BinaryStruct() {
            @BinaryField(index=1, length=8)
            int i1 = Integer.MAX_VALUE;
            @BinaryField(index=2, length=12*8)
            public String s2 = "hello world!";
        };

        byte[] expected = "0hello world!".getBytes();
        expected[0] = (byte)0xFF;
        byte[] data = BinaryStructOutputStream.serialize(struct);
        assertArrayEquals(expected, data);
    }


    @Test
    public void customBinaryFieldTest() throws IOException {
        BinaryStruct struct = new BinaryStruct() {
            @BinaryStruct.CustomBinaryField(index=1, serializer=ByteStringSerializer.class)
            public String s1 = "1234";
        };

        byte[] data = BinaryStructOutputStream.serialize(struct);
        assertArrayEquals(new byte[]{0b0000_0001,0b0000_0010,0b0000_0011,0b0000_0100}, data);
    }
    public static class ByteStringSerializer implements BinaryFieldSerializer<String>{
        public String read(InputStream in, BinaryFieldData field) throws IOException {
            return null;
        }
        public void write(OutputStream out, String obj, BinaryFieldData field) throws IOException {
            for (char c : obj.toCharArray())
                out.write(Integer.parseInt(""+c));
        }
    }


    @Test
    public void variableLengthFieldTest() throws IOException {
        BinaryStruct struct = new BinaryStruct() {
            @BinaryField(index=1, length=8)
            private int s1 = 2;
            @VariableLengthBinaryField(index=2, lengthField="s1")
            private String s2 = "12345";
        };

        byte[] expected = "012".getBytes();
        expected[0] = 2;
        byte[] data = BinaryStructOutputStream.serialize(struct);
        assertArrayEquals(expected, data);
    }
}
