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
        if(n == DELIMETER)
            flushLog();
        else
            buffer.append(n);
        if(buffer.length() > MAX_BUFFER_SIZE)
            flushLog();
    }
    protected void log(byte[] b, int off, int len){
        try {
            if (logger.isLoggable()) {
                for(int i=0; i<len; ++i){
                    if(b[off+i] == DELIMETER){
                        buffer.append(new String(b, off, i, "UTF-8"));
                        flushLog();
                        off += i;
                        len -= i;
                        i = 0;
                    }
                }
                if(len > 0)
                    buffer.append(new String(b, off, len, "UTF-8"));
                if(buffer.length() > MAX_BUFFER_SIZE)
                    flushLog();
            }
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }

    protected void flushLog(){
        if(prefix != null)
            logger.log(prefix + ": " + buffer.toString());
        else
            logger.log(buffer.toString());
        clearLog();
    }
    protected void clearLog(){
        buffer.delete(0, buffer.length());
    }


    public interface LogCallback{
        public boolean isLoggable();
        public void log(String msg);
    }
}
