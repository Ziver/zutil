package zutil.jee.upload;

import org.apache.commons.fileupload.ProgressListener;


/**
 * This is a File Upload Listener that is used by Apache
 * Commons File Upload to monitor the progress of the 
 * uploaded file.
 */
public class FileUploadListener implements ProgressListener{
	private static final long serialVersionUID = 1L;
	public static enum Status{
		Initializing,
		Uploading,
		Processing,
		Done,
		Error
	}
	
	private String id;
	private volatile String filename;
	private volatile long bytes = 0l;
	private volatile long length = 0l;
	private volatile int item = 0;
	private volatile Status status;
	private volatile long time;
	
	// Speed
	private volatile int speed;
	private volatile long speedRead;
	private volatile long speedTime;
	
	public FileUploadListener(){
		id = ""+(int)(Math.random()*Integer.MAX_VALUE);
		status = Status.Initializing;
	}
	
	public void update(long pBytesRead, long pContentLength, int pItems) {
		if(pContentLength < 0)	this.length = pBytesRead;
		else					this.length = pContentLength;
		this.bytes = pBytesRead;
		this.item = pItems;
		
		// Calculate Speed
		if(speedTime == 0 || speedTime+1000<System.currentTimeMillis()){
			speedTime = System.currentTimeMillis();
			speed = (int)(pBytesRead-speedRead);
			speedRead = pBytesRead;
		}

		// Set Status
		status = Status.Uploading;
		time = System.currentTimeMillis();
	}
	
	protected void setFileName(String filename){
		this.filename = filename;
		item++;
	}
	protected void setStatus(Status status){
		this.status = status;
		time = System.currentTimeMillis();
	}
	
	
	public String getID(){
		return id;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public long getBytesRead() {
		return bytes;
	}

	public long getContentLength() {
		return length;
	}

	public long getItem() {
		return item;
	}
	
	public Status getStatus(){
		return status;
	}
	
	protected long getTime(){
		return time;
	}
	
	/**
	 * @return bytes per second
	 */
	public int getSpeed(){
		return speed;
	}

	/**
	 * Calculate the percent complete
	 */
	public int getPercentComplete(){
		if(length == 0)
			return 0;
		return (int)((100 * bytes) / length);
	}
}