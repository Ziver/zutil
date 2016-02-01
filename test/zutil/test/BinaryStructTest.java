package zutil.test;

import org.junit.Test;
import zutil.parser.binary.BinaryStruct;
import zutil.parser.binary.BinaryStructParser;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.*;

/**
 * Created by ezivkoc on 2016-01-28.
 */
public class BinaryStructTest {
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

        BinaryStructParser.parse(struct, new byte[]{0,0,0,1, 0,0,0,2});
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
                assertFalse(b2);
            }
        };

        BinaryStructParser.parse(struct, new byte[]{0b0100_000});
        struct.assertObj();
    }

    @Test
    public void basicStringTest(){
        BinaryTestStruct struct = new BinaryTestStruct() {
            @BinaryField(index=1, length=8*12)
            public String s1;

            public void assertObj(){
                assertEquals(s1, "hello world!");
            }
        };

        BinaryStructParser.parse(struct, "hello world!".getBytes());
        struct.assertObj();
    }
}
