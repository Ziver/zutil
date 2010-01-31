package zutil.wrapper;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This class saves all the input data in to an StringBuffer
 * 
 * @author Ziver
 *
 */
public class StringOutputStream extends OutputStream{
	// The buffer
	protected StringBuffer buffer;
	
	/**
	 * Creates an new instance of this class
	 */
	public StringOutputStream(){
		buffer = new StringBuffer();
	}
	
	@Override
	public void write(int b) throws IOException {
		buffer.append( b );
	}

	@Override
	public void write(byte[] b) {
		buffer.append( new String(b) );
	}
	
	@Override
	public void write(byte[] b, int off, int len) {
		buffer.append( new String(b, off, len) );
	}

	/**
	 * @return the String with the data
	 */
	@Override
	public String toString() {
		return buffer.toString();
	}
}
