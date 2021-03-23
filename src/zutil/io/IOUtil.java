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

import java.io.*;

/**
 * Utility class for streams and general IO stuff
 *
 * @author Ziver
 *
 */
public class IOUtil {

    /**
     * Reads and returns all the content of a stream.
     * The InputStream will not be closed
     *
     * @param stream      the stream to read from
     * @return a byte array with the stream contents
     */
    public static byte[] readContent(InputStream stream) throws IOException{
        return readContent(stream, false);
    }
    /**
     * Reads and returns all the content of a stream.
     *
     * @param stream      the stream to read from
     * @param close       true if the stream should be closed at the end
     * @return a byte array with the stream contents
     */
    public static byte[] readContent(InputStream stream, boolean close) throws IOException{
        DynamicByteArrayStream dyn_buff = new DynamicByteArrayStream();
        byte[] buff = new byte[8192];
        int len;
        while ((len = stream.read(buff)) >= 0) {
            dyn_buff.append(buff, 0, len);
        }

        if (close) stream.close();
        return dyn_buff.getBytes();
    }


    /**
     * Reads and returns all the content of a stream as a String.
     * The stream will not be closed.
     *
     * @param stream      the stream to read from
     * @return a String with the content of the stream
     */
    public static String readContentAsString(InputStream stream) throws IOException{
        return readContentAsString(stream, -1,false);
    }
    /**
     * Reads and returns all the content of a stream as a String.
     *
     * @param stream      the stream to read from
     * @param close       true if the stream should be closed at the end
     * @return a String with the content of the stream
     */
    public static String readContentAsString(InputStream stream, boolean close) throws IOException{
        return readContentAsString(stream, -1, close);
    }
    /**
     * Reads and returns the given length from a stream and as String.
     * The stream will not be closed.
     *
     * @param stream      the stream to read from
     * @param length      the amount of characters to read from the stream
     * @return a String with the content of the stream
     */
    public static String readContentAsString(InputStream stream, int length) throws IOException{
        return readContentAsString(stream, length, false);
    }
    /**
     * Reads and returns the given length from a stream and as String.
     * The stream will not be closed.
     *
     * @param stream      the stream to read from
     * @param length      the amount of characters to read from the stream
     * @return a String with the content of the stream
     */
    public static String readContentAsString(InputStream stream, int length, boolean close) throws IOException{
        StringBuilder str = (length > 0 ? new StringBuilder(length) : new StringBuilder());

        int readLength = 0;
        int c;
        while ((length < 0 || readLength < length) && (c = stream.read()) >= 0) {
            str.append((char) c);
            readLength++;
        }

        if (close) stream.close();
        return str.toString();
    }


    /**
     * Reads and returns all the content of a stream as a String.
     * The stream will not be closed.
     *
     * @param reader      the stream to read from
     * @return a String with the content of the stream
     */
    public static String readContentAsString(Reader reader) throws IOException{
        return readContentAsString(reader, false);
    }
    /**
     * Reads and returns all the content of a stream as a String.
     *
     * @param reader      the stream to read from
     * @param close       true if the stream should be closed at the end
     * @return a String with the content of the stream
     */
    public static String readContentAsString(Reader reader, boolean close) throws IOException{
        return readContentAsString(reader, -1, close);
    }
    /**
     * Reads and returns the given length from a stream and as String.
     * The stream will not be closed.
     *
     * @param reader      the stream to read from
     * @param length      the amount of characters to read from the stream
     * @return a String with the content of the stream
     */
    public static String readContentAsString(Reader reader, int length) throws IOException{
        return readContentAsString(reader, length,false);
    }
    /**
     * Reads and returns the given length from a stream and as String.
     *
     * @param reader      the stream to read from
     * @param length      the amount of characters to read from the stream, set negative value to read until end of the stream
     * @param close       true if the stream should be closed at the end
     * @return a String with the content of the stream
     */
    public static String readContentAsString(Reader reader, int length, boolean close) throws IOException{
        StringBuilder str = (length > 0 ? new StringBuilder(length) : new StringBuilder());

        int readLength = 0;
        int c;
        while ((length < 0 || readLength < length) && (c = reader.read()) >= 0) {
            str.append((char) c);
            readLength++;
        }

        if (close) reader.close();
        return str.toString();
    }


    /**
     * Reads one line terminated by a new line or carriage return from a stream.
     * Will only read ASCII based char streams.
     *
     * @param in        the stream to read from
     * @return          a String that contains one line excluding line terminating
     *                  characters, null if it is the end of the stream
     */
    public static String readLine(InputStream in) throws IOException {
        StringBuilder str = new StringBuilder(80);
        int c;
        while ((c=in.read()) >= 0 && (c != '\n') && (c != '\r'))
            str.append((char)c);
        if (c == '\r')
            in.read(); // if the last char is carriage return we assume the next char in the stream will be new line so skip it
        if (c == -1 && str.length() == 0)
            return null; // End of the stream
        return str.toString();
    }
    /**
     * Reads one line terminated by a new line or carriage return from a Reader.
     * Will only read ASCII based char streams.
     *
     * @param in        the Reader to read from
     * @return          a String that contains one line excluding line terminating
     *                  characters, null if it is the end of the stream
     */
    public static String readLine(Reader in) throws IOException {
        StringBuilder str = new StringBuilder(80);
        int c;
        while ((c=in.read()) >= 0 && (c != '\n') && (c != '\r'))
            str.append((char)c);
        if (c == '\r')
            in.read(); // if the last char is carriage return we assume the next char in the stream will be new line so skip it
        if (c == -1 && str.length() == 0)
            return null; // End of the stream
        return str.toString();
    }


    /**
     * Copies all data from one InputStream to another OutputStream.
     * The streams will not be closed after method has returned.
     *
     * @param in    the source stream
     * @param out   the target stream
     */
    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buff = new byte[8192]; // This is the default BufferedInputStream buffer size
        int len;
        while ((len = in.read(buff)) > 0) {
            out.write(buff, 0, len);
        }
    }

    /**
     * Copies all data from one Reader to another Writer.
     * The streams will not be closed after method has returned.
     *
     * @param in    the source stream
     * @param out   the target stream
     */
    public static void copyStream(Reader in, Writer out) throws IOException {
        char[] buff = new char[8192]; // This is the default BufferedReader buffer size
        int len;
        while ((len = in.read(buff)) > 0) {
            out.write(buff, 0, len);
        }
    }
}
