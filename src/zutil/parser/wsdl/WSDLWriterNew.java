/*******************************************************************************
 * Copyright (c) 2011 Ziver Koc
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
 ******************************************************************************/
package zutil.parser.wsdl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.ibm.wsdl.extensions.PopulatedExtensionRegistry;
import com.ibm.wsdl.extensions.soap.SOAPConstants;
import com.sun.org.apache.xerces.internal.dom.DocumentImpl;

import zutil.io.StringOutputStream;
import zutil.net.ws.WSMethodDef;
import zutil.net.ws.WSParameterDef;
import zutil.net.ws.WSReturnObject;
import zutil.net.ws.WSReturnObject.WSValueName;
import zutil.net.ws.WebServiceDef;

public class WSDLWriterNew{
	
	private WebServiceDef ws;
	private String cache;
	private String soapURL;
	
	public WSDLWriterNew( WebServiceDef ws ){
		this.ws = ws;
		try {
			Definition  wsdl = generateWSDL( );
			
			StringOutputStream out = new StringOutputStream();
			
			javax.wsdl.xml.WSDLWriter writer = WSDLFactory.newInstance().newWSDLWriter();
			writer.writeWSDL(wsdl, out);
			
			//OutputFormat format = OutputFormat.createPrettyPrint();
			//XMLWriter writer = new XMLWriter( out, format );
			//writer.write( wsdlType );
			
			this.cache = out.toString();
			out.close();			
		} catch (WSDLException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * @param 		binding		adds this binding to the WSDL generation
	 */
	public void setSOAPAddress( String url ){
		this.soapURL = url;
	}
	
	
	
	public void write( PrintStream out ) {
		out.print( cache );
	}
	
	public void write( OutputStream out ) throws IOException {
		out.write( cache.getBytes() );
	}
	
	
	private Document generateDefinition(){
		Document wsdl = DocumentHelper.createDocument();
		Element definitions = wsdl.addElement("wsdl:definitions");
		definitions.addAttribute("targetNamespace", ws.getNamespace());
		definitions.addNamespace("tns", ws.getNamespace()+"?type");
		definitions.addNamespace("xsd", "http://www.w3.org/2001/XMLSchema");
		definitions.addNamespace("wsdl", "http://schemas.xmlsoap.org/wsdl/"); 
		definitions.addNamespace("SOAP-ENC", "http://schemas.xmlsoap.org/soap/encoding/");
		
		generateType(definitions, null);
		generateMessages(definitions);
		generatePortType(definitions);
		generateBinding(definitions);
		generateService(definitions);
		
		return wsdl;
	}
	
	private void generateMessegas(Element definitions){

	}
	
	private void generateMessage(Element parent){
		Element root = parent.addElement("wsdl:message");
		//TODO
	}
	
	private void generatePortType(Element definitions){
		Element root = definitions.addElement("wsdl:portType");
		//TODO
	}
	
	private void generatePortOperation(Element parent){
		Element root = parent.addElement("wsdl:operation");
		//TODO
	}
	
	private void generateBinding(Element definitions){
		Element root = definitions.addElement("wsdl:binding");
		//TODO
	}
	
	private void generateSOAPOperation(Element definitions){
		Element root = definitions.addElement("soap:operation");
		//TODO
	}
	
	private void generateService(Element parent){
		Element root = parent.addElement("wsdl:service");
		root.setName(ws.getName()+"Service");
		
		Element port = root.addElement("wsdl:port");
		port.addAttribute("name", "");
		port.addAttribute("binding", "tns:"+ws.getName()+"Port");
		
		Element address = port.addElement("soap:address");
		address.addAttribute("location", null);
	}

	/**
	 * This function generates the Type section of the WSDL.
	 * <b><pre>
	 * -wsdl:definitions
	 *     -wsdl:type
	 *  </pre></b>
	 */
	private void generateType(Element definitions, ArrayList<Class<?>> types){
		Element typeE = definitions.addElement("wsdl:types");	
		Element schema = typeE.addElement("xsd:schema");
		schema.addAttribute("targetNamespace", ws.getNamespace()+"?type");
		
		// empty type
		Element empty = schema.addElement("xsd:complexType");
		empty.addAttribute("name", "empty");
		empty.addElement("xsd:sequence");

		for(int n=0; n<types.size() ;n++){
			Class<?> c = types.get(n);
			// Generate Array type
			if(c.isArray()){
				Class<?> ctmp = getClass(c);
				
				Element type = schema.addElement("xsd:complexType");
				type.addAttribute("name", 
						"ArrayOf"+getClassSOAPName(c).replaceAll("[\\[\\]]", ""));
								
				Element sequence = type.addElement("xsd:sequence");
				
				Element element = sequence.addElement("xsd:element");
				element.addAttribute("minOccurs", "0");
				element.addAttribute("maxOccurs", "unbounded");
				element.addAttribute("name", "element");
				element.addAttribute("nillable", "true");
				if( WSReturnObject.class.isAssignableFrom(ctmp) )
					element.addAttribute("type", "tns:"+getClassSOAPName(c).replace("[]", ""));
				else
					element.addAttribute("type", "xsd:"+getClassSOAPName(c).replace("[]", ""));
				
				if(!types.contains(ctmp))
					types.add(ctmp);
			}
			// Generate SOAPObject type
			else if(WSReturnObject.class.isAssignableFrom(c)){
				Element type = schema.addElement("xsd:complexType");
				type.addAttribute("name", getClassSOAPName(c));

				Element sequence = type.addElement("xsd:sequence");

				Field[] fields = c.getFields();
				for(int i=0; i<fields.length ;i++){
					WSValueName tmp = fields[i].getAnnotation( WSValueName.class );
					String name;
					if(tmp != null) name = tmp.value();
					else name = "field"+i;

					Element element = sequence.addElement("xsd:element");
					element.addAttribute("name", name);

					// Check if the object is an SOAPObject
					Class<?> cTmp = getClass(fields[i].getType());
					if( WSReturnObject.class.isAssignableFrom(cTmp) ){
						element.addAttribute("type", "tns:"+getClassSOAPName(cTmp));
						if(!types.contains(cTmp))
							types.add(cTmp);
					}
					else{
						element.addAttribute("type", "xsd:"+getClassSOAPName(fields[i].getType()));
					}
					// Is the Field optional
					if(tmp != null && tmp.optional())
						element.addAttribute("minOccurs", "0");
				}
			}
		}
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	// TODO: FIX THESE ARE DUPLICATES FROM SOAPHttpPage
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	private Class<?> getClass(Class<?> c){
		if(c!=null && c.isArray()){
			return getClass(c.getComponentType());
		}
		return c;
	}
	
	private String getClassSOAPName(Class<?> c){
		Class<?> cTmp = getClass(c);
		if( byte[].class.isAssignableFrom(c) ){
			return "base64Binary";
		}
		else if( WSReturnObject.class.isAssignableFrom(cTmp) ){
			return c.getSimpleName();
		}
		else{
			String ret = c.getSimpleName().toLowerCase();
			
			if( 	 cTmp == Integer.class ) 	ret = ret.replaceAll("integer", "int");
			else if( cTmp == Character.class )	ret = ret.replaceAll("character", "char");
			
			return ret;
		}
	}
	
	public void close() {}

}
