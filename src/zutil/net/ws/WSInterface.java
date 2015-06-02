/*
 * Copyright (c) 2015 ezivkoc
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

package zutil.net.ws;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
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
	 * Specifies the name space for method.
	 * 
	 * @author Ziver
	 */
	@Retention(RetentionPolicy.RUNTIME)
	//@Target(ElementType.TYPE)
	public @interface WSNamespace {
		String value();
	}
}
