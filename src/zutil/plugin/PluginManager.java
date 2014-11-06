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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import zutil.io.file.FileSearch;
import zutil.io.file.FileUtil;
import zutil.parser.DataNode;
import zutil.parser.json.JSONParser;

/**
 * This class will search the file system for files
 * with the name "plugin.json" that defines data 
 * parameters for a single plugin.
 * The class will only load the latest version of the specific plugin.
 * 
 * @author Ziver
 */
public class PluginManager<T> implements Iterable<PluginData<T>>{
	private HashMap<String, PluginData<T>> plugins;
	
	public static <T> PluginManager<T> load(Class<T> intfClass) throws IOException{
		return new PluginManager<T>(intfClass);
	}

	
	private PluginManager(Class<T> intfClass) throws IOException{
		FileSearch search = new FileSearch(new File("."));
		search.setRecursive(true);
		search.searchFolders(false);
		search.setFileName("plugin.json");
		
		for(FileSearch.FileSearchItem file : search){
			DataNode node = JSONParser.read(FileUtil.getContent(file.getUrl()));
			PluginData<T> plugin = new PluginData<T>(intfClass.getName(), node);
			
			if(node.get("interfaces").getString(intfClass.getName()) != null){
				if(plugins.containsKey(plugin.getName())){
					if(plugins.get(plugin.getName()).getVersion() < plugin.getVersion())
						plugins.put(plugin.getName(), plugin);
				}
				else{
					plugins.put(plugin.getName(), plugin);
				}
			}
		}
	}
	
	@Override
	public Iterator<PluginData<T>> iterator() {
		return plugins.values().iterator();
	}
	
}