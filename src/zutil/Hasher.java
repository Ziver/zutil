package zutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
	 * Returns the MD5 hash of the given object
	 * 
	 * @param object The object to hash
	 * @return String containing the hash
	 */
	public static String MD5(Serializable object){
		try {
			return hash(object, "MD5");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns the SHA-1 hash of the given object
	 * 
	 * @param object The object to hash
	 * @return String containing the hash
	 */
	public static String SHA1(Serializable object){
		try {
			return hash(object, "SHA-1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns the hash of the given object
	 * 
	 * @param object The object to hash
	 * @param hashType The hash method (MD2, MD5, SHA-1, SHA-256, SHA-384, SHA-512 )
	 * @return String containing the hash
	 * @throws NoSuchAlgorithmException
	 * @throws IOException 
	 */
	public static String hash(Serializable object, String hashType) throws Exception {
		MessageDigest md = null;
		md = MessageDigest.getInstance(hashType); //MD5 || SHA
		md.update(Converter.toBytes(object));

		byte raw[] = md.digest();
		return Converter.toHexString(raw);//(new BASE64Encoder()).encode(raw);
	}


	/**
	 * MurmurHash2 ported from cpp source
	 * 
	 * @param object The Key
	 * @param seed Seed
	 * @return A MurmurHash of the key
	 * @throws Exception 
	 */
	public static int MurmurHash(Serializable object, int seed) throws Exception{
		byte[] data = Converter.toBytes(object);
		int length = data.length;

		//Constants
		int m = 0x5bd1e995;
		int r = 24;

		// Initialize the hash to a 'random' value
		int h = seed ^ length;

		int i=0;
		for(; i+4<length ;i+=4){
			// get the first 4 bytes
			int k = data[i+3] & 0xff;
			k <<= 8;
			k |= data[i+2] & 0xff;
			k <<= 8;
			k |= data[i+1] & 0xff;
			k <<= 8;
			k |= data[i+0] & 0xff;

			k *= m;
			k ^= k >>> r;
			k *= m;

			h *= m;
			h ^= k;
		}

		// Handle the last few bytes of the input
		i = length % 4;

		switch(i){
		case 3: h ^= data[length-3] << 16;
		case 2: h ^= data[length-2] << 8;
		case 1: h ^= data[length-1];
			h *= m;
		}

		h ^= h >>> 13;
		h *= m;
		h ^= h >>> 15;

		return h;
	}
}