package zutil.io;

import java.io.OutputStream;

/**
 * An OutputStream that does nothing but discard data. Similar to /dev/null
 *
 * Created by Ziver on 2016-07-08.
 */
public class NullOutputStream extends OutputStream {

    @Override
    public void write(int b) { }

    @Override
    public void write(byte b[]) { }

    @Override
    public void write(byte b[], int off, int len) { }
}
