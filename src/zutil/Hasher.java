package zutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sun.misc.BASE64Encoder;

public class Hasher {
	
	/**
	 * Returns a hash of a file
	 * 
	 * @param file The path to the file
	 * @param hashType The hash type
	 * @return A String with the hash
	 * @throws NoSuchAlgorithmException
	 * @throws IOException 
	 */
	public static String hash(File file, String hashType) throws NoSuchAlgorithmException, IOException {
		MessageDigest digest = MessageDigest.getInstance(hashType);//"MD5"
		InputStream is = new FileInputStream(file);
		String output = "";
		byte[] buffer = new byte[8192];
		int read = 0;
		try {
			while( (read = is.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}		
			byte[] md5sum = digest.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);
			output = bigInt.toString(16);
		}
		catch(IOException e) {
			throw new RuntimeException("Unable to process file for MD5", e);
		}
		is.close();
		
		MultiPrintStream.out.println("File Hash: "+output);
		return output;
	}

	/**
	 * Returns the hash of the given object
	 * 
	 * @param object The object to hash
	 * @param hashType The hash method
	 * @return String containing the hash
	 * @throws NoSuchAlgorithmException
	 */
	public static String hash(Serializable object, String hashType) throws NoSuchAlgorithmException {
		MessageDigest md = null;
		md = MessageDigest.getInstance(hashType); //MD5 || SHA
		md.update(Converter.toBytes(object));

		byte raw[] = md.digest();
		return (new BASE64Encoder()).encode(raw);
	}
}
