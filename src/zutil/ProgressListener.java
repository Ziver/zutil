package zutil;

/**
 * This interface is used in some classes to handle the progress
 * of some action
 * 
 * @author Ziver
 *
 */
public interface ProgressListener {
	
	/**
	 * This method is called when the progress is updated
	 * @param source The source object of the progress
	 * @param percent The progress of the object (0-100)
	 */
	public void progressUpdate(Object source, Object info, double percent);
}
