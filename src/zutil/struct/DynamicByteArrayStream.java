package zutil.struct;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class DynamicByteArrayStream extends InputStream{
	/** The byte array container */
	private ArrayList<byte[]> bytes;
	/** The current size of the stream */
	private int size;
	/** points to the current index in the Arraylist */
	private int globalPointer;
	/** points localy in the current index in the ArrayList */
	private int localPointer;
	/** The current position */
	private int currentPos;

	/**
	 * Create a new instance of DynamicByteArrayStream
	 */
	public DynamicByteArrayStream(){
		bytes = new ArrayList<byte[]>();
		size = 0;
		globalPointer = 0;
		localPointer = 0;
		currentPos = 0;
	}

	/**
	 * Append an byte array to the stream
	 * @param b The byte array to add
	 */
	public synchronized void add(byte[] b){
		bytes.add(b);
		size += b.length;
	}

	/**
	 * Clears this stream from the byte arrays
	 */
	public synchronized void clear(){
		size = 0;
		globalPointer = 0;
		localPointer = 0;
		currentPos = 0;
		
		bytes.clear();
	}
	
	@Override
	public synchronized int read() throws IOException {
		if(currentPos >= size){
			return -1;
		}
		int ret = bytes.get(globalPointer)[localPointer] & 0xff;
		currentPos++;
		localPointer++;
		if(localPointer >= bytes.get(globalPointer).length){
			globalPointer++;
			localPointer = 0;
		}
		return ret;
	}
/*
	public synchronized int read(byte b[], int off, int len) {
		System.out.println("read off:"+off+" len: "+len);
		if(currentPos+off >= size){
			return -1;
		}
		off += localPointer;
		while(off>0){
			if(bytes.get(globalPointer).length < off){
				globalPointer++;
				off -= bytes.get(globalPointer).length;
			}
			else break;
		}

		int length;
		int oldLen = len;
		while(len > 0){
			length = bytes.get(globalPointer).length;
			System.arraycopy(b, 0, bytes.get(globalPointer), 0, (length<len ? length : len));
			len -= length;
			if(len > 0) globalPointer++;
			if(bytes.size() <= globalPointer) break;
		}
		localPointer = 0;
		currentPos += ( len<0 ? oldLen : oldLen-len);
		return ( len<0 ? oldLen : oldLen-len);
	}*/

    public synchronized int available() {
    	return size - currentPos;
        }
	
	public synchronized void reset() {
		globalPointer = 0;
		localPointer = 0;
		currentPos = 0;
	}

	public void close() throws IOException {
	}
}
