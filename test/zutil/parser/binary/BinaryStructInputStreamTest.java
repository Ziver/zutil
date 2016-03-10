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

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;


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

    // TODO: add full non lined length support
    // @Test
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
}
