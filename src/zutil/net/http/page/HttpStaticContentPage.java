package zutil.net.http.page;

import zutil.net.http.HttpHeader;
import zutil.net.http.HttpPage;
import zutil.net.http.HttpPrintStream;

import java.io.IOException;
import java.util.Map;


/**
 * A page serving static content
 */
public class HttpStaticContentPage implements HttpPage {
    private String contentType;
    private String content;


    public HttpStaticContentPage(String content) {
        this.setContent(content);
    }


    public void setContent(String content) {
        this.content = content;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }


    @Override
    public void respond(HttpPrintStream out,
                        HttpHeader headers,
                        Map<String, Object> session,
                        Map<String, String> cookie,
                        Map<String, String> request) throws IOException {
        if (contentType != null)
            out.setHeader(HttpHeader.HEADER_CONTENT_TYPE, contentType);

        out.setHeader(HttpHeader.HEADER_CONTENT_LENGTH, String.valueOf(content.length()));
        out.print(content);
    }
}
