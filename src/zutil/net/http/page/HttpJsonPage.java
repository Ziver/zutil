package zutil.net.http.page;

import zutil.net.http.HttpHeader;
import zutil.net.http.HttpPage;
import zutil.net.http.HttpPrintStream;
import zutil.parser.DataNode;
import zutil.parser.json.JSONWriter;

import java.util.Map;

/**
 * A page handling responses in JSON format.
 */
public abstract class HttpJsonPage implements HttpPage {

    @Override
    public void respond(HttpPrintStream out,
                        HttpHeader headers,
                        Map<String, Object> session,
                        Map<String, String> cookie,
                        Map<String, String> request) {

        out.setHeader(HttpHeader.HEADER_CONTENT_TYPE, "application/json");
        DataNode json = jsonRespond(out, headers, session, cookie, request);

        if (json != null) {
            JSONWriter writer = new JSONWriter(out);
            writer.write(json);
            writer.close();
        }
    }


    protected abstract DataNode jsonRespond(HttpPrintStream out,
                                            HttpHeader headers,
                                            Map<String, Object> session,
                                            Map<String, String> cookie,
                                            Map<String, String> request);
}
