package zutil.net.http.page;

import org.junit.Test;
import zutil.io.IOUtil;
import zutil.net.http.HttpHeader;
import zutil.net.http.HttpPrintStream;
import zutil.net.http.HttpTestUtil;
import zutil.parser.DataNode;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Ziver on 2016-11-04.
 */
public class HttpJsonPageTest {

    private HttpJsonPage page = new HttpJsonPage() {
        @Override
        protected DataNode jsonRespond(HttpPrintStream out, HttpHeader headers, Map<String, Object> session, Map<String, String> cookie, Map<String, String> request) {
            return new DataNode(DataNode.DataType.Map);
        }
    };



    @Test
    public void simpleResponse() throws IOException {
        HttpHeader header = HttpTestUtil.makeRequest(page);
        assertEquals("application/json", header.getHeader("Content-Type"));
        assertEquals("{}", IOUtil.readContentAsString(header.getInputStream()));
    }
}