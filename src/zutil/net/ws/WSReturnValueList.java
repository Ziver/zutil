package zutil.net.ws;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

/**
 * This interface is for returning multiple object.
 * All the public fields in the class that implements 
 * this class will be set as return values. And the 
 * implementing class will be transparent.
 * 
 * @author Ziver
 *
 */
public class WSReturnValueList {
	
	/**
	 * Method comments for the WSDL. 
	 * These comments are put in the operation part of the WSDL
	 * 
	 * @author Ziver
	 */
	@Retention(RetentionPolicy.RUNTIME)
	public @interface WSDLDocumentation{
		String value();
	}
	
	/**
	 * Disables publication of the given field.
	 * 
	 * @author Ziver
	 */
	/*@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface SOAPDisabledValue { }*/
	
	/**
	 * Annotation that assigns a name to the return value
	 * to the field.
	 * 
	 * @author Ziver
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface WSValueName {
	    String value();
	}
	
	
	public Object getValue(Field field) throws IllegalArgumentException, IllegalAccessException{
		return field.get(this);
	}
}

