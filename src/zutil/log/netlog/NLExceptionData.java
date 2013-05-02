package zutil.log.netlog;

public class NLExceptionData {
	
	private int count;
	private String name;
	private String message;
	private String stackTrace;
	
	NLExceptionData(String name, String message, String stackTrace){
		this.count = 0;
		this.name = name;
		this.message = message;
		this.stackTrace = stackTrace;
	}

	
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}
}
