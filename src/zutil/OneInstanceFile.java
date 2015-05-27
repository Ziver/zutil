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

package zutil;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

/**
 * This class checks if the application is already running
 * by Locking a file
 * 
 * @author Ziver Koc
 */
public class OneInstanceFile implements OneInstance{
	private File file;
	private FileChannel channel;
	private FileLock lock;

	/**
	 * Creates a OneApp class
	 * 
	 * @param filename The name of the file to be locked
	 */
	public OneInstanceFile(String filename){
		this.file = new File(System.getProperty("user.home"), filename);
	}

	/**
	 * Checks if the file have already bean locked
	 * 
	 * @return True if the file is locked else false
	 */
	public boolean check() {
		boolean tmp = lockApp();
		if( tmp ) closeLock();
		return !tmp;
	}

	/**
	 * Locks the file
	 *  
	 * @return False if there are a error else true
	 */
	public boolean lockApp() {	    
		try {
			channel = new RandomAccessFile(file, "rw").getChannel();

			try {
				lock = channel.tryLock();
			}
			catch (OverlappingFileLockException e) {
				// already locked by this application
				return false;
			}

			if (lock == null || lock.isShared()) {
				// already locked by another application
				return false;
			}

			Runtime.getRuntime().addShutdownHook(new Thread() {
				// destroy the lock when the JVM is closing
				public void run() {
					closeLock();
					deleteFile();
				}
			});
			return true;
		}
		catch (Exception e) {
			closeLock();
			return false;
		}
	}

	private void closeLock() {
		try { 
			lock.release(); 
			channel.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void deleteFile() {
		try { 
			file.delete(); 
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}



