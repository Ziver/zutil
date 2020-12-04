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

import zutil.io.IOUtil;
import zutil.io.file.FileSearcher;
import zutil.log.LogUtil;
import zutil.parser.DataNode;
import zutil.parser.json.JSONParser;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

/**
 * This class will search the file system for files with the name "plugin.json"
 * that defines data parameters for a plugin.
 * The class will only load the latest version of a specific plugin with the same name.
 * <p>
 * Example plugin.json content:<pre>
 * {
 *     "version": 1.0,
 *     "name": "Nice name of Plugin",
 *     "description": "This is a example plugin description",
 *     "interfaces": [
 *         {"plugin.interface.class": "plugin.implementation.class"},
 *         {"wa.server.plugin.WAFrontend": "wa.server.plugin.apache.ApacheFrontend"}
 *     ]
 * }
 * </pre>
 *
 * @author Ziver
 */
public class PluginManager<T> implements Iterable<PluginData>{
    private static Logger log = LogUtil.getLogger();

    private HashMap<String, PluginData> plugins;


    /**
     * Constructor that will look for plugins in the working directory.
     */
    public PluginManager(){
        this("./");
    }
    /**
     * Constructor that will look for plugins in the specified directory.
     */
    public PluginManager(String path){
        plugins = new HashMap<>();

        FileSearcher search = new FileSearcher(new File(path));
        search.setRecursive(true);
        search.searchFolders(false);
        search.searchCompressedFiles(true);
        search.setFileName("plugin.json");

        log.fine("Searching for plugins...");
        for(FileSearcher.FileSearchItem file : search){
            try {
                DataNode node = JSONParser.read(IOUtil.readContentAsString(file.getInputStream(), true));
                log.fine("Found plugin: " + file.getPath());
                PluginData plugin = new PluginData(node);

                if (!plugins.containsKey(plugin.getName())){
                    plugins.put(plugin.getName(), plugin);
                }
                else {
                    double version = plugins.get(plugin.getName()).getVersion();
                    if(version < plugin.getVersion())
                        plugins.put(plugin.getName(), plugin);
                    else if(version == plugin.getVersion())
                        log.fine("Ignoring duplicate plugin: " + plugin);
                    else
                        log.fine("Ignoring outdated plugin: " + plugin);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return a Iterator of all enabled plugins.
     */
    @Override
    public Iterator<PluginData> iterator() {
        return new EnabledPluginIterator(toList(plugins.values().iterator()));
    }
    /**
     * @return a Iterator for singleton Objects from all plugins that are enabled and
     *          has defined implementations of the given interface.
     */
    public <K> Iterator<K> getSingletonIterator(Class<K> intf) {
        return new PluginSingletonIterator<>(iterator(), intf);
    }
    /**
     * @return a Iterator for classes from all plugins that are enabled and has defined
     *          implementations of the given interface.
     */
    public <K> Iterator<Class<? extends K>> getClassIterator(Class<K> intf) {
        return new PluginClassIterator<>(iterator(), intf);
    }

    /**
     * @return a Iterator of all plugins, independently on if they are enabled or disabled.
     */
    public Iterator<PluginData> iteratorAll() {
        return plugins.values().iterator();
    }

    /**
     * @return a List of enabled plugins.
     */
    public List<PluginData> toArray() {
        return toList(iterator());
    }
    /**
     * @return a list of enabled plugins that has specified the provided interface in their definition.
     */
    public <K> List<K> toArray(Class<K> intf) {
        return toList(getSingletonIterator(intf));
    }

    /**
     * @return a List of all plugins, independently on if they are enabled or disabled.
     */
    public List<PluginData> toArrayAll() {
        return toList(iteratorAll());
    }




    private <K> List<K> toList(Iterator<K> it) {
        ArrayList<K> list = new ArrayList<>();
        while(it.hasNext())
            list.add(it.next());
        return list;
    }

    /**
     * @return the PluginData representing the given plugin by name, returns null if
     *          there is no plugin by that name.
     */
    public PluginData getPluginData(String pluginName) {
        return plugins.get(pluginName);
    }


    /**
     * A Iterator that only returns enabled plugins.
     */
    protected static class EnabledPluginIterator implements Iterator<PluginData> {
        private List<PluginData> pluginList;
        private int nextIndex = 0;

        EnabledPluginIterator(List<PluginData> pluginList) {
            this.pluginList = pluginList;
        }

        @Override
        public boolean hasNext() {
            for (int i = nextIndex; i < pluginList.size(); i++) {
                if (pluginList.get(i).isEnabled())
                    return true;
            }
            return false;
        }

        @Override
        public PluginData next() {
            if(!hasNext())
                throw new NoSuchElementException();
            return pluginList.get(nextIndex++);
        }
    }


    protected static class PluginClassIterator<T> implements Iterator<Class<? extends T>> {
        private Class<T> intf;
        private Iterator<PluginData> pluginIt;
        private Iterator<Class<?>> classIt;

        PluginClassIterator(Iterator<PluginData> it, Class<T> intf){
            this.intf = intf;
            this.pluginIt = it;
        }

        @Override
        public boolean hasNext() {
            if(pluginIt == null)
                return false;
            if(classIt != null && classIt.hasNext())
                return true;

            while(pluginIt.hasNext()) {
                classIt = pluginIt.next().getClassIterator(intf);
                if(classIt.hasNext())
                    return true;
            }
            classIt = null;
            return false;
        }

        @Override
        public Class<? extends T> next() {
            if(!hasNext())
                throw new NoSuchElementException();
            return (Class<? extends T>) classIt.next();
        }
    }


    protected static class PluginSingletonIterator<T> implements Iterator<T> {
        private Class<T> intf;
        private Iterator<PluginData> pluginIt;
        private Iterator<T> objectIt;

        PluginSingletonIterator(Iterator<PluginData> it, Class<T> intf){
            this.intf = intf;
            this.pluginIt = it;
        }

        @Override
        public boolean hasNext() {
            if(pluginIt == null)
                return false;
            if(objectIt != null && objectIt.hasNext())
                return true;

            while(pluginIt.hasNext()) {
                objectIt = pluginIt.next().getObjectIterator(intf);
                if(objectIt.hasNext())
                    return true;
            }
            objectIt = null;
            return false;
        }

        @Override
        public T next() {
            if(!hasNext())
                throw new NoSuchElementException();
            return objectIt.next();
        }
    }
}