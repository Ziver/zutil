package zutil.struct;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class DynamicByteArrayStream extends InputStream{
	/** The byte array container */
	private ArrayList<byte[]> bytes;
	/** The current size of the stream */
	private int size;
	/** points to the current index in the ArrayList */
	private int byteArrayIndex;
	/** points locally in the current index in the ArrayList */
	private int localPointer;
	/** The current position */
	private int pos;

	/**
	 * Create a new instance of DynamicByteArrayStream
	 */
	public DynamicByteArrayStream(){
		bytes = new ArrayList<byte[]>();
		size = 0;
		byteArrayIndex = 0;
		localPointer = 0;
		pos = 0;
	}

	/**
	 * Append an byte array to the stream
	 * @param b The byte array to add
	 */
	public synchronized void add(byte[] b){
		bytes.add(b);
		size += b.length;
	}

	@Override
	public synchronized int read() throws IOException {
		if(pos >= size)	return -1;
		
		int ret = bytes.get(byteArrayIndex)[localPointer] & 0xff;
		pos++;
		localPointer++;
		if(localPointer >= bytes.get(byteArrayIndex).length){
			byteArrayIndex++;
			localPointer = 0;
		}
		return ret;
	}

	public synchronized int read(byte b[], int off, int len) {
		//System.out.println("*****************************************************");
		//System.out.println("off: "+off+" len: "+len);
		//System.out.println("size: "+size+" arraylen: "+bytes.size());
		//System.out.println("pos: "+pos+" localPointer: "+localPointer);
		if(len <= 0) return 0;
		if(pos >= size)	return -1;
		
		int bytes_read = 0;
		if(pos+len >= size) len = size - pos;
		for(int i=0; i<len ;i++){
			byte[] src = bytes.get(byteArrayIndex);
			if(localPointer+len-i >= src.length){
				//System.out.println("1");
				int length = src.length-localPointer;
				System.arraycopy(src, localPointer, b, off+i, length);
				
				localPointer = 0;
				byteArrayIndex++;
				bytes_read += length;
				i += length;
			}
			else{
				//System.out.println("2");
				int length = len-i;
				System.arraycopy(src, localPointer, b, off+i, length);
				
				localPointer += length;
				bytes_read += length;
				i += length;
			}
		}
		pos += len;
		//System.out.println("new_pos: "+pos+" read: "+bytes_read);
		return bytes_read;
	}

	public synchronized int available() {
		return size - pos;
	}

	/**
	 * Clears this stream from the byte arrays
	 */
	public synchronized void clear(){
		size = 0;
		reset();
		bytes.clear();
	}
	
	public synchronized void reset() {
		byteArrayIndex = 0;
		localPointer = 0;
		pos = 0;
	}

	public void close() throws IOException {
		//bytes = null;
	}
}
