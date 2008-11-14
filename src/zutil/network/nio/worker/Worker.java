package zutil.network.nio.worker;

import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

import zutil.network.nio.NioNetwork;


public abstract class Worker implements Runnable {
	private LinkedList<WorkerDataEvent> queue = new LinkedList<WorkerDataEvent>();
	
	public void processData(NioNetwork server, SocketChannel socket, Object data) {
		synchronized(queue) {
			queue.add(new WorkerDataEvent(server, socket, data));
			queue.notify();
		}
	}
	
	/**
	 * @return The event queue
	 */
	protected List<WorkerDataEvent> getEventQueue(){
		return queue;
	}
	
	/**
	 * @return If there is a event in the queue
	 */
	protected boolean hasEvent(){
		return !queue.isEmpty();
	}
	
	/**
	 * Polls a event from the list or waits until there is a event
	 * @return The next event
	 */
	protected WorkerDataEvent pollEvent(){
		while(queue.isEmpty()) {
			try {
				this.wait();
			} catch (InterruptedException e) {}
		}
		return queue.poll();
	}
	
	public void run(){
		update();
	}
	
	public abstract void update();
}
