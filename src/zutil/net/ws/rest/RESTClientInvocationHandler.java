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

package zutil.net.ws.rest;

import zutil.io.IOUtil;
import zutil.log.LogUtil;
import zutil.net.http.HttpClient;
import zutil.net.http.HttpHeader;
import zutil.net.http.HttpURL;
import zutil.net.ws.WSMethodDef;
import zutil.net.ws.WSParameterDef;
import zutil.net.ws.WebServiceDef;
import zutil.parser.DataNode;
import zutil.parser.json.JSONParser;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

/**
 * This is an abstract client that will do generic requests to a
 * REST Web service using JSON response.
 *
 * TODO: Implement WSPath and WSRequestType
 */
public class RESTClientInvocationHandler implements InvocationHandler {
    private static Logger logger = LogUtil.getLogger();

    private WebServiceDef wsDef;
    /**
     * Web address of the web service
     */
    protected URL serviceUrl;

    public RESTClientInvocationHandler(URL url, WebServiceDef webServiceDef) {
        this.serviceUrl = url;
        this.wsDef = webServiceDef;
    }


    /**
     * Makes a request to the target web service
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Generate request

        WSMethodDef methodDef = wsDef.getMethod(method.getName());
        HttpURL url = generateRESTRequest(methodDef, args);

        String requestType = "GET";
        if (methodDef.getRequestType() != null)
            requestType = methodDef.getRequestType().toString();

        // Send request

        HttpClient request = new HttpClient(HttpClient.HttpRequestType.POST);
        request.setURL(url);
        request.setType(requestType);

        logger.fine("Sending request for: " + url);
        HttpHeader response = request.send();
        logger.fine("Received response(" + response.getResponseStatusCode() + ")");

        // Parse response

        String rspStr = IOUtil.readContentAsString(request.getResponseInputStream());
        request.close();

        //if (logger.isLoggable(Level.FINEST)) {
            System.out.println("********** Response: " + url);
            System.out.println(rspStr);
        //}

        Object rspObj = parseRESTResponse(methodDef, rspStr);
        return rspObj;
    }


    private HttpURL generateRESTRequest(WSMethodDef methodDef, Object[] args) {
        HttpURL url = new HttpURL(serviceUrl);

        String host = serviceUrl.getPath();
        if (host.endsWith("/"))
            host = host.substring(0, host.length() - 1);

        url.setPath(host + methodDef.getPath());

        List<WSParameterDef> params =  methodDef.getInputs();
        for (int i = 0; i < params.size(); i++) {
            WSParameterDef param = params.get(i);
            url.setParameter(param.getName(), args[i].toString());
        }

        return url;
    }

    private Object parseRESTResponse(WSMethodDef methodDef, String str) {
        DataNode json = JSONParser.read(str);
        List<WSParameterDef> outputs = methodDef.getOutputs();

        if (outputs.size() == 1) {
            if (outputs.get(0).getParamClass().isAssignableFrom(DataNode.class))
                return json;
        }

        throw new RuntimeException("WS JSON return type currently not supported: " + methodDef);
    }
}
