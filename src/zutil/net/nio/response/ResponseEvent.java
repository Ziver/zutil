/*
 * Copyright (c) 2015 ezivkoc
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

package zutil.net.nio.response;


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
