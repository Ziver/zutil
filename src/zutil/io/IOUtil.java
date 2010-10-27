package zutil.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Utility class for streams and general IO stuff
 * 
 * @author Ziver
 *
 */
public class IOUtil {

	/**
	 * Reads and returns the content of a file as a String.
	 * Or use FileUtils.readFileToString(file);
	 * 
	 * @param 		stream 		is the file stream to read
	 * @return 					The file content
	 * @throws IOException
	 */
	public static String getContent(InputStream stream) throws IOException{
		BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		StringBuffer ret = new StringBuffer();
		int tmp;

		while((tmp=in.read()) != -1){
			ret.append((char)tmp);
		}
		
		in.close();
		return ret.toString();
	}
}
