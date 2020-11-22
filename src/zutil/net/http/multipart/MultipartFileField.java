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

package zutil.net.http.multipart;

import zutil.io.IOUtil;
import zutil.log.LogUtil;

import java.io.*;
import java.util.Map;
import java.util.logging.Logger;

import static zutil.net.http.multipart.MultipartParser.HEADER_CONTENT_TYPE;


/**
 * A class for handling multipart files
 *
 * @author Ziver
 */
public class MultipartFileField implements MultipartField{
    private static final Logger logger = LogUtil.getLogger();

    private String fieldname;
    private String filename;
    private String contentType;
    private byte[] content;
    private InputStream in;


    protected MultipartFileField(Map<String,String> headers, InputStream in) {
        this.fieldname = headers.get("name");
        this.filename = headers.get("filename");
        this.contentType = headers.get(HEADER_CONTENT_TYPE);
        this.in = in;
    }

    /**
     * @return the amount of data received for this field
     */
    public long getLength(){
        return 0; //TODO:
    }

    /**
     * @return the field name
     */
    public String getName(){
        return fieldname;
    }

    public String getFilename(){
        return filename;
    }

    public String getContentType() {
        return contentType;
    }



    /**
     * First time this method is called the contents of the
     * file will be read into a byte array and returned.
     * Subsequent calls will just return the array without
     * reading any more data from the stream.
     *
     * Note: Only one of the methods {@link #getContent()} or
     * {@link #saveToFile(File)} can be used as they will consume the data in the stream.
     *
     * @return a byte array containing the file data. null if the Stream has already been consumed
     */
    public byte[] getContent() throws IOException {
        if (in != null) {
            content = IOUtil.readContent(in);
            in = null; // reset InputStream
        }
        return content;
    }

    /**
     * Reads in all data and save it into the specified file.
     *
     * Note: Only one of the methods {@link #getContent()} or
     * {@link #saveToFile(File)} can be used as they will consume the data in the stream.
     *
     * @param   file    is the new file where the data will be stored
     */
    public void saveToFile(File file) throws IOException {
        if (in == null)
            throw new IOException("Stream already consumed.");
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        IOUtil.copyStream(in, out);
        out.close();
        in = null; // reset InputStream
    }

}
