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

package zutil.plugin;

import zutil.io.IOUtil;
import zutil.io.file.FileSearcher;
import zutil.log.LogUtil;
import zutil.parser.DataNode;
import zutil.parser.json.JSONParser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

/**
 * This class will search the file system for files
 * with the name "plugin.json" that defines data 
 * parameters for a single plugin.
 * The class will only load the latest version of the specific plugin.
 * 
 * @author Ziver
 */
public class PluginManager<T> implements Iterable<PluginData>{
	private static Logger log = LogUtil.getLogger();

	private HashMap<String, PluginData> plugins;


	public static <T> PluginManager<T> load(String path){
		return new PluginManager<T>(path);
	}

	public PluginManager(){
		this("./");
	}
	public PluginManager(String path){
		plugins = new HashMap<String, PluginData>();

		FileSearcher search = new FileSearcher(new File(path));
		search.setRecursive(true);
		search.searchFolders(false);
		search.searchCompressedFiles(true);
		search.setFileName("plugin.json");

		log.fine("Searching for plugins...");
		for(FileSearcher.FileSearchItem file : search){
			try {
				DataNode node = JSONParser.read(IOUtil.getContentAsString(file.getInputStream()));
				log.fine("Found plugin: "+file.getPath());
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
						log.fine("Ignoring outdated plugin: "+plugin);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public Iterator<PluginData> iterator() {
		return plugins.values().iterator();
	}
	public <T> Iterator<T> iterator(Class<T> intf) {
		return new PluginInterfaceIterator<T>(plugins.values().iterator(), intf);
	}

	public ArrayList<PluginData> toArray() {
		ArrayList<PluginData> list = new ArrayList<PluginData>();
		Iterator<PluginData> it = iterator();
		while(it.hasNext())
			list.add(it.next());
		return list;
	}
	public <T> ArrayList<T> toArray(Class<T> intf) {
		ArrayList<T> list = new ArrayList<T>();
		Iterator<T> it = iterator(intf);
		while(it.hasNext())
			list.add(it.next());
		return list;
	}


	public class PluginInterfaceIterator<T> implements Iterator<T> {
		private Class<T> intf;
		private Iterator<PluginData> pluginIt;
		private Iterator<T> objectIt;

		PluginInterfaceIterator(Iterator<PluginData> it, Class<T> intf){
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
				objectIt = pluginIt.next().getIterator(intf);
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

		@Override
		public void remove() {
			throw new RuntimeException("Iterator is ReadOnly");
		}
	}
}