/*
 * Copyright (c) 2015 ezivkoc
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
        byte[] buff = new byte[256];
        int len = 0;
		while((len = stream.read(buff)) != -1){
			dyn_buff.append(buff, 0, len);
		}
        stream.close();

		return dyn_buff.getBytes();
	}

    /**
     * Reads and returns all the content of a stream as a String.
     * This function will close the inout stream at the end.
     *
     * @param 		stream
     * @return a String with the content of the stream
     */
    public static String getContentString(InputStream stream) throws IOException{
        StringBuilder str = new StringBuilder();
        String line;
        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        while((line = in.readLine()) != null){
            str.append(line).append("\n");
        }
        in.close();

        return str.toString();
    }

    /**
     * Copies all data from the input stream to the output stream
     */
    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buff = new byte[256];
        int len;
        while((len = in.read(buff)) > 0){
            out.write(buff, 0, len);
        }
    }
}
