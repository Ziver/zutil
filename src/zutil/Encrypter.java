/*******************************************************************************
 * Copyright (c) 2011 Ziver Koc
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
package zutil;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;


/**
 * Basic symmetric encryption example
 */
public class Encrypter {
	public static final String BLOWFISH_ALGO = "Blowfish";
	public static final String DES_ALGO = "DES";
	public static final String DESEDE_ALGO = "DESede";
	public static final String TRIPLEDES_ALGO = "TripleDES";
	public static final String AES_ALGO = "AES";
	public static final String PASSPHRASE_TOWFISH_ALGO = "PBEWithSHAAndTwofish-CBC";
	public static final String PASSPHRASE_TRIPLEDES_ALGO = "PBEWithMD5AndTripleDES";
	public static final String PASSPHRASE_DES_ALGO = "PBEWithMD5AndDES";

	// 8-byte Salt
	public static byte[] salt = {
		(byte)0xA9, (byte)0x9B, (byte)0xC8, (byte)0x32,
		(byte)0x56, (byte)0x35, (byte)0xE3, (byte)0x03
	};
	// Iteration count
	public static int iterationCount = 19;

	private Cipher encipher;
	private Cipher decipher;
	private Key key;
	private AlgorithmParameterSpec paramSpec;

	/**
	 * Generates a random key
	 * @param algorithm The algorithm to use
	 */
	public Encrypter(String algorithm) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException{
		KeyGenerator keygenerator = KeyGenerator.getInstance(algorithm);

		key = keygenerator.generateKey();
		encipher = Cipher.getInstance(key.getAlgorithm());
		decipher = Cipher.getInstance(key.getAlgorithm());
		encipher.init(Cipher.ENCRYPT_MODE, key);
		decipher.init(Cipher.DECRYPT_MODE, key);
	}

	/**
	 * Uses the given key for encryption
	 * @param key The key to use
	 */
	public Encrypter(Key key) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException{
		this.key = key;
		encipher = Cipher.getInstance(key.getAlgorithm());
		decipher = Cipher.getInstance(key.getAlgorithm());
		encipher.init(Cipher.ENCRYPT_MODE, key);
		decipher.init(Cipher.DECRYPT_MODE, key);
	}

	/**
	 * Creates a encrypter with a passphrase
	 * 
	 * @param stringKey The pass
	 * @param algorithm The algoritm to use
	 */
	public Encrypter(String stringKey, String algorithm) throws NoSuchAlgorithmException{
		try {
			// Install SunJCE provider
			Provider sunJce = new com.sun.crypto.provider.SunJCE();
			Security.addProvider(sunJce);

			// Generate the secret key specs.
			KeySpec keySpec = new PBEKeySpec(stringKey.toCharArray());		

			key = SecretKeyFactory.getInstance(algorithm).generateSecret(keySpec);
			paramSpec = new PBEParameterSpec(salt, iterationCount);

			encipher = Cipher.getInstance(key.getAlgorithm());
			decipher = Cipher.getInstance(key.getAlgorithm());
			encipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
			decipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Encrypts the given data
	 * 
	 * @param data Data to encrypt
	 * @return The encrypted data
	 */
	public byte[] encrypt(byte[] data){
		try {
			byte[] encryption = new byte[encipher.getOutputSize(data.length)];
			int ctLength = encipher.update(data, 0, data.length, encryption, 0);

			ctLength += encipher.doFinal(encryption, ctLength);
			return encryption;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Adds encryption to the OutputStream
	 * @param out The OutputStream to enable encryption on
	 * @return A new encrypted OutputStream
	 */
	public OutputStream encrypt(OutputStream out) {
		// Bytes written to out will be encrypted
		return new CipherOutputStream(out, encipher);

	}

	/**
	 * Decrypts encrypted data
	 * @param encrypted The encrypted data
	 * @return The decrypted data
	 */
	public byte[] decrypt(byte[] encrypted){
		try {
			byte[] dataTmp = new byte[encrypted.length];
			int ptLength = decipher.update(encrypted, 0, encrypted.length, dataTmp, 0);
			ptLength += decipher.doFinal(dataTmp, ptLength);

			byte[] data = new byte[ptLength];
			System.arraycopy(dataTmp, 0, data, 0, ptLength);
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Adds decryption to the InputStream
	 * @param in The InputStream to enable decryption on
	 * @return A new decrypted InputStream
	 */
	public InputStream decrypt(InputStream in) {
		// Bytes read from in will be decrypted
		return new CipherInputStream(in, decipher);

	}
	
	/**
	 * @return The key for this encrypter
	 */
	public Key getKey(){
		return key;
	}
	
	/**
	 * @return The algorithm used by this encrypter
	 */
	public String getAlgorithm(){
		return key.getAlgorithm();
	}
	
	/**
	 * Randomizes the salt for the key
	 */
	public static void randomizeSalt(){
		Random random = new Random();
		random.nextBytes(salt);
	}
}
