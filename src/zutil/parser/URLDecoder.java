package zutil.parser;

/**
 * This utility class will decode Strings encoded with % sign's to a normal String
 *
 * Created by Ziver on 2015-12-11.
 */
public class URLDecoder {
    
    public static String decode(String url){
        if(url == null)
            return null;
        StringBuilder out = new StringBuilder();
        for (int i=0; i<url.length(); ++i) {
            char c = url.charAt(i);
            switch (c){
                case '+':
                    out.append(' ');
                    break;
                case '%':
                    if (i+2 < url.length()) {
                        char ascii = (char)Integer.parseInt("" + url.charAt(i + 1) + url.charAt(i + 2), 16);
                        out.append(ascii);
                        i += 2;
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
    }
}
