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

import zutil.converter.Converter;

import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hasher {

    /**
     * Returns a hash of a file
     *
     * @param 		file 		is the path to the file
     * @param 		hashType 	is the hash type
     * @return 					a String with the hash
     */
    public static String hash(File file, String hashType) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance(hashType); //"MD5"
        InputStream is = new FileInputStream(file);
        String output;
        byte[] buffer = new byte[8192];
        int read;
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
     * @param 		str 		is the String to hash
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
     * Returns the MD5 hash of the given file
     *
     * @param	file	is the file to hash
     * @return 			an String containing the hash
     */
    public static String MD5(File file) throws IOException{
        try {
            return hash(file, "MD5");
        } catch (NoSuchAlgorithmException e) {
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
     * Returns the SHA-256 hash with a key for the given String
     *
     * @param 		str 		is the String to hash
     * @param		key			is the key to use with the hash
     * @return 					an String containing the hash
     */
    public static String HMAC_SHA256(String str, String key){
        return HMAC("HmacSHA256", str.getBytes(), key.getBytes());
    }

    /**
     * Returns a HMAC hash with a key for the given String
     *
     * @param 		algo 		specifies the algorithm to be used
     * @param 		data 		is the String to hash
     * @param		key			is the key to use with the hash
     * @return 					an String containing the hash
     */
    public static String HMAC(String algo, byte[] data, byte[] key){
        try {
            // Get an hmac_sha1 key from the raw key bytes
            SecretKeySpec signingKey = new SecretKeySpec(key, algo);

            // Get a MAC instance and initialize with the signing key
            Mac mac = Mac.getInstance(algo);
            mac.init(signingKey);

            // Compute the HMAC on input data bytes
            byte[] raw = mac.doFinal( data );

            return Converter.toHexString(raw);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String PBKDF2(String data, String salt, int iterations){
        try {
            PBEKeySpec spec = new PBEKeySpec(
                    data.toCharArray(),
                    salt.getBytes(),
                    iterations,
                    256);
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] raw = f.generateSecret(spec).getEncoded();

            return Converter.toHexString(raw);
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
     */
    public static String hash(byte[] data, String hashType) throws Exception {
        MessageDigest md;
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
