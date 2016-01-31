package zutil.test;

import org.junit.Test;
import zutil.ByteUtil;

import static org.junit.Assert.assertEquals;

/**
 * Created by Ziver on 2016-01-31.
 */
public class ByteUtilTest {


    @Test
    public void getBits(){
        assertEquals(1, ByteUtil.getBits((byte)0b1000_0000, 7, 1));
        assertEquals(1, ByteUtil.getBits((byte)0b0001_0000, 4, 1));
        assertEquals(1, ByteUtil.getBits((byte)0b0000_0001, 0, 1));

        assertEquals(3, ByteUtil.getBits((byte)0b0110_0000, 6, 2));

        assertEquals((byte)0xFF, ByteUtil.getBits((byte)0b1111_1111, 7, 8));
    }
}
