package zutil.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * TODO: boundry
 * 
 * @author Ziver
 *
 */
public class BoundaryBufferedInputStream extends FilterInputStream{
	/** The size of the buffer in Byte */
	public static final int DEFAULT_BUF_SIZE = 64*1024;

	/** The Buffer */
	protected byte buffer[];
	/** The end of the buffer */
	protected int buf_end = 0;
	/** The position in the buffer */
	protected int buf_pos = 0;
	/** The boundary */
	protected byte[] boundary;


	/**
	 * Creates a instance of this class with a default buffer size of 64K
	 * 
	 * @param 	in			is the InputStream that the buffer will use
	 */
	public BoundaryBufferedInputStream(InputStream in){
		this(in, DEFAULT_BUF_SIZE);
	}

	/**
	 * Creates a instance of this class
	 * 
	 * @param 	in			is the InputStream that the buffer will use
	 * @param	buf_size	speifies the buffer size
	 */
	public BoundaryBufferedInputStream(InputStream in, int buf_size){
		super(in);
		buf_end = 0;
		buf_pos = 0;
		buffer = new byte[buf_size];
	}

	/**
	 * Moves the remaining data to the beginning of the 
	 * buffer and then fills the buffer with data from 
	 * the source stream to the buffer
	 * 
	 * @return 			the size of the buffer
	 * @throws IOException
	 */
	protected int fillBuffer() throws IOException {
		int leftover = buf_end - buf_pos;
		System.arraycopy(buffer, buf_pos, buffer, 0, buf_end);
		int n = super.read(buffer, leftover, buffer.length );
		if(n+leftover >= 0) {
			buf_end = leftover + n;
			buf_pos = 0;
		}
		return n+leftover;
	}

	
	/**
	 * @return 			the next byte in the buffer
	 */
	public final int read() throws IOException{
		if(buf_pos >= buf_end-boundary.length) {
			if(fillBuffer() < 0)
				return -1;
		}
		if(buf_end == 0) {
			return -1;
		} else {
			return buffer[buf_pos++];
		}
	}

	/**
	 * Fills the given array with data from the buffer
	 * 
	 * @param 	b 		is the array that will be filled
	 * @return 			the amount of bytes read or -1 if EOF
	 */	
	public int read(byte b[]) throws IOException {
		return read(b, 0, b.length);
	}

	/**
	 * Reads a given length of bytes from the buffer
	 * 
	 * @param 	b 		is the array that will be filled
	 * @param 	off 	is the offset in the array
	 * @param 	len 	is the amount to read
	 * @return 			the amount of bytes read or -1 if EOF
	 */
	public int read(byte b[], int off, int len) throws IOException {
		if(buf_pos >= buf_end-boundary.length) {
			if(fillBuffer() < 0)
				return -1; // EOF
		}
		int leftover = buf_end - buf_pos;
		// Copy from buffer
		if(len <= leftover) {
			System.arraycopy(buffer, buf_pos, b, off, len);
			buf_pos += len;
			return len;
		}
		
		System.arraycopy(buffer, buf_pos, b, off, leftover);
		int n = super.read(b, off+leftover, len-leftover );
		fillBuffer();
		if( n >= 0 )
			return leftover + n;
		return leftover;
	}

	/**
	 * TODO: Skips over the boundary
	 */
	public void next(){
		
	}
	
	/**
     * @param	n 	the number of bytes to be skipped.
     * @return		the actual number of bytes skipped.
	 */
	public long skip(long n) throws IOException {
		int leftover = buf_end - buf_pos;
		if(n > leftover){
			buf_pos = buf_end;
			return leftover;
		}
		return buf_pos += n;
	}
	
	/**
	 * Sets the boundary for the stream
	 */
	public void setBoundary(String b){
		this.boundary = b.getBytes();
	}
	
	/**
	 * Sets the boundary for the stream
	 */
	public void setBoundary(byte[] b){
		boundary = new byte[b.length];
		System.arraycopy(b, 0, boundary, 0, b.length);
	}

	/**
	 * @return     an estimate of the number of bytes that can be read (or skipped
	 *             over) from this input stream without blocking.
	 * @exception  IOException  if an I/O error occurs.
	 */
	public int available() throws IOException {
		return buf_end - buf_pos;
	}

	/**
     * Tests if this input stream supports the mark and 
     * reset methods.
     */
	public boolean markSupported(){
		return false;
	}
}
