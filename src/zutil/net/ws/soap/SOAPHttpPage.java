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
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.xml.sax.SAXException;
import zutil.converter.Converter;
import zutil.log.LogUtil;
import zutil.net.http.HttpHeader;
import zutil.net.http.HttpPage;
import zutil.net.http.HttpPrintStream;
import zutil.net.ws.*;
import zutil.net.ws.WSReturnObject.WSValueName;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is an HTTPPage for the HTTPServer that 
 * handles soap messages.
 * 
 * TODO: Header should be variables not methods
 * TODO: Read WSReturnObjects as input parameter
 * TODO: Ability to have multiple arrays of same WSReturnObject
 * 
 * Features:
 * Input:
 * <br>-int
 * <br>-double
 * <br>-float
 * <br>-char
 * <br>-String
 * <br>-byte[]
 * <br>-And the Wrappers Classes except for Byte
 * 
 * Output:
 * <br>-WSReturnObject
 * <br>-byte[]
 * <br>-int
 * <br>-double
 * <br>-float
 * <br>-char
 * <br>-String
 * <br>-Arrays of Output
 * <br>-And the Wrappers Classes except for Byte
 * 
 * @author Ziver
 */
public class SOAPHttpPage implements HttpPage{
	private static final Logger logger = LogUtil.getLogger();
	
	/** The object that the functions will be invoked from **/
	private WebServiceDef wsDef;
	/** This instance of the web service class is used if session is disabled **/
	private WSInterface ws;
	/** Session enabled **/
	private boolean session_enabled;

	public SOAPHttpPage( WebServiceDef wsDef ){
		this.wsDef = wsDef;
		this.session_enabled = false;
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

	/**
	 * Sets the web service object to the specified one.
	 * Only used when session is disabled
	 */
	public void setObject(WSInterface obj) {
		this.ws = obj;		
	}
	

	public void respond(HttpPrintStream out,
			HttpHeader headers,
			Map<String, Object> session, 
			Map<String, String> cookie,
			Map<String, String> request) {

		try {
			out.setHeader("Content-Type", "text/xml");
			out.flush();

            WSInterface obj = null;
            if(session_enabled){
                if( session.containsKey("SOAPInterface"))
                    obj = (WSInterface)session.get("SOAPInterface");
                else{
                    obj = wsDef.newInstance();
                    session.put("SOAPInterface", obj);
                }
            }
            else{
                if( ws == null )
                    ws = wsDef.newInstance();
                obj = ws;
            }

            Document document = genSOAPResponse( request.get(""), obj);

			OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter( out, format );
            writer.write( document );


            // DEBUG
            if( logger.isLoggable(Level.FINEST) ){
                System.out.println("********** Request");
                System.out.println(request);
                System.out.println("********** Response");
                writer = new XMLWriter( System.out, format );
                writer.write( document );
            }
		} catch (Exception e) {
			logger.log(Level.WARNING, "Unhandled request", e);
		}
	}
	
	/**
	 * Generates a soap response for the given XML
	 * 
	 * @param		xml 	is the XML request
	 * @return 				a Document with the response
	 */
	public Document genSOAPResponse(String xml){
		try {
			WSInterface obj = null;
			if( ws == null ) 
				ws = wsDef.newInstance();
			obj = ws;
			
			return genSOAPResponse(xml, obj );
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception in SOAP generation", e);
		}
		return null;
	}

	protected Document genSOAPResponse(String xml, WSInterface obj){
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
				if(e instanceof SOAPException || e instanceof SAXException || e instanceof DocumentException)
					fault.addElement("faultcode").setText( "soap:Client" );
				else 
					fault.addElement("faultcode").setText( "soap:Server" );
				// The fault message
				if( e.getMessage() == null || e.getMessage().isEmpty()) 
					fault.addElement("faultstring").setText( ""+e.getClass().getSimpleName() );
				else
					fault.addElement("faultstring").setText( ""+e.getMessage() );
				logger.log(Level.WARNING, "Caught exception from SOAP Class", e);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception in SOAP generation", e);
		}

		return document;
	}
	
	/**
	 * Converts an String XML to an Element
	 * 
	 * @param 		xml 		is the string XML
	 * @return 					the XML root Element
	 */
	protected static Element getXMLRoot(String xml) throws DocumentException {
		if(xml != null && !xml.isEmpty()){
			Document document = DocumentHelper.parseText(xml);
			return document.getRootElement();
		}
		return null;
	}

	/**
	 * Takes an XML Element and invokes all the it's child elements as methods.
	 * 
	 * @param 		obj				is the object that the methods will be called from
	 * @param 		requestRoot 	is the Element where the children lies
	 * @param 		responseRoot	is the root element of the response
	 */
	@SuppressWarnings("unchecked")
	private void prepareInvoke(WSInterface obj, Element requestRoot, Element responseRoot) throws Throwable{
		Iterator<Element> it = requestRoot.elementIterator();
		while( it.hasNext() ){
			Element e = it.next();
			if( wsDef.hasMethod( e.getQName().getName()) ){
				WSMethodDef m = wsDef.getMethod( e.getQName().getName() );
				Object[] params = new Object[ m.getInputCount() ];

				// Get the parameter values
				for(int i=0; i<m.getInputCount() ;i++){
					WSParameterDef param = m.getInput( i );
					if( e.element(param.getName()) != null ){
						params[i] = Converter.fromString(
								e.element(param.getName()).getTextTrim(),
								param.getParamClass());
                    }
				}
				
				// Invoke
				Object ret = m.invoke(obj, params);

				// generate response XML
				if( m.getOutputCount()>0 ){
					Element response = responseRoot.addElement("");
					response.addNamespace("m",  m.getNamespace() );
					response.setName("m:"+m.getName()+"Response");
					
					if( ret instanceof WSReturnObject ){
						Field[] f = ret.getClass().getFields();
						for(int i=0;  i<m.getOutputCount() ;i++){
							WSParameterDef param = m.getOutput( i );
							generateSOAPXMLForObj(response,((WSReturnObject)ret).getValue(f[i]) , param.getName());
						}
					}
					else{
						generateSOAPXMLForObj(response, ret, m.getOutput(0).getName());
					}
				}
			}
			else{
				throw new NoSuchMethodException("Unable to find method: "+e.getQName().getName()+"!");
			}
		}
	}





	/**
	 * Generates a XML Element for a given Object. This method can
	 * handle return values as XML Elements, WSReturnObject and
	 * Java basic data types.
	 * 
	 * @param		root 		is the parent Element
	 * @param 		obj 		is the object that is the return value
	 * @param 		elementName 		is the name of the parent Element
	 */
	protected static void generateSOAPXMLForObj(Element root, Object obj, String elementName) throws IllegalArgumentException, IllegalAccessException{
		if(obj == null) return;
		if(byte[].class.isAssignableFrom(obj.getClass())){
			Element valueE = root.addElement( elementName );
			valueE.addAttribute("type", "xsd:"+ getSOAPClassName(obj.getClass()));
			String tmp = new sun.misc.BASE64Encoder().encode((byte[])obj);
			tmp = tmp.replaceAll("\\s", "");
			valueE.setText(tmp);
		}
		// return an array
		else if(obj.getClass().isArray()){
			Element array = root.addElement( (elementName.equals("element") ? "Array" : elementName) );
			String arrayType = "xsd:"+ getSOAPClassName(obj.getClass());
			arrayType = arrayType.replaceFirst("\\[\\]", "["+Array.getLength(obj)+"]");

			array.addAttribute("type", "soap:Array");
			array.addAttribute("soap:arrayType", arrayType);
			for(int i=0; i<Array.getLength(obj) ;i++){
				generateSOAPXMLForObj(array, Array.get(obj, i), "element");
			}
		}		
		else{
			Element objectE = root.addElement( elementName );
			if(obj instanceof Element)
				objectE.add( (Element)obj );
			else if(obj instanceof WSReturnObject){
				Field[] fields = obj.getClass().getFields();
				for(int i=0; i<fields.length ;i++){
					WSValueName tmp = fields[i].getAnnotation( WSValueName.class );
					String name;
					if(tmp != null) name = tmp.value();
					else name = "field"+i;
					generateSOAPXMLForObj(objectE, fields[i].get(obj), name);
				}
			}
			else {
				objectE.addAttribute("type", "xsd:"+ getSOAPClassName(obj.getClass()));
				objectE.addText( ""+obj );
			}
		}
	}

	
	protected static String getSOAPClassName(Class<?> c){
		Class<?> cTmp = getClass(c);
		if(byte[].class.isAssignableFrom(c)){
			return "base64Binary";
		}
		else if( WSReturnObject.class.isAssignableFrom(cTmp) ){
			return c.getSimpleName();
		}
		else{
			String ret = c.getSimpleName().toLowerCase();
			
			if(cTmp == Integer.class) 		ret = ret.replaceAll("integer", "int");
			else if(cTmp == Character.class)ret = ret.replaceAll("character", "char");
			
			return ret;
		}
	}

	protected static Class<?> getClass(Class<?> c){
		if(c!=null && c.isArray()){
			return getClass(c.getComponentType());
		}
		return c;
	}
}

