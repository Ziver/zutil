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

package zutil.log;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class will log all data that passes through a InputStream.
 * The logging is done with Javas standard Logger with Level.FINEST.
 *
 * Created by Ziver on 2015-10-15.
 */
public class InputStreamLogger extends InputStream implements StreamLogger.LogCallback {
    private static final Logger logger = LogUtil.getLogger();

    private InputStream in;
    private StreamLogger log;

    public InputStreamLogger(InputStream in){
        this(null, in);
    }
    public InputStreamLogger(String prefix, InputStream in){
        this.in = in;
        this.log = new StreamLogger(prefix, this);
    }


    public boolean isLoggable(){
        return logger.isLoggable(Level.FINEST);
    }
    public void log(String msg){
        logger.finest(msg);
    }

    //************** PROXY METHODS ******************
    public int read() throws IOException{
        int n = in.read();
        log.log(n);
        return n;
    }
    public int read(byte b[]) throws IOException {
        int n = in.read(b);
        log.log(b, 0, n);
        return n;
    }
    public int read(byte b[], int off, int len) throws IOException {
        int n = in.read(b, off, len);
        log.log(b, off, n);
        return n;
    }
    public long skip(long n) throws IOException {
        return in.skip(n);
    }
    public int available() throws IOException {
        return in.available();
    }
    public void close() throws IOException {
        in.close();
        log.flushLog();
    }
    public void mark(int readlimit) {
        in.mark(readlimit);
    }
    public void reset() throws IOException {
        in.reset();
        log.clearLog();
    }
    public boolean markSupported() {
        return in.markSupported();
    }
}
