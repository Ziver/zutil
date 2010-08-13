package zutil.network.http.soap;

import javax.wsdl.WSDLException;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import zutil.network.ws.WSInterface;
import zutil.network.ws.WSMethodDef;
import zutil.network.ws.WebServiceDef;

/**
 * This is an factory that generates clients for web services
 * 
 * @author Ziver
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
