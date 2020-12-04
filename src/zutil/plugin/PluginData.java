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

package zutil.plugin;

import zutil.log.LogUtil;
import zutil.parser.DataNode;

import java.util.*;
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

    private final double pluginVersion;
    private final String pluginName;
    private final String pluginDescription;
    private boolean enabled = true;
    private HashMap<Class<?>, List<Class<?>>>  classMap;
    private HashMap<Class, Object> objectMap;


    protected PluginData(DataNode data) {
        classMap = new HashMap<>();
        objectMap = new HashMap<>();

        pluginVersion = data.getDouble("version");
        pluginName = data.getString("name");
        pluginDescription = data.getString("description");
        log.fine("Plugin: " + this);

        DataNode node = data.get("interfaces");
        if(node.isMap())
            addInterfaces(node);
        else if(node.isList()) {
            for (DataNode childNode : node) {
                addInterfaces(childNode);
            }
        }
    }
    private void addInterfaces(DataNode node){
        Iterator<String> intf_it = node.keyIterator();
        while (intf_it.hasNext()) {
            String pluginIntf = intf_it.next();
            String className = node.get(pluginIntf).getString();

            Class intfClass = getClassByName(pluginIntf);
            Class pluginClass = getClassByName(className);
            if (intfClass == null || pluginClass == null)
                log.warning("Plugin interface: " +
                        (intfClass==null ? "(Not Available) " : "") + pluginIntf + " --> " +
                        (pluginClass==null ? "(Not Available) " : "") + className);
            else
                log.finer("Plugin interface: "+ pluginIntf +" --> "+ className);

            if (intfClass == null || pluginClass == null)
                continue;

            if (!classMap.containsKey(intfClass))
                classMap.put(intfClass, new ArrayList<>());
            classMap.get(intfClass).add(pluginClass);
        }
    }
    private static Class getClassByName(String name) {
        try {
            return Class.forName(name);
        }catch (Exception e){
            //log.log(Level.WARNING, null, e); // No need to log, we are handling it
        }
        return null;
    }


    /**
     * @return a version number for the plugin (specified in the plugin.json file
     */
    public double getVersion(){
        return pluginVersion;
    }
    /**
     * @return the name of the plugin
     */
    public String getName(){
        return pluginName;
    }
    /**
     * @return the name of the plugin
     */
    public String getDescription(){
        return pluginDescription;
    }
    /**
     * @return if this plugin is enabled
     */
    public boolean isEnabled(){
        return enabled;
    }

    /**
     * Enables or disables this plugin
     */
    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }


    /**
     * @return a Iterator for all singleton Object instance that implement the specified interface
     */
    public <T> Iterator<T> getObjectIterator(Class<T> intf){
        if(!classMap.containsKey(intf))
            return Collections.emptyIterator();
        return new PluginSingletonIterator<>(classMap.get(intf).iterator());
    }
    /**
     * @return a Iterator for all classes implementing the interface by the plugin
     */
    public Iterator<Class<?>> getClassIterator(Class<?> intf){
        if(!classMap.containsKey(intf))
            return Collections.emptyIterator();
        return classMap.get(intf).iterator();
    }

    private <T> T getObject(Class<T> objClass) {
        try {
            if (!objectMap.containsKey(objClass))
                objectMap.put(objClass, objClass.newInstance());
            return (T) objectMap.get(objClass);
        } catch (Exception e) {
            log.log(Level.WARNING, "Unable to instantiate plugin class: " + objClass, e);
        }
        return null;
    }

    /**
     * @return true if the specified interface is defined by the plugin
     */
    public boolean contains(Class<?> intf){
        return classMap.containsKey(intf);
    }

    public String toString(){
        return getName()+"(version: "+getVersion()+")";
    }


    /**
     * A Iterator that goes through all defined classes that implements
     * a provided interface and returns a singleton instance of that class
     */
    private class PluginSingletonIterator<T> implements Iterator<T>{
        private Iterator<Class<?>> classIt;
        private T currentObj;

        public PluginSingletonIterator(Iterator<Class<?>> it) {
            classIt = it;
        }

        @Override
        public boolean hasNext() {
            if(currentObj != null)
                return true;
            while (classIt.hasNext()){
                currentObj = (T)getObject(classIt.next());
                if(currentObj != null)
                    return true;
            }
            return false;
        }

        @Override
        public T next() {
            if(!hasNext())
                throw new NoSuchElementException();
            T tmp = currentObj;
            currentObj = null;
            return tmp;
        }

        @Override
        public void remove() {
            throw new RuntimeException("Iterator is ReadOnly");
        }
    }
}
