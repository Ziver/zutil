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

package zutil.log.net;

import zutil.net.nio.message.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogRecord;

public class NetLogMessage implements Message {
	private static final long serialVersionUID = 1L;
    private static final SimpleDateFormat dataFormat =
            new SimpleDateFormat("yyyy--MM-dd HH:mm:ss");
	
	private long   timestamp;
	private String level;
	private int    threadID;
	private String className;
	private String methodName;
	private String log;


	public NetLogMessage(String level, long timestamp, String log){
		this.level = level;
		this.timestamp = timestamp;
		this.log = log;
	}

	public NetLogMessage( LogRecord record ){
		timestamp	= record.getMillis();
		level		= record.getLevel().getName();
		threadID	= record.getThreadID();
		className	= record.getSourceClassName();
		methodName	= record.getSourceMethodName();
		log			= record.getMessage();
	}

	
	
	public String getTimestamp() {
		return dataFormat.format(new Date(timestamp));
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public int getThreadID() {
		return threadID;
	}

	public void setThreadID(int threadID) {
		this.threadID = threadID;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}
	

}
