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

package zutil.net.http.multipart;

import zutil.ProgressListener;
import zutil.net.http.HttpHeaderParser;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses a multipart/form-data http request, 
 * saves files to temporary location.
 * 
 * http://www.ietf.org/rfc/rfc1867.txt
 * 
 * @author Ziver
 *
 */
public class MultipartParser {
	/** This is the temporary directory for the received files	 */
	private File tempDir;
	/** This is the delimiter that will separate the fields */
	private String delimiter;
	/** The length of the HTTP Body */
	private long contentLength;
	/** This is the input stream */
	private BufferedReader in;
	
	/** This is the listener that will listen on the progress */
	private ProgressListener<MultipartParser,MultipartField> listener;
	
	
	
	public MultipartParser(BufferedReader in, HttpHeaderParser header){
		this.in = in;
		
		String cotype = header.getHeader("Content-type");
		cotype = cotype.split(" *; *")[1];
		delimiter = cotype.split(" *= *")[1];
		
		contentLength = Long.parseLong( header.getHeader("Content-Length") );
	}
	
	public MultipartParser(BufferedReader in, HttpServletRequest req){
		this.in = in;
		
		String cotype = req.getHeader("Content-type");
		cotype = cotype.split(" *; *")[1];
		delimiter = cotype.split(" *= *")[1];
		
		contentLength = req.getContentLength();
	}
	
	public MultipartParser(BufferedReader in, String delimiter, long length){
		this.in = in;
		this.delimiter = delimiter;		
		this.contentLength = length;
	}
	
	
	/**
	 * @param listener is the listener that will be called for progress
	 */
	public void setListener(ProgressListener<MultipartParser,MultipartField> listener){
		this.listener = listener;
	}
	
	/**
	 * Parses the HTTP Body and returns a list of fields
	 *  
	 * @return A list of FormField
	 */
	public List<MultipartField> parse() throws IOException{
		ArrayList<MultipartField> list = new ArrayList<MultipartField>();
		parse(list, delimiter);
		return list;
	}


	private void parse(List<MultipartField> list, String delimiter) throws IOException{
		// TODO: 
	}

	
	/**
	 * Creates a temporary file in either the system 
	 * temporary folder or by the setTempDir() function
	 * 
	 * @return the temporary file
	 */
	protected File createTempFile() throws IOException{
		if(tempDir != null) 
			return File.createTempFile("upload", ".part", tempDir.getAbsoluteFile());
		else
			return File.createTempFile("upload", ".part");
	}
	
	/**
	 * Sets the initial delimiter
	 * 
	 * @param delimiter is the new delimiter
	 */
	public void setDelimiter(String delimiter){
		this.delimiter = delimiter;
	}
	
	public void setTempDir(File dir){
		if(!dir.isDirectory())
			throw new RuntimeException("\""+dir.getAbsolutePath()+"\" is not a directory!");
		if(!dir.canWrite())
			throw new RuntimeException("\""+dir.getAbsolutePath()+"\" is not writable!");
		tempDir = dir;
	}
	
	public long getContentLength(){
		return contentLength;
	}
}
