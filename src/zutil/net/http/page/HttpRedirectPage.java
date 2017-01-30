package zutil.net.http.page;

import zutil.net.http.HttpHeader;
import zutil.net.http.HttpPage;
import zutil.net.http.HttpPrintStream;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Ziver on 2016-06-19.
 */
public class HttpRedirectPage implements HttpPage{

    private boolean permanent;
    private String redirectUrl;


    public HttpRedirectPage(String redirectUrl){
        this.redirectUrl = redirectUrl;
    }

    public void setPermanentRedirect(boolean permanent){
        this.permanent = permanent;
    }


    @Override
    public void respond(HttpPrintStream out,
                        HttpHeader headers,
                        Map<String, Object> session,
                        Map<String, String> cookie,
                        Map<String, String> request) throws IOException {

        out.setStatusCode((permanent ? 301 : 307));
        out.setHeader("Location", redirectUrl);
        out.print(
                "<!DOCTYPE HTML>\n" +
                "<html lang='en-US'>\n" +
                "    <head>\n" +
                "        <meta charset='UTF-8'>\n" +
                "        <meta http-equiv='refresh' content='0;url="+ redirectUrl +"'>\n" +
                "        <script type='text/javascript'>\n" +
                "            window.location.href = '"+ redirectUrl +"'\n" +
                "        </script>\n" +
                "        <title>Page Redirection</title>\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        If you are not redirected automatically, follow the <a href='"+ redirectUrl +"'>link to "+ redirectUrl +"</a>\n" +
                "    </body>\n" +
                "</html>"
        );
    }
}
