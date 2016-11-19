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
    public static String getImageUrl(String email){
        return getImageUrl(email, null, -1);
    }
    /**
     * @param   email   the email assosicated with the avatar
     * @param   size    the requested image size. default is 80px
     * @return a http url as a String that points to a avatar image
     */
    public static String getImageUrl(String email, int size){
        return getImageUrl(email, null, size);
    }
    /**
     * @param   email   the email assosicated with the avatar
     * @param   format  the picture file format. e.g. "jpg", "png"
     * @return a http url as a String that points to a avatar image
     */
    public static String getImageUrl(String email, String format){
        return getImageUrl(email, format, -1);
    }
    /**
     * @param   email   the email assosicated with the avatar
     * @param   format  the picture file format. e.g. "jpg", "png"
     * @param   size    the requested image size. default is 80px
     * @return a http url as a String that points to a avatar image
     */
    public static String getImageUrl(String email, String format, int size){
        String formatStr = (format!=null ? "."+format : "");
        String sizeStr   = (size > 0     ? "?size="+size : "");
        return new StringBuilder(GRAVATAR_IMG_PREFIX)
                .append(getHash(email))
                .append(formatStr)
                .append(sizeStr)
                .toString();
    }


    private static String getHash(String email){
        email = (""+email).trim();
        email = email.toLowerCase();
        return Hasher.MD5(email);
    }
}
