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
