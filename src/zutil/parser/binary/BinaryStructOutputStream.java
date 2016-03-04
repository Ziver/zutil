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

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


/**
 * A stream class that generates a byte stream from
 * binary struct objects.
 *
 * @author Ziver
 */
public class BinaryStructOutputStream {

    private OutputStream out;
    private byte rest;
    private int restLength;


    public BinaryStructOutputStream(OutputStream out){
        this.out = out;

        rest = 0;
        restLength = 0;
    }


    /**
     * Generate a binary byte array from the provided struct.
     * The byte array will be left
     */
/*    public byte[] serialize(BinaryStruct struct) {

    }*/

    /**
     * Generate a binary stream from the provided struct and
     * write the data to the underlying stream.
     */
    public void write(BinaryStruct struct) throws IOException {
        List<BinaryFieldData> structDataList = BinaryFieldData.getStructFieldList(struct.getClass());

        for (BinaryFieldData field : structDataList){
            byte[] data = field.getValue(struct);

            for (int i=data.length-1; i>=0; --i) {
                out.write(data[i]);
            }
        }
    }


    /**
     * Writes any outstanding data to the stream
     */
    public void flush() throws IOException {
        if(restLength > 0){
            out.write(0xFF & rest);
            rest = 0;
            restLength = 0;
        }
        out.flush();
    }

    /**
     * Flushes and closes the underlying stream
     */
    public void close() throws IOException {
        flush();
        out.close();
    }
}
