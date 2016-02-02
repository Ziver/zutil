package zutil;

/**
 * Utility functions for byte primitive type
 *
 * Created by Ziver on 2016-01-30.
 */
public class ByteUtil {
    /** Bitmask array used by utility functions **/
    private static final int[][] BYTE_MASK = new int[][]{
            {0b0000_0001},
            {0b0000_0010, 0b0000_0011},
            {0b0000_0100, 0b0000_0110, 0b0000_0111},
            {0b0000_1000, 0b0000_1100, 0b0000_1110, 0b0000_1111},
            {0b0001_0000, 0b0001_1000, 0b0001_1100, 0b0001_1110, 0b0001_1111},
            {0b0010_0000, 0b0011_0000, 0b0011_1000, 0b0011_1100, 0b0011_1110, 0b0011_1111},
            {0b0100_0000, 0b0110_0000, 0b0111_0000, 0b0111_1000, 0b0111_1100, 0b0111_1110, 0b0111_1111},
            {0b1000_0000, 0b1100_0000, 0b1110_0000, 0b1111_0000, 0b1111_1000, 0b1111_1100, 0b1111_1110, 0b1111_1111}
    };

    /**
     * Creates a new subbyte from offset and with a length and shifts the data to the left
     *
     * @param   data    is the byte data
     * @param   offset   is the bit index, valid values 0-7
     * @param   length  is the length of bits to return, valid values 1-8
     * @return a new byte containing a subbyte defined by the offset and length
     */
    public static byte getShiftedBits(byte data, int offset, int length){
        int ret = 0xFF & getBits(data, offset, length);
        ret = ret >>> offset+1-length;
        return (byte) ret;
    }

    /**
     * Creates a new subbyte from offset and with a length
     *
     * @param   data    is the byte data
     * @param   offset   is the bit index, valid values 0-7
     * @param   length  is the length of bits to return, valid values 1-8
     * @return a new byte containing a subbyte defined by the offset and length
     */
    public static byte getBits(byte data, int offset, int length){
        length--;
        if(0 > offset || offset > 7)
            throw new IllegalArgumentException("Invalid index argument, allowed value is 0-7");
        if(length < 0 && offset-length < 0)
            throw new IllegalArgumentException("Invalid length argument: "+length+", allowed values 1-8 depending on index");

        byte ret = (byte) (data & BYTE_MASK[offset][length]);
        return  ret;
    }

}
