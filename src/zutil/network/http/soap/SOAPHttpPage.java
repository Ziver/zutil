package zutil.network.http.soap;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Import;
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

/**
 * This is an HTTPPage for the HTTPServer that 
 * handles soap messages.
 * 
 * TODO: Header should be variables not methods
 * TODO: Read SOAPObjects as input parameter
 * TODO: Ability to have multiple arrays of same SOAPObject
 * 
 * Features:
 * Input:
 * <br>-int
 * <br>-double
 * <br>-float
 * <br>-char
 * <br>-String
 * <br>-byte[]
 * <br>-And the Wrappers except byte
 * 
 * Output:
 * <br>-SOAPObjects
 * <br>-SOAPReturnObjectList
 * <br>-byte[]
 * <br>-int
 * <br>-double
 * <br>-float
 * <br>-char
 * <br>-String
 * <br>-Arrays of Output
 * <br>-And the Wrappers except byte
 * 
 * @author Ziver
 */
public class SOAPHttpPage implements HttpPage{
	// valid methods for this soap page
	private HashMap<String, MethodCache> methods;
	// contains an method and the names for the parameters
	private class MethodCache{
		String[] paramName;
		boolean[] paramOptional;
		String[] returnName;
		Class<?>[] returnClass;
		Method method;
		boolean header;

		MethodCache(Method m){
			method = m;
			paramName = new String[method.getParameterTypes().length];
			paramOptional = new boolean[method.getParameterTypes().length];
			header = false;
			
			Class<?> tmp = m.getReturnType();
			if( SOAPReturnValueList.class.isAssignableFrom( tmp )){
				returnName = new String[ tmp.getFields().length ];
				returnClass = new Class<?>[ tmp.getFields().length ];
			}
			else if( !tmp.isAssignableFrom( void.class )){
				returnName = new String[1];
				returnClass = new Class<?>[1];
			}
			else{
				returnName = new String[0];
				returnClass = new Class<?>[0];
			}
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
	// Session enabled
	private boolean session_enabled;

	public SOAPHttpPage(String url, SOAPInterface interf) throws WSDLException{
		//if(!SOAPInterface.class.isAssignableFrom(interf) )
		//	throw new ClassCastException("Class does not implement SOAPInterface!");
		this.url = url;
		this.interf = interf;
		this.session_enabled = false;
		methods = new HashMap<String, MethodCache>();

		for(Method m : interf.getClass().getDeclaredMethods()){
			// check for public methods
			if((m.getModifiers() & Modifier.PUBLIC) > 0 && 
					!m.isAnnotationPresent(SOAPInterface.SOAPDisabled.class)){
				MethodCache chasch = new MethodCache(m);
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
				tmp.append(") => ");

				// the return parameter name
				SOAPInterface.SOAPReturnName returnName = m.getAnnotation(SOAPInterface.SOAPReturnName.class);
				if( SOAPReturnValueList.class.isAssignableFrom( m.getReturnType() ) ){
					Class<?> retClass = m.getReturnType();
					for(int i=0; i<retClass.getFields().length ;i++){
						if(i!=0) tmp.append(", ");
						SOAPReturnValueList.SOAPValueName retValName = retClass.getFields()[i]
						                   .getAnnotation( SOAPReturnValueList.SOAPValueName.class );
						if(retValName != null) chasch.returnName[i] = retValName.value();
						else chasch.returnName[i] = retClass.getFields()[i].getName();
						chasch.returnClass[i] = retClass.getFields()[i].getType();
						tmp.append(chasch.returnClass[i].getSimpleName()+" "+chasch.returnName[i]);
					}
				}
				else if( chasch.returnName.length>0 ){
					if(returnName != null) chasch.returnName[0] = returnName.value();
					else chasch.returnName[0] = "return";
					chasch.returnClass[0] = m.getReturnType();
					tmp.append(chasch.returnClass[0].getSimpleName()+" "+chasch.returnName[0]);
				}

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
	
	/**
	 * Enables session support, if enabled then a new instance 
	 * of the SOAPInterface will be created, if disabled then 
	 * only the given object will be used as an static interface
	 * 
	 * @param enabled is if session should be enabled
	 */
	public void enableSession(boolean enabled){
		this.session_enabled = enabled;
	}


	public void respond(HttpPrintStream out,
			Map<String, String> client_info,
			Map<String, Object> session, 
			Map<String, String> cookie,
			Map<String, String> request) {

		try {
			out.setHeader("Content-Type", "text/xml");
			out.flush();

			if(request.containsKey("wsdl")){
				WSDLWriter writer = WSDLFactory.newInstance().newWSDLWriter();
				writer.writeWSDL(wsdl, out);
			}
			else if(request.containsKey("type")){
				OutputFormat format = OutputFormat.createPrettyPrint();
				XMLWriter writer = new XMLWriter( out, format );
				writer.write( wsdlType );
			}
			else{
				SOAPInterface obj = null;
				if(session_enabled){ 
					if( session.containsKey("SOAPInterface"))
						obj = (SOAPInterface)session.get("SOAPInterface");
					else{
						obj = interf.getClass().newInstance();
						session.put("SOAPInterface", obj);
					}
				}
				else{
					obj = interf;
				}
				
				Document document = genSOAPResponse( request.get(""), obj);
				
				OutputFormat format = OutputFormat.createCompactFormat();
				XMLWriter writer = new XMLWriter( out, format );
				writer.write( document );
				
				
				// DEBUG
				OutputFormat format2 = OutputFormat.createPrettyPrint();
				System.err.println("********** Request");
				System.err.println(request);
				System.out.println("********** Response");
				writer = new XMLWriter( System.out, format2 );
				writer.write( document );
				
			}
		} catch (Exception e) {
			e.printStackTrace(MultiPrintStream.out);			
		}
	}
	
	/**
	 * Generates a soap response for the given XML
	 * @param xml is the XML request
	 * @return a Document with the response
	 */
	public Document genSOAPResponse(String xml){
		try {
			SOAPInterface o = null;
			if(session_enabled) o = interf.getClass().newInstance();
			else o = interf;
			
			return genSOAPResponse(xml, o );
		} catch (Exception e) {
			e.printStackTrace(MultiPrintStream.out);
		}
		return null;
	}

	protected Document genSOAPResponse(String xml, SOAPInterface obj){
		Document document = DocumentHelper.createDocument();
		Element envelope = document.addElement("soap:Envelope");
		try {
			envelope.addNamespace("soap", "http://schemas.xmlsoap.org/soap/envelope/");
			envelope.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
			envelope.addNamespace("xsd", "http://www.w3.org/2001/XMLSchema");
			
			Element body = envelope.addElement( "soap:Body" );
			try{
				Element request = getXMLRoot(xml);
				if(request == null) return document;
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
		} catch (Exception e) {
			e.printStackTrace(MultiPrintStream.out);
		}

		return document;
	}
	
	/**
	 * Converts an String XML to an Element
	 * 
	 * @param msg is the string XML
	 * @return the XML root Element
	 */
	private Element getXMLRoot(String xml) throws Exception {
		if(xml != null && !xml.isEmpty()){
			Document document = DocumentHelper.parseText(xml);
			return document.getRootElement();
		}
		return null;
	}

	/**
	 * Takes an XML Element and invokes all the 
	 * Child Elements as methods.
	 * 
	 * @param obj is the object that the methods will be called from
	 * @param requestRoot is the Element where the children lies
	 * @param responseRoot is the root element of the response
	 */
	@SuppressWarnings("unchecked")
	private void prepareInvoke(SOAPInterface obj, Element requestRoot, Element responseRoot) throws Throwable{
		Iterator<Element> it = requestRoot.elementIterator();
		while( it.hasNext() ){
			Element e = it.next();
			if(methods.containsKey(e.getQName().getName())){
				MethodCache m = methods.get(e.getQName().getName());
				Object[] params = new Object[m.paramName.length];

				// Get the parameter values
				for(int i=0; i<m.paramName.length ;i++){
					if(e.element(m.paramName[i]) != null)
						params[i] = convertToClass(
								e.element(m.paramName[i]).getTextTrim(),
								m.method.getParameterTypes()[i]);
				}
				// MultiPrintStream.out.println("invoking: "+m.method.getName()+" "+MultiPrintStream.out.dumpToString(params));
				// Invoke
				Object ret = invoke(obj, m.method, params);

				// generate response XML
				if( m.returnClass.length>0 ){
					SOAPInterface.SOAPNameSpace namespace = m.method.getAnnotation(SOAPInterface.SOAPNameSpace.class);
					Element response = responseRoot.addElement("");
					if( namespace != null )
						response.addNamespace("m",  namespace.value());
					else
						response.addNamespace("m",  url+""+m.method.getName());
					response.setName("m:"+m.method.getName()+"Response");
					if( ret instanceof SOAPReturnValueList ){
						Field[] f = ret.getClass().getFields();
						for(int i=0; i<m.returnName.length ;i++ ){
							generateReturnXML(response,((SOAPReturnValueList)ret).getValue(f[i]) , m.returnName[i], m);
						}
					}
					else{
						generateReturnXML(response, ret, m.returnName[0], m);
					}
				}
			}
			else{
				throw new Exception("No such method: "+e.getQName().getName()+"!");
			}
		}
	}

	/**
	 * Converts an given String to a specified class
	 */
	protected Object convertToClass(String data, Class<?> c) throws IOException{
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
		else if(byte[].class.isAssignableFrom(c))
										return new sun.misc.BASE64Decoder().decodeBuffer(data);
		return null;
	}
	
	/**
	 * Invokes a specified method
	 * 
	 * @param m is the function
	 * @param params a vector with arguments
	 * @throws Throwable 
	 */
	protected Object invoke(Object obj, Method m, Object[] params) throws Throwable{
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
	 * Generates an return XML Element. This function can
	 * handle return values as XML Elements, SOAPObject and the
	 * Java basic data types.
	 * 
	 * @param root is the parent Element
	 * @param ret is the object that is the return value
	 * @param ename is the name of the parent Element
	 * @param m is the method that returned the ret value
	 */
	private void generateReturnXML(Element root, Object ret, String ename, MethodCache m) throws IllegalArgumentException, IllegalAccessException{
		if(ret == null) return;
		if(byte[].class.isAssignableFrom(ret.getClass())){
			Element valueE = root.addElement( ename );
			valueE.addAttribute("type", "xsd:"+getClassSOAPName(ret.getClass()));
			String tmp = new sun.misc.BASE64Encoder().encode((byte[])ret);
			tmp = tmp.replaceAll("\\s", "");
			valueE.setText(tmp);
		}
		// return an array
		else if(ret.getClass().isArray()){
			Element array = root.addElement( (ename.equals("element") ? "Array" : ename) );
			String arrayType = "xsd:"+getClassSOAPName(ret.getClass());
			arrayType = arrayType.replaceFirst("\\[\\]", "["+Array.getLength(ret)+"]");

			array.addAttribute("type", "soap:Array");
			array.addAttribute("soap:arrayType", arrayType);
			for(int i=0; i<Array.getLength(ret) ;i++){
				generateReturnXML(array, Array.get(ret, i), "element", m);
			}
		}		
		else{
			Element objectE = root.addElement( ename ); //getClassSOAPName(ret.getClass())
			if(ret instanceof Element)
				objectE.add( (Element)ret );
			else if(ret instanceof SOAPObject){
				Field[] fields = ret.getClass().getFields();
				for(int i=0; i<fields.length ;i++){
					SOAPFieldName tmp = fields[i].getAnnotation(SOAPObject.SOAPFieldName.class);
					String name;
					if(tmp != null) name = tmp.value();
					else name = "field"+i;
					generateReturnXML(objectE, fields[i].get(ret), name, m);
				}
			}
			else {
				objectE.addAttribute("type", "xsd:"+getClassSOAPName(ret.getClass()));
				objectE.addText( ""+ret );
			}
		}
	}

	
	private String getClassSOAPName(Class<?> c){
		Class<?> cTmp = getClass(c);
		if(byte[].class.isAssignableFrom(c)){
			return "base64Binary";
		}
		else if( SOAPObject.class.isAssignableFrom(cTmp) ){
			return c.getSimpleName();
		}
		else{
			String ret = c.getSimpleName().toLowerCase();
			
			if(cTmp == Integer.class) 		ret = ret.replaceAll("integer", "int");
			else if(cTmp == Character.class)ret = ret.replaceAll("character", "char");
			
			return ret;
		}
	}

	//********************************************************
	//*********   WSDL Generation    *************************	
	
	/**
	 * Generates an WSDL document for the class
	 * 
	 * @throws WSDLException
	 */
	private void generateWSDL() throws WSDLException{
		ArrayList<Class<?>> types = new ArrayList<Class<?>>();
		
		String tns = url+"?wsdl";
		String xsd = "http://www.w3.org/2001/XMLSchema";
		String soap = "http://schemas.xmlsoap.org/wsdl/soap/";
		String wsdln = "http://schemas.xmlsoap.org/wsdl/";
		String td = url+"?type";

		PopulatedExtensionRegistry extReg = new PopulatedExtensionRegistry();
		WSDLFactory factory = WSDLFactory.newInstance();
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
		exception.setUndefined(true);
		Part epart = wsdl.createPart();
		epart.setName("message");
		epart.setTypeName(new QName(xsd, "string"));
		exception.addPart(epart);
		wsdl.addMessage(exception);
		
		Message empty = wsdl.createMessage();
		empty.setQName(new QName(tns, "empty"));
		empty.setUndefined(false);
		epart = wsdl.createPart();
		epart.setName("empty");
		epart.setTypeName(new QName(td, "empty"));
		empty.addPart(epart);
		wsdl.addMessage(empty);	

		// Types import
		Import imp = wsdl.createImport();
		imp.setNamespaceURI(td);
		imp.setLocationURI(td);
		wsdl.addImport(imp);
		
		// PortType
		PortType portType = wsdl.createPortType();
		portType.setQName(new QName(tns, portTypeName));
		portType.setUndefined(false);
		for(MethodCache m : methods.values()){
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
					part.setTypeName(new QName( xsd, 
							getClassSOAPName(m.method.getParameterTypes()[i])));
					if(m.paramOptional[i])
						part.getExtensionAttribute(new QName("minOccurs", "0"));
					msgIn.addPart(part);
				}
				wsdl.addMessage(msgIn);
				Input input = wsdl.createInput();
				input.setMessage(msgIn);
				operation.setInput(input);
			}
			else{
				Input input = wsdl.createInput();
				input.setMessage(empty);
				operation.setInput(input);
			}
			//********** Response Message
			if( m.returnName.length>0 ){
				Message msgOut = wsdl.createMessage();
				msgOut.setQName(new QName(tns, m.method.getName()+"Response"));
				msgOut.setUndefined(false);
				
				for( int i=0; i<m.returnName.length ;i++ ){					
					Class<?> retClass = m.returnClass[i];
					//MultiPrintStream.out.println(m.method.getName()+"=>"+m.returnName[i]+"="+retClass);
					
					// Parts
					Part part = wsdl.createPart();
					part.setName( m.returnName[i] );
					msgOut.addPart(part);
					
					Class<?> cTmp = getClass( retClass );
					// is an binary array
					if(byte[].class.isAssignableFrom( retClass )){
						part.setTypeName(new QName(xsd, "base64Binary"));
					}
					// is an array?
					else if( retClass.isArray()){
						part.setTypeName(new QName(td, 
								"ArrayOf"+getClassSOAPName( retClass ).replaceAll("[\\[\\]]", "")));
						// add to type generation list
						if(!types.contains( retClass ))
							types.add( retClass );
					}
					else if( SOAPObject.class.isAssignableFrom(cTmp) ){					
						// its an SOAPObject
						part.setTypeName(new QName(td, getClassSOAPName( retClass )));
						// add to type generation list
						if(!types.contains(cTmp))
							types.add(cTmp);
					}
					else{// its an Object 
						part.setTypeName(new QName(xsd, getClassSOAPName( retClass )));
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
		//soapBinding.setRequired(true);
		soapBinding.setTransportURI("http://schemas.xmlsoap.org/soap/http");
		binding.addExtensibilityElement(soapBinding);

		for(MethodCache m : methods.values()){
			BindingOperation operation = wsdl.createBindingOperation();
			operation.setName(m.method.getName());

			SOAPOperation soapOperation = (SOAPOperation)extReg.createExtension(BindingOperation.class, SOAPConstants.Q_ELEM_SOAP_OPERATION);
			soapOperation.setSoapActionURI(url+""+m.method.getName());
			operation.addExtensibilityElement(soapOperation);

			// input
			BindingInput input = wsdl.createBindingInput();
			// Header
			if(m.header){
				SOAPHeader soapHeader = (SOAPHeader)extReg.createExtension(BindingInput.class, SOAPConstants.Q_ELEM_SOAP_HEADER);
				soapHeader.setUse("literal");
				soapHeader.setNamespaceURI(url+""+m.method.getName());
				input.addExtensibilityElement(soapHeader);
			}// Body
			else{
				SOAPBody soapBody = (SOAPBody)extReg.createExtension(BindingInput.class, SOAPConstants.Q_ELEM_SOAP_BODY);
				soapBody.setUse("literal");
				soapBody.setNamespaceURI(url+""+m.method.getName());
				input.addExtensibilityElement(soapBody);
			}
			operation.setBindingInput(input);

			// output
			if(!m.method.getReturnType().equals( void.class )){
				BindingOutput output = wsdl.createBindingOutput();
				// Header
				if(m.header){
					SOAPHeader soapHeader = (SOAPHeader)extReg.createExtension(BindingInput.class, SOAPConstants.Q_ELEM_SOAP_HEADER);
					soapHeader.setUse("literal");
					soapHeader.setNamespaceURI(url+""+m.method.getName());
					output.addExtensibilityElement(soapHeader);
				}// Body
				else{
					SOAPBody soapBody = (SOAPBody)extReg.createExtension(BindingInput.class, SOAPConstants.Q_ELEM_SOAP_BODY);
					soapBody.setUse("literal");
					soapBody.setNamespaceURI(url+""+m.method.getName());
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
		ser.setQName(new QName(tns, interf.getClass().getSimpleName()+"Service"));
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
		definitions.addAttribute("targetNamespace", url);
		definitions.addNamespace("tns", url+"?type");
		definitions.addNamespace("xsd", "http://www.w3.org/2001/XMLSchema");
		definitions.addNamespace("wsdl", "http://schemas.xmlsoap.org/wsdl/"); 
		definitions.addNamespace("SOAP-ENC", "http://schemas.xmlsoap.org/soap/encoding/");

		Element typeE = definitions.addElement("wsdl:types");	
		Element schema = typeE.addElement("xsd:schema");
		schema.addAttribute("targetNamespace", url+"?type");
		
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
				
				/*// .Net can't handle this code
				Element complexContent = type.addElement("complexContent");
				
				Element restriction = complexContent.addElement("restriction");
				restriction.addAttribute("base", "SOAP-ENC:Array");
				
				Element attribute = restriction.addElement("attribute");
				attribute.addAttribute("ref", "SOAP-ENC:arrayType");
				attribute.addAttribute("wsdl:arrayType", "tns:"+getClassSOAPName(c));
				*/
				
				Element sequence = type.addElement("xsd:sequence");
				
				Element element = sequence.addElement("xsd:element");
				element.addAttribute("minOccurs", "0");
				element.addAttribute("maxOccurs", "unbounded");
				element.addAttribute("name", "element");
				element.addAttribute("nillable", "true");
				if(SOAPObject.class.isAssignableFrom(ctmp))
					element.addAttribute("type", "tns:"+getClassSOAPName(c).replace("[]", ""));
				else
					element.addAttribute("type", "xsd:"+getClassSOAPName(c).replace("[]", ""));
				
				if(!types.contains(ctmp))
					types.add(ctmp);
			}
			// Generate SOAPObject type
			else if(SOAPObject.class.isAssignableFrom(c)){
				Element type = schema.addElement("xsd:complexType");
				type.addAttribute("name", getClassSOAPName(c));

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

	private Class<?> getClass(Class<?> c){
		if(c!=null && c.isArray()){
			return getClass(c.getComponentType());
		}
		return c;
	}
}

