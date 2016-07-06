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

import java.io.File;
import java.io.IOException;


/**
 * A class for handling multipart files
 * 
 * @author Ziver
 */
public class MultipartFileField implements MultipartField{
	/** This is the temporary directory for the received files	 */
	private File tempDir;
	protected String filename;
	protected File file;
	

	protected MultipartFileField() throws IOException {
		this.file = createTempFile();
	}
	
	/**
	 * @return the amount of data received for this field
	 */
	public long getLength(){
		return 0; //TODO:
	}

    /**
     * @return the specific file name for this field.
     */
    public String getName(){
        return filename;
    }

	/**
	 * @return the File class that points to the received file
	 */
	public File getFile(){
		return file;
	}
	
	/**
	 * Moves the received file.
	 * 
	 * @param   newFile    is the new location to move the file to
	 * @return if the move was successful 
	 */
	public boolean moveFile(File newFile){
		boolean success = file.renameTo(newFile);
		if(success)
			file = newFile;
		return success;
	}

	/**
	 * Creates a temporary file in either the system
	 * temporary folder or by the setTempDir() function
	 *
	 * @return the temporary file
	 */
	protected File createTempFile() throws IOException {
		if(tempDir != null)
			return File.createTempFile("upload", ".part", tempDir.getAbsoluteFile());
		else
			return File.createTempFile("upload", ".part");
	}


	public void setTempDir(File dir){
		if(!dir.isDirectory())
			throw new RuntimeException("\""+dir.getAbsolutePath()+"\" is not a directory!");
		if(!dir.canWrite())
			throw new RuntimeException("\""+dir.getAbsolutePath()+"\" is not writable!");
		tempDir = dir;
	}
}
