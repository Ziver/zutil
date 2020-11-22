/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Ziver Koc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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

        out.setResponseStatusCode((permanent ? 301 : 307));
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
