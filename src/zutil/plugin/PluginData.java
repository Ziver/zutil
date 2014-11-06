/*******************************************************************************
 * Copyright (c) 2014 Ziver
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

package zutil.plugin;

import zutil.parser.DataNode;

import java.net.URLClassLoader;

/**
 * This class contains information about a plugin
 * and implementation instances of the plugin interfaces
 *  
 * @author Ziver
 */
public class PluginData<T> {
	private double pluginVersion;
	private String pluginName;	
	private String pluginClass;
	
	private T obj;
	
	
	protected PluginData(String intf, DataNode data){		
		pluginVersion = data.getDouble("version");
		pluginName = data.getString("name");		
		pluginClass = data.get("interfaces").getString(intf);
	}
	
	public double getVersion(){
		return pluginVersion;
	}
	public String getName(){
		return pluginName;
	}
	
	@SuppressWarnings("unchecked")
	public T getObject() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		//if(obj == null)
		//	new URLClassLoader(pluginClass);
		//	//obj = (T) Class.forName(pluginClass).newInstance();
		return obj;
	}

}
