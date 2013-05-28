/*******************************************************************************
 * Copyright (c) 2013 Ziver
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
 ******************************************************************************/

package zutil.net.http.multipart;

import java.io.File;


/**
 * A class for handling multipart files
 * 
 * @author Ziver
 */
public class MultipartFile extends MultipartField{
	protected String filename;
	protected File file;
	

	protected MultipartFile(File tempFile){
		this.file = tempFile;
	}
	
	/**
	 * @return the amount of data received for this field
	 */
	public long getReceivedBytes(){
		return received;
	}
	
	/**
	 * @return the value of the field
	 */
	public String getValue(){
		return null;
	}
	
	/**
	 * @return the filename
	 */
	public String getFilename(){
		return filename;
	}
	
	/**
	 * @return the File class that points to the received file
	 */
	public File getFile(){
		return file;
	}
	
	/**
	 * Moves this file
	 * 
	 * @param new_file is the new location to move the file to
	 * @return if the move was successful 
	 */
	public boolean moveFile(File new_file){
		boolean success = file.renameTo(new_file);
		if(success)
			file = new_file;
		return success;
	}
}
