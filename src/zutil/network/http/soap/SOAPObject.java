package zutil.network.http.soap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This class is used as an return Object for SOAP.
 * If an class implements this interface then it can return 
 * multiple values through the SOAPInterface. Example:
 * <pre>
 * 	private static class TestObject implements SOAPObject{
 *		@SOAPFieldName("name")
 *		public String name;
 *		@SOAPFieldName("lastname")
 *		public String lastname;
 *
 *		public TestObject(String n, String l){
 *			name = n;
 *			lastname = l;
 *		}
 *	}
 * </pre>
 * 
 * @author Ziver
 *
 */
public interface SOAPObject{
	/**
	 * Specifies the SOAP name of an field.
	 * The fields that are available for SOAP should
	 * be declared public.
	 * 
	 * @author Ziver
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface SOAPFieldName {
		String value();
		boolean optional() default false;
	}
	
	/**
	 * This generates an documentation tag in the
	 * WSDL for the object type
	 * 
	 * @author Ziver
	 */
	/*
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface WSDLDocumentation {
		String value();
	}*/
}