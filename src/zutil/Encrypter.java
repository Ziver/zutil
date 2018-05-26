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

package zutil;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Random;


/**
 * Basic symmetric encryption example
 */
public class Encrypter {
    // Choices are available at: http://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html
    public enum Algorithm {
        /** Advanced Encryption Standard as specified by NIST in FIPS 197. Also known as the Rijndael algorithm by Joan Daemen and Vincent Rijmen, AES is a 128-bit block cipher supporting keys of 128, 192, and 256 bits. **/
        AES,
        /** The AES key wrapping algorithm as described in RFC 3394. **/
        AESWrap,
        /** A stream cipher believed to be fully interoperable with the RC4 cipher developed by Ron Rivest. For more information, see K. Kaukonen and R. Thayer, "A Stream Cipher Encryption Algorithm 'Arcfour'", Internet Draft (expired), draft-kaukonen-cipher-arcfour-03.txt. **/
        ARCFOUR,
        /** The Blowfish block cipher designed by Bruce Schneier. **/
        Blowfish,
        /** Counter/CBC Mode, as defined in NIST Special Publication SP 800-38C. **/
        CCM,
        /** The Digital Encryption Standard as described in FIPS PUB 46-3. **/
        DES,
        /** Triple DES Encryption (also known as DES-EDE, 3DES, or Triple-DES). Data is encrypted using the DES algorithm three separate times. It is first encrypted using the first subkey, then decrypted with the second subkey, and encrypted with the third subkey. **/
        DESede,
        /** The DESede key wrapping algorithm as described in RFC 3217 . **/
        DESedeWrap,
        /** Elliptic Curve Integrated Encryption Scheme **/
        ECIES,
        /** Galois/Counter Mode, as defined in NIST Special Publication SP 800-38D. **/
        GCM,
        /** Variable-key-size encryption algorithms developed by Ron Rivest for RSA Data Security, Inc. **/
        RC2,
        /** Variable-key-size encryption algorithms developed by Ron Rivest for RSA Data Security, Inc. (See note prior for ARCFOUR.) **/
        RC4,
        /** Variable-key-size encryption algorithms developed by Ron Rivest for RSA Data Security, Inc. **/
        RC5,
        /** The RSA encryption algorithm as defined in PKCS #1 **/
        RSA
    }
    public enum Digest {
        MD2,
        MD5,
        SHA1,
        SHA256,
        SHA384,
        SHA512,
        HmacMD5,
        HmacSHA1,
        HmacSHA256,
        HmacSHA384,
        HmacSHA512
    }


    // 8-byte Salt
    public static byte[] salt = {
            (byte)0xA9, (byte)0x9B, (byte)0xC8, (byte)0x32,
            (byte)0x56, (byte)0x35, (byte)0xE3, (byte)0x03
    };


    private Cipher encipher;
    private Cipher decipher;
    private Key key;
    private AlgorithmParameterSpec paramSpec;

    /**
     * Generates a random key
     * @param   crypto  is algorithm to encrypt/decrypt with
     */
    public Encrypter(Algorithm  crypto) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException{
        KeyGenerator keygenerator = KeyGenerator.getInstance(crypto.toString());

        key = keygenerator.generateKey();
        encipher = Cipher.getInstance(key.getAlgorithm());
        decipher = Cipher.getInstance(key.getAlgorithm());
        encipher.init(Cipher.ENCRYPT_MODE, key);
        decipher.init(Cipher.DECRYPT_MODE, key);
    }

    /**
     * Uses the given key for encryption
     * @param   key     is an existing key to use for encrypting/decrypting
     */
    public Encrypter(Key key) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException{
        this.key = key;
        encipher = Cipher.getInstance(key.getAlgorithm());
        decipher = Cipher.getInstance(key.getAlgorithm());
        encipher.init(Cipher.ENCRYPT_MODE, key);
        decipher.init(Cipher.DECRYPT_MODE, key);
    }

    /**
     * Creates a encrypter with a passphrase.
     *
     * @param   stringKey   is a passphrase to use as key
     * @param   crypto      is algorithm to encrypt/decrypt with
     */
    public Encrypter(String stringKey, Algorithm crypto) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException {
        this(stringKey, Digest.HmacSHA1, crypto, 500,
                (crypto==Algorithm.DES ? 64 : 128));
    }

    public Encrypter(String stringKey, Digest digest, Algorithm crypto, int iteration, int keyBitSize) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException {
        // Install SunJCE provider
        Provider sunJce = new com.sun.crypto.provider.SunJCE();
        Security.addProvider(sunJce);

        // Generate the secret key specs.
        //String instance = "PBEWith"+ digest +"And"+ crypto;
        String instance = "PBKDF2With"+ digest;
        SecretKeyFactory factory = SecretKeyFactory.getInstance(instance);
        KeySpec keySpec = new PBEKeySpec(stringKey.toCharArray(), salt, iteration, keyBitSize);
        SecretKey tmp = factory.generateSecret(keySpec);
        key = new SecretKeySpec(tmp.getEncoded(), crypto.toString());
        //key = new SecretKeySpec(stringKey.getBytes(), crypto.toString());

        encipher = Cipher.getInstance(key.getAlgorithm());
        decipher = Cipher.getInstance(key.getAlgorithm());
        encipher.init(Cipher.ENCRYPT_MODE, key);
        decipher.init(Cipher.DECRYPT_MODE, key);
    }



    /**
     * Encrypts the given data
     *
     * @param   data    is the data to encrypt
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
     * @param   out     is the OutputStream to enable encryption on
     * @return A new encrypted OutputStream
     */
    public OutputStream encrypt(OutputStream out) {
        // Bytes written to out will be encrypted
        return new CipherOutputStream(out, encipher);

    }

    /**
     * Decrypts encrypted data
     * @param   encrypted   is the encrypted data
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
     * @param   in      is the InputStream to enable decryption on
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
     * @return the algorithm used by this encrypter
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
