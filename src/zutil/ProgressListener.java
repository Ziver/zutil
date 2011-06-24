package zutil;

/**
 * This interface is used in some classes to handle the progress
 * of some action
 * 
 * @author Ziver
 *
 */
public interface ProgressListener<S,D> {
	
	/**
	 * This method is called when the progress is updated
	 * 
	 * @param source is the source object of the progress
	 * @param info is some information from the source object
	 * @param percent is the progress of the object (0-100)
	 */
	public void progressUpdate(S source, D info, double percent);
}
