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

package zutil.net.mqtt.packet;

import zutil.parser.binary.BinaryFieldData;
import zutil.parser.binary.BinaryFieldSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Code from MQTT specification
 */
public class MqttVariableIntSerializer implements BinaryFieldSerializer<Integer> {

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
        } while (x > 0);
    }
}
