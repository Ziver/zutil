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
/**
 *
 * Specifies web service definitions. Which methods that will
 * be published and other related metadata.
 * Only public and non static methods will be published or
 * the WSIgnore annotation can be used to ignore the specific method.
 *
 * Example:
 * <pre>
 *	private static class Test implements WSInterface{
 *		public Test(){}
 *
 *		&#64;WSDocumentation("This is a description of the method")
 *		&#64;WSDLParamDocumentation("arg1 = variable description?")
 *		public void pubZ(
 *				&#64;WSParamName("arg1") int randomName)
 *				throws Exception{
 *			....
 *		}
 *
 *		&#64;WSReturnName("param")
 *		public String pubA(
 *				&#64;WSParamName(value="optArg", optional=true) String optionalParam)
 *				throws Exception{
 *			....
 *		}
 *
 *		&#64;WSIgnore()
 *		public void privatZ(....){
 *			...
 *		}
 *	}
 *
 * </pre>
 * @author Ziver
 */
public interface WSInterface {

    enum RequestType {
        HTTP_GET,
        HTTP_POST,
        HTTP_PUT,
        HTTP_DELETE
    }


    /**
     * Annotation that assigns a name to an parameters
     * in an method.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @interface WSParamName {
        String value();
        boolean optional() default false;
    }

    /**
     * Annotation that assigns a name to the return value
     * in an method.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface WSReturnName {
        String value();
    }

    /**
     * Skipp publication of the given method
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface WSIgnore { }

    /**
     * Method or Parameter comments for the WSDL.
     * These comments are put in the message part of the WSDL
     */
    @Retention(RetentionPolicy.RUNTIME)
    @interface WSDocumentation{
        String value();
    }

    /**
     * Parameter comments for the WSDL.
     * These comments are put in the message part of the WSDL
     */
    @Retention(RetentionPolicy.RUNTIME)
    @interface WSParamDocumentation{
        String value();
    }

    /**
     * This method will be used in the header.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface WSHeader { }

    /**
     * Specifies the name space for the method.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @interface WSNamespace {
        String value();
    }

    /**
     * Specifies the request type.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface WSRequestType {
        RequestType value();
    }

    /**
     * Specifies the specific path for the method overriding the auto generated path.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface WSPath {
        String value();
    }
}
