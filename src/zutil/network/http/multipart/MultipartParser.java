package zutil.net.http.multipart;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import zutil.ProgressListener;
import zutil.net.http.HTTPHeaderParser;

/**
 * Parses a multipart/form-data http request, 
 * saves files to temporary location.
 * 
 * http://www.ietf.org/rfc/rfc1867.txt
 * 
 * @author Ziver
 *
 */
public class MultipartParser {
	/** This is the temporary directory for the received files	 */
	private File tempDir;
	/** This is the delimiter that will separate the fields */
	private String delimiter;
	/** The length of the HTTP Body */
	private long contentLength;
	/** This is the input stream */
	private BufferedReader in;
	
	/** This is the listener that will listen on the progress */
	private ProgressListener<MultipartField> listener;
	
	
	
	public MultipartParser(BufferedReader in, HTTPHeaderParser header){
		this.in = in;
		
		String cotype = header.getHeader("Content-type");
		cotype = cotype.split(" *; *")[1];
		delimiter = cotype.split(" *= *")[1];
		
		contentLength = Long.parseLong( header.getHeader("Content-Length") );
	}
	
	public MultipartParser(BufferedReader in, HttpServletRequest req){
		this.in = in;
		
		String cotype = req.getHeader("Content-type");
		cotype = cotype.split(" *; *")[1];
		delimiter = cotype.split(" *= *")[1];
		
		contentLength = req.getContentLength();
	}
	
	public MultipartParser(BufferedReader in, String delimiter, long length){
		this.in = in;
		this.delimiter = delimiter;		
		this.contentLength = length;
	}
	
	
	/**
	 * @param listener is the listener that will be called for progress
	 */
	public void setListener(ProgressListener<MultipartField> listener){
		this.listener = listener;
	}
	
	/**
	 * Parses the HTTP Body and returns a list of fields
	 *  
	 * @return A list of FormField
	 */
	public List<MultipartField> parse() throws IOException{
		ArrayList<MultipartField> list = new ArrayList<MultipartField>();
		// TODO: parse(list, delimiter);
		return list;
	}

// TODO: 
/* 
	private void parse(List<MultipartField> list, String delimiter) throws IOException{
		String line = "";
		MultipartField field = null;
		delimiter = "--"+delimiter;
		String endDelimiter = delimiter+"--";
		BufferedWriter out = null;
		// Parsing the stream
		while(line != null){
			line = in.readLine();
			// Skip empty lines
			if(line == null || line.trim().isEmpty())
				continue;
			// End of field
			else if(line.equals( endDelimiter )){
				list.add(field);
				if(out != null)	out.close();
				field.length = field.file.length();
				out = null;
				field = null;
				continue;
			}
			// New field
			else if(line.equals( delimiter )){
				if(field != null){
					list.add(field);
					if(out != null)	out.close();
					field.length = field.file.length();
					out = null;
					field = null;
				}
				// Read the content-disposition
				line = in.readLine();
				if(line.toLowerCase().startsWith("content-disposition")){
					line = line.split(":", 2)[1];
					String[] fieldData = line.split(" *; *");
					//String type = fieldData[0].toLowerCase();
					field = new MultipartField();
					field.type = MultipartField.FieldType.Field;
					
					// Parse content-disposition parameters
					for(String param : fieldData){
						String[] temp = param.split(" *= *");
						if(temp[0].equalsIgnoreCase("name"))
							field.fieldname = temp[1];
						else if(temp[0].equalsIgnoreCase("filename")){
							field.filename = temp[1];
							field.file = createTempFile();
							out = new BufferedWriter(new FileWriter(field.file));
							field.type = MultipartField.FieldType.File;
						}
					}
				}
				else 
					throw new IOException("MultipartForm parse error unrecognized line: "+line);
			}
			// Read field data
			else if(field != null){
				if(field.type == MultipartField.FieldType.File){
					out.append(line);
				}
				else{
					field.value += line;
				}
				field.received += line.length();
			}
		}
		
		if(field != null)
			throw new IOException("MultipartForm parse error stream ended prematurely");
	}
*/
	
	/**
	 * Creates a temporary file in either the system 
	 * temporary folder or by the setTempDir() function
	 * 
	 * @return the temporary file
	 */
	protected File createTempFile() throws IOException{
		if(tempDir != null) 
			return File.createTempFile("upload", ".part", tempDir.getAbsoluteFile());
		else
			return File.createTempFile("upload", ".part");
	}
	
	/**
	 * Sets the initial delimiter
	 * 
	 * @param delimiter is the new delimiter
	 */
	public void setDelimiter(String delimiter){
		this.delimiter = delimiter;
	}
	
	public void setTempDir(File dir){
		if(!dir.isDirectory())
			throw new RuntimeException("\""+dir.getAbsolutePath()+"\" is not a directory!");
		if(!dir.canWrite())
			throw new RuntimeException("\""+dir.getAbsolutePath()+"\" is not writable!");
		tempDir = dir;
	}
	
	public long getContentLength(){
		return contentLength;
	}
}
