package zutil.network.nio.message;

public class GridMessage<T> extends Message{
	private static final long serialVersionUID = 1L;
	
	// Client type messages
	/** Computation job return right answer **/
	public static final int COMP_SUCCESSFUL = 1; // 
	/** Initial static data **/
	public static final int COMP_INCORRECT = 2; // 
	/** Computation job return wrong answer **/
	public static final int COMP_ERROR = 3; // 
	/** There was an error computing **/
	public static final int REGISTER = 4; // 
	/** Register at the server **/
	public static final int UNREGISTER = 5; // 
	/** Request new computation data **/
	public static final int NEW_DATA = 6; // 
	
	// Server type messages
	/** Sending initial static data **/
	public static final int INIT_DATA = 100;
	/** Sending new dynamic data **/
	public static final int COMP_DATA = 101;

	
	private int type;
	private int jobID;
	private T data;

	/**
	 * Creates a new GridMessage
	 * 
	 * @param type is the type of message
	 * @param jobID is the id of the job
	 */
	public GridMessage(int type){
		this(type, 0, null);
	}
	
	/**
	 * Creates a new GridMessage
	 * 
	 * @param type is the type of message
	 * @param jobID is the id of the job
	 */
	public GridMessage(int type, int jobID){
		this(type, jobID, null);
	}
	
	/**
	 * Creates a new GridMessage
	 * 
	 * @param type is the type of message
	 * @param jobID is the id of the job
	 * @param data is the data to send with this message
	 */
	public GridMessage(int type, int jobID, T data){
		this.type = type;
		this.jobID = jobID;
		this.data = data;
	}
	
	/**
	 * @return the type of message
	 */
	public int messageType(){
		return type;
	}
	
	/**
	 * @return the job id for this message
	 */
	public int getJobQueueID(){
		return jobID;
	}
	
	/**
	 * @return the data in this message, may not always carry any data.
	 */
	public T getData(){
		return data;
	}
}
