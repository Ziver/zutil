package zutil.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A InputStream wrapper that counts the current position in the data stream which equals the amount of data read.
 */
public class PositionalInputStream extends FilterInputStream {

    private long pos = 0;
    private long mark = 0;


    /**
     * @param in the underlying input stream.
     */
    public PositionalInputStream(InputStream in) {
        super(in);
    }


    /**
     * @return the current position in the data stream
     */
    public synchronized long getPosition() {
        return pos;
    }


    @Override
    public int read() throws IOException {
        int b = super.read();

        synchronized(this) {
            if (b >= 0) pos += 1;
        }
        return b;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int n = super.read(b, off, len);

        synchronized(this) {
            if (n > 0) pos += n;
        }
        return n;
    }

    @Override
    public long skip(long skip) throws IOException {
        long n = super.skip(skip);

        synchronized(this) {
            if (n > 0) pos += n;
        }
        return n;
    }

    @Override
    public void mark(int readLimit) {
        super.mark(readLimit);

        synchronized(this) {
            mark = pos;
        }
    }

    @Override
    public synchronized void reset() throws IOException {
        super.reset();

        synchronized(this) {
            // Only update the position if mark is supported,
            // as reset succeeds even if mark is not supported.
            if (markSupported())
                pos = mark;
        }
    }
}
