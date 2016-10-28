package zutil.net.http.page;

import zutil.Encrypter;
import zutil.Hasher;
import zutil.net.http.HttpHeader;
import zutil.net.http.HttpPage;
import zutil.net.http.HttpPrintStream;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Map;

/**
 * A abstract page that requires HTTP Digest authentication
 * to access the subclass HttpPage.
 *
 * @see <a href="https://tools.ietf.org/html/rfc2069">rfc2069</a>
 * @author Ziver
 */
public abstract class HttpDigestAuthPage implements HttpPage{
    private static final String DEAFULT_REALM = "Login";
    private static final String HTTP_AUTH_HEADER = "WWW-Authenticate";
    private static final String HTTP_CLIENT_HEADER = "Authorization";
    private static final String AUTH_TYPE = "Digest";

    private static final String AUTH_REALM = "realm";
    private static final String AUTH_NONCE = "nonce";
    private static final String AUTH_OPAQUE = "opaque"; // OPTIONAL can be used as session data
    private static final String AUTH_USERNAME = "username";
    private static final String AUTH_URI = "uri";
    private static final String AUTH_RESPONSE = "response";


    private String realm = DEAFULT_REALM;
    private SecureRandom secRandom = new SecureRandom();



    public void setRealm(String realm){
        this.realm = realm;
    }



    @Override
    public final void respond(HttpPrintStream out,
                        HttpHeader headers,
                        Map<String, Object> session,
                        Map<String, String> cookie,
                        Map<String, String> request) throws IOException {

        if (headers.getHeader(HTTP_CLIENT_HEADER) == null) {
            session.put(AUTH_NONCE, generateNonce());
            out.setStatusCode(401);
            out.setHeader(HTTP_AUTH_HEADER, generateAuthHeader((String) session.get(AUTH_NONCE)));
        }
        else{
            authRespond(out, headers, session, cookie, request);
        }
    }


    private String generateAuthHeader(String nonce){
        StringBuilder str = new StringBuilder();
        str.append(AUTH_TYPE).append(' ');
        str.append(AUTH_REALM).append("=\"").append(realm).append("\", ");
        str.append(AUTH_NONCE).append("=\"").append(nonce).append("\", ");
        //str.append(AUTH_OPAQUE).append("=\"").append("ll").append("\"");
        return str.toString();
    }

    private String generateNonce(){
        byte[] buff = new byte[128/8];
        secRandom.nextBytes(buff);
        return Hasher.SHA1(buff);
    }

    private static String generateH1(String username, String password, String realm) {
        String ha1 = null;
        // If the algorithm directive's value is "MD5" or unspecified, then HA1 is
        //    HA1=MD5(username:realm:password)
        ha1 = Hasher.MD5(username +":"+ realm +":"+ password);
        // If the algorithm directive's value is "MD5-sess", then HA1 is
        //    HA1=MD5(MD5(username:realm:password):nonce:cnonce)
        return ha1;
    }

    private static String generateH2(String uri) {
        String ha2;
        // If the qop directive's value is "auth" or is unspecified, then HA2 is
        //     HA2=MD5(method:digestURI)
        ha2 = Hasher.MD5("MD5:"+ uri);
        // If the qop directive's value is "auth-int", then HA2 is
        //     HA2=MD5(method:digestURI:MD5(entityBody))
        return ha2;
    }

    private static String generateResponseHash(String ha1, String ha2, String nonce){
        String response;
        // If the qop directive's value is "auth" or "auth-int", then compute the response as follows:
        //     response=MD5(HA1:nonce:nonceCount:cnonce:qop:HA2)
        // If the qop directive is unspecified, then compute the response as follows:
        //     response=MD5(HA1:nonce:HA2)
        response = Hasher.MD5(ha1 +":"+ nonce +":"+ ha2);
        return response;
    }


    public abstract void authRespond(HttpPrintStream out,
                              HttpHeader headers,
                              Map<String, Object> session,
                              Map<String, String> cookie,
                              Map<String, String> request) throws IOException;
}
