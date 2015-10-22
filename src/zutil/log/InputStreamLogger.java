package zutil.log;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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
