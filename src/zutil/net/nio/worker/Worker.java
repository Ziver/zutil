/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Ziver Koc
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
 */

package zutil.net.nio.worker;

import zutil.net.nio.NioNetwork;

import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;


public abstract class Worker {
	private LinkedList<WorkerEventData> queue = new LinkedList<>();


	public void processData(NioNetwork server, SocketAddress remote, Object data) {
		synchronized(queue) {
			queue.add(new WorkerEventData(server, remote, data));
			queue.notify();
		}
	}
	

	/**
	 * @return true if there is a event in the queue
	 */
	protected boolean hasEvent(){
		return !queue.isEmpty();
	}
	
	/**
	 * Polls a event from the list or blocks until there is a event available
     *
	 * @return the next event
	 */
	protected WorkerEventData pollEvent(){
		synchronized(queue) {
			while (queue.isEmpty()) {
				try {
					queue.wait();
				} catch (InterruptedException e) {}
			}
		}
		return queue.poll();
	}
}
