package zutil.network.http.soap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.wsdl.Binding;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPHeader;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.namespace.QName;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.xml.sax.SAXException;

import zutil.MultiPrintStream;
import zutil.network.http.HttpPage;
import zutil.network.http.HttpPrintStream;
import zutil.network.http.soap.SOAPInterface.WSDLDocumentation;
import zutil.network.http.soap.SOAPInterface.WSDLParamDocumentation;
import zutil.network.http.soap.SOAPObject.SOAPFieldName;

import com.ibm.wsdl.extensions.PopulatedExtensionRegistry;
import com.ibm.wsdl.extensions.soap.SOAPConstants;
import com.sun.org.apache.xerces.internal.dom.DocumentImpl;


public class SOAPHttpPage implements HttpPage{
	// valid methods for this soap page
	private HashMap<String, MethodChasch> methods;
	// contains an method and the names for the parameters
	private class MethodChasch{
		String[] paramName;
		boolean[] paramOptional;
		String returnName;
		Method method;
		boolean header;

		MethodChasch(Method m){
			method = m;
			paramName = new String[method.getParameterTypes().length];
			paramOptional = new boolean[method.getParameterTypes().length];
			header = false;
		}
	}
	// The object that the functions will be invoked from
	private SOAPInterface interf;
	// The WSDL document
	private Definition wsdl;
	// The WSDL Type part
	private Document wsdlType;
	// the URL to this soap page
	private String url;

	public SOAPHttpPage(String url, SOAPInterface interf) throws WSDLException{
		//if(!SOAPInterface.class.isAssignableFrom(interf) )
		//	throw new ClassCastException("Class does not implement SOAPInterface!");
		this.url = url;
		this.interf = interf;
		methods = new HashMap<String, MethodChasch>();

		for(Method m : interf.getClass().getDeclaredMethods()){
			// check for public methods
			if(m.getModifiers() == Modifier.PUBLIC && 
					!m.isAnnotationPresent(SOAPInterface.SOAPDisabled.class)){
				MethodChasch chasch = new MethodChasch(m);
				StringBuffer tmp = new StringBuffer(m.getName()+"(");

				// Get the parameter names
				Annotation[][] paramAnnotation = m.getParameterAnnotations();

				for(int i=0; i<paramAnnotation.length ;i++){
					for(Annotation annotation : paramAnnotation[i]){
						if(annotation instanceof SOAPInterface.SOAPParamName){
							SOAPInterface.SOAPParamName paramName = (SOAPInterface.SOAPParamName) annotation;
							chasch.paramName[i] = paramName.value();
							chasch.paramOptional[i] = paramName.optional();
						}
					}
					// if no name was found then use default
					if(chasch.paramName[i] == null)
						chasch.paramName[i] = "args"+i;

					tmp.append(m.getParameterTypes()[i].getSimpleName()+" "+chasch.paramName[i]);
					if( i<paramAnnotation.length-1 ) tmp.append(", ");
				}
				tmp.append(")");

				// the return param name
				SOAPInterface.SOAPReturnName returnName = m.getAnnotation(SOAPInterface.SOAPReturnName.class);
				if(returnName != null) chasch.returnName = returnName.value();
				else chasch.returnName = "return";

				// SOAP header?
				if(m.getAnnotation(SOAPInterface.SOAPHeader.class) != null) 
					chasch.header = true;

				// save in HashMap
				MultiPrintStream.out.println("New SOAP Method Registered: "+tmp);
				methods.put(m.getName(), chasch);
			}
		}

		generateWSDL();

		try {
			// WSDL
			MultiPrintStream.out.println();
			WSDLFactory factory = WSDLFactory.newInstance();
			WSDLWriter writer = factory.newWSDLWriter();
			writer.writeWSDL(wsdl, MultiPrintStream.out);
			MultiPrintStream.out.println();
			// WSDL Type
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter xmlWriter = new XMLWriter( MultiPrintStream.out, format );
			xmlWriter.write( wsdlType );
			MultiPrintStream.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void respond(HttpPrintStream out,
			HashMap<String, String> client_info,
			HashMap<String, String> session, HashMap<String, String> cookie,
			HashMap<String, String> request) {

		try {
			out.sendHeader("Content-Type: text/xml");

			if(request.containsKey("wsdl")){
				out.println("");
				out.flush();
				WSDLWriter writer = WSDLFactory.newInstance().newWSDLWriter();
				writer.writeWSDL(wsdl, out);
			}
			else if(request.containsKey("type")){
				OutputFormat format = OutputFormat.createPrettyPrint();
				XMLWriter writer = new XMLWriter( out, format );
				writer.write( wsdlType );
			}
			else{
				Document document = soap( request.get("") );

				OutputFormat format = OutputFormat.createPrettyPrint();
				XMLWriter writer = new XMLWriter( out, format );
				writer.write( document );
			}
		} catch (Exception e) {
			e.printStackTrace(MultiPrintStream.out);
		}

	}

	public Document soap(String xml){
		try {
			Document document = DocumentHelper.createDocument();
			Element envelope = document.addElement("soap:Envelope");
			envelope.add(new Namespace("soap", "http://www.w3.org/2001/12/soap-envelope"));
			envelope.addAttribute("soap:encodingStyle", "http://www.w3.org/2001/12/soap-encoding");


			Element body = envelope.addElement( "soap:Body" );
			try{
				Element request = getXMLRoot(xml);
				Object obj = interf.getClass().newInstance();
				// Header
				if( request.element("Header") != null){
					Element header = envelope.addElement( "soap:Header" );
					prepareInvoke( obj, request.element("Header"), header );
				}

				// Body
				if( request.element("Body") != null){
					prepareInvoke( obj, request.element("Body"), body );
				}
			}catch(Throwable e){
				body.clearContent();
				Element fault = body.addElement("soap:Fault");
				// The fault source
				if(e instanceof SOAPClientException || e instanceof SAXException || e instanceof DocumentException)
					fault.addElement("faultcode").setText( "soap:Client" );
				else 
					fault.addElement("faultcode").setText( "soap:Server" );
				// The fault message
				if( e.getMessage() == null || e.getMessage().isEmpty()) 
					fault.addElement("faultstring").setText( ""+e.getClass().getSimpleName() );
				else
					fault.addElement("faultstring").setText( ""+e.getMessage() );
				e.printStackTrace(MultiPrintStream.out);
			}

			return document;
		} catch (Exception e) {
			e.printStackTrace(MultiPrintStream.out);
		}

		return null;
	}

	/**
	 * Takes an XML Element and invokes all the 
	 * chide Elements as methods.
	 * 
	 * @param obj is the object that the methods will be called from
	 * @param requestRoot is the Element where the children lies
	 * @param responseRoot is the root element of the response
	 */
	@SuppressWarnings("unchecked")
	private void prepareInvoke(Object obj, Element requestRoot, Element responseRoot) throws Throwable{
		Iterator<Element> it = requestRoot.elementIterator();
		while( it.hasNext() ){
			Element e = it.next();
			if(methods.containsKey(e.getQName().getName())){
				MethodChasch m = methods.get(e.getQName().getName());
				Object[] params = new Object[m.paramName.length];

				// Get the param values
				for(int i=0; i<m.paramName.length ;i++){
					if(e.element(m.paramName[i]) != null)
						params[i] = convertToClass(
								e.element(m.paramName[i]).getTextTrim(),
								m.method.getParameterTypes()[i]);
				}
				// MultiPrintStream.out.println("invoking: "+m.method.getName()+" "+MultiPrintStream.out.dumpToString(params));
				// Invoke
				Object ret = invoke(obj, m.method, params);

				// generate response xml
				if(m.method.getReturnType() != void.class){
					Element response = responseRoot.addElement(m.method.getName()+"Response");
					createReturnXML(response, ret, m.returnName, m);
				}
			}
			else{
				throw new Exception("No such method: "+e.getQName().getName()+"!");
			}
		}
	}

	/**
	 * Generates an return XML Element. This function can
	 * handle return values as XML Elements, SOAPObject and the
	 * Java basic data types.
	 * 
	 * @param root is the parent Element
	 * @param ret is the object that is the return value
	 * @param ename is the name of the parent Element
	 * @param m is the method that returned the ret
	 */
	private void createReturnXML(Element root, Object ret, String ename, MethodChasch m) throws IllegalArgumentException, IllegalAccessException{
		// return an array
		if(ret.getClass().isArray()){
			Element array = root.addElement( (ename.equals("element") ? "Array" : ename) );
			String arrayType = "xsd:"+ret.getClass().getSimpleName().toLowerCase();
			arrayType = arrayType.replaceFirst("\\[\\]", "["+Array.getLength(ret)+"]");

			array.addAttribute("type", "soap:Array");
			array.addAttribute("soap:arrayType", arrayType);
			for(int i=0; i<Array.getLength(ret) ;i++){
				createReturnXML(array, Array.get(ret, i), "element", m);
			}
		}		
		else{
			if(ret instanceof Element)
				root.add( (Element)ret );
			if(ret instanceof SOAPObject){
				Element objectE = root.addElement( ret.getClass().getSimpleName().toLowerCase() );
				Field[] fields = ret.getClass().getFields();
				for(int i=0; i<fields.length ;i++){
					SOAPFieldName tmp = fields[i].getAnnotation(SOAPObject.SOAPFieldName.class);
					String name;
					if(tmp != null) name = tmp.value();
					else name = "field"+i;
					createReturnXML(objectE, fields[i].get(ret), name, m);
				}
			}
			else {
				Element valueE = root.addElement( ename );
				valueE.addAttribute("type", "xsd:"+ret.getClass().getSimpleName().toLowerCase());
				valueE.addText( ""+ret );
			}
		}
	}

	/**
	 * Converts an given String to a specified class
	 */
	private Object convertToClass(String data, Class<?> c){
		if(data == null || data.isEmpty())
			return null;
		
		if(     c == String.class) 		return data;
		else if(c == Integer.class) 	return Integer.parseInt(data);
		else if(c == int.class) 		return Integer.parseInt(data);
		else if(c == Long.class) 		return Long.parseLong(data);
		else if(c == long.class) 		return Long.parseLong(data);
		else if(c == Float.class) 		return Float.parseFloat(data);
		else if(c == float.class) 		return Float.parseFloat(data);
		else if(c == Double.class) 		return Double.parseDouble(data);
		else if(c == double.class) 		return Double.parseDouble(data);
		else if(c == Boolean.class) 	return Boolean.parseBoolean(data);
		else if(c == boolean.class) 	return Boolean.parseBoolean(data);
		else if(c == Byte.class) 		return Byte.parseByte(data);
		else if(c == byte.class) 		return Byte.parseByte(data);
		return null;
	}

	/**
	 * Invokes a specified method
	 * 
	 * @param m is the function
	 * @param params a vector with arguments
	 * @throws Throwable 
	 */
	public Object invoke(Object obj, Method m, Object[] params) throws Throwable{
		try {
			return m.invoke(obj, params );
		} catch (IllegalArgumentException e) {
			throw new SOAPClientException("Arguments missing for "+m.getName()+"!");
		} catch (IllegalAccessException e) {
			throw e;
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
	}

	/**
	 * Converts an String XML to an Element
	 * 
	 * @param msg is the string XML
	 * @return the XML root Element
	 */
	public Element getXMLRoot(String xml) throws Exception {
		if(xml != null && !xml.isEmpty()){
			Document document = DocumentHelper.parseText(xml);
			return document.getRootElement();
		}
		return null;
	}

	/**
	 * Generates an WSDL document for the class
	 * 
	 * @throws WSDLException
	 */
	private void generateWSDL() throws WSDLException{
		ArrayList<Class<?>> types = new ArrayList<Class<?>>();

		PopulatedExtensionRegistry extReg = new PopulatedExtensionRegistry();
		WSDLFactory factory = WSDLFactory.newInstance();
		String tns = url+"?wsdl";
		String xsd = "http://www.w3.org/2001/XMLSchema";
		String soap = "http://schemas.xmlsoap.org/wsdl/soap/";
		String wsdln = "http://schemas.xmlsoap.org/wsdl/";
		String td = url+"?type";
		String portTypeName = this.interf.getClass().getSimpleName()+"PortType";

		wsdl = factory.newDefinition();
		wsdl.setQName(new QName(tns, this.interf.getClass().getSimpleName()));
		wsdl.setTargetNamespace(tns);
		wsdl.addNamespace("tns", tns);
		wsdl.addNamespace("xsd", xsd);
		wsdl.addNamespace("soap", soap);
		wsdl.addNamespace("wsdl", wsdln);
		wsdl.addNamespace("td", td);

		Message exception = wsdl.createMessage();
		exception.setQName(new QName(tns, "exception"));
		exception.setUndefined(false);
		Part epart = wsdl.createPart();
		epart.setName("message");
		epart.setTypeName(new QName(xsd, "string"));
		exception.addPart(epart);
		wsdl.addMessage(exception);	

		// Types import
		/*
		Import imp = wsdl.createImport();
		imp.setNamespaceURI(td);
		imp.setLocationURI(td);
		wsdl.addImport(imp);
		 */
		// PrtType
		PortType portType = wsdl.createPortType();
		portType.setQName(new QName(tns, portTypeName));
		portType.setUndefined(false);
		for(MethodChasch m : methods.values()){
			Operation operation = wsdl.createOperation();			
			//********* Request Messages
			if(m.paramName.length > 0){
				Message msgIn = wsdl.createMessage();
				msgIn.setQName(new QName(tns, m.method.getName()+"Request"));
				msgIn.setUndefined(false);

				//***** Documentation
				WSDLParamDocumentation tmpParamDoc = m.method.getAnnotation(SOAPInterface.WSDLParamDocumentation.class);
				if(tmpParamDoc != null){
					org.w3c.dom.Document xmldoc= new DocumentImpl();
					org.w3c.dom.Element paramDoc = xmldoc.createElement("wsdl:documentation");
					paramDoc.setTextContent(tmpParamDoc.value());
					msgIn.setDocumentationElement(paramDoc);
				}

				// Parameters
				for(int i=0; i<m.paramName.length ;i++){
					// Parts
					Part part = wsdl.createPart();
					part.setName(m.paramName[i]);
					part.setTypeName(new QName(xsd, 
							m.method.getParameterTypes()[i].getSimpleName().toLowerCase()));
					if(m.paramOptional[i])
						part.getExtensionAttribute(new QName("minOccurs", "0"));
					msgIn.addPart(part);
				}
				wsdl.addMessage(msgIn);
				Input input = wsdl.createInput();
				input.setMessage(msgIn);
				operation.setInput(input);
			}
			//********** Response Message
			if(!m.method.getReturnType().equals( void.class )){
				Message msgOut = wsdl.createMessage();
				msgOut.setQName(new QName(tns, m.method.getName()+"Response"));
				msgOut.setUndefined(false);

				// Parts
				Part part = wsdl.createPart();
				part.setName(m.returnName);
				msgOut.addPart(part);

				// Generate new type if the object is an SOAPObject
				Class<?> cTmp = getClass(m.method.getReturnType());
				if(( SOAPObject.class.isAssignableFrom(cTmp) )){
					// is is an array?
					if(m.method.getReturnType().isArray()){
						part.setTypeName(new QName(soap, "Array"));
						part.setExtensionAttribute(
								new QName(soap, "arrayType"),
								new QName(td, m.method.getReturnType().getSimpleName().toLowerCase()));
					}
					else{ // its an Object
						part.setTypeName(new QName(td, 
								m.method.getReturnType().getSimpleName().toLowerCase()));
					}
					// add to type generation list
					if(!types.contains(cTmp))
						types.add(cTmp);
				}
				else{
					// is is an array?
					if(m.method.getReturnType().isArray()){
						part.setTypeName(new QName(soap, "Array"));
						part.setExtensionAttribute(
								new QName(soap, "arrayType"),
								new QName(xsd, m.method.getReturnType().getSimpleName().toLowerCase()));
					}
					else{ // its an Object
						part.setTypeName(new QName(xsd, 
								m.method.getReturnType().getSimpleName().toLowerCase()));
					}
				}

				wsdl.addMessage(msgOut);				
				Output output = wsdl.createOutput();			
				output.setMessage(msgOut);			
				operation.setOutput(output);			
			}
			//************* Exceptions	
			if(m.method.getExceptionTypes().length <= 0){
				Fault fault = wsdl.createFault();
				fault.setMessage(exception);
				operation.addFault(fault);
			}
			//************* Operations			
			operation.setName(m.method.getName());			
			operation.setUndefined(false);			

			//***** Documentation
			WSDLDocumentation tmpDoc = m.method.getAnnotation(SOAPInterface.WSDLDocumentation.class);
			if(tmpDoc != null){
				// <!-- example -->
				org.w3c.dom.Document xmldoc= new DocumentImpl();
				org.w3c.dom.Element doc = xmldoc.createElement("wsdl:documentation");
				doc.setTextContent(tmpDoc.value());
				operation.setDocumentationElement(doc);
			}

			portType.addOperation(operation);
		}
		wsdl.addPortType(portType);

		// Binding
		Binding binding = wsdl.createBinding();
		binding.setQName(new QName(tns, interf.getClass().getSimpleName()+"Binding"));
		binding.setPortType(portType);
		binding.setUndefined(false);

		SOAPBinding soapBinding = (SOAPBinding)extReg.createExtension(Binding.class, SOAPConstants.Q_ELEM_SOAP_BINDING);
		soapBinding.setStyle("rpc");
		soapBinding.setRequired(true);
		soapBinding.setTransportURI("http://schemas.xmlsoap.org/soap/http");
		binding.addExtensibilityElement(soapBinding);

		for(MethodChasch m : methods.values()){
			BindingOperation operation = wsdl.createBindingOperation();
			operation.setName(m.method.getName());

			SOAPOperation soapOperation = (SOAPOperation)extReg.createExtension(BindingOperation.class, SOAPConstants.Q_ELEM_SOAP_OPERATION);
			soapOperation.setSoapActionURI("");
			operation.addExtensibilityElement(soapOperation);

			// input
			if(m.paramName.length > 0){
				BindingInput input = wsdl.createBindingInput();
				// Header
				if(m.header){
					SOAPHeader soapHeader = (SOAPHeader)extReg.createExtension(BindingInput.class, SOAPConstants.Q_ELEM_SOAP_HEADER);
					soapHeader.setUse("literal");
					input.addExtensibilityElement(soapHeader);
				}// Body
				else{
					SOAPBody soapBody = (SOAPBody)extReg.createExtension(BindingInput.class, SOAPConstants.Q_ELEM_SOAP_BODY);
					soapBody.setUse("literal");
					input.addExtensibilityElement(soapBody);
				}
				operation.setBindingInput(input);
			}

			// output
			if(!m.method.getReturnType().equals( void.class )){
				BindingOutput output = wsdl.createBindingOutput();
				// Header
				if(m.header){
					SOAPHeader soapHeader = (SOAPHeader)extReg.createExtension(BindingInput.class, SOAPConstants.Q_ELEM_SOAP_HEADER);
					soapHeader.setUse("literal");
					output.addExtensibilityElement(soapHeader);
				}// Body
				else{
					SOAPBody soapBody = (SOAPBody)extReg.createExtension(BindingInput.class, SOAPConstants.Q_ELEM_SOAP_BODY);
					soapBody.setUse("literal");
					output.addExtensibilityElement(soapBody);
				}
				operation.setBindingOutput(output);
			}

			binding.addBindingOperation(operation);
		}
		wsdl.addBinding(binding);

		// Service
		Port port = wsdl.createPort();
		port.setName( interf.getClass().getSimpleName()+"Port" );
		port.setBinding(binding);
		SOAPAddress addr = (SOAPAddress)extReg.createExtension(Port.class, SOAPConstants.Q_ELEM_SOAP_ADDRESS);
		addr.setLocationURI(url);
		port.addExtensibilityElement(addr);

		Service ser = wsdl.createService();
		ser.addPort(port);
		wsdl.addService(ser);

		// generate the complexTypes
		generateWSDLType(types);
	}

	/**
	 * This function generates the Type part of the WSDL.
	 * Should be cabled after generateWSDL has finished.
	 * 
	 */
	private void generateWSDLType(ArrayList<Class<?>> types){
		wsdlType = DocumentHelper.createDocument();
		Element definitions = wsdlType.addElement( "wsdl:definitions" );
		definitions.addNamespace("targetNamespace", url+"?type");
		definitions.addNamespace("xsd", "http://www.w3.org/2001/XMLSchema");
		definitions.addNamespace("wsdl", "http://schemas.xmlsoap.org/wsdl/");

		Element typeE = definitions.addElement("wsdl:types");	
		Element schema = typeE.addElement("xsd:schema");	

		for(int n=0; n<types.size() ;n++){
			Class<?> c = types.get(n);
			Element type = schema.addElement("xsd:complexType");
			type.addAttribute("name", c.getSimpleName().toLowerCase());

			Element sequence = type.addElement("xsd:sequence");

			Field[] fields = c.getFields();
			for(int i=0; i<fields.length ;i++){
				SOAPFieldName tmp = fields[i].getAnnotation(SOAPObject.SOAPFieldName.class);
				String name;
				if(tmp != null) name = tmp.value();
				else name = "field"+i;

				Element element = sequence.addElement("xsd:element");
				element.addAttribute("name", name);

				// Check if the object is an SOAPObject
				Class<?> cTmp = getClass(fields[i].getType());
				if(SOAPObject.class.isAssignableFrom(cTmp)){
					element.addAttribute("type", "tns:"+cTmp.getSimpleName().toLowerCase());
					if(!types.contains(cTmp))
						types.add(cTmp);
				}
				else
					element.addAttribute("type", "xsd:"+cTmp.getSimpleName().toLowerCase());
				// Is the Field optional
				if(tmp != null && tmp.optional())
					element.addAttribute("minOccurs", "0");
			}
		}
	}

	private Class<?> getClass(Class<?> c){
		if(c.isArray()){
			return getClass(c.getComponentType());
		}
		return c;
	}

	//*******************************************************************************************
	//**************************** TEST *********************************************************

	public static void main(String[] args){
		try {
			new SOAPHttpPage("http://test.se:8080/", new Test()).test();
		} catch (WSDLException e) {
			e.printStackTrace();
		}
	}

	private static class TestObject2 implements SOAPObject{
		public String lol = "lol11";
		public String lol2 = "lol22";
	}

	private static class TestObject implements SOAPObject{
		@SOAPFieldName(value="lolz", optional=true)
		public String lol = "lol1";
		@SOAPFieldName("lolx")
		public String lol2 = "lol2";
		public TestObject2 l = new TestObject2();
	}

	private static class Test implements SOAPInterface{
		public Test(){}

		@SOAPHeader()
		@WSDLDocumentation("hello")
		public void pubZ(
				@SOAPParamName(value="olle", optional=true) int lol) throws Exception{ 
			//System.out.println("Param: "+lol);
			throw new Exception("Ziver is the fizle");
		}

		@SOAPReturnName("param")
		@WSDLParamDocumentation("null is the shizzle")
		public String[][] pubA (
				@SOAPParamName("Ztring") String lol) throws Exception{ 
			//System.out.println("ParamZ: "+lol); 
			return new String[][]{{"test","test2"},{"test3","test4"}};
		}

		@SOAPReturnName("zivarray")
		@WSDLParamDocumentation("null is the shizzle")
		public TestObject[] pubX (
				@SOAPParamName("Ztring") String lol) throws Exception{ 
			return new TestObject[]{new TestObject(), new TestObject()};
		}

		@SOAPDisabled()
		public void privaZ(){ }
		protected void protZ(){ }			
	}

	public void test(){
		// Response		
		try {	
			Document document = soap(
					"<?xml version=\"1.0\"?>" +
					"<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
					"	<soap:Body xmlns:m=\"http://www.example.org/stock\">\n" +
					//"		<m:pubA>\n" +
					//"			<m:Ztring>IBM</m:Ztring>\n" +
					//"		</m:pubA>\n" +
					//"		<m:pubZ>\n" +
					//"			<m:olle>66</m:olle>\n" +
					//"		</m:pubZ>\n" +
					"		<m:pubX>\n" +
					"			<m:Ztring>IBM</m:Ztring>\n" +
					"		</m:pubX>\n" +
					"	</soap:Body>\n" +
			"</soap:Envelope>");
			System.out.println();	

			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter writer = new XMLWriter( System.out, format );
			writer.write( document );

			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
