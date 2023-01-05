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

package zutil.net.ws.openapi;

import zutil.net.http.HttpHeader;
import zutil.net.http.HttpPage;
import zutil.net.http.HttpPrintStream;
import zutil.net.ws.WebServiceDef;
import zutil.net.ws.wsdl.WSDLWriter;

import java.io.IOException;
import java.util.Map;

/**
 * User: Ziver
 */
public class OpenAPIHttpPage implements HttpPage {
    /**
     * The WSDL document
     **/
    private WSDLWriter wsdl;


    public OpenAPIHttpPage(WebServiceDef wsDef) {
        wsdl = new WSDLWriter(wsDef);
    }


    public void respond(HttpPrintStream out,
                        HttpHeader headers,
                        Map<String, Object> session,
                        Map<String, String> cookie,
                        Map<String, String> request) throws IOException {

        if (request.containsKey("json")) {
            out.setHeader(HttpHeader.HEADER_CONTENT_TYPE, "application/json");
            wsdl.write(out);
        } else {
            // Output human-readable interface

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("   <title>OpenAPI Documentation</title>");
            out.println();
            out.println("   <link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/swagger-ui-dist@3.17.0/swagger-ui.css\">");
            out.println("   <script src=\"https://unpkg.com/swagger-ui-dist@3/swagger-ui-bundle.js\"></script>");
            out.println();
            out.println("   <script>");
            out.println("       function render() {");
            out.println("           var ui = SwaggerUIBundle({");
            out.println("                   url: '" + headers.getRequestURL() + "?json',");
            out.println("                   dom_id: '#swagger-ui',");
            out.println("                   presets: [");
            out.println("                       SwaggerUIBundle.presets.apis,");
            out.println("                       SwaggerUIBundle.SwaggerUIStandalonePreset");
            out.println("                   ]");
            out.println("           });");
            out.println("       }");
            out.println("   </script>");
            out.println("</head>");
            out.println("<body onload=\"render()\">");
            out.println("   <div id=\"swagger-ui\"></div>");
            out.println("</body>");
            out.println("</html>");
        }
    }
}
