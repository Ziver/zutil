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

package zutil.net.update;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import zutil.Hasher;
import zutil.io.file.FileUtil;

/**
 * This class is used to store the files
 * and there hashes
 * 
 * @author Ziver
 */
public class FileInfo implements Serializable{
	private static final long serialVersionUID = 1L;

	private transient File file;
	private String path;
	private String hash;
	private long size;

	public FileInfo(String root, File file) throws IOException{
		path = FileUtil.relativePath(file, root);		
		hash = Hasher.MD5(file);
		size = file.length();
		this.file = file;
	}


	public String getPath() {
		return path;
	}
	public String getHash() {
		return hash;
	}
	public long getSize() {
		return size;
	}
	public File getFile(){
		return file;
	}


	public boolean equals(Object comp){
		if(comp instanceof FileInfo){
			FileInfo tmp = (FileInfo)comp;
			return path.equals(tmp.path) && hash.equals(tmp.hash);
		}
		return false;
	}

	public String toString(){
		return path;
	}
}
