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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * Defines a web service (Server side) from a class implementing the {@link zutil.net.ws.WSInterface}
 *
 * @author Ziver
 */
public class WebServiceDef {
    /** This is the WSInterface class **/
    private Class<? extends WSInterface> intf;
    /** Namespace of the service **/
    private String namespace;
    /** Name of the web service **/
    private String name;
    /** Human readable description of the service **/
    private String documentation = "";
    /** A map of methods in this Service **/
    private HashMap<String,WSMethodDef> methods = new HashMap<>();



    public WebServiceDef(Class<? extends WSInterface> intf){
        this.intf = intf;
        name = intf.getSimpleName();

        WSInterface.WSNamespace namespaceAnnotation = intf.getAnnotation(WSInterface.WSNamespace.class);
        if (namespaceAnnotation != null)
            this.namespace = namespaceAnnotation.value();

        WSInterface.WSDocumentation documentationAnnotation = intf.getAnnotation(WSInterface.WSDocumentation.class);
        if (documentationAnnotation != null)
            this.documentation = documentationAnnotation.value();

        for(Method m : intf.getDeclaredMethods()){
            // Check for public methods
            if ((m.getModifiers() & Modifier.PUBLIC) > 0 &&
                    !m.isAnnotationPresent(WSInterface.WSIgnore.class)){
                WSMethodDef method = new WSMethodDef(this, m);
                methods.put(method.getName(), method);
            }
        }
    }

    /**
     * @return the class that defines this web service
     */
    public Class<? extends WSInterface> getWSClass(){
        return intf;
    }

    /**
     * @return the name of the Service (usually the class name of the WSInterface)
     */
    public String getName(){
        return name;
    }

    /**
     * @return a human readable description of the service, or a empty String if no documentation has been provided.
     */
    public String getDocumentation(){
        return documentation;
    }

    /**
     * @param 		name		is the name of the method
     * @return					if there is a method by the given name
     */
    public boolean hasMethod( String name ){
        return methods.containsKey( name );
    }

    /**
     * @param 		name		is the name of the method
     * @return					the method or null if there is no such method
     */
    public WSMethodDef getMethod( String name ){
        return methods.get( name );
    }

    /**
     * @return a Set of all the method names
     */
    public Set<String> getMethodNames(){
        return methods.keySet();
    }

    /**
     * @return all the methods
     */
    public Collection<WSMethodDef> getMethods(){
        return methods.values();
    }

    /**
     * @return the namespace of this web service ( usually the URL of the service )
     */
    public String getNamespace(){
        return namespace;
    }

    public WSInterface newInstance() throws InstantiationException, IllegalAccessException {
        return intf.newInstance();
    }
}
