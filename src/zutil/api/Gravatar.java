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

package zutil.api;

import zutil.Hasher;

/**
 * This class generate Gravatar image urls
 */
public class Gravatar {
    private static final String GRAVATAR_IMG_PREFIX = "https://www.gravatar.com/avatar/";


    /**
     * @param   email   the email assosicated with the avatar
     * @return a http url as a String that points to a avatar image
     */
    public static String getImageUrl(String email) {
        return getImageUrl(email, null, -1);
    }
    /**
     * @param   email   the email assosicated with the avatar
     * @param   size    the requested image size. default is 80px
     * @return a http url as a String that points to a avatar image
     */
    public static String getImageUrl(String email, int size) {
        return getImageUrl(email, null, size);
    }
    /**
     * @param   email   the email assosicated with the avatar
     * @param   format  the picture file format. e.g. "jpg", "png"
     * @return a http url as a String that points to a avatar image
     */
    public static String getImageUrl(String email, String format) {
        return getImageUrl(email, format, -1);
    }
    /**
     * @param   email   the email assosicated with the avatar
     * @param   format  the picture file format. e.g. "jpg", "png"
     * @param   size    the requested image size. default is 80px
     * @return a http url as a String that points to a avatar image
     */
    public static String getImageUrl(String email, String format, int size) {
        String formatStr = (format!=null ? "." + format : "");
        String sizeStr   = (size > 0     ? "?size=" + size : "");
        return new StringBuilder(GRAVATAR_IMG_PREFIX)
                .append(getHash(email))
                .append(formatStr)
                .append(sizeStr)
                .toString();
    }


    private static String getHash(String email) {
        email = ("" + email).trim();
        email = email.toLowerCase();
        return Hasher.MD5(email);
    }
}
