/*
 * Copyright (c) 2015 ezivkoc
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

package zutil.io.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Timer;
import java.util.TimerTask;

import zutil.io.MultiPrintStream;

/**
 * This class calls a given listener 
 * when a file is changed
 * 
 * @author Ziver
 *
 */
public class FileWatcher extends TimerTask{
	private FileChangeListener listener;
	private long lastChanged;
	private File file;
	
	/**
	 * Creates a watcher for the given file whit the check 
	 * interval of 1 second
	 * 
	 * @param 		file 	is the file to check
	 * @throws FileNotFoundException 
	 */
	public FileWatcher(File file) throws FileNotFoundException{
		this(file, 1000);
	}
	
	/**
	 * Creates a watcher for the given file whit the given 
	 * check interval
	 * 
	 * @param 		file 			is the file
	 * @param 		intervall 		is the interval
	 * @throws FileNotFoundException 
	 */
	public FileWatcher(File file, int intervall) throws FileNotFoundException{
		if(file==null || !file.exists()) 
			throw new FileNotFoundException("File not found: "+file);
		this.file = file;
		lastChanged = file.lastModified();
		
		Timer t = new Timer(true);
		t.schedule(this, 0, intervall);
	}
	
	public void setListener(FileChangeListener listener){
		this.listener = listener;
	}

	@Override
	public void run() {
		if (lastChanged != file.lastModified()) {
			lastChanged = file.lastModified();
			if(listener != null){
				listener.fileChangedEvent(file);
			}
			else{
				MultiPrintStream.out.println("File Changed: "+file);
			}
		}
	}
}
