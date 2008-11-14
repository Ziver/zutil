package zutil.network.nio.response;

import java.util.LinkedList;
import java.util.List;


public abstract class ResponseHandler implements Runnable{
	private List<ResponseEvent> queue = new LinkedList<ResponseEvent>();
	
	public ResponseHandler(){
		
	}

	public synchronized void addResponseEvent(ResponseEvent re){
		queue.add(re);
		notify();
	}
	
	public synchronized void removeResponseEvent(ResponseEvent re){
		queue.remove(re);
	}
	
	public void run() {
		while(true) {
			try {
				this.wait();
			} catch (InterruptedException e) {}
			
			update();
		}
	}

	public synchronized void update(){
		while(!queue.isEmpty()){
			queue.get(0).handleResponse();
			if(queue.get(0).gotResponse()){
				queue.remove(0);
			}
		}
	}
}
