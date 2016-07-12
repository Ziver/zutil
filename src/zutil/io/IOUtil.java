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
     * This function will close the inout stream at the end.
	 * 
	 * @param 		stream
	 * @return the stream contents
	 */
	public static byte[] getContent(InputStream stream) throws IOException{
        DynamicByteArrayStream dyn_buff = new DynamicByteArrayStream();
        byte[] buff = new byte[8192];
        int len = 0;
		while((len = stream.read(buff)) != -1){
			dyn_buff.append(buff, 0, len);
		}
        stream.close();

		return dyn_buff.getBytes();
	}

    /**
     * Reads and returns all the content of a stream as a String.
     * This function will close the input stream at the end.
     *
     * @param 		stream
     * @return a String with the content of the stream
     */
    public static String getContentAsString(InputStream stream) throws IOException{
        return getContentAsString(new InputStreamReader(stream));
    }

    /**
     * Reads and returns all the content of a stream as a String.
     * This function will close the input stream at the end.
     *
     * @param 		reader
     * @return a String with the content of the stream
     */
    public static String getContentAsString(Reader reader) throws IOException{
        StringBuilder str = new StringBuilder();
        BufferedReader in = null;
        if(reader instanceof BufferedReader)
            in = (BufferedReader) reader;
        else
            in = new BufferedReader(reader);

        String line;
        while((line = in.readLine()) != null){
            str.append(line).append("\n");
        }
        in.close();

        return str.toString();
    }

    /**
     * Reads on line terminated by a new line or carriage return from a stream.
     *
     * @param   in      the stream to read from
     * @return          a String that contains one line excluding line terminating
     *                  characters, null if it is the end of the stream
     */
    public static String readLine(InputStream in) throws IOException {
        StringBuilder str = new StringBuilder(80);
        int c = 0;
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
     */
    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buff = new byte[8192]; // This is the default BufferedInputStream buffer size
        int len;
        while((len = in.read(buff)) > 0){
            out.write(buff, 0, len);
        }
    }

    /**
     * Copies all data from one Reader to another Writer.
     * The streams will not be closed after method has returned.
     */
    public static void copyStream(Reader in, Writer out) throws IOException {
        char[] buff = new char[8192]; // This is the default BufferedReader buffer size
        int len;
        while((len = in.read(buff)) > 0){
            out.write(buff, 0, len);
        }
    }
}
