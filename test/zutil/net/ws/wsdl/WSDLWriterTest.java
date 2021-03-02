/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Ziver Koc
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

import org.junit.Test;
import zutil.net.ws.WebServiceDef;
import zutil.net.ws.soap.SOAPTest;

import static org.junit.Assert.assertEquals;

public class WSDLWriterTest {

    @Test
    public void basicTest() {
        WSDLWriter writer = new WSDLWriter(new WebServiceDef(SOAPTest.MainSOAPClass.class));
        writer.addService(new WSDLServiceSOAP("example.com"));

        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "\n" +
                "<wsdl:definitions xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\" xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\" xmlns:http=\"http://schemas.xmlsoap.org/wsdl/http/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap-enc=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:tns=\"http://test.se:8080/type\" targetNamespace=\"http://test.se:8080\">\n" +
                "  <wsdl:types>\n" +
                "    <xsd:schema targetNamespace=\"http://test.se:8080/type\">\n" +
                "      <xsd:complexType name=\"empty\">\n" +
                "        <xsd:sequence/>\n" +
                "      </xsd:complexType>\n" +
                "      <xsd:complexType name=\"ArrayOfstring\">\n" +
                "        <xsd:sequence>\n" +
                "          <xsd:element minOccurs=\"0\" maxOccurs=\"unbounded\" name=\"element\" nillable=\"true\" type=\"xsd:string\"/>\n" +
                "        </xsd:sequence>\n" +
                "      </xsd:complexType>\n" +
                "      <xsd:complexType name=\"ArrayOfSpecialReturnClass\">\n" +
                "        <xsd:sequence>\n" +
                "          <xsd:element minOccurs=\"0\" maxOccurs=\"unbounded\" name=\"element\" nillable=\"true\" type=\"tns:SpecialReturnClass\"/>\n" +
                "        </xsd:sequence>\n" +
                "      </xsd:complexType>\n" +
                "      <xsd:complexType name=\"SpecialReturnClass\">\n" +
                "        <xsd:sequence>\n" +
                "          <xsd:element name=\"otherValue1\" type=\"xsd:string\"/>\n" +
                "          <xsd:element name=\"otherName2\" type=\"xsd:string\"/>\n" +
                "          <xsd:element name=\"field2\" type=\"xsd:base64Binary\"/>\n" +
                "          <xsd:element name=\"field3\" type=\"tns:InnerClass\"/>\n" +
                "        </xsd:sequence>\n" +
                "      </xsd:complexType>\n" +
                "      <xsd:complexType name=\"InnerClass\">\n" +
                "        <xsd:sequence>\n" +
                "          <xsd:element name=\"field0\" type=\"xsd:string\"/>\n" +
                "          <xsd:element name=\"field1\" type=\"xsd:string\"/>\n" +
                "        </xsd:sequence>\n" +
                "      </xsd:complexType>\n" +
                "    </xsd:schema>\n" +
                "  </wsdl:types>\n" +
                "  <wsdl:message name=\"stringArrayMethodRequest\">\n" +
                "    <wsdl:part name=\"StringName\" type=\"xsd:string\"/>\n" +
                "  </wsdl:message>\n" +
                "  <wsdl:message name=\"stringArrayMethodResponse\">\n" +
                "    <wsdl:part name=\"stringArray\" type=\"td:ArrayOfstring\"/>\n" +
                "  </wsdl:message>\n" +
                "  <wsdl:message name=\"simpleReturnClassMethodRequest\">\n" +
                "    <wsdl:part name=\"byte\" type=\"xsd:string\"/>\n" +
                "  </wsdl:message>\n" +
                "  <wsdl:message name=\"simpleReturnClassMethodResponse\">\n" +
                "    <wsdl:part name=\"otherParam1\" type=\"xsd:string\"/>\n" +
                "    <wsdl:part name=\"param2\" type=\"xsd:string\"/>\n" +
                "  </wsdl:message>\n" +
                "  <wsdl:message name=\"exceptionMethodRequest\">\n" +
                "    <wsdl:part name=\"otherParam1\" type=\"xsd:int\" minOccurs=\"0\"/>\n" +
                "    <wsdl:part name=\"otherParam2\" type=\"xsd:int\" minOccurs=\"0\"/>\n" +
                "  </wsdl:message>\n" +
                "  <wsdl:message name=\"specialReturnMethodRequest\">\n" +
                "    <wsdl:part name=\"StringName2\" type=\"xsd:string\"/>\n" +
                "  </wsdl:message>\n" +
                "  <wsdl:message name=\"specialReturnMethodResponse\">\n" +
                "    <wsdl:part name=\"specialReturnClass\" type=\"td:ArrayOfSpecialReturnClass\"/>\n" +
                "  </wsdl:message>\n" +
                "  <wsdl:message name=\"empty\">\n" +
                "    <wsdl:part name=\"empty\" type=\"td:empty\"/>\n" +
                "  </wsdl:message>\n" +
                "  <wsdl:message name=\"exception\">\n" +
                "    <wsdl:part name=\"exception\" type=\"td:string\"/>\n" +
                "  </wsdl:message>\n" +
                "  <wsdl:portType name=\"MainSOAPClassPortType\">\n" +
                "    <wsdl:operation name=\"stringArrayMethod\">\n" +
                "      <wsdl:input message=\"tns:stringArrayMethodRequest\"/>\n" +
                "      <wsdl:output message=\"tns:stringArrayMethodResponse\"/>\n" +
                "      <wsdl:fault message=\"tns:exception\"/>\n" +
                "    </wsdl:operation>\n" +
                "    <wsdl:operation name=\"simpleReturnClassMethod\">\n" +
                "      <wsdl:input message=\"tns:simpleReturnClassMethodRequest\"/>\n" +
                "      <wsdl:output message=\"tns:simpleReturnClassMethodResponse\"/>\n" +
                "      <wsdl:fault message=\"tns:exception\"/>\n" +
                "    </wsdl:operation>\n" +
                "    <wsdl:operation name=\"exceptionMethod\">\n" +
                "      <wsdl:documentation>Documentation of method exceptionMethod()</wsdl:documentation>\n" +
                "      <wsdl:input message=\"tns:exceptionMethodRequest\"/>\n" +
                "    </wsdl:operation>\n" +
                "    <wsdl:operation name=\"specialReturnMethod\">\n" +
                "      <wsdl:input message=\"tns:specialReturnMethodRequest\"/>\n" +
                "      <wsdl:output message=\"tns:specialReturnMethodResponse\"/>\n" +
                "      <wsdl:fault message=\"tns:exception\"/>\n" +
                "    </wsdl:operation>\n" +
                "    <wsdl:operation name=\"voidMethod\"/>\n" +
                "  </wsdl:portType>\n" +
                "  <wsdl:binding name=\"MainSOAPClassBinding\" type=\"tns:MainSOAPClassPortType\">\n" +
                "    <soap:binding style=\"rpc\" transport=\"http://schemas.xmlsoap.org/soap/http\"/>\n" +
                "    <wsdl:operation name=\"stringArrayMethod\">\n" +
                "      <soap:operation soapAction=\"http://test.se:8080/stringArrayMethod\"/>\n" +
                "      <wsdl:input>\n" +
                "        <soap:body use=\"literal\" namespace=\"http://test.se:8080/stringArrayMethod\"/>\n" +
                "      </wsdl:input>\n" +
                "      <wsdl:output>\n" +
                "        <soap:body use=\"literal\" namespace=\"http://test.se:8080/stringArrayMethod\"/>\n" +
                "      </wsdl:output>\n" +
                "    </wsdl:operation>\n" +
                "    <wsdl:operation name=\"simpleReturnClassMethod\">\n" +
                "      <soap:operation soapAction=\"http://test.se:8080/simpleReturnClassMethod\"/>\n" +
                "      <wsdl:input>\n" +
                "        <soap:body use=\"literal\" namespace=\"http://test.se:8080/simpleReturnClassMethod\"/>\n" +
                "      </wsdl:input>\n" +
                "      <wsdl:output>\n" +
                "        <soap:body use=\"literal\" namespace=\"http://test.se:8080/simpleReturnClassMethod\"/>\n" +
                "      </wsdl:output>\n" +
                "    </wsdl:operation>\n" +
                "    <wsdl:operation name=\"exceptionMethod\">\n" +
                "      <soap:operation soapAction=\"http://test.se:8080/exceptionMethod\"/>\n" +
                "      <wsdl:input>\n" +
                "        <soap:body use=\"literal\" namespace=\"http://test.se:8080/exceptionMethod\"/>\n" +
                "      </wsdl:input>\n" +
                "    </wsdl:operation>\n" +
                "    <wsdl:operation name=\"specialReturnMethod\">\n" +
                "      <soap:operation soapAction=\"http://test.se:8080/specialReturnMethod\"/>\n" +
                "      <wsdl:input>\n" +
                "        <soap:body use=\"literal\" namespace=\"http://test.se:8080/specialReturnMethod\"/>\n" +
                "      </wsdl:input>\n" +
                "      <wsdl:output>\n" +
                "        <soap:body use=\"literal\" namespace=\"http://test.se:8080/specialReturnMethod\"/>\n" +
                "      </wsdl:output>\n" +
                "    </wsdl:operation>\n" +
                "    <wsdl:operation name=\"voidMethod\">\n" +
                "      <soap:operation soapAction=\"http://test.se:8080/voidMethod\"/>\n" +
                "      <wsdl:input>\n" +
                "        <soap:body use=\"literal\" namespace=\"http://test.se:8080/voidMethod\"/>\n" +
                "      </wsdl:input>\n" +
                "    </wsdl:operation>\n" +
                "  </wsdl:binding>\n" +
                "  <wsdl:service name=\"MainSOAPClassService\">\n" +
                "    <wsdl:port name=\"MainSOAPClassPort\" binding=\"tns:MainSOAPClassBinding\">\n" +
                "      <soap:address location=\"example.com\"/>\n" +
                "    </wsdl:port>\n" +
                "  </wsdl:service>\n" +
                "</wsdl:definitions>\n",
                writer.write());
    }
}