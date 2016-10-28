package zutil.net.http.page;

import org.junit.Test;
import zutil.Hasher;
import zutil.io.StringOutputStream;
import zutil.net.http.HttpHeader;
import zutil.net.http.HttpHeaderParser;
import zutil.net.http.HttpPrintStream;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Ziver on 2016-10-26.
 */
public class HttpDigestAuthPageTest {
    private static final String PAGE_CONTENT = "Hello World!";

    @Test
    public void cleanRequest() throws IOException {
        HttpHeader rspHeader = makeRequest(new HttpHeader());

        assertEquals(401, rspHeader.getHTTPCode());
        assertTrue(rspHeader.getHeader("WWW-Authenticate") != null);
        assertEquals("Digest", parseAuthType(rspHeader));
        Map<String,String> authHeader = parseAuthHeader(rspHeader);
        assertTrue(authHeader.containsKey("realm"));
        assertTrue(authHeader.containsKey("nonce"));
    }


    @Test
    public void authenticate() throws IOException {
        HttpHeader reqHeader = new HttpHeader();
        HttpHeader rspHeader = makeRequest(reqHeader);
        Map<String,String> authHeader = parseAuthHeader(rspHeader);
        reqHeader = new HttpHeader();

        String realm = authHeader.get("realm");
        String nonce = authHeader.get("nonce");
        String uri = "/login";

        String ha1 = Hasher.MD5("username:password");
        String ha2 = Hasher.MD5("MD5:/" +uri);
        String response = Hasher.MD5(ha1 +":"+ nonce +":"+ ha2);
        reqHeader.setHeader("Authorization", "Digest username=\"username\", " +
                "realm=\""+realm+"\", " +
                "nonce=\""+nonce+"\", " +
                "uri=\""+uri+"\", " +
                "response=\""+response+"\"");
        rspHeader = makeRequest(reqHeader);
        assertEquals(200, rspHeader.getHTTPCode());
    }






    public static HttpHeader makeRequest(HttpHeader headers) throws IOException {
        StringOutputStream buff = new StringOutputStream();
        HttpPrintStream out = new HttpPrintStream(buff);
        new HttpDigestTestPage().respond(
                out, headers, new HashMap(), new HashMap(), new HashMap());
        out.flush();
        HttpHeaderParser parser = new HttpHeaderParser(buff.toString());
        return parser.read();
    }

    public static String parseAuthType(HttpHeader headers){
        String tmp = headers.getHeader("WWW-Authenticate");
        return tmp.substring(0, tmp.indexOf(' '));
    }
    public static HashMap<String,String> parseAuthHeader(HttpHeader headers){
        HashMap<String,String> authHeaders = new HashMap<>();
        String tmp = headers.getHeader("WWW-Authenticate");
        HttpHeaderParser.parseHeaderValues(authHeaders,
                tmp.substring(tmp.indexOf(' ')+1),
                ",");
        return authHeaders;
    }

    private static class HttpDigestTestPage extends HttpDigestAuthPage{
        @Override
        public void authRespond(HttpPrintStream out,
                                HttpHeader headers,
                                Map<String, Object> session,
                                Map<String, String> cookie,
                                Map<String, String> request) throws IOException {
            out.print(PAGE_CONTENT);
        }
    }
}