package zutil.io.file;

import java.io.File;

/**
 * Interface for the FileWatcher class
 * 
 * @author Ziver
 */
public interface FileChangeListener{
	
	/**
	 * This method is called when there is a change in a file
	 * 
	 * @param file The file that has changed
	 */
	public void fileChangedEvent(File file);
}