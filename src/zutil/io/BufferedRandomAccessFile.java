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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * This class is a buffer for the RandomeAccesFile
 * Inspiration:
 * http://www.javaworld.com/javaworld/javatips/jw-javatip26.html
 *
 * @author Ziver
 */
public class BufferedRandomAccessFile extends RandomAccessFile{
    // The size of the buffer in Byte
    private int BUF_SIZE = 64*1024;

    // The Buffer
    byte buffer[];
    // The end of the buffer
    int buf_end = 0;
    // The position in the buffer
    int buf_pos = 0;
    // The real file pointer position where the buffer starts
    long file_pos = 0;

    /**
     * Create a instance of this buffer
     *
     * @param filename is the file to read from
     * @param mode as in {@link java.io.RandomAccessFile#RandomAccessFile(File file, String mode)}
     */
    public BufferedRandomAccessFile(String filename, String mode) throws IOException{
        this(new File(filename), mode);
    }

    /**
     * Create a instance of this buffer
     *
     * @param file is the file to read from
     * @param mode as in {@link java.io.RandomAccessFile#RandomAccessFile(File file, String mode)}
     */
    public BufferedRandomAccessFile(File file, String mode) throws IOException{
        super(file,mode);
        invalidate();
        buffer = new byte[BUF_SIZE];
    }

    /**
     * Create a instance of this buffer
     *
     * @param filename is the file to read from
     * @param mode as in {@link java.io.RandomAccessFile#RandomAccessFile(File file, String mode)}
     * @param bufsize is the buffer size in bytes
     */
    public BufferedRandomAccessFile(String filename, String mode, int bufsize) throws IOException{
        this(new File(filename), mode, bufsize);
    }

    /**
     * Create a instance of this buffer
     *
     * @param file is the file to read from
     * @param mode as in {@link java.io.RandomAccessFile#RandomAccessFile(File file, String mode)}
     * @param bufsize is the buffer size in bytes
     */
    public BufferedRandomAccessFile(File file, String mode, int bufsize) throws IOException{
        super(file,mode);
        invalidate();
        BUF_SIZE = bufsize;
        buffer = new byte[BUF_SIZE];
    }

    /**
     * Reads in data from the file to the buffer
     *
     * @return the buffer
     */
    private int fillBuffer() throws IOException {
        int n = super.read(buffer, 0, BUF_SIZE );
        if(n >= 0) {
            file_pos +=n;
            buf_end = n;
            buf_pos = 0;
        }
        return n;
    }

    /**
     * Resets the buffer
     */
    private void invalidate() throws IOException {
        buf_end = 0;
        buf_pos = 0;
        file_pos = super.getFilePointer();
    }

    /**
     * @return the next byte in the buffer
     */
    public final int read() throws IOException{
        if(buf_pos >= buf_end) {
            if(fillBuffer() < 0)
                return -1;
        }
        if(buf_end == 0) {
            return -1;
        } else {
            buf_pos++;
            return buffer[buf_pos-1];
        }
    }

    /**
     * Fills the given array with data from the buffer
     *
     * @param b is the array that will be filled
     * @return the amount of bytes read or -1 if eof
     */
    public int read(byte b[]) throws IOException {
        return read(b, 0, b.length);
    }

    /**
     * Reads a given length of bytes from the buffer
     *
     * @param b is the array that will be filled
     * @param off is the offset in the array
     * @param len is the amount to read
     * @return the amount of bytes read or -1 if eof
     */
    public int read(byte b[], int off, int len) throws IOException {
        if(buf_pos >= buf_end) {
            if(fillBuffer() < 0)
                return -1; // EOF
        }

        // Copy from buffer
        int leftover = buf_end - buf_pos;
        if(len <= leftover) {
            System.arraycopy(buffer, buf_pos, b, off, len);
            buf_pos += len;
            return len;
        }

        System.arraycopy(buffer, buf_pos, b, off, leftover);
        int n = super.read(b, off+leftover, len-leftover );
        fillBuffer();
        if( n >= 0 )
            return leftover + n;
        return leftover;
        /*for(int i = 0; i < len; i++) {
            int c = this.read();
            if(c != -1)
                b[off+i] = (byte)c;
            else {
                return i;
            }
        }
        return len;*/
    }

    /**
     * @return the file pointer in the file
     */
    public long getFilePointer() {
        long l = file_pos;
        return (l - buf_end + buf_pos) ;
    }

    /**
     * Changes the file pointer to another position
     *
     * @param pos The position to move the pointer to
     */
    public void seek(long pos) throws IOException {
        int n = (int)(file_pos - pos);
        if(n >= 0 && n <= buf_end) {
            buf_pos = buf_end - n;
        } else {
            super.seek(pos);
            invalidate();
        }
    }

    /**
     * This method is a replacement for readLine()
     *
     * @return the next line in the file
     */
     public final String readNextLine() throws IOException {
        String str;
        if(buf_end-buf_pos <= 0) {
            if(fillBuffer() < 0) {
                throw new IOException("Error filling buffer!");
            }
        }
        int lineEnd = -1;
        for(int i = buf_pos; i < buf_end; i++) {
            if(buffer[i] == '\n') {
                lineEnd = i;
                break;
            }
        }
        if(lineEnd < 0) {
            StringBuilder input = new StringBuilder(256);
            int c;
            while (((c = read()) != -1) && (c != '\n')) {
                input.append((char)c);
            }
            if ((c == -1) && (input.length() == 0)) {
                return null;
            }
            return input.toString();
        }

        if(lineEnd > 0 && buffer[lineEnd-1] == '\r'){
            str = new String(buffer, buf_pos, lineEnd - buf_pos -1);
        }
        else {
            str = new String(buffer, buf_pos, lineEnd - buf_pos);
        }
        buf_pos = lineEnd +1;
        return str;
     }

}
