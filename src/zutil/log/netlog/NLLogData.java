package zutil.log.netlog;

public class NLLogData {
	
	private String level;
	private long timestamp;
	private String log;
	
	NLLogData(String level, long timestamp, String log){
		this.level = level;
		this.timestamp = timestamp;
		this.log = log;
	}

	
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}
}
