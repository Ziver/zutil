package zutil.network.http.multipart;

import java.io.BufferedReader;
import java.io.File;

import zutil.ProgressListener;


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