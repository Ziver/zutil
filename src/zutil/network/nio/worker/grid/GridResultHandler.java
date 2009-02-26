package zutil.network.nio.worker.grid;

/**
 * Handles the incoming results from the grid
 * 
 * @author Ziver
 */
public interface GridResultHandler<T> {
	public void resultEvent(int jobID, boolean correct, T result);
}
