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

package zutil.parser;

import zutil.converter.Converter;

import java.io.UnsupportedEncodingException;

/**
 * This utility class will decode Strings encoded with % sign's to a normal String
 *
 * Created by Ziver on 2015-12-11.
 */
public class URLDecoder {

    public static String decode(String url) {
        if (url == null)
            return null;

        try {
            StringBuilder out = new StringBuilder();
            byte[] buffer = null;
            for (int i=0; i<url.length(); ++i) {
                char c = url.charAt(i);
                switch (c) {
                    case '+':
                        out.append(' ');
                        break;
                    case '%':
                        if (i+2 < url.length()) {
                            if (buffer == null)
                                buffer = new byte[url.length()];
                            int bufferPos = 0;
                            while (i<url.length() && url.charAt(i) == '%') {
                                buffer[bufferPos++] = Converter.hexToByte(url.charAt(i + 1), url.charAt(i + 2));
                                i += 3;
                            }
                            --i; // Go back one step as i will be incremented in the main for loop
                            out.append(new String(buffer, 0, bufferPos, "UTF-8"));
                        }
                        else
                            out.append(c);
                        break;
                    default:
                        out.append(c);
                        break;
                }
            }
            return out.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace(); // Should never happen
        }
        return null;
    }
}
