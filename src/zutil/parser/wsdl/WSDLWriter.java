package zutil.parser.wsdl;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.logging.Logger;

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
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.namespace.QName;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.ibm.wsdl.extensions.PopulatedExtensionRegistry;
import com.ibm.wsdl.extensions.soap.SOAPConstants;
import com.sun.org.apache.xerces.internal.dom.DocumentImpl;

import zutil.log.LogUtil;
import zutil.network.ws.WSMethodDef;
import zutil.network.ws.WSObject;
import zutil.network.ws.WSParameterDef;
import zutil.network.ws.WebServiceDef;
import zutil.network.ws.WSObject.WSFieldName;

public class WSDLWriter{
	private static final Logger logger = LogUtil.getLogger();
	
	private WebServiceDef ws;
	private StringBuilder cache;
	private OutputStream out;

	
	public void write(WebServiceDef object) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Generates an WSDL document for the class
	 * 
	 * @throws WSDLException
	 */
	private void generateWSDL(String url) throws WSDLException{
		ArrayList<Class<?>> types = new ArrayList<Class<?>>();
		
		String tns = url+"?wsdl";
		String xsd = "http://www.w3.org/2001/XMLSchema";
		String soap = "http://schemas.xmlsoap.org/wsdl/soap/";
		String wsdln = "http://schemas.xmlsoap.org/wsdl/";
		String td = url+"?type";

		PopulatedExtensionRegistry extReg = new PopulatedExtensionRegistry();
		WSDLFactory factory = WSDLFactory.newInstance();
		String portTypeName = ws.getName()+"PortType";

		Definition wsdl = factory.newDefinition();
		wsdl.setQName(new QName(tns, ws.getName()));
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
		for(WSMethodDef method : ws.getMethods()){
			Operation operation = wsdl.createOperation();			
			//********* Request Messages
			if(method.inputCount() > 0){
				Message msgIn = wsdl.createMessage();
				msgIn.setQName(new QName(tns, method.getName()+"Request"));
				msgIn.setUndefined(false);

				//***** Documentation
				if(method.getDocumentation() != null){
					org.w3c.dom.Document xmldoc= new DocumentImpl();
					org.w3c.dom.Element paramDoc = xmldoc.createElement("wsdl:documentation");
					paramDoc.setTextContent( method.getDocumentation() );
					msgIn.setDocumentationElement(paramDoc);
				}

				// Parameters
				for(WSParameterDef param : method.getInputs()){
					// Parts
					Part part = wsdl.createPart();
					part.setName(param.getName());
					part.setTypeName(new QName( xsd, 
							getClassSOAPName( param.getParamClass() )));
					if( param.isOptional() )
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
			if( method.outputCount() > 0 ){
				Message msgOut = wsdl.createMessage();
				msgOut.setQName(new QName(tns, method.getName()+"Response"));
				msgOut.setUndefined(false);
				
				for( WSParameterDef param : method.getOutputs() ){					
					// Parts
					Part part = wsdl.createPart();
					part.setName( param.getName() );
					msgOut.addPart(part);
					
					Class<?> paramClass = param.getParamClass();
					Class<?> valueClass = getClass( paramClass );
					// is an binary array
					if(byte[].class.isAssignableFrom( paramClass )){
						part.setTypeName(new QName(xsd, "base64Binary"));
					}
					// is an array?
					else if( paramClass.isArray()){
						part.setTypeName(new QName(td, 
								"ArrayOf"+getClassSOAPName( paramClass ).replaceAll("[\\[\\]]", "")));
						// add to type generation list
						if(!types.contains( paramClass ))
							types.add( paramClass );
					}
					else if( WSObject.class.isAssignableFrom(valueClass) ){					
						// its an SOAPObject
						part.setTypeName(new QName(td, getClassSOAPName( paramClass )));
						// add to type generation list
						if(!types.contains(valueClass))
							types.add(valueClass);
					}
					else{// its an Object 
						part.setTypeName(new QName(xsd, getClassSOAPName( paramClass )));
					}
				}

				wsdl.addMessage(msgOut);				
				Output output = wsdl.createOutput();			
				output.setMessage(msgOut);			
				operation.setOutput(output);			
			}
			//************* Exceptions	
			if( method.exceptionCount() > 0){
				Fault fault = wsdl.createFault();
				fault.setMessage(exception);
				operation.addFault(fault);
			}
			//************* Operations			
			operation.setName(method.getName());			
			operation.setUndefined(false);			

			//***** Documentation
			if(method.getDocumentation() != null){
				// <!-- example -->
				org.w3c.dom.Document xmldoc= new DocumentImpl();
				org.w3c.dom.Element doc = xmldoc.createElement("wsdl:documentation");
				doc.setTextContent( method.getDocumentation() );
				operation.setDocumentationElement(doc);
			}

			portType.addOperation(operation);
		}
		wsdl.addPortType(portType);

		// Binding
		Binding binding = wsdl.createBinding();
		binding.setQName(new QName(tns, ws.getName()+"Binding"));
		binding.setPortType(portType);
		binding.setUndefined(false);

		SOAPBinding soapBinding = (SOAPBinding)extReg.createExtension(Binding.class, SOAPConstants.Q_ELEM_SOAP_BINDING);
		soapBinding.setStyle("rpc");
		//soapBinding.setRequired(true);
		soapBinding.setTransportURI("http://schemas.xmlsoap.org/soap/http");
		binding.addExtensibilityElement(soapBinding);

		for(WSMethodDef method : ws.getMethods()){
			BindingOperation operation = wsdl.createBindingOperation();
			operation.setName(method.getName());

			SOAPOperation soapOperation = (SOAPOperation)extReg.createExtension(BindingOperation.class, SOAPConstants.Q_ELEM_SOAP_OPERATION);
			soapOperation.setSoapActionURI(url+""+method.getName());
			operation.addExtensibilityElement(soapOperation);

			// input
			BindingInput input = wsdl.createBindingInput();
			// TODO: Header
			/*if(m.header){
				SOAPHeader soapHeader = (SOAPHeader)extReg.createExtension(BindingInput.class, SOAPConstants.Q_ELEM_SOAP_HEADER);
				soapHeader.setUse("literal");
				soapHeader.setNamespaceURI(url+""+method.getName());
				input.addExtensibilityElement(soapHeader);
			}*/
			// Body
			//else{
				SOAPBody soapBody = (SOAPBody)extReg.createExtension(BindingInput.class, SOAPConstants.Q_ELEM_SOAP_BODY);
				soapBody.setUse("literal");
				soapBody.setNamespaceURI(url+""+method.getName());
				input.addExtensibilityElement(soapBody);
			//}
			operation.setBindingInput(input);

			// output
			if( method.outputCount() > 0 ){
				BindingOutput output = wsdl.createBindingOutput();
				// TODO: Header
				/*if(m.header){
					SOAPHeader soapHeaderBind = (SOAPHeader)extReg.createExtension(BindingInput.class, SOAPConstants.Q_ELEM_SOAP_HEADER);
					soapHeaderBind("literal");
					soapHeaderBind(url+""+method.getName());
					output.addExtensibilityElement(soapHeaderBind);
				}*/
				// Body
				//else{
					SOAPBody soapBodyBind = (SOAPBody)extReg.createExtension(BindingInput.class, SOAPConstants.Q_ELEM_SOAP_BODY);
					soapBodyBind.setUse("literal");
					soapBodyBind.setNamespaceURI(url+""+method.getName());
					output.addExtensibilityElement(soapBodyBind);
				//}
				operation.setBindingOutput(output);
			}

			binding.addBindingOperation(operation);
		}
		wsdl.addBinding(binding);

		// Service
		Port port = wsdl.createPort();
		port.setName( ws.getName()+"Port" );
		port.setBinding(binding);
		SOAPAddress addr = (SOAPAddress)extReg.createExtension(Port.class, SOAPConstants.Q_ELEM_SOAP_ADDRESS);
		addr.setLocationURI(url);
		port.addExtensibilityElement(addr);

		Service ser = wsdl.createService();
		ser.setQName(new QName(tns, ws.getName()+"Service"));
		ser.addPort(port);
		wsdl.addService(ser);

		// generate the complexTypes
		generateWSDLType(url, types);
	}

	/**
	 * This function generates the Type part of the WSDL.
	 * Should be cabled after generateWSDL has finished.
	 * 
	 */
	private void generateWSDLType(String url, ArrayList<Class<?>> types){
		Document wsdlType = DocumentHelper.createDocument();
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
								
				Element sequence = type.addElement("xsd:sequence");
				
				Element element = sequence.addElement("xsd:element");
				element.addAttribute("minOccurs", "0");
				element.addAttribute("maxOccurs", "unbounded");
				element.addAttribute("name", "element");
				element.addAttribute("nillable", "true");
				if(WSObject.class.isAssignableFrom(ctmp))
					element.addAttribute("type", "tns:"+getClassSOAPName(c).replace("[]", ""));
				else
					element.addAttribute("type", "xsd:"+getClassSOAPName(c).replace("[]", ""));
				
				if(!types.contains(ctmp))
					types.add(ctmp);
			}
			// Generate SOAPObject type
			else if(WSObject.class.isAssignableFrom(c)){
				Element type = schema.addElement("xsd:complexType");
				type.addAttribute("name", getClassSOAPName(c));

				Element sequence = type.addElement("xsd:sequence");

				Field[] fields = c.getFields();
				for(int i=0; i<fields.length ;i++){
					WSFieldName tmp = fields[i].getAnnotation(WSObject.WSFieldName.class);
					String name;
					if(tmp != null) name = tmp.value();
					else name = "field"+i;

					Element element = sequence.addElement("xsd:element");
					element.addAttribute("name", name);

					// Check if the object is an SOAPObject
					Class<?> cTmp = getClass(fields[i].getType());
					if(WSObject.class.isAssignableFrom(cTmp)){
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
	
	private String getClassSOAPName(Class<?> c){
		Class<?> cTmp = getClass(c);
		if(byte[].class.isAssignableFrom(c)){
			return "base64Binary";
		}
		else if( WSObject.class.isAssignableFrom(cTmp) ){
			return c.getSimpleName();
		}
		else{
			String ret = c.getSimpleName().toLowerCase();
			
			if(cTmp == Integer.class) 		ret = ret.replaceAll("integer", "int");
			else if(cTmp == Character.class)ret = ret.replaceAll("character", "char");
			
			return ret;
		}
	}
	
	public void close() {}

}
