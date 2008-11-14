package zutil.network.nio.response;


public abstract class ResponseEvent {
	private Object rsp = null;

	public synchronized boolean handleResponse(Object rsp) {
		this.rsp = rsp;
		notify();
		return true;
	}

	/**
	 * Blocks the Thread until there is a response
	 */
	public synchronized void waitForResponse() {
		while(!gotResponse()) {
			try {
				this.wait();
			} catch (InterruptedException e) {}
		}

		responseEvent(rsp);
	}

	/**
	 * Handles the response
	 */
	public void handleResponse(){
		if(gotResponse()){
			responseEvent(rsp);
		}
	}

	/**
	 * @return If there is an response
	 */
	public boolean gotResponse(){
		return (rsp != null);
	}

	protected abstract void responseEvent(Object rsp);
}
