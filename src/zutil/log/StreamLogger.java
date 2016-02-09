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

package zutil.log;

import com.mysql.jdbc.log.Log;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Ziver on 2015-10-15.
 */
public class StreamLogger {
    private static final byte DELIMETER = '\n';
    private static final int MAX_BUFFER_SIZE = 1024;


    private LogCallback logger;
    private String prefix;
    private StringBuilder buffer;


    protected StreamLogger(String prefix, LogCallback logger){
        if(logger == null)
            throw new NullPointerException("LogCallback can not be NULL");
        this.prefix = prefix;
        this.logger = logger;
        this.buffer = new StringBuilder();
    }


    protected void log(int n){
        if(n < 0 || n == DELIMETER)
            flushLog();
        else
            buffer.append((char)n);
        if(buffer.length() > MAX_BUFFER_SIZE)
            flushLog();
    }
    protected void log(byte[] b, int off, int len){
        if (logger.isLoggable()) {
            for(int i=0; i<len; ++i){
                if(b[off+i] == DELIMETER)
                    flushLog();
                else
                    buffer.append((char)b[off+i]);
            }
            if(buffer.length() > MAX_BUFFER_SIZE)
                flushLog();
        }
    }

    protected void flushLog(){
        if(buffer.length() > 0) {
            if (prefix != null)
                logger.log(prefix + ": " + buffer.toString());
            else
                logger.log(buffer.toString());
            clearLog();
        }
    }
    protected void clearLog(){
        buffer.delete(0, buffer.length());
    }


    public interface LogCallback{
        public boolean isLoggable();
        public void log(String msg);
    }
}
