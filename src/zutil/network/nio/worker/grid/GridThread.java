package zutil.net.nio.worker.grid;

/**
 * This interface is the thread that will do 
 * all the computation in the grid
 * 
 * @author Ziver
 */
public abstract class GridThread implements Runnable{
	/**
	 * The initial static and final data will be sent to this 
	 * method.
	 * 
	 * @param data is the static and or final data
	 */
	public abstract void setInitData(Object data);
	
	public void run(){
		while(true){
			GridJob tmp = null;
			try {
				tmp = GridClient.getNextJob();
				compute(tmp);
			} catch (Exception e) {
				e.printStackTrace();
				if(tmp != null){
					GridClient.jobError(tmp.jobID);
				}
			}
		}
	}
	
	/**
	 * Compute the given data and return 
	 * @param data
	 */
	public abstract void compute(GridJob data) throws Exception;
}
