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

package zutil.io.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class FileSearch implements Iterable<File>{
	private File root;
	
	// Search parameters
	private String fileName;
	private String extension;
	private boolean recursive;
	//private int depth;
	private boolean searchFiles;
	private boolean searchFolders;
	
	
	public FileSearch(File root){
		this.root = root;
		searchFiles = true;
		searchFolders = true;
	}
	
	
	/**
	 * @param 	file	Sets the exact file name to search for (includes extension)
	 */
	public void setFileName(String file){
		fileName = file;
	}
	
	/**
	 * Sets the file extensions to search for (should not include . at the beggining)
	 */
	public void setExtension(String ext){
		
	}
	
	/**
	 * Sets if the search should go into sub-folders
	 */
	public void setRecursive(boolean recursive){
		this.recursive = recursive;
	}
	
	/**
	 * Sets how deep into folders the search should go 
	 * (Recursion needs to be enabled for this attribute to be used)
	 */
	//public void setDepth(int depth){
	//	this.depth = depth;
	//}
	
	public void searchFiles(boolean searchFiles){
		this.searchFiles = searchFiles;
	}
	public void searchFolders(boolean searchFolders){
		this.searchFolders = searchFolders;
	}

	
	@Override
	public Iterator<File> iterator() {
		return new FileSearchIterator();
	}
	
	
	protected class FileSearchIterator implements Iterator<File>{
		private ArrayList<File> fileList;
		private int currentIndex;

		public FileSearchIterator(){
			fileList = new ArrayList<File>();
			currentIndex = 0;
			
			addToFileList(root.listFiles());
			next();
		}
		
		@Override
		public boolean hasNext() {
			return currentIndex != fileList.size();
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public File next() {
			if(currentIndex < 0)
				return null;
			// Temporarily save the current file
			File ret = fileList.get(currentIndex);
			
			// Find the next file
			for(; currentIndex<fileList.size(); currentIndex++){
				File file = fileList.get(currentIndex);
				if(recursive && file.isDirectory()){
					addToFileList(file.listFiles());
					if(searchFolders && file.getName().equalsIgnoreCase(fileName))
						break;
						
				}
				else if(searchFiles && file.isFile()){
					if(extension != null && FileUtil.getFileExtension(file).equalsIgnoreCase(extension))
						break;
					else if(fileName != null && file.getName().equalsIgnoreCase(fileName))
						break;
				}
			}
			
			return ret;
		}
		
		private void addToFileList(File[] list){
			for(File file : list){
				fileList.add(file);
			}
		}
		
	}
}
