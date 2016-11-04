package zutil.net.http.page;

import zutil.net.http.HttpHeader;
import zutil.net.http.HttpPage;
import zutil.net.http.HttpPrintStream;
import zutil.parser.DataNode;
import zutil.parser.json.JSONWriter;

import java.io.IOException;
import java.util.Map;

/**
 * @author Ziver on 2016-11-04.
 */
public abstract class HttpJsonPage implements HttpPage {

    @Override
    public void respond(HttpPrintStream out,
                        HttpHeader headers,
                        Map<String, Object> session,
                        Map<String, String> cookie,
                        Map<String, String> request) throws IOException {

        out.setHeader("Content-Type", "application/json");
        JSONWriter writer = new JSONWriter(out);
        writer.write(jsonRespond(headers, session, cookie, request));
        writer.close();
    }


    protected abstract DataNode jsonRespond(HttpHeader headers,
                                            Map<String, Object> session,
                                            Map<String, String> cookie,
                                            Map<String, String> request);
}
