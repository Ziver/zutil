package zutil.net.mqtt.packet;

import zutil.parser.binary.BinaryFieldData;
import zutil.parser.binary.BinaryFieldSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Code from MQTT specification
 */
public class MqttVariableIntSerializer implements BinaryFieldSerializer<Integer>{

    @Override
    public Integer read(InputStream in, BinaryFieldData field) throws IOException {
        int multiplier = 1;
        int value = 0;
        int encodedByte;
        do {
            encodedByte = in.read();
            value += (encodedByte & 127) * multiplier;
            if (multiplier > 128 * 128 * 128)
                throw new IOException("Malformed Remaining Length");
            multiplier *= 128;
        } while ((encodedByte & 128) != 0);
        return value;
    }

    @Override
    public void write(OutputStream out, Integer obj, BinaryFieldData field) throws IOException {
        int x = obj;
        int encodedByte;
        do {
            encodedByte = x % 128;
            x = x / 128;
            // if there are more data to encode, set the top bit of this byte
            if (x > 0)
                encodedByte = encodedByte & 128;
            out.write(encodedByte);
        } while ( x > 0 );
    }
}
