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

package zutil.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class DynamicByteArrayStream extends InputStream{
    /** The byte array container */
    private ArrayList<byte[]> bytes;
    /** Current virtual position of the stream */
    private int globalPos;
    /** Current virtual size of the stream */
    private int globalSize;
    /** Points the current byte array index */
    private int globalArrayIndex;
    /** Points to a local index in the current byte array */
    private int localArrayOffset;


    /**
     * Create a new instance of DynamicByteArrayStream
     */
    public DynamicByteArrayStream(){
        bytes = new ArrayList<>();
        globalPos = 0;
        globalSize = 0;
        globalArrayIndex = 0;
        localArrayOffset = 0;
    }

    /**
     * Append an byte array to the stream
     *
     * @param 		b 			is the byte array to add.
     */
    public synchronized void append(byte[] b){
        bytes.add(b);
        globalSize += b.length;
    }

    /**
     * Append an byte array to the stream.
     * NOTE: This function will copy data.
     *
     * @param 		b 			is the byte array to add
     * @param 		offset		is the offset in the byte array
     * @param 		length		is the amount of data to add
     */
    public synchronized void append(byte[] b, int offset, int length){
        byte[] new_b = new byte[length];
        System.arraycopy(b, offset, new_b, 0, length);
        bytes.add(new_b);
        globalSize += length;
    }

    @Override
    public synchronized int read() {
        if(globalPos >= globalSize)	return -1;

        int ret = bytes.get(globalArrayIndex)[localArrayOffset] & 0xff;
        globalPos++;
        localArrayOffset++;
        if(localArrayOffset >= bytes.get(globalArrayIndex).length){
            globalArrayIndex++;
            localArrayOffset = 0;
        }
        return ret;
    }

    public synchronized int read(byte b[], int off, int len) {
        if(len <= 0) return 0;
        if(globalPos >= globalSize)	return -1;

        int bytes_read=0;
        if(globalPos+len >= globalSize) len = globalSize - globalPos;
        while(bytes_read<len){
            byte[] src = bytes.get(globalArrayIndex);
            // Read length is LONGER than local array
            if(localArrayOffset +len-bytes_read > src.length){
                int length = src.length- localArrayOffset;
                System.arraycopy(src, localArrayOffset, b, off+bytes_read, length);

                localArrayOffset = 0;
                globalArrayIndex++;
                bytes_read += length;
            }
            // Read length is SHORTER than local array
            else{
                int length = len-bytes_read;
                System.arraycopy(src, localArrayOffset, b, off+bytes_read, length);

                localArrayOffset += length;
                bytes_read += length;
            }
        }
        globalPos += len;
        return bytes_read;
    }

    public synchronized int available() {
        return globalSize - globalPos;
    }

    /**
     * Clears this stream from the byte arrays
     */
    public synchronized void clear(){
        globalSize = 0;
        reset();
        bytes.clear();
    }

    public synchronized void reset() {
        globalArrayIndex = 0;
        localArrayOffset = 0;
        globalPos = 0;
    }

    public void close() throws IOException {
        clear();
    }

    /**
     * @return 		all of the buffers content as a byte array.
     */
    public byte[] getBytes(){
        byte[] data = new byte[globalSize];
        this.read(data, 0, globalSize);
        return data;
    }


    /**
     * WARNING: This function might return a malformed String.
     *
     * @return all the contents of the buffers as a String.
     */
    public String toString(){
        return new String( this.getBytes() );
    }
}
