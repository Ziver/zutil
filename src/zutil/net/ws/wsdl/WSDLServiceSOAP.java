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

package zutil.net.ws.wsdl;

import org.dom4j.Element;
import zutil.net.ws.WSMethodDef;

/**
 * User: Ziver
 */
public class WSDLServiceSOAP extends WSDLService{

    public WSDLServiceSOAP(String url){
        super(url);
    }

    @Override
    public String getServiceType() { return "soap"; }

    @Override
    public void generateBinding(Element definitions) {
        // definitions -> binding -> soap:binding
        Element soap_binding = definitions.addElement("soap:binding");
        soap_binding.addAttribute("style", "rpc");
        soap_binding.addAttribute("transport", "http://schemas.xmlsoap.org/soap/http");
    }

    @Override
    public void generateOperation(Element definitions, WSMethodDef method) {
        // definitions -> binding -> operation
        Element operation = definitions.addElement("wsdl:operation");
        operation.addAttribute("name", method.getName());

        // definitions -> binding -> operation -> soap:operation
        Element soap_operation = operation.addElement("soap:operation");
        soap_operation.addAttribute("soapAction", method.getNamespace());

        //*************************** Input
        // definitions -> binding -> operation -> input
        Element input = operation.addElement("wsdl:input");
        // definitions -> binding -> operation -> input -> body
        Element input_body = input.addElement("soap:body");
        input_body.addAttribute("use", "literal");
        input_body.addAttribute("namespace", method.getNamespace());

        //*************************** output
        if(!method.getOutputs().isEmpty()){
            // definitions -> binding -> operation -> output
            Element output = operation.addElement("wsdl:output");
            // definitions -> binding -> operation -> input -> body
            Element output_body = output.addElement("soap:body");
            output_body.addAttribute("use", "literal");
            output_body.addAttribute("namespace", method.getNamespace());
        }
    }
}
