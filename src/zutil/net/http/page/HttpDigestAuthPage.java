package zutil.net.http.page;

import zutil.net.http.HttpHeader;
import zutil.net.http.HttpPage;
import zutil.net.http.HttpPrintStream;

import java.io.IOException;
import java.util.Map;

/**
 * A abstract page that requires HTTP Digest authentication
 * to access the subclass HttpPage.
 *
 * @see <a href="https://tools.ietf.org/html/rfc2617">rfc2617</a>
 * @author Ziver
 */
public abstract class HttpDigestAuthPage implements HttpPage{
    private static final String HTTP_AUTH_HEADER = "WWW-Authenticate";
    private static final String AUTH_TYPE = "Digest";

    private static final String AUTH_REALM = "realm";
    private static final String AUTH_QUALITY_OF_PROTECTION = "qop";
    private static final String AUTH_NONCE = "nonce";
    private static final String AUTH_OPAQUE = "opaque";

    private static final String AUTH_USERNAME = "username";
    private static final String AUTH_URI = "uri";
    private static final String AUTH_CNONCE = "cnonce";
    private static final String AUTH_RESPONSE = "response";


    private String realm;


    @Override
    public final void respond(HttpPrintStream out,
                        HttpHeader headers,
                        Map<String, Object> session,
                        Map<String, String> cookie,
                        Map<String, String> request) throws IOException {

        out.setStatusCode(401);
        out.setHeader(HTTP_AUTH_HEADER, generateAuthHeader());
    }

    private String generateAuthHeader(){
        StringBuilder str = new StringBuilder();
        str.append(AUTH_TYPE).append(' ');
        str.append(AUTH_REALM).append("=\"").append("ll").append("\", ");
        str.append(AUTH_QUALITY_OF_PROTECTION).append("=\"").append("ll").append("\", ");
        str.append(AUTH_NONCE).append("=\"").append("ll").append("\", ");
        str.append(AUTH_OPAQUE).append("=\"").append("ll").append("\"");
        return str.toString();
    }



    public abstract void authRespond(HttpPrintStream out,
                              HttpHeader headers,
                              Map<String, Object> session,
                              Map<String, String> cookie,
                              Map<String, String> request) throws IOException;
}
