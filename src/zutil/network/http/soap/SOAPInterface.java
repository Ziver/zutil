package zutil.network.http.soap;

import java.lang.annotation.*;

/**
 * 
 * Specifies SOAP parameters names an other things. 
 * Example:
 * <pre>
 *	private static class Test implements SOAPInterface{
 *		public Test(){}
 *	
 *		@SOAPHeader()
 *		@WSDLDocumentation("blabla")
 *		@WSDLParamDocumentation("olle = an variable?")
 *		public void pubZ( 
 *				@SOAPParamName("olle") int lol) 
 *				throws Exception{ 
 *			....
 *		}
 *	
 *		@SOAPReturnName("param")
 *		public String pubA( 
 *				@SOAPParamName(value="lol", optional=true) String lol) 
 *				throws Exception{ 
 *			....
 *		}
 *	
 *		@SOAPDisabled()
 *		public void privaZ(....){ 
 *			...
 *		}		
 *	}
 * 
 * </pre>
 * @author Ziver
 */
public interface SOAPInterface {
	/**
	 * Annotation that assigns a name to an parameters
	 * in an method.
	 * 
	 * @author Ziver
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.PARAMETER)
	public @interface SOAPParamName {
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
	public @interface SOAPReturnName {
	    String value();
	}
	
	/**
	 * Disables SOAP publication of the given method
	 * 
	 * @author Ziver
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface SOAPDisabled { }
	
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
	 * Parameter comments for the WSDL. 
	 * These comments are put in the message part of the WSDL
	 * 
	 * @author Ziver
	 */
	@Retention(RetentionPolicy.RUNTIME)
	public @interface WSDLParamDocumentation{
		String value();
	}
	
	/**
	 * This method will be used in the header of the soap.
	 * 
	 * @author Ziver
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface SOAPHeader { }
}
