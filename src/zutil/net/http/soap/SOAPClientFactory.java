/*******************************************************************************
 * Copyright (c) 2013 Ziver Koc
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
package zutil.net.http.soap;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.wsdl.WSDLException;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import zutil.log.LogUtil;
import zutil.net.ws.WSInterface;
import zutil.net.ws.WSMethodDef;
import zutil.net.ws.WSParameterDef;
import zutil.net.ws.WebServiceDef;

/**
 * This is an factory that generates clients for web services
 * 
 * @author Ziver
 */
public class SOAPClientFactory {
	private static Logger logger = LogUtil.getLogger();
	
	/**
	 * Generates a Client Object for the web service.
	 * 
	 * @param 	<T> 	is the class of the web service definition
	 * @param 	intf 	is the class of the web service definition
	 * @return a client Object
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getClient(Class<T> intf) throws InstantiationException, IllegalAccessException, CannotCompileException, NotFoundException, WSDLException{
		if( !WSInterface.class.isAssignableFrom( intf )){
			throw new ClassCastException("The Web Service class is not a subclass of WSInterface!");
		}
		return getClient( intf, new WebServiceDef((Class<? extends WSInterface>)intf) );
	}
	
	/**
	 * Generates a Client Object for the web service.
	 * 
	 * @param 	<T> 	is the class of the web service definition
	 * @param 	intf 	is the class of the web service definition
	 * @param 	wsDef 	is the web service definition of the intf parameter
	 * @return a client Object
	 */
	public static <T> T getClient(Class<T> intf, WebServiceDef wsDef) throws InstantiationException, IllegalAccessException, CannotCompileException, NotFoundException{
		if( !WSInterface.class.isAssignableFrom( intf )){
			throw new ClassCastException("The Web Service class is not a subclass of WSInterface!");
		}
		
		// Generate the class
		ClassPool pool = ClassPool.getDefault();
		CtClass intfClass = pool.get( intf.getName() );
		CtClass cc = pool.makeClass(intf.getName()+"Impl_"+ (int)(Math.random()*10000));
		
		// Is intf an interface
		if( intf.isInterface() ){
			cc.addInterface( intfClass );
		}
		// or a class
		else{
			cc.setSuperclass( intfClass );
		}
		
		// Add the logger class
		CtField logger = CtField.make(
				"private static "+Logger.class.getName()+" logger = "+LogUtil.class.getName()+".getLogger();", 
				cc);
		cc.addField(logger);
		
		// Generate the methods
		for(WSMethodDef methodDef : wsDef.getMethods()){			
			// Create method
			CtMethod method = CtNewMethod.make(
									getOutputClass(methodDef.getOutputs()),			// Return type 
									methodDef.getName(),							// Method name
									getParameterClasses(methodDef.getInputs()),		// Parameters
									new CtClass[]{pool.get( SOAPException.class.getName() )},	// Exceptions 
									generateCodeBody(methodDef),					// Code Body 
									cc); 											// Class
			cc.addMethod(method);
		}
		
		// Initiate the class
		@SuppressWarnings("unchecked")
		Class<T> c = cc.toClass();
        T obj = c.newInstance();
        
		return obj;		
	}
	
	/**
	 * Generates a generic method code body that calls the SOAPAbstractClient class
	 */
	private static String generateCodeBody(WSMethodDef m) {
		logger.finer("Generating method "+m.getName()+"(...)");
		
		StringBuilder body = new StringBuilder("{\n");
		// Logging
		body.append( "logger.fine(\"Executing method: "+m.getName()+"(...)\");\n" );
		
		// Generate parameter list
		body.append( HashMap.class.getName()+"<String,Object> params = new "+HashMap.class.getName()+"<String,Object>();\n" );
		for(WSParameterDef param : m.getInputs()){
			body.append( "params.put(\""+param.getName()+"\", "+param.getName()+");\n");
		}
		
		// Call SOAPAbstractClient class
		if(m.getOutputCount() > 0) // non void function
			body.append( "return " );
		body.append( SOAPAbstractClient.class.getName()+".request(\""+m.getName()+"\", params);\n" );
		
		body.append("}");
		logger.finest("######################  BODY  #########################");
		logger.finest(body.toString());
		logger.finest("#######################################################");
		return body.toString();
	}

	private static CtClass getParameterClass(WSParameterDef param) throws NotFoundException{
		return ClassPool.getDefault().get( param.getClass().getName() );
	}
	
	private static CtClass[] getParameterClasses(List<WSParameterDef> params) throws NotFoundException{
		CtClass[] c = new CtClass[params.size()];
		
		int i = 0;
		for(WSParameterDef param : params){
			c[i++] = getParameterClass(param);
		}
		
		return c;
	}
	
	private static CtClass getOutputClass(List<WSParameterDef> params) throws NotFoundException{
		if(params.isEmpty()){
			return CtClass.voidType;
		}
		else if(params.size() == 1){
			return ClassPool.getDefault().get( params.get(0).getClass().getName() );
		}
		throw new IllegalArgumentException("Unknown return type");
	}
}
