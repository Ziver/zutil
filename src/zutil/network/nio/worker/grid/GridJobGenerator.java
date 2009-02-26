package zutil.network.nio.worker.grid;


/**
 * Generates new jobs for the grid to compute
 * 
 * @author Ziver
 */
public interface GridJobGenerator<T> {
	/**
	 * @return static and final values that do not change for every job
	 */
	public Object initValues();
	/**
	 * @return a new generated job
	 */
	public T generateJob();
}
