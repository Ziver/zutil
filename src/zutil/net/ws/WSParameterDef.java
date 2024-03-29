/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Ziver Koc
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

import java.lang.annotation.Annotation;

/**
 * This is a web service parameter definition class
 *
 * @author Ziver
 */
public class WSParameterDef {
    /** The parent method **/
    private WSMethodDef mDef;
    /** The class type of the parameter **/
    private Class<?> paramClass;
    /** The web service name of the parameter **/
    private String name;
    /** Developer documentation **/
    private String documentation;
    /** If this parameter is optional **/
    private boolean optional = false;


    protected WSParameterDef(WSMethodDef mDef, Class<?> paramClass, Annotation[] annotations) {
        this.mDef = mDef;
        this.paramClass = paramClass;

        for (Annotation annotation : annotations) {
            if (annotation == null)
                continue;

            if (annotation instanceof WSInterface.WSParamName) {
                WSInterface.WSParamName paramNameAnnotation = (WSInterface.WSParamName) annotation;
                this.name = paramNameAnnotation.value();
                this.optional = paramNameAnnotation.optional();
            } else if (annotation instanceof WSInterface.WSReturnName) {
                WSInterface.WSReturnName returnAnnotation = (WSInterface.WSReturnName) annotation;
                this.name = returnAnnotation.value();
            } else if (annotation instanceof WSInterface.WSDocumentation) {
                WSInterface.WSDocumentation documentationAnnotation = (WSInterface.WSDocumentation) annotation;
                this.documentation = documentationAnnotation.value();
            }
        }
    }


    public Class<?> getParamClass() {
        return paramClass;
    }

    public String getName() {
        return name;
    }
    protected void setName(String name) {
        this.name = name;
    }

    public String getDocumentation() {
        return documentation;
    }
    protected void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public boolean isOptional() {
        return optional;
    }
    protected void setOptional(boolean optional) {
        this.optional = optional;
    }

    public WSMethodDef getMethod() {
        return mDef;
    }
}
