/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Ziver Koc
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

package zutil.jee.upload;

import org.apache.commons.fileupload.ProgressListener;
import zutil.StringUtil;
import zutil.parser.DataNode;
import zutil.parser.DataNode.DataType;


/**
 * This is a File Upload Listener that is used by Apache
 * Commons File Upload to monitor the progress of the
 * uploaded file.
 */
public class FileUploadListener implements ProgressListener{
    public enum Status{
        Initializing,
        Uploading,
        Processing,
        Done,
        Error
    }

    private String id;
    private volatile Status status;
    private volatile String filename;
    private volatile String message;
    private volatile long bytes = 0L;
    private volatile long length = 0L;
    private volatile int item = 0;
    private volatile long time;

    // Speed
    private volatile int speed;
    private volatile long speedRead;
    private volatile long speedTime;

    public FileUploadListener() {
        id = "" +(int)(Math.random()*Integer.MAX_VALUE);
        status = Status.Initializing;
        filename = "";
        message = "";
    }

    public void update(long pBytesRead, long pContentLength, int pItems) {
        if (pContentLength < 0)	this.length = pBytesRead;
        else					this.length = pContentLength;
        this.bytes = pBytesRead;
        this.item = pItems;

        // Calculate Speed
        if (speedTime == 0 || speedTime+1000 < System.currentTimeMillis() || pBytesRead == pContentLength) {
            speedTime = System.currentTimeMillis();
            speed = (int)(pBytesRead-speedRead);
            speedRead = pBytesRead;
        }
        //try {Thread.sleep(10);} catch (Exception e) {}

        // Set Status
        status = Status.Uploading;
        time = System.currentTimeMillis();
    }

    protected void setFileName(String filename) {
        this.filename = filename;
    }
    protected void setStatus(Status status) {
        this.status = status;
        time = System.currentTimeMillis();
    }
    protected void setMessage(String msg) {
        this.message = msg;
    }


    public String getID() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public long getBytesRead() {
        return bytes;
    }

    public long getContentLength() {
        return length;
    }

    public long getItem() {
        return item;
    }

    public Status getStatus() {
        return status;
    }

    protected long getTime() {
        return time;
    }

    protected String getMessage() {
        return message;
    }

    /**
     * @return bytes per second
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Calculate the percent complete
     */
    public int getPercentComplete() {
        if (length == 0)
            return 0;
        return (int)((100 * bytes) / length);
    }

    public DataNode getJSON() {
        DataNode node = new DataNode(DataType.Map);
        node.set("id", id);

        node.set("status", status.toString());
        node.set("message", message.replaceAll("\"", "\\\""));
        node.set("filename", filename);
        node.set("percent", getPercentComplete());

        node.set("uploaded", StringUtil.formatByteSizeToString(bytes));
        node.set("total", StringUtil.formatByteSizeToString(length));
        node.set("speed", StringUtil.formatByteSizeToString(speed) + "/s");
        return node;
    }
}
