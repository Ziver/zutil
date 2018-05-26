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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A stream that is handled as a iterator. The stream will read
 * until it gets to a boundary and will return -1 (end of stream).
 * The stream will not return any data until the {@link #next()}
 * method is called.
 *
 * @author Ziver
 *
 */
public class BufferedBoundaryInputStream extends FilterInputStream{
    /** The size of the buffer in bytes */
    protected static final int DEFAULT_BUF_SIZE = 8192;

    /** The raw buffer */
    private byte buffer[];
    /** The current position in the buffer */
    private int buf_pos = 0;
    /** The end position of the buffer */
    private int buf_end = 0;
    /** Boundary position, 0< means no boundary found */
    private int buf_bound_pos = -1;
    /** The boundary (the delimiter)  */
    private byte[] boundary;
    /** The position in the buffer where user has marked, -1 if no mark is set **/
    private int buf_mark = -1;
    /** The read limit of the mark, after this limit the mark will be invalidated **/
    private int buf_mark_limit = 0;


    /**
     * Creates a instance of this class with a default buffer size of 64K
     *
     * @param 	in			is the InputStream that the buffer will use
     */
    public BufferedBoundaryInputStream(InputStream in){
        this(in, DEFAULT_BUF_SIZE);
    }

    /**
     * Creates a instance of this class
     *
     * @param 	in			is the InputStream that the buffer will use
     * @param	buf_size	speifies the buffer size
     */
    public BufferedBoundaryInputStream(InputStream in, int buf_size){
        super(in);
        buffer = new byte[buf_size];
    }


    /**
     * @return 			the next byte from the stream or -1 if EOF or stream has encountered a boundary
     */
    public int read() throws IOException{
        if (fillBuffer() < 0)
            return -1;

        if(isOnBoundary())
            return -1; // boundary
        return buffer[buf_pos++];
    }

    /**
     * Fills the given array with data from the buffer
     *
     * @param 	b 		is the array that will be filled
     * @return 			the amount of bytes read or -1 if EOF or stream is on a boundary
     */
    public int read(byte b[]) throws IOException {
        return read(b, 0, b.length);
    }

    /**
     * Reads a given length of bytes from the buffer
     *
     * @param 	b 		is the array that will be filled
     * @param 	off 	is the offset in the array
     * @param 	len 	is the amount to read
     * @return 			the amount of bytes read or -1 if EOF or stream is on a boundary
     */
    public int read(byte b[], int off, int len) throws IOException {
        if (fillBuffer() < 0)
            return -1; // EOF
        if (isOnBoundary())
            return -1; // boundary

        // The request is larger then the buffer size
        int leftover = available();
        if (len > leftover)
            len = leftover;

        // the boundary is in the read range
        if (buf_pos < buf_bound_pos && buf_bound_pos < buf_pos+len)
            len = buf_bound_pos - buf_pos;

        System.arraycopy(buffer, buf_pos, b, off, len);
        buf_pos += len;
        return len;
    }


    /**
     * @return if the current position in the buffer is a boundary
     */
    public boolean isOnBoundary(){
        return buf_bound_pos == buf_pos;
    }



    /**
     * Checks if there is more data available after the boundary.
     * Note if the data between boundaries are longer than the buffer
     * size then this method will return true until the next boundary
     * is available in the buffer or the end of the stream.
     *
     * @return true if there is more data available after the closest boundary.
     */
    public boolean hasNext() throws IOException {
        if (buf_bound_pos < 0 && fillBuffer() >= 0)
            return true; // the boundary not in the buffer?
        if (buf_bound_pos >= 0 && buf_bound_pos + boundary.length < buf_end)
            return true; // there is more data beyond boundary in the buffer
        return false;
    }
    /**
     * Skips over the closest boundary
     */
    public void next() throws IOException {
        // read data until we find the next boundary or get to the end of the stream
        if (buf_bound_pos < 0) {
            while (fillBuffer() >= 0 && buf_bound_pos < 0)
                buf_pos = buf_end;
        }

        if (buf_bound_pos >= 0){ // is boundary in buffer?
            buf_pos += boundary.length;
            findNextBoundary();
        }
    }


    /**
     * Skips a specific amounts of bytes in the buffer.
     * Note that his method does not check for boundaries
     * and it will only skip in the local buffer.
     *
     * @param	n 	the number of bytes to be skipped.
     * @return		the actual number of bytes skipped,
     *              0 if it has reach the end of the buffer but does not mean end of stream.
     */
    public long skip(long n) throws IOException {
        int leftover = available();
        if(n > leftover){
            buf_pos = buf_end;
            return leftover;
        }
        return buf_pos += n;
    }


    /**
     * Sets the boundary for the stream
     */
    public void setBoundary(String b){
        this.boundary = b.getBytes();
        findNextBoundary(); // redo the search with the new boundary
    }

    /**
     * Sets the boundary for the stream
     */
    public void setBoundary(byte[] b){
        boundary = new byte[b.length];
        System.arraycopy(b, 0, boundary, 0, b.length);
        findNextBoundary(); // redo the search with the new boundary
    }

    /**
     * @return     an estimate of the number of bytes that can be read (or skipped
     *             over) from this buffered input stream without blocking.
     */
    public int available() throws IOException {
        if (super.available() <= 0)
            return buf_end - buf_pos; // return the whole stream as there are no more boundaries
        else if (buf_end < boundary.length)
            return 0; // we need a safety block in case boundary is split
        return buf_end - boundary.length - buf_pos;
    }



    /**
     * @return true for BufferedBoundaryInputStream
     */
    public boolean markSupported(){
        return true;
    }

    /**
     * See {@link InputStream#mark(int)} for details
     *
     * @param   readlimit   the amount of data that can be read before
     *                      the mark is invalidated. Note that if the
     *                      readlimit is larger than the buffer size,
     *                      then the buffer size will be used instead.
     */
    public void mark(int readlimit) {
        buf_mark_limit = readlimit;
        buf_mark = buf_pos;
    }

    /**
     * See {@link InputStream#reset()} for details
     *
     * @exception  IOException if the mark is or has been invalidated
     */
    public void reset() throws IOException {
        if (buf_mark < 0)
            throw new IOException("Resetting to invalid mark");
        buf_mark_limit = 0;
        buf_pos = buf_mark;
        buf_mark = -1;
        findNextBoundary();
    }



    /**
     * Checks if the buffer needs to be appended with data.
     * If so it moves the remaining data to the beginning of the
     * buffer and then fills the buffer with data from the source
     * stream.
     *
     * @return 			the number of new bytes read from the source stream,
     *                  or -1 if the buffer is empty and it is the end of the stream
     */
    private int fillBuffer() throws IOException {
        // Do we need to fill the buffer
        if(buf_pos < buf_end-boundary.length)
            return 0;

        int leftover = buf_end - buf_pos;
        int tmp_pos = buf_pos;
        // Mark handling
        if (buf_mark > 0) {
            // Mark enabled and it has not already been moved to the beginning of the buffer
            leftover = buf_end - buf_mark;
            buf_pos  = buf_mark;
        } else if (buf_pos >= buf_mark_limit ||
                buf_mark_limit >= buffer.length) {
            // we have passed the read limit or read limit is bigger than the buffer, so reset mark
            buf_mark = -1;
            buf_mark_limit = 0;
        }

        // Move the end of the buffer to the start to not miss any split boundary
        if (leftover > 0 && buf_pos != 0)
            System.arraycopy(buffer, buf_pos, buffer, 0, leftover);
        // Set new positions
        if (buf_mark >= 0){
            buf_pos = tmp_pos - buf_mark;
            buf_mark = 0;
        } else
            buf_pos = 0;
        buf_end = leftover;

        // Copy in new data from the stream
        int n = -1;
        if (super.available() > 0) { // is there any data available
            n = super.read(buffer, buf_end, buffer.length - buf_end);
            if (n >= 0)
                buf_end = leftover = buf_end + n;
        }

        // Update boundary position
        findNextBoundary();
        return ((leftover > 0 && n < 0) ? 0 : n);
    }

    /**
     * Searches for the nearest boundary from the current buffer position
     */
    private void findNextBoundary(){
        // No need to check for boundary if buffer is smaller than the boundary length
        for (int i = buf_pos; i <= buf_end-boundary.length; i++) {
            for (int b = 0; b < boundary.length; b++) {
                if (buffer[i + b] != boundary[b])
                    break;
                else if (b == boundary.length - 1) {
                    buf_bound_pos = i;
                    return;
                }
            }
        }
        buf_bound_pos = -1;
    }
}
