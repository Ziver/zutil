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
package zutil.net.ws;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This class is used as an return Object for a web service.
 * If an class implements this interface then it can return 
 * multiple values through the SOAPInterface. Example:
 * <pre>
 * 	private static class TestObject implements WSObject{
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
public interface WSObject{
	/**
	 * Specifies the name of a field.
	 * The fields that are available in the service should
	 * be declared public.
	 * 
	 * @author Ziver
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface WSFieldName {
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
