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

import static org.junit.Assert.assertEquals;


public class HasherTest {

    @Test
    public void MD5Test() {
        assertEquals(Hasher.MD5("AAAABBBB"),   "9da4fc50e09e5eeb8ae8149ef4f23792");
        assertEquals(Hasher.MD5("qwerty12345"),"85064efb60a9601805dcea56ec5402f7");
        assertEquals(Hasher.MD5("123456789"),  "25f9e794323b453885f5181f1b624d0b");
        assertEquals(Hasher.MD5("Test45"),     "fee43a4c9d88769e14ec6a1d8b80f2e7");
    }

    @Test
    public void SHA1Test() {
        assertEquals(Hasher.SHA1("AAAABBBB"),   "7cd188ef3a9ea7fa0ee9c62c168709695460f5c0");
        assertEquals(Hasher.SHA1("qwerty12345"),"4e17a448e043206801b95de317e07c839770c8b8");
        assertEquals(Hasher.SHA1("123456789"),  "f7c3bc1d808e04732adf679965ccc34ca7ae3441");
        assertEquals(Hasher.SHA1("Test45"),     "9194c6e64a6801e24e63a924d5843a46428d2b3a");
    }

    @Test
    public void PBKDF2() {
        assertEquals(Hasher.PBKDF2("AAAABBBB", "s", 1000),
                "8da1853fe2a2efc82aa444e7274b74cf25190e580898bbaac83c502099506dd7");
        assertEquals(Hasher.PBKDF2("AAAABBBB", "salt", 1000),
                "550057b797d2e862c5a9aea9b9fae7c47de4f8179b6c6348f75cc2ede8322329");
        assertEquals(Hasher.PBKDF2("password", "salt", 500),
                "5078bf72f89ba6f1b69cc1697eccde1df5758a157707cb1bad0ef459dcd2eec3");
        assertEquals(Hasher.PBKDF2("password", "salt", 1000),
                "6e88be8bad7eae9d9e10aa061224034fed48d03fcbad968b56006784539d5214");
    }
}
