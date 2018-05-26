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

import zutil.net.ws.WSInterface.WSDocumentation;
import zutil.net.ws.WSInterface.WSNamespace;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a web service method definition class
 *
 * @author Ziver
 */
// TODO: Header parameters
public class WSMethodDef {
    /** The parent web service definition **/
    private WebServiceDef wsDef;
    /** A list of input parameters **/
    private ArrayList<WSParameterDef> inputs;
    /** A List of return parameters of the method **/
    private ArrayList<WSParameterDef> outputs;
    /** A List of exceptions that this method throws **/
    private ArrayList<Class<?>> exceptions;
    /** The real method that this class represent, can be null if its a remote method **/
    private Method method;
    /** Documentation of the method **/
    private String doc;
    /** This is the namespace of the method **/
    private String namespace;
    /** The published name of the method **/
    private String name;


    /**
     *
     * @param me is a method in a class that implements WSInterface
     */
    protected WSMethodDef( WebServiceDef wsDef, Method me) {
        if( !WSInterface.class.isAssignableFrom(me.getDeclaringClass()) )
            throw new ClassCastException("Declaring class does not implement WSInterface!");
        this.wsDef = wsDef;
        method = me;
        inputs = new ArrayList<WSParameterDef>();
        outputs = new ArrayList<WSParameterDef>();
        exceptions = new ArrayList<Class<?>>();
        name = method.getName();

        //***** Documentation & Namespace
        WSDocumentation tmpDoc = method.getAnnotation( WSDocumentation.class );
        if(tmpDoc != null){
            doc = tmpDoc.value();
        }
        WSNamespace tmpSpace = method.getAnnotation( WSNamespace.class );
        if( tmpSpace != null )
            namespace = tmpSpace.value();
        else
            namespace = wsDef.getNamespace()+"?#"+name;

        //***** Exceptions
        for( Class<?> exc : method.getExceptionTypes() ){
            exceptions.add( exc );
        }

        //********* Get the input parameter names **********
        Annotation[][] paramAnnotation = method.getParameterAnnotations();
        Class<?>[] inputTypes = method.getParameterTypes();

        for(int i=0; i<paramAnnotation.length ;i++){
            WSParameterDef param = new WSParameterDef( this );
            for(Annotation annotation : paramAnnotation[i]){
                if(annotation instanceof WSInterface.WSParamName){
                    WSInterface.WSParamName paramName = (WSInterface.WSParamName) annotation;
                    param.setName( paramName.value() );
                    param.setOptional( paramName.optional() );
                }
            }
            param.setParamClass( inputTypes[i] );
            // if no name was found then use default
            if(param.getName() == null)
                param.setName( "args"+i );

            inputs.add( param );
        }

        //********  The return parameter name ************
        WSInterface.WSReturnName returnName = method.getAnnotation(WSInterface.WSReturnName.class);
        if( WSReturnObject.class.isAssignableFrom( method.getReturnType() ) ){
            Class<?> retClass = method.getReturnType();
            Field[] fields = retClass.getFields();

            for(int i=0; i<fields.length ;i++){
                WSParameterDef ret_param = new WSParameterDef( this );
                WSReturnObject.WSValueName retValName = fields[i]
                                   .getAnnotation( WSReturnObject.WSValueName.class );
                if(retValName != null)
                    ret_param.setName( retValName.value() );
                else
                    ret_param.setName( fields[i].getName() );
                ret_param.setParamClass( fields[i].getType() );
                outputs.add( ret_param );
            }
        }
        else if( method.getReturnType() != void.class ){
            WSParameterDef ret_param = new WSParameterDef( this );
            if(returnName != null)
                ret_param.setName(returnName.value());
            else
                ret_param.setName("return");
            ret_param.setParamClass( method.getReturnType() );
            outputs.add( ret_param );
        }
    }

    /**
     * @return the published name of the method
     */
    public String getName(){
        return name;
    }

    /**
     * @return the number of exceptions this method throws
     */
    public int exceptionCount(){
        return exceptions.size();
    }

    /**
     * @return a list of exceptions this method throws
     */
    public List<Class<?>> getExceptions(){
        return exceptions;
    }

    /**
     * @return the number of parameters for this method
     */
    public int getInputCount(){
        return inputs.size();
    }

    /**
     * @return a list of input parameters
     */
    public List<WSParameterDef> getInputs(){
        return inputs;
    }

    /**
     * @param		index		is a index
     * @return					a {@link WSParameterDef} object in the given index
     */
    public WSParameterDef getInput( int index ){
        return inputs.get( index );
    }

    /**
     * @return the number of parameters for this method
     */
    public int getOutputCount(){
        return outputs.size();
    }

    /**
     * @return a list of input parameters
     */
    public List<WSParameterDef> getOutputs(){
        return outputs;
    }

    /**
     * @param		index		is a index
     * @return					a {@link WSParameterDef} object in the given index
     */
    public WSParameterDef getOutput( int index ){
        return outputs.get( index );
    }

    /**
     * @return Documentation of the method if one exists or else null
     */
    public String getDocumentation(){
        return doc;
    }

    /**
     * @return the namespace of the method
     */
    public String getNamespace(){
        return namespace;
    }

    public WebServiceDef getWebService(){
        return wsDef;
    }

    /**
     * Invokes a specified method
     *
     * @param		obj 		the object the method will called on
     * @param 		params 		a vector with arguments
     */
    public Object invoke(Object obj, Object[] params) throws Exception {
        return this.method.invoke(obj, params );
    }


    public String toString(){
        StringBuilder tmp = new StringBuilder();
        boolean first = true;
        tmp.append(name).append("(");
        for(WSParameterDef param : inputs){
            if(first)
                first = false;
            else
                tmp.append(" ,");
            tmp.append(param.getParamClass().getSimpleName());
            tmp.append(" ");
            tmp.append(param.getName());
        }
        tmp.append(") => ");
        first = true;
        for(WSParameterDef param : outputs){
            if(first)
                first = false;
            else
                tmp.append(" ,");
            tmp.append(param.getParamClass().getSimpleName());
            tmp.append(" ");
            tmp.append(param.getName());
        }
        return tmp.toString();
    }
}
