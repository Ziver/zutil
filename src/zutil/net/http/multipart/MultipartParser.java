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
    private static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
    private static final String HEADER_CONTENT_TYPE        = "Content-Type";

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
	public MultipartParser(InputStream in, HttpHeader header){
		this(in,
                parseDelimiter(header.getHeader("Content-type")),
                Long.parseLong( header.getHeader("Content-Length")));
	}
	public MultipartParser(HttpServletRequest req) throws IOException {
		this(req.getInputStream(),
                parseDelimiter(req.getHeader("Content-type")),
                req.getContentLength());
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
        private BufferedReader buffIn;
        private HttpHeaderParser parser;
        private boolean firstIteration;


        protected MultiPartIterator(){
            this.boundaryIn = new BufferedBoundaryInputStream(in);
            this.buffIn = new BufferedReader(new InputStreamReader(boundaryIn));
            this.parser = new HttpHeaderParser(buffIn);
            this.parser.setReadStatusLine(false);

            this.boundaryIn.setBoundary("--"+delimiter);
            firstIteration = true;
        }


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
                String tmp = buffIn.readLine(); // read the new line after the delimiter
                if (tmp == null || tmp.equals("--"))
                    return null;

                HttpHeader header = parser.read();
                String disposition = header.getHeader(HEADER_CONTENT_DISPOSITION);
                String contentType = header.getHeader("Content-Type");
                if (contentType != null && !contentType.equalsIgnoreCase("application/octet-stream"))
                    logger.warning("Unsupported ontent-Type: "+contentType);
                if (disposition != null){
                    HashMap<String,String> map = new HashMap<>();
                    HttpHeaderParser.parseHeaderValue(map, disposition);
                    if (map.containsKey("form-data")){
                        if (map.containsKey("filename")){
                            MultipartFileField field = new MultipartFileField(
                                    map.get("name"),
                                    map.get("filename"),
                                    contentType,
                                    buffIn);
                            return field;
                        }
                        else{
                            MultipartStringField field = new MultipartStringField(
                                    map.get("name"),
                                    buffIn);
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
