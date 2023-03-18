package zutil.parser.binary.serializer;

import org.junit.Test;
import zutil.ArrayUtil;
import zutil.ByteUtil;
import zutil.parser.binary.BinaryFieldData;
import zutil.parser.binary.BinaryStruct;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TwoByteLengthPrefixedDataSerializerTest implements BinaryStruct {

    @BinaryField(index = 10, length = 1)
    private String tmpStringField;
    @BinaryField(index = 20, length = 1)
    private byte[] tmpByteField;

    @Test(expected = StreamCorruptedException.class)
    public void readPrematureEnd0() throws IOException {
        TwoByteLengthPrefixedDataSerializer serializer = new TwoByteLengthPrefixedDataSerializer();

        // 0 length stream
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[0]);
        serializer.read(inputStream, null, this);
    }

    @Test(expected = StreamCorruptedException.class)
    public void readPrematureEnd1() throws IOException {
        TwoByteLengthPrefixedDataSerializer serializer = new TwoByteLengthPrefixedDataSerializer();

        // 1 length stream
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[]{0x00});
        serializer.read(inputStream, null, this);
    }

    @Test
    public void read() throws IOException, NoSuchFieldException {
        TwoByteLengthPrefixedDataSerializer serializer = new TwoByteLengthPrefixedDataSerializer();
        List<BinaryFieldData> fieldDataList = BinaryFieldData.getStructFieldList(this.getClass());
        BinaryFieldData stringFieldData = fieldDataList.get(0);
        BinaryFieldData byteFieldData = fieldDataList.get(1);

        // 0 Length
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[]{0x00, 0x00});
        assertEquals("", serializer.read(inputStream, stringFieldData, this));
        inputStream.reset();
        assertArrayEquals(new byte[0], (byte[])serializer.read(inputStream, byteFieldData, this));

        // String "1234"
        inputStream = new ByteArrayInputStream(ArrayUtil.combine(new byte[]{0x00, 0x04}, "1234".getBytes(StandardCharsets.UTF_8)));
        assertEquals("1234", serializer.read(inputStream, stringFieldData, this));
        inputStream.reset();
        assertArrayEquals("1234".getBytes(StandardCharsets.UTF_8), (byte[])serializer.read(inputStream, byteFieldData, this));
    }

    @Test
    public void write() throws IOException {
        TwoByteLengthPrefixedDataSerializer serializer = new TwoByteLengthPrefixedDataSerializer();
        List<BinaryFieldData> fieldDataList = BinaryFieldData.getStructFieldList(this.getClass());
        BinaryFieldData stringFieldData = fieldDataList.get(0);
        BinaryFieldData byteFieldData = fieldDataList.get(1);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // 0 Length
        outputStream.reset();outputStream.reset();
        serializer.write(outputStream, null, stringFieldData, this);
        assertArrayEquals(new byte[]{0, 0}, outputStream.toByteArray());
        outputStream.reset();
        serializer.write(outputStream, null, byteFieldData, this);
        assertArrayEquals(new byte[]{0, 0}, outputStream.toByteArray());

        // 0 Length
        outputStream.reset();
        serializer.write(outputStream, "", stringFieldData, this);
        assertArrayEquals(new byte[]{0, 0}, outputStream.toByteArray());
        outputStream.reset();
        serializer.write(outputStream, new byte[0], byteFieldData, this);
        assertArrayEquals(new byte[]{0, 0}, outputStream.toByteArray());

        // String "1234"
        outputStream.reset();
        serializer.write(outputStream, "1234", stringFieldData, this);
        assertArrayEquals(new byte[]{0, 4, 49, 50, 51, 52}, outputStream.toByteArray());
        outputStream.reset();
        serializer.write(outputStream, "1234".getBytes(StandardCharsets.UTF_8), byteFieldData, this);
        assertArrayEquals(new byte[]{0, 4, 49, 50, 51, 52}, outputStream.toByteArray());
    }
}