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
