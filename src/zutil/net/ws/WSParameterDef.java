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

/**
 * This is a web service parameter definition class
 * 
 * @author Ziver
 */
public class WSParameterDef{
    /** The parent method **/
    private WSMethodDef mDef;
    /** The class type of the parameter **/
    private Class<?> paramClass;
    /** The web service name of the parameter **/
    private String name;
    /** Developer documentation **/
    private String doc;
    /** If this parameter is optional **/
    private boolean optional;
    /** Is it an header parameter **/
    //boolean header;

    protected WSParameterDef( WSMethodDef mDef ){
        this.mDef = mDef;
        this.optional = false;
    }


    public Class<?> getParamClass() {
        return paramClass;
    }
    protected void setParamClass(Class<?> paramClass) {
        this.paramClass = paramClass;
    }

    public String getName() {
        return name;
    }
    protected void setName(String name) {
        this.name = name;
    }

    public String getDoc() {
        return doc;
    }
    protected void setDoc(String doc) {
        this.doc = doc;
    }

    public boolean isOptional() {
        return optional;
    }
    protected void setOptional(boolean optional) {
        this.optional = optional;
    }

    public WSMethodDef getMethod(){
        return mDef;
    }
}
