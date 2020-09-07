package zutil.net.http.page;

import org.junit.Before;
import org.junit.Test;
import zutil.Hasher;
import zutil.io.IOUtil;
import zutil.net.http.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

/**
 * @author Ziver on 2016-10-26.
 */
public class HttpDigestAuthPageTest {
    private static final String PAGE_CONTENT = "Hello World!";
    private static final String PAGE_USERNAME = "username";
    private static final String PAGE_PASSWORD = "password";

    private HttpDigestAuthPage page;

    @Before
    public void init(){
        page = new HttpDigestAuthPage(new TestPage());
        page.addUser(PAGE_USERNAME, PAGE_PASSWORD.toCharArray());
    }



    @Test
    public void cleanRequest() throws IOException {
        HttpHeader rspHeader = HttpTestUtil.makeRequest(page);

        assertEquals(401, rspHeader.getStatusCode());
        assertNotNull(rspHeader.getHeader("WWW-Authenticate"));
        assertEquals("Digest", parseAuthType(rspHeader));
        Map<String,String> authHeader = parseAuthHeader(rspHeader);
        assertTrue(authHeader.containsKey("realm"));
        assertTrue(authHeader.containsKey("nonce"));
        assertThat(IOUtil.readContentAsString(rspHeader.getInputStream()),
                not(containsString(PAGE_CONTENT)));
    }

    @Test
    public void authenticate() throws IOException {
        HttpHeader rspHeader = authenticate(PAGE_USERNAME, PAGE_PASSWORD);
        assertEquals(200, rspHeader.getStatusCode());
        assertThat(IOUtil.readContentAsString(rspHeader.getInputStream()),
                containsString(PAGE_CONTENT));
    }
    @Test
    public void wrongUsername() throws IOException {
        HttpHeader rspHeader = authenticate(PAGE_USERNAME+"wrong", PAGE_PASSWORD);
        assertEquals(403, rspHeader.getStatusCode());
        assertThat(IOUtil.readContentAsString(rspHeader.getInputStream()),
                not(containsString(PAGE_CONTENT)));
    }
    @Test
    public void wrongPassword() throws IOException {
        HttpHeader rspHeader = authenticate(PAGE_USERNAME, PAGE_PASSWORD+"wrong");
        assertEquals(403, rspHeader.getStatusCode());
        assertThat(IOUtil.readContentAsString(rspHeader.getInputStream()),
                not(containsString(PAGE_CONTENT)));
    }





    public HttpHeader authenticate(String username, String password) throws IOException {
        HttpHeader reqHeader = new HttpHeader();
        HttpHeader rspHeader = HttpTestUtil.makeRequest(page, reqHeader);
        Map<String,String> authHeader = parseAuthHeader(rspHeader);
        reqHeader = new HttpHeader();

        String realm = authHeader.get("realm");
        String nonce = authHeader.get("nonce");
        String uri = "/login";
        String ha1 = Hasher.MD5(username+":"+realm+":"+password);
        String ha2 = Hasher.MD5("MD5:" +uri);
        String response = Hasher.MD5(ha1 +":"+ nonce +":"+ ha2);
        reqHeader.setRequestURL(uri);
        reqHeader.setHeader("Authorization", "Digest " +
                "username=\""+username+"\", " +
                "realm=\""+realm+"\", " +
                "nonce=\""+nonce+"\", " +
                "uri=\""+uri+"\", " +
                "response=\""+response+"\"");

        return HttpTestUtil.makeRequest(page, reqHeader);
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

    private static class TestPage implements HttpPage {
        @Override
        public void respond(HttpPrintStream out,
                                HttpHeader headers,
                                Map<String, Object> session,
                                Map<String, String> cookie,
                                Map<String, String> request) throws IOException {
            out.print(PAGE_CONTENT);
        }
    }
}