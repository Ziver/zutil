/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Ziver Koc
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

import org.junit.Test;
import zutil.Encrypter.Algorithm;

import static org.junit.Assert.assertEquals;


public class EncrypterTest {
    public static final String data = "Hello there, wats yor name, my is a secret, 123456789";
    public static final String key = "abcdefghijklmnopqrstuvwxyz";


    @Test
    public void encryptDES() throws Exception {
        Encrypter.randomizeSalt();
        Encrypter encrypter = new Encrypter(key, Algorithm.DES);
        Encrypter decrypter = new Encrypter(key, Algorithm.DES);

        assertEquals(data, encryptDecrypt(encrypter, decrypter, data));
    }

    @Test
    public void encryptBLOWFISH() throws Exception {
        Encrypter.randomizeSalt();
        Encrypter encrypter = new Encrypter(Algorithm.Blowfish);
        Encrypter.randomizeSalt();
        Encrypter decrypter = new Encrypter(encrypter.getKey());

        assertEquals(data, encryptDecrypt(encrypter, decrypter, data));
    }

    @Test
    public void encryptAES() throws Exception {
        Encrypter.randomizeSalt();
        Encrypter encrypter = new Encrypter(key, Algorithm.AES);
        Encrypter decrypter = new Encrypter(key, Algorithm.AES);

        assertEquals(data, encryptDecrypt(encrypter, decrypter, data));
    }



    public static String encryptDecrypt(Encrypter encrypter, Encrypter decrypter, String data) {
        byte[] encrypted = encrypter.encrypt(data.getBytes());
        byte[] decrypted = decrypter.decrypt(encrypted);
        return new String(decrypted);
    }
}
