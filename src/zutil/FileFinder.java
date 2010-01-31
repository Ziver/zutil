package zutil;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * File path utilities
 * 
 * @author Ziver
 */
public class FileFinder {
	
	/**
	 * Returns a String with a relative path from the given path
	 * 
	 * @param file is the file to get a relative path from
	 * @param path is the path
	 * @return A String with a relative path
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
	 * @param path is the path to the file (no / if not absolute path)
	 * @return A File object for the file
	 */
	public static File find(String path){
		try {
			File file = new File(path);
			if(file!=null && file.exists()){
				return file;
			}
			return new File(findURL(path).toURI());
		} catch (Exception e) {
			//e.printStackTrace(MultiPrintStream.out);
		}
		return null;
	}
	
	/**
	 * Returns the URL to the given file
	 * 
	 * @param path is the path to the file (no / if not absolute path)
	 * @return A URL object for the file
	 * @throws URISyntaxException 
	 */
	public static URL findURL(String path){
		return FileFinder.class.getClassLoader().getResource(path);
	}
	
	/**
	 * Returns a InputStream from the path
	 * 
	 * @param path is the path to the file (no / if not absolute path)
	 * @return A InputStream object for the file
	 */
	public static InputStream getInputStream(String path){
		try {
			File file = new File(path);
			if(file!=null && file.exists()){
				return new BufferedInputStream( new FileInputStream( file ) );
			}
			return FileFinder.class.getClassLoader().getResourceAsStream(path);
		} catch (Exception e) {
			//e.printStackTrace(MultiPrintStream.out);
		}
		return null;
	}
	
	/**
	 * Reads and returns the content of a file as a String.
	 * Or use FileUtils.readFileToString(file);
	 * 
	 * @param file is the file to read
	 * @return The file content
	 * @throws IOException
	 */
	public static String getFileContent(File file) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(file));
		StringBuffer ret = new StringBuffer();
		int tmp;

		while((tmp=in.read()) != -1){
			ret.append((char)tmp);
		}
		
		in.close();
		return ret.toString();
	}
	
	/**
	 * Returns a ArrayList with all the files in a folder and sub folders
	 * 
	 * @param dir is the directory to search in
	 * @return The ArrayList with the files
	 */
	public static List<File> search(File dir){
		return search(dir, new LinkedList<File>(), true);
	}
	
	/**
	 * Returns a ArrayList with all the files in a folder and sub folders
	 * 
	 * @param dir is the directory to search in
	 * @param fileList is the ArrayList to add the files to
	 * @param recursice is if the method should search the sub directories to.
	 * @return The ArrayList with the files
	 */
	public static List<File> search(File dir, List<File> fileList, boolean recursive){
		String[] temp = dir.list();
		File file;
		
		if(temp != null){
			for(int i=0; i<temp.length ;i++){
				file = new File(dir.getPath()+File.separator+temp[i]);
				if(recursive && file.isDirectory()){
					search(new File(dir.getPath()+File.separator+temp[i]+File.separator), fileList, recursive);
				}
				else if(file.isFile()){
					//MultiPrintStream.out.println("File Found: "+file);
					fileList.add(file);
				}			
			}
		}
		
		return fileList;
	}
	
	/**
	 * Returns the extension of the file
	 * 
	 * @param file is the file
	 * @return The extension
	 */
	public static String fileExtension(File file){
		return fileExtension(file.getName());
	}
	
	/**
	 * Returns the extension of the file
	 * 
	 * @param file is the file
	 * @return The extension
	 */
	public static String fileExtension(String file){
		if(file.lastIndexOf(".")==-1)
			return "";
		return file.substring(file.lastIndexOf(".")+1,file.length());
	}
	
}
