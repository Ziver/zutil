package zutil.net.nio.worker.grid;

/**
 * A internal class for handling the jobs
 * 
 * @author Ziver
 */
public class GridJob{
	public int jobID;
	public Object job;
	public long timestamp;
	
	public GridJob(int jobID, Object job){
		this.jobID = jobID;
		this.job = job;
		renewTimeStamp();
	}
	
	public void renewTimeStamp(){
		timestamp = System.currentTimeMillis();
	}
}