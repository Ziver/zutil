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
package zutil.net.http.soap;

import javax.wsdl.WSDLException;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import zutil.net.ws.WSInterface;
import zutil.net.ws.WSMethodDef;
import zutil.net.ws.WebServiceDef;

/**
 * This is an factory that generates clients for web services
 * 
 * @author Ziver
 * TODO: Incomplete
 */
public class SOAPClientFactory {
	
	/**
	 * Generates a Client Object for the web service.
	 * 
	 * @param <T> is the class of the web service definition
	 * @param intf is the class of the web service definition
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
	 * @param <T> is the class of the web service definition
	 * @param intf is the class of the web service definition
	 * @param wsDef is the web service definition of the intf parameter
	 * @return a client Object
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getClient(Class<T> intf, WebServiceDef wsDef) throws InstantiationException, IllegalAccessException, CannotCompileException, NotFoundException{
		if( !WSInterface.class.isAssignableFrom( intf )){
			throw new ClassCastException("The Web Service class is not a subclass of WSInterface!");
		}
		
		// Generate the class
		ClassPool pool = ClassPool.getDefault();
		CtClass cc = pool.makeClass(intf.getName()+"Impl_"+Math.random());
		
		CtClass intfClass = pool.get( intf.getName() );
		
		// Is intf an interface
		if( intf.isInterface() ){
			cc.addInterface( intfClass );
		}
		// or a class
		else{
			cc.setSuperclass( intfClass );
		}
		
		// Generate the methods
		// TODO:
		for(WSMethodDef methodDef : wsDef.getMethods()){
			CtMethod method = CtNewMethod.make("public int m(int i){}", cc);
			method.insertBefore("System.out.println(\"Hello.say():\");");
		}
		
		// Initiate the class
		Class<T> c = cc.toClass();
        T obj = c.newInstance();
        
		return obj;		
	}
}