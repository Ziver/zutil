package zutil.net.http.page;

import org.junit.Test;
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
        HttpHeader header = new HttpHeader();
        HttpHeader output = makeRequest(header);

        assertEquals(401, output.getHTTPCode());
        assertTrue(output.getHeader("WWW-Authenticate") != null);
        assertEquals("Digest", parseAuthType(output));
        Map<String,String> authHeader = parseAuthHeader(output);
        assertTrue(authHeader.containsKey("realm"));
        assertTrue(authHeader.containsKey("qop"));
        assertTrue(authHeader.containsKey("nonce"));
        assertTrue(authHeader.containsKey("opaque"));
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