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

package zutil.log.net;

import java.util.logging.LogRecord;

import zutil.net.nio.message.Message;

public class NetLogExceptionMessage extends Message {
	private static final long serialVersionUID = 1L;
	
	private int    count;
	private String name;
	private String message;
	private String stackTrace;
	
	NetLogExceptionMessage(String name, String message, String stackTrace){
		this.count = 1;
		this.name = name;
		this.message = message;
		this.stackTrace = stackTrace;
	}

	public NetLogExceptionMessage(LogRecord record) {
		Throwable exception = record.getThrown();
		
		this.count = 1;
		this.name = exception.getClass().getName();
		this.message = exception.getMessage();
		this.stackTrace = "";
		for(int i=0; i<exception.getStackTrace().length; i++){
			this.stackTrace += exception.getStackTrace()[i].toString();
		}
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result
				+ ((stackTrace == null) ? 0 : stackTrace.hashCode());
		return result;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		NetLogExceptionMessage other = (NetLogExceptionMessage) obj;
		if (name.equals(other.name) || message.equals(other.message) || 
				stackTrace.equals(other.stackTrace)) {
			return true;
		}
		return false;
	}

	public void addCount(int add){
		count += add;
	}
	
	public int getCount() {
		return count;
	}

	public String getName() {
		return name;
	}

	public String getMessage() {
		return message;
	}

	public String getStackTrace() {
		return stackTrace;
	}
}
