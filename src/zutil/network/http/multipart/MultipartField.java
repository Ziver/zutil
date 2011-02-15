package zutil.net.http.multipart;

import java.io.BufferedReader;
import java.io.File;

import zutil.ProgressListener;


/**
 * A class for handling multipart field
 * 
 * @author Ziver
 */
public class MultipartField{
	protected long received;
	protected long length;
	protected String contentType;
	
	protected String fieldname;
	protected String value;
	
	
	protected MultipartField(){
		
	}
	
	/**
	 * @return the amount of data received for this field
	 */
	public long getReceivedBytes(){
		return received;
	}
	
	/**
	 * @return the fieldname
	 */
	public String getFieldname(){
		return fieldname;
	}
	
	/**
	 * @return the value of the field
	 */
	public String getValue(){
		return value;
	}
	
	protected void parse(){
		
	}
}