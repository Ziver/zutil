/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Ziver Koc
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

package zutil.net.ws.soap;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import zutil.io.IOUtil;
import zutil.log.LogUtil;
import zutil.net.http.HttpClient;
import zutil.net.http.HttpHeader;
import zutil.net.ws.WSInterface;
import zutil.net.ws.WSMethodDef;
import zutil.net.ws.WSParameterDef;
import zutil.net.ws.WebServiceDef;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is an abstract client that will do generic requests to a
 * SOAP Web service
 *
 * @author Ziver
 */
public class SOAPClientInvocationHandler implements InvocationHandler {
    private static Logger logger = LogUtil.getLogger();

    private WebServiceDef wsDef;
    /**
     * Web address of the web service
     */
    protected URL url;


    public SOAPClientInvocationHandler(URL url, WebServiceDef wsDef) {
        this.url = url;
        this.wsDef = wsDef;
    }


    /**
     * Makes a request to the target web service
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Generate XML
        Document document = genSOAPRequest((WSInterface) proxy, method.getName(), args);
        String reqXml = document.asXML();

        // Send request
        HttpClient request = new HttpClient(HttpClient.HttpRequestType.POST);
        request.setURL(url);
        request.setContent(reqXml);
        HttpHeader response = request.send();
        String rspXml = IOUtil.readContentAsString(request.getResponseInputStream());
        request.close();

        // DEBUG
        if (logger.isLoggable(Level.FINEST)) {
            System.out.println("********** Request");
            System.out.println(reqXml);
            System.out.println("********** Response");
            System.out.println(rspXml);
        }

        return parseSOAPResponse(rspXml);
    }


    private Document genSOAPRequest(WSInterface obj, String targetMethod, Object[] args) {
        logger.fine("Sending request for " + targetMethod);
        Document document = DocumentHelper.createDocument();
        Element envelope = document.addElement("soap:Envelope");
        WSMethodDef methodDef = wsDef.getMethod(targetMethod);
        try {
            envelope.addNamespace("soap", "http://schemas.xmlsoap.org/soap/envelope/");
            envelope.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
            envelope.addNamespace("xsd", "http://www.w3.org/2001/XMLSchema");

            Element body = envelope.addElement("soap:Body");
            Element method = body.addElement("");
            method.addNamespace("m", methodDef.getNamespace());
            method.setName("m:" + methodDef.getName() + "Request");

            List<WSParameterDef> outputParamDefs = methodDef.getOutputs();
            for (int i = 0; i < outputParamDefs.size(); i++) {
                WSParameterDef param = outputParamDefs.get(i);
                SOAPHttpPage.generateSOAPXMLForObj(method, args[i], param.getName());
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception in SOAP generation", e);
        }

        return document;
    }

    private Object parseSOAPResponse(String xml) {
        try {
            Element response = SOAPHttpPage.getXMLRoot(xml);
            // TODO:
        } catch (DocumentException e) {
            logger.log(Level.SEVERE, "Unable to parse SOAP response", e);
        }
        return null;
    }
}
