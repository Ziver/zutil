package zutil.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BoundryBufferedInputStream extends FilterInputStream{

	protected BoundryBufferedInputStream(InputStream in) {
		super(in);
	}


	public int read() throws IOException {
		return in.read();
	}

	public int read(byte b[], int off, int len) throws IOException {
		return in.read(b, off, len);
	}

	public long skip(long n) throws IOException {
		return in.skip(n);
	}

	/**
	 * Returns an estimate of the number of bytes that can be read (or
	 * skipped over) from this input stream without blocking by the next
	 * caller of a method for this input stream. The next caller might be
	 * the same thread or another thread.  A single read or skip of this
	 * many bytes will not block, but may read or skip fewer bytes.
	 * <p>
	 * This method returns the result of {@link #in in}.available().
	 *
	 * @return     an estimate of the number of bytes that can be read (or skipped
	 *             over) from this input stream without blocking.
	 * @exception  IOException  if an I/O error occurs.
	 */
	public int available() throws IOException {
		return in.available();
	}



	/**
	 * Marks the current position in this input stream. A subsequent 
	 * call to the <code>reset</code> method repositions this stream at 
	 * the last marked position so that subsequent reads re-read the same bytes.
	 * <p>
	 * The <code>readlimit</code> argument tells this input stream to 
	 * allow that many bytes to be read before the mark position gets 
	 * invalidated. 
	 * <p>
	 * This method simply performs <code>in.mark(readlimit)</code>.
	 *
	 * @param   readlimit   the maximum limit of bytes that can be read before
	 *                      the mark position becomes invalid.
	 * @see     java.io.FilterInputStream#in
	 * @see     java.io.FilterInputStream#reset()
	 */
	public synchronized void mark(int readlimit) {
		in.mark(readlimit);
	}

	/**
	 * Repositions this stream to the position at the time the 
	 * <code>mark</code> method was last called on this input stream. 
	 * <p>
	 * This method
	 * simply performs <code>in.reset()</code>.
	 * <p>
	 * Stream marks are intended to be used in
	 * situations where you need to read ahead a little to see what's in
	 * the stream. Often this is most easily done by invoking some
	 * general parser. If the stream is of the type handled by the
	 * parse, it just chugs along happily. If the stream is not of
	 * that type, the parser should toss an exception when it fails.
	 * If this happens within readlimit bytes, it allows the outer
	 * code to reset the stream and try another parser.
	 *
	 * @exception  IOException  if the stream has not been marked or if the
	 *               mark has been invalidated.
	 * @see        java.io.FilterInputStream#in
	 * @see        java.io.FilterInputStream#mark(int)
	 */
	public synchronized void reset() throws IOException {
		in.reset();
	}


}
