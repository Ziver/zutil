package zutil.net.ws;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import javax.wsdl.WSDLException;

public class WebServiceDef {
	/** A map of methods in this Service **/
	private HashMap<String,WSMethodDef> methods;
	/** URL of the Service **/
	//private String url;
	/** Namespace of the service **/
	//private String namespace;
	/** Name of the web service **/
	private String name;
	/** This is the WSInterface class **/
	private Class<? extends WSInterface> intf;
	
	
	public WebServiceDef(Class<? extends WSInterface> intf) throws WSDLException{
		this.intf = intf;
		methods = new HashMap<String,WSMethodDef>();
		name = intf.getSimpleName();

		for(Method m : intf.getDeclaredMethods()){
			// check for public methods
			if((m.getModifiers() & Modifier.PUBLIC) > 0 && 
					!m.isAnnotationPresent(WSInterface.WSDisabled.class)){
				WSMethodDef method = new WSMethodDef(m);
				methods.put(method.getName(), method);
			}
		}
	}
	
	/**
	 * @return the class that defines this web service
	 */
	public Class<? extends WSInterface> getWSClass(){
		return intf;
	}
	
	/**
	 * @return the name of the Service (usually the class name of the WSInterface)
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * @return a Set of all the method names
	 */
	public Set<String> getMethodNames(){
		return methods.keySet();
	}
	
	/**
	 * @return all the methods
	 */
	public Collection<WSMethodDef> getMethods(){
		return methods.values();
	}
}
