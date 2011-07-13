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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import zutil.net.ws.WSInterface.WSDocumentation;

// TODO: Header parameters
public class WSMethodDef {
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
	/** The published name of the method **/
	private String name;
	

	/**
	 * 
	 * @param me is a method in a class that implements WSInterface
	 */
	public WSMethodDef(Method me) {
		if(!WSInterface.class.isAssignableFrom(me.getDeclaringClass()) )
			throw new ClassCastException("Declaring class does not implement WSInterface!");
		method = me;
		inputs = new ArrayList<WSParameterDef>();
		outputs = new ArrayList<WSParameterDef>();
		exceptions = new ArrayList<Class<?>>();
		name = method.getName();

		//***** Documentation
		WSDocumentation tmpDoc = method.getAnnotation(WSInterface.WSDocumentation.class);
		if(tmpDoc != null){
			doc = tmpDoc.value();
		}
		//***** Exceptions
		for( Class<?> exc : method.getExceptionTypes() ){
			exceptions.add( exc );
		}
		//********* Get the input parameter names **********
		Annotation[][] paramAnnotation = method.getParameterAnnotations();

		for(int i=0; i<paramAnnotation.length ;i++){
			WSParameterDef param = new WSParameterDef();
			for(Annotation annotation : paramAnnotation[i]){
				if(annotation instanceof WSInterface.WSParamName){
					WSInterface.WSParamName paramName = (WSInterface.WSParamName) annotation;
					param.name = paramName.value();
					param.optional = paramName.optional();
				}
			}
			// if no name was found then use default
			if(param.name == null)
				param.name = "args"+i;

			inputs.add( param );
		}

		//********  The return parameter name ************
		WSInterface.WSReturnName returnName = method.getAnnotation(WSInterface.WSReturnName.class);
		if( WSReturnValueList.class.isAssignableFrom( method.getReturnType() ) ){
			Class<?> retClass = method.getReturnType();
			Field[] fields = retClass.getFields();
			for(int i=0; i<fields.length ;i++){
				WSParameterDef ret_param = new WSParameterDef();
				WSReturnValueList.WSValueName retValName = fields[i]
				                   .getAnnotation( WSReturnValueList.WSValueName.class );
				if(retValName != null) ret_param.name = retValName.value();
				else ret_param.name = fields[i].getName();
				ret_param.paramClass = fields[i].getType();
				outputs.add( ret_param );
			}
		}
		else{
			WSParameterDef ret_param = new WSParameterDef();
			if(returnName != null) ret_param.name = returnName.value();
			else ret_param.name = "return";
			ret_param.paramClass = method.getReturnType();
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
	public int inputCount(){
		return inputs.size();
	}
	
	/**
	 * @return a list of input parameters
	 */
	public List<WSParameterDef> getInputs(){
		return inputs;
	}
	
	/**
	 * @return the number of parameters for this method
	 */
	public int outputCount(){
		return outputs.size();
	}
	
	/**
	 * @return a list of input parameters
	 */
	public List<WSParameterDef> getOutputs(){
		return outputs;
	}
	
	/**
	 * @return Documentation of the method if one exists or else null
	 */
	public String getDocumentation(){
		return doc;
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
			tmp.append(param.paramClass.getSimpleName());
			tmp.append(" ");
			tmp.append(param.name);
		}
		tmp.append(") => ");
		first = true;
		for(WSParameterDef param : outputs){
			if(first)
				first = false;
			else
				tmp.append(" ,");
			tmp.append(param.paramClass.getSimpleName());
			tmp.append(" ");
			tmp.append(param.name);
		}
		return tmp.toString();
	}
}
