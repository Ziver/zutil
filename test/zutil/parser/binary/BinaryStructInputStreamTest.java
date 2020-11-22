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

package zutil.parser.binary;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static junit.framework.TestCase.*;


/**
 * Created by Ziver
 */
public class BinaryStructInputStreamTest {
    interface BinaryTestStruct extends BinaryStruct{
        void assertObj();
    }

    @Test
    public void basicIntTest(){
        BinaryTestStruct struct = new BinaryTestStruct() {
            @BinaryField(index=1, length=32)
            public int i1;
            @BinaryField(index=2, length=32)
            public int i2;

            public void assertObj(){
                assertEquals(1, i1);
                assertEquals(2, i2);
            }
        };

        BinaryStructInputStream.read(struct, new byte[]{0,0,0,1, 0,0,0,2});
        struct.assertObj();
    }

    @Test
    public void basicBooleanTest(){
        BinaryTestStruct struct = new BinaryTestStruct() {
            @BinaryField(index=1, length=1)
            public boolean b1;
            @BinaryField(index=2, length=1)
            public boolean b2;

            public void assertObj(){
                assertFalse(b1);
                assert(b2);
            }
        };

        BinaryStructInputStream.read(struct, new byte[]{0b0100_0000});
        struct.assertObj();
    }

    @Test
    public void basicStringTest(){
        BinaryTestStruct struct = new BinaryTestStruct() {
            @BinaryField(index=1, length=8*12)
            public String s1;

            public void assertObj(){
                assertEquals("hello world!", s1);
            }
        };

        BinaryStructInputStream.read(struct, "hello world!".getBytes());
        struct.assertObj();
    }

    @Test
    public void basicByteTest(){
        BinaryTestStruct struct = new BinaryTestStruct() {
            @BinaryField(index=1, length=1)
            public byte b1;
            @BinaryField(index=2, length=4)
            public byte b2;
            @BinaryField(index=3, length=3)
            public byte b3;

            public void assertObj(){
                assertEquals(0, b1);
                assertEquals(6, b2);
                assertEquals(6, b3);
            }
        };

        BinaryStructInputStream.read(struct, new byte[]{0b0011_0110});
        struct.assertObj();
    }

    @Test
    public void nonLinedLength(){
        BinaryTestStruct struct = new BinaryTestStruct() {
            @BinaryField(index=1, length=12)
            public int i1;
            @BinaryField(index=2, length=12)
            public int i2;

            public void assertObj(){
                assertEquals(1, i1);
                assertEquals(2048, i2);
            }
        };

        BinaryStructInputStream.read(struct, new byte[]{0b0000_0000,0b0001_1000,0b0000_0000});
        struct.assertObj();
    }

    @Test
    public void nonLinedLength2(){
        BinaryTestStruct struct = new BinaryTestStruct() {
            @BinaryField(index=1, length=12)
            public int i1;
            @BinaryField(index=2, length=12)
            public int i2;

            public void assertObj(){
                assertEquals(17, i1);
                assertEquals(2048, i2);
            }
        };

        BinaryStructInputStream.read(struct, new byte[]{0b0000_0001,0b0001_1000,0b0000_0000});
        struct.assertObj();
    }

    @Test
    public void mixedType(){
        BinaryTestStruct struct = new BinaryTestStruct() {
            @BinaryField(index=1, length=4)
            public int i1;
            @BinaryField(index=2, length=1)
            public boolean b2;
            @BinaryField(index=3, length=1)
            public boolean b3;
            @BinaryField(index=4, length=1)
            public boolean b4;
            @BinaryField(index=5, length=1)
            public boolean b5;

            public void assertObj(){
                assertEquals("i1", 6, i1);
                assertTrue("b2", b2);
                assertTrue("b3", b3);
                assertFalse("b4", b4);
                assertTrue("b5", b5);
            }
        };

        BinaryStructInputStream.read(struct, new byte[]{0b0110_1101});
        struct.assertObj();
    }


    @Test
    public void customBinaryField(){
        BinaryTestStruct struct = new BinaryTestStruct() {
            @CustomBinaryField(index=1, serializer=ByteStringSerializer.class)
            public String s1;

            public void assertObj(){
                assertEquals("1234", s1);
            }
        };

        BinaryStructInputStream.read(struct, new byte[]{0b0000_0001,0b0000_0010,0b0000_0011,0b0000_0100});
        struct.assertObj();
    }
    public static class ByteStringSerializer implements BinaryFieldSerializer<String>{
        public String read(InputStream in, BinaryFieldData field) throws IOException {
            StringBuilder ret = new StringBuilder();
            for (int c; (c=in.read()) > 0; )
                ret.append(c);
            return ret.toString();
        }
        public void write(OutputStream out, String obj, BinaryFieldData field) {}
    }


    @Test
    public void variableLengthField(){
        BinaryTestStruct struct = new BinaryTestStruct() {
            @BinaryField(index=1, length=8)
            public int i1;
            @VariableLengthBinaryField(index=2, lengthField="i1")
            public String s2;

            public void assertObj(){
                assertEquals(3, i1);
                assertEquals("123", s2);
            }
        };

        byte[] data = "0123456".getBytes();
        data[0] = 3;
        BinaryStructInputStream.read(struct, data);
        struct.assertObj();
    }


    @Test
    public void shiftLeftBy(){
        assertEquals(0, BinaryStructInputStream.shiftLeftBy(0, 1));
        assertEquals(1, BinaryStructInputStream.shiftLeftBy(1, 1));
        assertEquals(3, BinaryStructInputStream.shiftLeftBy(3, 1));
        assertEquals(4, BinaryStructInputStream.shiftLeftBy(4, 1));
        assertEquals(5, BinaryStructInputStream.shiftLeftBy(5, 1));
        assertEquals(6, BinaryStructInputStream.shiftLeftBy(6, 1));
        assertEquals(7, BinaryStructInputStream.shiftLeftBy(7, 1));

        assertEquals(0, BinaryStructInputStream.shiftLeftBy(1, 2));
        assertEquals(2, BinaryStructInputStream.shiftLeftBy(3, 2));
        assertEquals(6, BinaryStructInputStream.shiftLeftBy(7, 2));

        assertEquals(0, BinaryStructInputStream.shiftLeftBy(7, 8));
        assertEquals(2, BinaryStructInputStream.shiftLeftBy(7, 6));
        assertEquals(3, BinaryStructInputStream.shiftLeftBy(7, 5));
        assertEquals(6, BinaryStructInputStream.shiftLeftBy(7, 2));

        // Cross 1 byte border
        assertEquals(7, BinaryStructInputStream.shiftLeftBy(0, 2));
        assertEquals(6, BinaryStructInputStream.shiftLeftBy(1, 4));
        assertEquals(4, BinaryStructInputStream.shiftLeftBy(3, 8));
        assertEquals(0, BinaryStructInputStream.shiftLeftBy(3, 12));
        assertEquals(0, BinaryStructInputStream.shiftLeftBy(7, 16));

        // Cross 2 byte borders
        assertEquals(0, BinaryStructInputStream.shiftLeftBy(7, 32));
        assertEquals(7, BinaryStructInputStream.shiftLeftBy(7, 33));
        assertEquals(6, BinaryStructInputStream.shiftLeftBy(7, 34));
        assertEquals(5, BinaryStructInputStream.shiftLeftBy(7, 35));
        assertEquals(4, BinaryStructInputStream.shiftLeftBy(7, 36));
        assertEquals(3, BinaryStructInputStream.shiftLeftBy(7, 37));
        assertEquals(2, BinaryStructInputStream.shiftLeftBy(7, 38));
        assertEquals(1, BinaryStructInputStream.shiftLeftBy(7, 39));
        assertEquals(0, BinaryStructInputStream.shiftLeftBy(7, 40));
        assertEquals(7, BinaryStructInputStream.shiftLeftBy(7, 41));
        assertEquals(6, BinaryStructInputStream.shiftLeftBy(6, 41));
        assertEquals(5, BinaryStructInputStream.shiftLeftBy(5, 41));
        assertEquals(4, BinaryStructInputStream.shiftLeftBy(4, 41));
        assertEquals(3, BinaryStructInputStream.shiftLeftBy(3, 41));
        assertEquals(2, BinaryStructInputStream.shiftLeftBy(2, 41));
        assertEquals(1, BinaryStructInputStream.shiftLeftBy(1, 41));
        assertEquals(0, BinaryStructInputStream.shiftLeftBy(7, 64));
    }
}
