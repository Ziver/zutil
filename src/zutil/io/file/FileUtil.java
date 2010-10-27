package zutil.io.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

import zutil.io.IOUtil;
import zutil.io.MultiPrintStream;

/**
 * File path utilities
 * 
 * @author Ziver
 */
public class FileUtil {
	
	/**
	 * Returns a String with a relative path from the given path
	 * 
	 * @param 		file 		is the file to get a relative path from
	 * @param 		path 		is the path
	 * @return 					A String with a relative path
	 */
	public static String relativePath(File file, String path){
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
			return new File(findURL(path).toURI());
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
	 * @param 		file 		is the file to read
	 * @return 					The file content
	 * @throws IOException
	 */
	public static String getFileContent(File file) throws IOException{
		return IOUtil.getContent( new FileInputStream(file) );
	}
	
	/**
	 * Reads and returns the content of a file as a String.
	 * Or use FileUtils.readFileToString(file);
	 * 
	 * @param 		url 		is the url to read
	 * @return 					The file content
	 * @throws IOException
	 */
	public static String getContent(URL url) throws IOException{
		return IOUtil.getContent( url.openStream() );
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
			MultiPrintStream.out.println("Dir Found : "+dir);
			fileList.add( dir );
		}
		
		File file;
		String[] temp = dir.list();
		if(temp != null){
			for(int i=0; i<temp.length ;i++){
				file = new File(dir.getPath()+File.separator+temp[i]);
				if(file.isDirectory()){
					search(new File(dir.getPath()+File.separator+temp[i]+File.separator), fileList, folders, recurse);
				}
				else if(file.isFile()){
					MultiPrintStream.out.println("File Found: "+file);
					fileList.add(file);
				}			
			}
		}
		
		return fileList;
	}
	
	/**
	 * Returns the extension of the file
	 * 
	 * @param 		file 		is the file
	 * @return 					The extension
	 */
	public static String fileExtension(File file){
		return fileExtension(file.getName());
	}
	
	/**
	 * Returns the extension of the file
	 * 
	 * @param 		file 		is the file
	 * @return 					The extension
	 */
	public static String fileExtension(String file){
		if(file.lastIndexOf(".")==-1)
			return "";
		return file.substring(file.lastIndexOf(".")+1,file.length());
	}
	
}
