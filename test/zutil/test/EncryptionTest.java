/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Ziver Koc
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
 */
package zutil.test;

import zutil.Encrypter;

public class EncryptionTest {
	public static String data = "Hello there wats yor name my is a secret 123456789";
	public static Encrypter enc;
	public static Encrypter enc2;
	
	public static void main(String[] args) throws Exception {
		System.out.println("input text : " + data);
		
		//****************************************************************************************
		System.out.println("Test1 passphrase");
		Encrypter.randomizeSalt();
		enc = new Encrypter("Hello World!!", Encrypter.PASSPHRASE_DES_ALGO);
		enc2 = new Encrypter("Hello World!!", Encrypter.PASSPHRASE_DES_ALGO);
	    
	    byte[] encrypted = enc.encrypt(data.getBytes());
	    System.out.println("cipher text: " + new String(encrypted) + " bytes: " + encrypted.length);
	    
	    byte[] decrypted = enc2.decrypt(encrypted);
	    System.out.println("plain text : " + new String(decrypted) + " bytes: " + decrypted.length);
	    
	  //****************************************************************************************	    
	    System.out.println("Test2 randome");
	    Encrypter.randomizeSalt();
		enc = new Encrypter(Encrypter.BLOWFISH_ALGO);
		Encrypter.randomizeSalt();
		enc2 = new Encrypter(enc.getKey());
		
	    encrypted = enc.encrypt(data.getBytes());
	    System.out.println("cipher text: " + new String(encrypted) + " bytes: " + encrypted.length);
	    
	    decrypted = enc2.decrypt(encrypted);
	    System.out.println("plain text : " + new String(decrypted) + " bytes: " + decrypted.length);
	  }
}
