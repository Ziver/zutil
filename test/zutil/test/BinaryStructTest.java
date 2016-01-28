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
    public void basicTest(){
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
}
