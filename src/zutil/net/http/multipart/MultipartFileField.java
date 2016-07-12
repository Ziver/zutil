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

import zutil.io.IOUtil;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


/**
 * A class for handling multipart files
 * 
 * @author Ziver
 */
public class MultipartFileField implements MultipartField{
	private String fieldname;
	private String filename;
    private String contentType;
    private InputStream in;


	protected MultipartFileField(String name, String filename, String contentType, BufferedReader in) throws IOException {
		this.fieldname = name;
		this.filename = filename;
		this.contentType = contentType;
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

    public InputStream getInputStream(){
        return in;
    }

	/**
	 * Reads in all data and save it into the specified file
	 * 
	 * @param   file    is the new file where the data will be stored
	 */
	public void saveToFile(File file) throws IOException {
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        IOUtil.copyStream(in, out);
        out.close();
	}

}
