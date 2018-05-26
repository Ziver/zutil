package zutil.net.http;

import zutil.io.StringOutputStream;

import java.io.IOException;
import java.util.HashMap;

public class HttpTestUtil {
    public static HashMap<String,Object> session = new HashMap();

    /**
     * Make a simple http request on the given page object
     */
    public static HttpHeader makeRequest(HttpPage page) throws IOException {
        return makeRequest(page, new HttpHeader());
    }
    /**
     * Make a simple http request on the given page object
     */
    public static HttpHeader makeRequest(HttpPage page, HttpHeader headers) throws IOException {
        StringOutputStream buff = new StringOutputStream();
        HttpPrintStream out = new HttpPrintStream(buff);
        page.respond(
                out, headers, session, new HashMap(), new HashMap());
        out.flush();
        HttpHeaderParser parser = new HttpHeaderParser(buff.toString());
        return parser.read();
    }
}