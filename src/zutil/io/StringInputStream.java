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

package zutil.io;

import java.io.InputStream;

/**
 * This InputStream uses a string as the data source.
 *
 * @author Ziver
 *
 */
public class StringInputStream extends InputStream {

    private StringBuilder buffer = new StringBuilder();
    private int pos;
    private int mark;


    /**
     * Creates a new instance of this class
     */
    public StringInputStream() { }

    public StringInputStream(String data) {
        add(data);
    }


    /**
     * Returns an estimate of the number of bytes
     * that can be read (or skipped over) from this
     * input stream without blocking by the next
     * invocation of a method for this input stream.
     */
    public int available() {
        return buffer.length() - pos;
    }


    /**
     * Reads the next byte of data from the input stream.
     */
    public synchronized int read() {
        if (available() <= 0)
            return -1;

        int ret = buffer.charAt(pos);
        pos++;
        return ret;
    }

    /**
     * Reads up to len bytes of data from the input stream into an array of bytes.
     */
    public synchronized int read(byte[] b, int off, int len) {
        if (available() <= 0)
            return -1;

        if (available() < len)
            len = available();

        for (int i=0; i<len; i++) {
            b[off + i] = (byte) buffer.charAt(pos);
            pos++;
        }

        return len;
    }


    /**
     * Skips over and discards n bytes of data from this
     * input stream.
     *
     * @param	n		is the amount characters to skip
     */
    public synchronized long skip(long n) {
        if (available() < n)
            n = available();

        pos += n;
        return n;
    }

    /**
     * Tests if this input stream supports the mark and
     * reset methods.
     */
    public boolean markSupported() {
        return true;
    }

    /**
     * Set the current position as a sae point that can be returned to with the {@link #reset()} method
     *
     * @param readLimit parameter not used.
     */
    public synchronized void mark(int readLimit) {
        mark = pos;
    }

    /**
     * Reset the reading position back to the mark point.
     */
    public synchronized void reset() {
        pos = mark;
    }


    /**
     * Closes this input stream and releases any system
     * resources associated with the stream.
     */
    public void close() {
        clear();
    }

    public synchronized void clear() {
        buffer = new StringBuilder();
        pos = 0;
        mark = 0;
    }

    /**
     * Clears the part of the String buffer that has already been read.
     */
    public synchronized void clearOld() {
        buffer.delete(0, pos);
        pos = 0;
        mark = 0;
    }

    public void add(String data) {
        buffer.append(data);
    }
}
