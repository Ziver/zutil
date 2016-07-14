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

package zutil.net.http.multipart;

import zutil.io.BufferedBoundaryInputStream;
import zutil.io.IOUtil;
import zutil.io.NullWriter;
import zutil.log.LogUtil;
import zutil.net.http.HttpHeader;
import zutil.net.http.HttpHeaderParser;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Parses a multipart/form-data http request, 
 * saves files to temporary location.
 * 
 * http://www.ietf.org/rfc/rfc1867.txt
 * 
 * @author Ziver
 *
 */
public class MultipartParser implements Iterable<MultipartField>{
    private static final Logger logger = LogUtil.getLogger();
    protected static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition".toUpperCase();
    protected static final String HEADER_CONTENT_TYPE        = "Content-Type".toUpperCase();

	/** This is the delimiter that will separate the fields */
	private String delimiter;
	/** The length of the HTTP Body */
	private long contentLength;
	/** This is the input stream */
	private InputStream in;

    private MultiPartIterator iterator;


    public MultipartParser(InputStream in, String delimiter, long length){
        this.in = in;
        this.delimiter = delimiter;
        this.contentLength = length;
    }
	public MultipartParser(HttpHeader header){
		this(header.getInputStream(),
                parseDelimiter(header.getHeader("Content-type")),
                Long.parseLong(header.getHeader("Content-Length")));
	}

    private static String parseDelimiter(String contentTypeHeader){
        String delimiter = contentTypeHeader.split(" *; *")[1];
        delimiter = delimiter.split(" *= *")[1];
        return delimiter;
    }

	public long getContentLength(){
		return contentLength;
	}



	@Override
	public Iterator<MultipartField> iterator() {
        if (iterator == null)
            iterator = new MultiPartIterator();
		return iterator;
	}


    protected class MultiPartIterator implements Iterator<MultipartField>{
        private BufferedBoundaryInputStream boundaryIn;
        private boolean firstIteration;


        protected MultiPartIterator(){
            this.boundaryIn = new BufferedBoundaryInputStream(in);

            this.boundaryIn.setBoundary("--"+delimiter);
            firstIteration = true;
        }


        /**
         * TODO: there is a bug where this returns true after the last MultiPart as it cannot read ahead. So use next() != null instead
         */
        @Override
        public boolean hasNext() {
            try {
                return boundaryIn.hasNext();
            } catch (IOException e) {
                logger.log(Level.SEVERE, null, e);
            }
            return false;
        }


        @Override
        public MultipartField next() {
            try {
                boundaryIn.next();
                if (firstIteration){
                    this.boundaryIn.setBoundary("\n--"+delimiter); // Add new-line to boundary after the first iteration
                    firstIteration = false;
                }
                String tmp = IOUtil.readLine(boundaryIn); // read the new line after the delimiter
                if (tmp == null || tmp.equals("--"))
                    return null;

                // Read Headers
                HashMap<String,String> headers = new HashMap<>();
                while ((tmp=IOUtil.readLine(boundaryIn)) != null && !tmp.isEmpty())
                    HttpHeaderParser.parseHeaderLine(headers, tmp);

                // Parse
                String disposition = headers.get(HEADER_CONTENT_DISPOSITION);
                if (disposition != null){
                    HttpHeaderParser.parseHeaderValue(headers, disposition);
                    if (headers.containsKey("form-data")){
                        if (headers.containsKey("filename")){
                            MultipartFileField field = new MultipartFileField(headers, boundaryIn);
                            return field;
                        }
                        else{
                            MultipartStringField field = new MultipartStringField(headers, boundaryIn);
                            return field;
                        }
                    }
                    else {
                        logger.warning("Only multipart form-data is supported");
                        return this.next(); // find next field
                    }
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, null, e);
            }
            return null;
        }


        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove not supported in read only stream.");
        }
    }
}
