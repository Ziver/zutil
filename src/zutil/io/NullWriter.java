package zutil.io;

import java.io.Writer;

/**
 * An Writer that does nothing but discard data. Similar to /dev/null
 *
 * Created by Ziver on 2016-07-08.
 */
public class NullWriter extends Writer{

    @Override
    public void write(int c) { }
    @Override
    public void write(char cbuf[]) { }
    @Override
    public void write(char cbuf[], int off, int len) { }
    @Override
    public void write(String str) { }
    @Override
    public void write(String str, int off, int len) { }

    @Override
    public Writer append(CharSequence csq) {
        return this;
    }
    @Override
    public Writer append(CharSequence csq, int start, int end) {
        return this;
    }
    @Override
    public Writer append(char c) {
        return this;
    }

    @Override
    public void flush() { }
    @Override
    public void close() { }
}
