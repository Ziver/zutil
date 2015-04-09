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

import zutil.ClassUtil;
import zutil.log.LogUtil;
import zutil.parser.DataNode;

import javax.xml.crypto.Data;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class contains information about a plugin
 * and implementation instances of the plugin interfaces
 *  
 * @author Ziver
 */
public class PluginData {
	private static Logger log = LogUtil.getLogger();

	private double pluginVersion;
	private String pluginName;
	private HashMap<Class, Class>  classMap;
	private HashMap<Class, Object> objectMap;

	
	protected PluginData(DataNode data) throws ClassNotFoundException, MalformedURLException {
		classMap = new HashMap<Class, Class>();
		objectMap = new HashMap<Class, Object>();

		pluginVersion = data.getDouble("version");
		pluginName = data.getString("name");
		log.fine("Plugin: " + this);

		DataNode node = data.get("interfaces");
		if(node.isMap())
			addInterfaces(node);
		else if(node.isList()) {
			Iterator<DataNode> intfs_it = node.iterator();
			while (intfs_it.hasNext()) {
				addInterfaces(intfs_it.next());
			}
		}
	}
	private void addInterfaces(DataNode node){
		Iterator<String> intf_it = node.keyIterator();
		while (intf_it.hasNext()) {
			String pluginIntf = intf_it.next();
			String className = node.get(pluginIntf).getString();
			try {
				Class.forName(className); // Check if class is available, will throw exception if class is not found
				log.finer("Plugin interface: " + pluginIntf + " --> " + className);
				classMap.put(
						getClassByName(pluginIntf),
						getClassByName(className));
			}catch (Exception e){
				log.finer("Plugin interface: " + pluginIntf + " --> (Not Available) " + className);
			}
		}
	}
	private static Class getClassByName(String name) {
		try {
			return Class.forName(name);
		}catch (Exception e){
			log.log(Level.WARNING, null, e);
		}
		return null;
	}


	public double getVersion(){
		return pluginVersion;
	}
	public String getName(){
		return pluginName;
	}

	public <T> T getObject(Class<T> intf) {
		if(classMap.containsKey(intf)) {
			try {
				Class subClass = classMap.get(intf);
				if (!objectMap.containsKey(subClass))
					objectMap.put(subClass, subClass.newInstance());
				return (T) objectMap.get(subClass);
			} catch (Exception e) {
				log.log(Level.WARNING, null, e);
			}
		}
		return null;
	}


	public boolean contains(Class<?> intf){
		return classMap.containsKey(intf);
	}

	public String toString(){
		return getName()+"(ver: "+getVersion()+")";
	}
}
