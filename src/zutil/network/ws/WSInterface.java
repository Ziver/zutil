package zutil.network.ws;

import java.lang.annotation.*;
/**
 * 
 * Specifies web service parameter names and other things. 
 * Example:
 * <pre>
 *	private static class Test implements WSInterface{
 *		public Test(){}
 *	
 *		@WSDocumentation("blabla")
 *		@WSDLParamDocumentation("olle = an variable?")
 *		public void pubZ( 
 *				@WSParamName("olle") int lol) 
 *				throws Exception{ 
 *			....
 *		}
 *	
 *		@WSReturnName("param")
 *		public String pubA( 
 *				@WSParamName(value="lol", optional=true) String lol) 
 *				throws Exception{ 
 *			....
 *		}
 *	
 *		@WSDisabled()
 *		public void privaZ(....){ 
 *			...
 *		}		
 *	}
 * 
 * </pre>
 * @author Ziver
 */
public interface WSInterface {
	/**
	 * Annotation that assigns a name to an parameters
	 * in an method.
	 * 
	 * @author Ziver
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.PARAMETER)
	public @interface WSParamName {
	    String value();
	    boolean optional() default false;
	}
	
	/**
	 * Annotation that assigns a name to the return value
	 * in an method.
	 * 
	 * @author Ziver
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface WSReturnName {
	    String value();
	}
	
	/**
	 * Disables publication of the given method
	 * 
	 * @author Ziver
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface WSDisabled { }
	
	/**
	 * Method or Parameter comments for the WSDL. 
	 * These comments are put in the message part of the WSDL
	 * 
	 * @author Ziver
	 */
	@Retention(RetentionPolicy.RUNTIME)
	public @interface WSDocumentation{
		String value();
	}

	/**
	 * Parameter comments for the WSDL. 
	 * These comments are put in the message part of the WSDL
	 * 
	 * @author Ziver
	 */
	@Retention(RetentionPolicy.RUNTIME)
	public @interface WSParamDocumentation{
		String value();
	}
	
	/**
	 * This method will be used in the header.
	 * 
	 * @author Ziver
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface WSHeader { }
	
	/**
	 * Specifies the name space for the method.
	 * 
	 * @author Ziver
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface WSNamespace {
		String value();
	}
}
