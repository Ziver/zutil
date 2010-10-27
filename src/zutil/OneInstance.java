package zutil;

/**
 * This interface is used to look if another instance of the
 * application is running
 * 
 * @author Ziver
 *
 */
public interface OneInstance {
	/**
	 * Checks if the application is already running
	 * 
	 * @return True if the file is locked else false
	 */
	public boolean check();

	/**
	 * Locks the application so that another one can not run
	 *  
	 * @return False if there are a error else true
	 */
	public boolean lockApp();
}
