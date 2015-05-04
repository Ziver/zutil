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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import zutil.io.IOUtil;
import zutil.log.LogUtil;

/**
 * File path utilities
 * 
 * @author Ziver
 */
public class FileUtil {
	private static final Logger logger = LogUtil.getLogger();
	
	/**
	 * Returns a String with a relative path from the given path
	 * 
	 * @param 		file 		is the file to get a relative path from
	 * @param 		path 		is the path
	 * @return 					A String with a relative path
	 */
	public static String relativePath(File file, String path){
		if( file == null || path == null )
			return null;
		String absolute = file.getAbsolutePath();
		String tmpPath = path.replaceAll(
				"[/\\\\]", 
				Matcher.quoteReplacement(File.separator));
		
		String relative = absolute.substring(
				absolute.indexOf(tmpPath)+path.length(), 
				absolute.length());
		return relative;
	}
	
	/**
	 * Returns the File object for the given file. 
	 * Can not point to files in JAR files.
	 * 
	 * @param 		path 		is the path to the file (no / if not absolute path)
	 * @return 					A File object for the file
	 */
	public static File find(String path){
		try {
			File file = new File(path);
			if(file!=null && file.exists()){
				return file;
			}
			URL url = findURL(path);
			if(url != null)
				return new File(url.toURI());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns the URL to the given file
	 * 
	 * @param 		path 		is the path to the file (no / if not absolute path)
	 * @return					A URL object for the file
	 * @throws URISyntaxException 
	 */
	public static URL findURL(String path){
		return Thread.currentThread().getContextClassLoader().getResource(path);
	}
	
	/**
	 * Returns a InputStream from the path
	 * 
	 * @param 		path 		is the path to the file (no / if not absolute path)
	 * @return 					A InputStream object for the file
	 */
	public static InputStream getInputStream(String path){
		try {
			File file = new File(path);
			if(file!=null && file.exists()){
				return new BufferedInputStream( new FileInputStream( file ) );
			}
			return Thread.currentThread().getContextClassLoader()
						.getResourceAsStream(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Reads and returns the content of a file as a String.
	 * Or use FileUtils.readFileToString(file);
	 * 
	 * @param 		file
	 * @return the file content
	 */
	public static String getContent(File file) throws IOException{
        InputStream in = new FileInputStream(file);
        String data = new String(IOUtil.getContent( in ));
        in.close();
        return data;
	}
	
	/**
	 * Reads and returns the content of a file as a String.
	 * Or use FileUtils.readFileToString(file);
	 * 
	 * @param 		url
	 * @return the file content
	 */
	public static String getContent(URL url) throws IOException{
        InputStream in = url.openStream();
        String data = new String(IOUtil.getContent( in ));
        in.close();
        return data;
	}
	
	/**
	 * Cache for the search functions
	 */
	private static HashMap<SearchItem,List<File>> search_cache = new HashMap<SearchItem,List<File>>();
	/**
	 * An Cache Item class to identify different cached items
	 * @author Ziver
	 */
	private static class SearchItem{
		private File dir;
		private boolean folders;
		private int recurse;
		
		protected SearchItem(File dir, boolean folders, int recurse){
			this.dir = dir;
			this.folders = folders;
			this.recurse = recurse;
		}
		
		public boolean equals(Object o){
			if(o!=null && o instanceof SearchItem){
				SearchItem si = (SearchItem)o;
				return dir.equals(si.dir) && folders == si.folders && recurse == si.recurse;
			}
			return false;
		}
		public int hashCode(){
			int hash = 133;
			hash = 23 * hash + dir.hashCode();
			hash = 23 * hash + (folders ? 1 : 0);
			hash = 23 * hash + recurse;
			return 0;
		}
	}
	
	/**
	 * Same as search(File dir) but it caches the result 
	 * to be used next time this function is called with 
	 * the same parameters.
	 */
	public static List<File> cachedSearch(File dir){
		return cachedSearch(dir, new LinkedList<File>(), true);
	}
	
	/**
	 * Same as search(File dir, List<File> fileList, boolean recursive)
	 * but is caches the result to be used next time this function is 
	 * called with the same parameters.
	 */
	public static List<File> cachedSearch(File dir, List<File> fileList, boolean recursive){
		return cachedSearch(dir, new LinkedList<File>(), false, (recursive ? Integer.MAX_VALUE : 0));
	}
	
	/**
	 * Same as search(File dir, List<File> fileList, boolean folders, int recurse)
	 * but is caches the result to be used next time this function is called
	 * with the same parameters.
	 */
	public static List<File> cachedSearch(File dir, List<File> fileList, boolean folders, int recurse){
		SearchItem si = new SearchItem(dir, folders, recurse);
		if( search_cache.containsKey(si) ){
			fileList.addAll( search_cache.get(si) );
			return fileList;
		}
		search(dir, fileList, folders, recurse);
		search_cache.put(si, fileList);
		return fileList;
	}
	
	/**
	 * Returns a List with all the files in a folder and sub folders
	 * 
	 * @param 		dir 		is the directory to search in
	 * @return 					The List with the files
	 */
	public static List<File> search(File dir){
		return search(dir, new LinkedList<File>(), true);
	}
	
	/**
	 * Returns a ArrayList with all the files in a folder and sub folders
	 * 
	 * @param 		dir 		is the directory to search in
	 * @param 		fileList 	is the List to add the files to
	 * @param 		recursive 	is if the method should search the sub directories to.
	 * @return					A List with the files
	 */
	public static List<File> search(File dir, List<File> fileList, boolean recursive){
		return search(dir, new LinkedList<File>(), false, (recursive ? Integer.MAX_VALUE : 0));
	}
	
	/**
	 * Returns a ArrayList with all the files in a folder and sub folders
	 * 
	 * @param 		dir 		is the directory to search in
	 * @param 		fileList 	is the List to add the files to
	 * @param 		folders 	is if the method should add the folders to the List
	 * @param 		recurse 	is how many times it should recurse into folders
	 * @return 					A List with the files and/or folders
	 */
	public static List<File> search(File dir, List<File> fileList, boolean folders, int recurse){
		if(recurse<0)
			return fileList;
		--recurse;
		if(folders){
			fileList.add( dir );
		}
		
		File file;
		String[] temp = dir.list();
		if(temp != null){
			for(int i=0; i<temp.length ;i++){
				file = new File(dir.getPath()+File.separator+temp[i]);
				if(file.isDirectory()){
					logger.finer("Found Folder: "+file);
					search(new File(dir.getPath()+File.separator+temp[i]+File.separator), fileList, folders, recurse);
				}
				else if(file.isFile()){
					logger.finer("Found File: "+file);
					fileList.add(file);
				}			
			}
		}
		
		return fileList;
	}
	
	/**
	 * Returns the extension(without the dot) of the file. e.g. "png" "avi"
	 * 
	 * @param 		file 		is the file
	 * @return 					The extension
	 */
	public static String getFileExtension(File file){
		return getFileExtension(file.getName());
	}
	
	/**
	 * Returns the extension(without the dot) of the file. e.g. "png" "avi"
	 * 
	 * @param 		file 		is the file
	 * @return 					The extension
	 */
	public static String getFileExtension(String file){
		if( file == null || file.lastIndexOf(".") == -1 )
			return "";
		return file.substring(file.lastIndexOf(".")+1, file.length());
	}

	/**
	 * Replaces the current extension on the file withe the given one.
	 * 
	 * @param 		file		is the name of the file
	 * @param 		ext			is the new extension, without the dot
	 * @return
	 */
	public static String changeExtension(String file, String ext) {
		if( file == null )
			return null;
		if( file.lastIndexOf(".") == -1 )
			return file+"."+ext;
		return file.substring(0, file.lastIndexOf(".")+1)+ext;
	}
	
	/**
	 * This function will replace some data between two boundaries.
	 * If the boundary is not found it will be added  to the end of the file.
	 * 
	 * @param 	file		is the file to modify
	 * @param 	boundary	is the start and end boundary to put the data between, this is a full line boundary.
	 * @param 	data		is the data that will be written to the file
	 */
	public static void writeBetweenBoundary(File file, String boundary, String data) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(file));
		StringBuilder output = new StringBuilder();
		
		String line;
		while((line = in.readLine()) != null){
			// Found starting boundary
			if(line.equals(boundary)){ 
				while((line = in.readLine()) != null)
					// Find ending boundary
					if(line.equals(boundary)) break; 
				// EOF and no ending boundary found
				if(line == null){
					in.close();
					throw new EOFException("No ending boundary found");
				}
				// Write the new data
				output.append(boundary).append('\n');
				output.append(data).append('\n');
				output.append(boundary).append('\n');
			}
			else
				output.append(line).append('\n');
		}
		in.close();
		
		// Save changes
		FileWriter out = new FileWriter(file);
		out.write(output.toString());
		out.close();
	}
}
