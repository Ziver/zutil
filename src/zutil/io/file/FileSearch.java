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

package zutil.io.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import zutil.io.InputStreamCloser;

public class FileSearch implements Iterable<FileSearch.FileSearchItem>{
	// Constants
	private static final List<String> compressedFileExtensions = Arrays.asList(new String[]{
			"jar", "zip"
	});

	// Constructor params
	private File root;

	// Search parameters
	private String fileName;
	private String extension;
	private boolean recursive;
	//private int depth;
	private boolean searchFiles;
	private boolean searchCompressedFiles;
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
	 * Sets the file extensions to search for (should not include . at the beginning)
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
	public void searchCompressedFiles(boolean searchCompressedFiles){
		this.searchCompressedFiles = searchCompressedFiles;
	}
	public void searchFolders(boolean searchFolders){
		this.searchFolders = searchFolders;
	}


	@Override
	public Iterator<FileSearchItem> iterator() {
		return new FileSearchIterator();
	}


	protected class FileSearchIterator implements Iterator<FileSearchItem>{
		private ArrayList<FileSearchItem> fileList;
		private int currentIndex;
		private FileSearchItem nextItem;

		public FileSearchIterator(){
			fileList = new ArrayList<FileSearchItem>();
			currentIndex = 0;

			addFiles(new FileSearchFileItem(root), root.list());
			next();
		}

		@Override
		public boolean hasNext() {
			return currentIndex < fileList.size();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public FileSearchItem next() {
			if(currentIndex < 0 || currentIndex >= fileList.size())
				return null;
			// Temporarily save the current file
			FileSearchItem ret = fileList.get(currentIndex);
			currentIndex++;

			// Find the next file
			for(; currentIndex<fileList.size(); currentIndex++){
				FileSearchItem file = fileList.get(currentIndex);
				if(recursive && file.isDirectory()){
					addFiles(file, file.listFiles());
					if(searchFolders && file.getName().equalsIgnoreCase(fileName))
						break;						
				}
				else if(searchCompressedFiles && file.isFile() &&
						compressedFileExtensions.contains(FileUtil.getFileExtension(file.getName()).toLowerCase())){
					try {
						ZipFile zipFile = new ZipFile(file.getPath());
						Enumeration<? extends ZipEntry> e = zipFile.entries();
						while(e.hasMoreElements()){
							ZipEntry entry = e.nextElement();
							fileList.add(new FileSearchZipItem(file.getPath(), entry));
						}
						zipFile.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else if(searchFiles && file.isFile()){
					if(extension != null && FileUtil.getFileExtension(file.getName()).equalsIgnoreCase(extension))
						break;
					else if(fileName != null && file.getName().equalsIgnoreCase(fileName))
						break;
				}
			}

			return ret;
		}

		private void addFiles(FileSearchItem root, String[] list){
			if(root instanceof FileSearchFileItem) {
				for (String file : list) {
					fileList.add(new FileSearchFileItem(
							new File(((FileSearchFileItem)root).file, file)));
				}
			}
		}

	}



	public interface FileSearchItem{
		/** @return a file or folder name **/
		public String getName();
		/** @return a path to the file or folder, in case of a compressed file the path to the package will be returned **/
		public String getPath();

		public boolean isCompressed();
		public boolean isFile();
		public boolean isDirectory();

		/** @return an InputStream if this is a file otherwise null **/
		public InputStream getInputStream() throws IOException;
		/** @return an String array with all files if this is a folder otherwise null **/
		public String[] listFiles();
	}


	public class FileSearchFileItem implements FileSearchItem{
		private File file;

		protected FileSearchFileItem(File file){
			this.file = file;
		}

		public String getName()             { return file.getName(); }
		public String getPath()             { return file.getAbsolutePath(); }

		public boolean isCompressed()       { return false; }
		public boolean isFile()             { return file.isFile(); }
		public boolean isDirectory()        { return file.isDirectory(); }

		public InputStream getInputStream() throws IOException { return new FileInputStream(file); }
		public String[] listFiles()         { return file.list(); }

	}

	public class FileSearchZipItem implements FileSearchItem{
		private String file;
		private ZipEntry entry;

		protected FileSearchZipItem(String file, ZipEntry entry){
			this.file = file;
			this.entry = entry;
		}

		public String getName()                           { return entry.getName(); }
		public String getPath()                           { return file; }

		public boolean isCompressed()                     { return true; }
		public boolean isFile()                           { return !entry.isDirectory(); }
		public boolean isDirectory()                      { return entry.isDirectory();	}

		public InputStream getInputStream() throws IOException {
			ZipFile zip = new ZipFile(file);
			return new InputStreamCloser(zip.getInputStream(entry), zip);
		}
		public String[] listFiles()         { return null; }

	}

}