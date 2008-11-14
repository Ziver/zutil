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
public class OneAppFile implements OneApp{
	private File file;
	private FileChannel channel;
	private FileLock lock;

	/**
	 * Creates a OneApp class
	 * 
	 * @param filename The name of the file to be locked
	 */
	public OneAppFile(String filename){
		this.file = new File(System.getProperty("user.home"), filename);
	}

	/**
	 * Checks if the file have already bean locked
	 * 
	 * @return True if the file is locked else false
	 */
	public boolean check() {
		boolean tmp = lockApp();
		closeLock();
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
				// already locked
				closeLock();
				return false;
			}

			if (lock == null) {
				closeLock();
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



