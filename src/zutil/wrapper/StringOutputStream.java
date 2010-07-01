package zutil.wrapper;

import java.io.OutputStream;

/**
 * This class saves all the input data in to an StringBuffer
 * 
 * @author Ziver
 *
 */
public class StringOutputStream extends OutputStream{
	// The buffer
	protected StringBuilder buffer;
	
	/**
	 * Creates an new instance of this class
	 */
	public StringOutputStream(){
		clear();
	}
	
	@Override
	public void write(int b) {
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

	public void clear(){
		buffer = new StringBuilder();
	}
	
	/**
	 * @return the String with the data
	 */
	@Override
	public String toString() {
		return buffer.toString();
	}
}
