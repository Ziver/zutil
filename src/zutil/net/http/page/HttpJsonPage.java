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
