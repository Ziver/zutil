/*******************************************************************************
 * Copyright (c) 2013 Ziver
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/

package zutil.net.nio.worker;

import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

import zutil.net.nio.NioNetwork;


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
