package zutil.log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class will log all data that passes through a InputStream.
 * The logging is done with Javas standard Logger with Level.FINEST.
 *
 * Created by Ziver on 2015-10-15.
 */
public class OutputStreamLogger extends OutputStream implements StreamLogger.LogCallback {
    private static final Logger logger = LogUtil.getLogger();

    private OutputStream out;
    private StreamLogger log;


    public OutputStreamLogger(OutputStream out){
        this(null, out);
    }
    public OutputStreamLogger(String prefix, OutputStream out){
        this.out = out;
        this.log = new StreamLogger(prefix, this);
    }


    public boolean isLoggable(){
        return logger.isLoggable(Level.FINEST);
    }
    public void log(String msg){
        logger.finest(msg);
    }

    //************** PROXY METHODS ******************
    public void write(int b) throws IOException{
        out.write(b);
        log.log(b);
    }
    public void write(byte b[]) throws IOException {
        out.write(b, 0, b.length);
        log.log(b, 0, b.length);
    }
    public void write(byte b[], int off, int len) throws IOException {
        out.write(b, off, len);
        log.log(b, off, len);
    }
    public void flush() throws IOException {
        out.flush();
        log.flushLog();
    }
    public void close() throws IOException {
        out.close();
        log.flushLog();
    }
}
