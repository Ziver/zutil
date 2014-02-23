/*******************************************************************************
 * Copyright (c) 2013 Ziver
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

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import zutil.io.StringOutputStream;
import zutil.net.ws.WSMethodDef;
import zutil.net.ws.WSParameterDef;
import zutil.net.ws.WSReturnObject;
import zutil.net.ws.WSReturnObject.WSValueName;
import zutil.net.ws.WebServiceDef;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class WSDLWriter{
    /** Current Web service definition ***/
    private WebServiceDef ws;
    /** Cache of generated WSDL **/
    private String cache;
    /** A list of services **/
    private ArrayList<WSDLService> services;

	public WSDLWriter( WebServiceDef ws ){
        this.services = new ArrayList<WSDLService>();
		this.ws = ws;
	}

    /**
     * Add a service to be published with the WSDL
     */
    public void addService(WSDLService serv){
        cache = null;
        services.add(serv);
    }

	public void write( PrintStream out ) {
		out.print(generate());
	}

	public void write( OutputStream out ) throws IOException {
		out.write(generate().getBytes() );
	}


	private String generate(){
		if(cache == null){
			try {
				OutputFormat outformat = OutputFormat.createPrettyPrint();
				StringOutputStream out = new StringOutputStream();			
				XMLWriter writer = new XMLWriter(out, outformat);

				Document docroot = generateDefinition();
				writer.write(docroot);

				writer.flush();
				this.cache = out.toString();
				out.close();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}			
		return cache;
	}

	private Document generateDefinition(){
		Document wsdl = DocumentHelper.createDocument();
		Element definitions = wsdl.addElement("wsdl:definitions");		
		definitions.addNamespace("wsdl", "http://schemas.xmlsoap.org/wsdl/"); 
		definitions.addNamespace("soap", "http://schemas.xmlsoap.org/wsdl/soap/");
		definitions.addNamespace("http", "http://schemas.xmlsoap.org/wsdl/http/");
		definitions.addNamespace("xsd", "http://www.w3.org/2001/XMLSchema");
		definitions.addNamespace("soap-enc", "http://schemas.xmlsoap.org/soap/encoding/");
		definitions.addNamespace("tns", ws.getNamespace()+"?type");
		definitions.addAttribute("targetNamespace", ws.getNamespace());

		generateType(definitions);
		generateMessages(definitions);
		generatePortType(definitions);
		generateBinding(definitions);
		generateService(definitions);

		return wsdl;
	}

	private void generateMessages(Element definitions){
		for( WSMethodDef method : ws.getMethods() ){
			generateMessage(definitions, method);
		}

		// Default message used for functions without input parameters
		// definitions -> message: empty
		Element empty = definitions.addElement("wsdl:message");
		empty.addAttribute("name", "empty");
		// definitions -> message: empty -> part
		Element empty_part = empty.addElement("wsdl:part");
		empty_part.addAttribute("name", "empty");
		empty_part.addAttribute("type", "td:empty");

		// Exception message
		// definitions -> message: exception
		Element exception = definitions.addElement("wsdl:message");
		exception.addAttribute("name", "exception");
		// definitions -> message: exception -> part
		Element exc_part = exception.addElement("wsdl:part");
		exc_part.addAttribute("name", "exception");
		exc_part.addAttribute("type", "td:string");
	}

	private void generateMessage(Element parent, WSMethodDef method){
		//*************************** Input
		if( method.getInputCount() > 0 ){
			// definitions -> message
			Element input = parent.addElement("wsdl:message");
			input.addAttribute("name", method.getName()+"Request");

			// Parameters
			for( WSParameterDef param : method.getInputs() ){
				// definitions -> message -> part
				Element part = input.addElement("wsdl:part");
				part.addAttribute("name", param.getName());
				part.addAttribute("type", "xsd:"+getClassName( param.getParamClass()));

				if( param.isOptional() )
					part.addAttribute("minOccurs", "0");
			}
		}
		//*************************** Output
		if( method.getOutputCount() > 0 ){
			// definitions -> message
			Element output = parent.addElement("wsdl:message");
			output.addAttribute("name", method.getName()+"Response");

			// Parameters
			for( WSParameterDef param : method.getOutputs() ){
				// definitions -> message -> part
				Element part = output.addElement("wsdl:part");
				part.addAttribute("name", param.getName());

				Class<?> paramClass = param.getParamClass();
				Class<?> valueClass = getClass( paramClass );
				// is an binary array
				if(byte[].class.isAssignableFrom( paramClass )){
					part.addAttribute("type", "xsd:base64Binary");
				}
				// is an array?
				else if( paramClass.isArray()){
					part.addAttribute("type", "td:" +getArrayClassName(paramClass));
				}
				else if( WSReturnObject.class.isAssignableFrom(valueClass) ){
					// its an SOAPObject
					part.addAttribute("type", "td:"+getClassName( paramClass ));
				}
				else{// its an Object 
					part.addAttribute("type", "xsd:"+getClassName( paramClass ));
				}
			}
		}
	}

	private void generatePortType(Element definitions){
		// definitions -> portType
		Element portType = definitions.addElement("wsdl:portType");
		portType.addAttribute("name", ws.getName()+"PortType");

		for( WSMethodDef method : ws.getMethods() ){
			// definitions -> portType -> operation
			Element operation = portType.addElement("wsdl:operation");
			operation.addAttribute("name", method.getName());

			// Documentation
			if(method.getDocumentation() != null){
				Element doc = operation.addElement("wsdl:documentation");
				doc.setText(method.getDocumentation());
			}

			//*************************** Input
			if( method.getInputCount() > 0 ){
				// definitions -> message
				Element input = operation.addElement("wsdl:input");
				input.addAttribute("message", "tns:"+method.getName()+"Request");
			}
			//*************************** Output
			if( method.getOutputCount() > 0 ){
				// definitions -> message
				Element output = operation.addElement("wsdl:output");
				output.addAttribute("message", "tns:"+method.getName()+"Response");
			}
			//*************************** Fault
			if( method.getOutputCount() > 0 ){
				// definitions -> message
				Element fault = operation.addElement("wsdl:fault");
				fault.addAttribute("message", "tns:exception");
			}
		}
	}


	private void generateBinding(Element definitions){
		// definitions -> binding
		Element binding = definitions.addElement("wsdl:binding");
		binding.addAttribute("name", ws.getName()+"Binding");
		binding.addAttribute("type", "tns:"+ws.getName()+"PortType");

        for(WSDLService serv : services){
            serv.generateBinding(binding);

            for(WSMethodDef method : ws.getMethods()){
                serv.generateOperation(binding, method);
            }
        }
	}


	private void generateService(Element parent){
		// definitions -> service
		Element root = parent.addElement("wsdl:service");
		root.addAttribute("name", ws.getName()+"Service");

		// definitions -> service -> port
		Element port = root.addElement("wsdl:port");
		port.addAttribute("name", ws.getName()+"Port");
		port.addAttribute("binding", "tns:"+ws.getName()+"Binding");

        for(WSDLService serv : services){
            // definitions -> service-> port -> address
            Element address = port.addElement(serv.getServiceType()+":address");
            address.addAttribute("location", serv.getServiceAddress());
        }
	}

	/**
	 * This function generates the Type section of the WSDL.
	 * <b><pre>
	 * -wsdl:definitions
	 *     -wsdl:type
	 *  </pre></b>
	 */
	private void generateType(Element definitions){
		ArrayList<Class<?>> types = new ArrayList<Class<?>>();
		// Find types
		for( WSMethodDef method : ws.getMethods() ){
			if( method.getOutputCount() > 0 ){
				for( WSParameterDef param : method.getOutputs() ){
					Class<?> paramClass = param.getParamClass();
					Class<?> valueClass = getClass(paramClass);
					// is an array? or special class 
					if( paramClass.isArray() || WSReturnObject.class.isAssignableFrom(valueClass)){
						// add to type generation list
						if(!types.contains( paramClass ))
							types.add( paramClass );
					}
				}
			}
		}

		// definitions -> types
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
				type.addAttribute("name", getArrayClassName(c));

				Element sequence = type.addElement("xsd:sequence");

				Element element = sequence.addElement("xsd:element");
				element.addAttribute("minOccurs", "0");
				element.addAttribute("maxOccurs", "unbounded");
				element.addAttribute("name", "element");
				element.addAttribute("nillable", "true");
				if( WSReturnObject.class.isAssignableFrom(ctmp) )
					element.addAttribute("type", "tns:"+getClassName(c).replace("[]", ""));
				else
					element.addAttribute("type", "xsd:"+getClassName(c).replace("[]", ""));

				if(!types.contains(ctmp))
					types.add(ctmp);
			}
			// Generate SOAPObject type
			else if(WSReturnObject.class.isAssignableFrom(c)){
				Element type = schema.addElement("xsd:complexType");
				type.addAttribute("name", getClassName(c));

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
						element.addAttribute("type", "tns:"+getClassName(cTmp));
						if(!types.contains(cTmp))
							types.add(cTmp);
					}
					else{
						element.addAttribute("type", "xsd:"+getClassName(fields[i].getType()));
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

	private String getArrayClassName(Class<?> c){
		return "ArrayOf"+getClassName(c).replaceAll("[\\[\\]]", "");
	}

	private String getClassName(Class<?> c){
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
