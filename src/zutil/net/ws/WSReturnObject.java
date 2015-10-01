/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Ziver Koc
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
import java.lang.reflect.Field;

/**
 * This class is used as an return Object for a web service.
 * If an class implements this interface then it can return 
 * multiple values through the WSInterface. And the 
 * implementing class will be transparent. Example:
 * 
 * <pre>
 * 	private static class TestObject implements WSReturnObject{
 *		@WSValueName("name")
 *		public String name;
 *		@WSValueName("lastname")
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
public class WSReturnObject{
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
	 * Annotation that assigns a name to the return value
	 * to the field.
	 * 
	 * @author Ziver
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface WSValueName {
	    String value();
	    boolean optional() default false;
	}
	
	
	public Object getValue(Field field) throws IllegalArgumentException, IllegalAccessException{
		return field.get(this);
	}
}
