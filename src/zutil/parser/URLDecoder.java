package zutil.parser;

import zutil.converters.Converter;

import java.io.UnsupportedEncodingException;

/**
 * This utility class will decode Strings encoded with % sign's to a normal String
 *
 * Created by Ziver on 2015-12-11.
 */
public class URLDecoder {
    
    public static String decode(String url){
        if(url == null)
            return null;

        try {
            StringBuilder out = new StringBuilder();
            byte[] buffer = null;
            for (int i=0; i<url.length(); ++i) {
                char c = url.charAt(i);
                switch (c){
                    case '+':
                        out.append(' ');
                        break;
                    case '%':
                        if (i+2 < url.length()) {
                            if(buffer == null)
                                buffer = new byte[url.length()];
                            int bufferPos = 0;
                            while(i<url.length() && url.charAt(i) == '%') {
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
