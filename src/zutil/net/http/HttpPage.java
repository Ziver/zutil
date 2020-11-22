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

package zutil.net.http;

import java.io.IOException;
import java.util.Map;

/**
 * This is a interface for a ordinary page for the HttpServer
 *
 * @author Ziver
 *
 */
public interface HttpPage{
    /**
     * This method has to be implemented for every page.
     * The method is called when a client sends a request
     * and is expecting a response from the specific page.
     *
     * @param out           is a output stream to the client
     * @param headers   	is the header received from the client
     * @param session       is the session associated with the current client
     * @param cookie        is cookie information from the client
     * @param request       is POST and GET requests from the client
     */
    void respond(HttpPrintStream out,
                 HttpHeader headers,
                 Map<String, Object> session,
                 Map<String, String> cookie,
                 Map<String, String> request) throws IOException;
}
