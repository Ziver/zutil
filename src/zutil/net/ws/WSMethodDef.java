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

import zutil.net.ws.WSInterface.WSDocumentation;
import zutil.net.ws.WSInterface.WSNamespace;
import zutil.net.ws.WSInterface.WSPath;
import zutil.net.ws.WSInterface.WSRequestType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This is a web service method definition class
 *
 * @author Ziver
 */
// TODO: Header parameters
public class WSMethodDef {
    /**
     * The parent web service definition
     **/
    private WebServiceDef wsDef;
    /**
     * A list of input parameters
     **/
    private ArrayList<WSParameterDef> inputs;
    /**
     * A List of return parameters of the method
     **/
    private ArrayList<WSParameterDef> outputs;
    /**
     * The object type of output object
     **/
    private Class<?> outputClass;
    /**
     * A List of exceptions that this method throws
     **/
    private ArrayList<Class<?>> exceptions;
    /**
     * The real method that this class represent, can be null if its a remote method
     **/
    private Method method;
    /**
     * Documentation of the method
     **/
    private String doc;
    /**
     * The namespace of the method
     **/
    private String namespace;
    /**
     * The type of request required to execute the method
     **/
    private WSInterface.RequestType requestType;
    /**
     * The published name of the method
     **/
    private String name;
    /**
     * The endpoint location
     **/
    private String path;


    /**
     * @param wsDef is the parent web service defining interface
     * @param me    is a method in a class that implements WSInterface
     */
    protected WSMethodDef(WebServiceDef wsDef, Method me) {
        if (!WSInterface.class.isAssignableFrom(me.getDeclaringClass()))
            throw new ClassCastException("Declaring class does not implement WSInterface!");

        this.wsDef = wsDef;
        this.method = me;
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.exceptions = new ArrayList<>();
        this.name = method.getName();

        // Handle documentation and namespace

        WSDocumentation docAnnotation = method.getAnnotation(WSDocumentation.class);
        if (docAnnotation != null)
            doc = docAnnotation.value();

        WSNamespace namespaceAnnotation = method.getAnnotation(WSNamespace.class);
        if (namespaceAnnotation != null)
            namespace = namespaceAnnotation.value();
        else
            namespace = wsDef.getNamespace() + "?#" + name;

        // ------------------------------------------------
        // Handle inputs
        // ------------------------------------------------

        Annotation[][] paramAnnotation = method.getParameterAnnotations();
        Class<?>[] inputTypes = method.getParameterTypes();

        for (int i = 0; i < paramAnnotation.length; i++) {
            WSParameterDef param = new WSParameterDef(this, inputTypes[i], paramAnnotation[i]);

            // if no name was found then generate one
            if (param.getName() == null)
                param.setName("args" + i);

            inputs.add(param);
        }

        // ------------------------------------------------
        // Handle outputs
        // ------------------------------------------------

        this.outputClass = method.getReturnType();
        if (WSReturnObject.class.isAssignableFrom(outputClass)) {
            Field[] fields = outputClass.getFields();

            for (Field field : fields) {
                WSParameterDef ret_param = new WSParameterDef(this, field.getType(), field.getAnnotations());

                if (ret_param.getName() == null)
                    ret_param.setName(field.getName());

                outputs.add(ret_param);
            }
        } else if (outputClass != void.class) {
            WSInterface.WSReturnName returnNameAnnotation = method.getAnnotation(WSInterface.WSReturnName.class);
            WSParameterDef ret_param = new WSParameterDef(this, method.getReturnType(), new Annotation[]{returnNameAnnotation});

            if (ret_param.getName() == null)
                ret_param.setName("return");

            outputs.add(ret_param);
        }

        // ------------------------------------------------
        // Handle Exceptions
        // ------------------------------------------------

        Collections.addAll(exceptions, method.getExceptionTypes());

        // ------------------------------------------------
        // Handle the request type
        // ------------------------------------------------

        WSRequestType requestTypeAnnotation = method.getAnnotation(WSRequestType.class);
        if (requestTypeAnnotation != null) {
            this.requestType = requestTypeAnnotation.value();
        } else {
            // Specific request type was not provided, try to figure it out by the method name

            if (name.startsWith("post"))
                this.requestType = WSInterface.RequestType.POST;
            else if (name.startsWith("put"))
                this.requestType = WSInterface.RequestType.PUT;
            else if (name.startsWith("delete"))
                this.requestType = WSInterface.RequestType.DELETE;
            else if (name.startsWith("patch"))
                this.requestType = WSInterface.RequestType.PATCH;
            else
                this.requestType = WSInterface.RequestType.GET;
        }

        // ------------------------------------------------
        // Handle endpoint path
        // ------------------------------------------------

        WSPath pathAnnotation = method.getAnnotation(WSPath.class);
        if (pathAnnotation != null)
            path = pathAnnotation.value();
        else
            path = this.name;

        if (!path.startsWith("/"))
            path = '/' + path;
    }

    /**
     * @return the published name of the method
     */
    public String getName() {
        return name;
    }

    /**
     * @return the path to the WS method endpoint
     */
    public String getPath() {
        return path;
    }

    /**
     * @return a list of exceptions this method throws
     */
    public List<Class<?>> getExceptions() {
        return exceptions;
    }

    /**
     * @return a list of input parameters
     */
    public List<WSParameterDef> getInputs() {
        return inputs;
    }

    /**
     * @return a list of input parameters
     */
    public List<WSParameterDef> getOutputs() {
        return outputs;
    }

    /**
     * @return the class of the output object
     */
    public Class<?> getOutputClass() {
        return outputClass;
    }

    /**
     * @return documentation of the method if one exists or else null
     */
    public String getDocumentation() {
        return doc;
    }

    /**
     * @return the type of request needed to execute this method
     */
    public WSInterface.RequestType getRequestType() {
        return requestType;
    }

    /**
     * @return the namespace or endpoint url of the method
     */
    public String getNamespace() {
        return namespace;
    }


    /**
     * Invokes a specified method
     *
     * @param params a vector with arguments
     * @param        obj the object the method will called on
     */
    public Object invoke(Object obj, Object[] params) throws Exception {
        return this.method.invoke(obj, params);
    }


    public String toString() {
        StringBuilder tmp = new StringBuilder();
        boolean first = true;

        tmp.append(name).append("(");

        for (WSParameterDef param : inputs) {
            if (first)
                first = false;
            else
                tmp.append(" ,");
            tmp.append(param.getParamClass().getSimpleName());
            tmp.append(" ");
            tmp.append(param.getName());
        }
        tmp.append(") => ");

        first = true;
        for (WSParameterDef param : outputs) {
            if (first)
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
