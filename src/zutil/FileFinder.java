package zutil;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
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
	 * @param file The file to get a relative path from
	 * @param path The path
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
	 * Returns the File object for the given file
	 * 
	 * @param path The path to the file (no / if not absolute path)
	 * @return A File object for the file
	 * @throws URISyntaxException 
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
	 * @param path The path to the file (no / if not absolute path)
	 * @return A URL object for the file
	 * @throws URISyntaxException 
	 */
	public static URL findURL(String path){
		return FileFinder.class.getClassLoader().getResource(path);
	}
	
	/**
	 * Returns a ArrayList with all the files in a folder and sub folders
	 * 
	 * @param dir The directory to search in
	 * @return The ArrayList with the files
	 */
	public static ArrayList<File> search(File dir){
		return search(dir, new ArrayList<File>());
	}
	
	/**
	 * Returns a ArrayList with all the files in a folder and sub folders
	 * 
	 * @param dir The directory to search in
	 * @param fileList The ArrayList to add the files to
	 * @return The ArrayList with the files
	 */
	public static ArrayList<File> search(File dir, ArrayList<File> fileList){
		String[] temp = dir.list();
		File file;
		
		for(int i=0; i<temp.length ;i++){
			file = new File(dir.getPath()+File.separator+temp[i]);
			if(file.isDirectory()){
				search(new File(dir.getPath()+File.separator+temp[i]+File.separator),fileList);
			}
			else if(file.isFile()){
				MultiPrintStream.out.println("File Found: "+file);
				fileList.add(file);
			}			
		}
		
		return fileList;
	}
	
	/**
	 * Returns the extension of the file
	 * @param file The file
	 * @return The extension
	 */
	public static String fileExtension(File file){
		return fileExtension(file.getName());
	}
	
	/**
	 * Returns the extension of the file
	 * @param file The file
	 * @return The extension
	 */
	public static String fileExtension(String file){
		if(file.lastIndexOf(".")==-1)
			return "";
		return file.substring(file.lastIndexOf(".")+1,file.length());
	}
	
}
