package zutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import zutil.converters.Converter;

public class Hasher {

	/**
	 * Returns a hash of a file
	 * 
	 * @param 		file 		is the path to the file
	 * @param 		hashType 	is the hash type
	 * @return 					a String with the hash
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
			throw new RuntimeException("Unable to process file for "+hashType+" hash", e);
		}
		is.close();

		return output;
	}

	/**
	 * Returns the MD5 hash of the given object
	 * 
	 * @param 		object 		is the String to hash
	 * @return 					an String containing the hash
	 */
	public static String MD5(String str){
		try {
			return hash(str, "MD5");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns the MD5 hash of the given object
	 * 
	 * @param 		object 		is the object to hash
	 * @return 					an String containing the hash
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
	 * @param 		str 		is the String to hash
	 * @return 					an String containing the hash
	 */
	public static String SHA1(String str){
		try {
			return hash(str, "SHA-1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns the SHA-1 hash of the given object
	 * 
	 * @param 		object 		is the object to hash
	 * @return 					an String containing the hash
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
	 * Hashes the given String as UTF8
	 * 
	 * @param 		object 		is the String
	 * @param 		hashType 	is the hash algorithm (MD2, MD5, SHA-1, SHA-256, SHA-384, SHA-512 )
	 * @return 					a hex String of the hash
	 */
	public static String hash(String object, String hashType) throws Exception {
		return hash(object.getBytes(), hashType);//(new BASE64Encoder()).encode(raw);
	}
	
	/**
	 * Returns the hash of the given object
	 * 
	 * @param 		object 		is the object to hash
	 * @param 		hashType 	is the hash method (MD2, MD5, SHA-1, SHA-256, SHA-384, SHA-512 )
	 * @return 					an String containing the hash
	 */
	public static String hash(Serializable object, String hashType) throws Exception {
		return hash(Converter.toBytes(object), hashType);//(new BASE64Encoder()).encode(raw);
	}

	/**
	 * Hashes a given byte array
	 * 
	 * @param 		data 		is the byte array to hash
	 * @param 		hashType 	is the hash method (MD2, MD5, SHA-1, SHA-256, SHA-384, SHA-512 )
	 * @return 					an String containing the hash
	 * @throws Exception
	 */
	public static String hash(byte[] data, String hashType) throws Exception {
		MessageDigest md = null;
		md = MessageDigest.getInstance(hashType); //MD5 || SHA
		md.update(data);

		byte raw[] = md.digest();
		return Converter.toHexString(raw);//(new BASE64Encoder()).encode(raw);
	}

	/**
	 * MurmurHash2 ported from c++ source
	 * 
	 * @param 		object 		is the Key
	 * @param 		seed 		is the seed
	 * @return 					A MurmurHash of the key
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