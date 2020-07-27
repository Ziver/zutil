/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Ziver Koc
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
import zutil.net.http.HttpHeaderParser;
import zutil.net.http.HttpURL;
import zutil.net.ws.WSInterface;
import zutil.net.ws.WSMethodDef;
import zutil.net.ws.WSParameterDef;
import zutil.net.ws.WebServiceDef;
import zutil.net.ws.soap.SOAPHttpPage;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
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
        // Generate XML
        HttpURL url = generateRESTRequest(method.getName(), args);

        // Send request
        HttpClient request = new HttpClient(HttpClient.HttpRequestType.POST);
        request.setURL(url);
        HttpHeaderParser response = request.send();
        String rspJson = IOUtil.readContentAsString(request.getResponseInputStream());
        request.close();

        // DEBUG
        if (logger.isLoggable(Level.FINEST)) {
            System.out.println("********** Request");
            System.out.println(url);
            System.out.println("********** Response");
            System.out.println(rspJson);
        }

        return parseRESTResponse(rspJson);
    }


    private HttpURL generateRESTRequest(String targetMethod, Object[] args) {
        logger.fine("Sending request for " + targetMethod);
        HttpURL url = new HttpURL(serviceUrl);

        WSMethodDef methodDef = wsDef.getMethod(targetMethod);
        url.setPath(serviceUrl.getPath()
                + (serviceUrl.getPath().endsWith("/") ? "" : "/")
                + methodDef.getName());

        List<WSParameterDef> params =  methodDef.getOutputs();
        for (int i = 0; i < params.size(); i++) {
            WSParameterDef param = params.get(i);
            url.setParameter(param.getName(), args[i].toString());
        }

        return url;
    }

    private Object parseRESTResponse(String json) {

        return null;
    }
}
