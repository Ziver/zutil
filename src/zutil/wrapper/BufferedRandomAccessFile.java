package zutil.wrapper;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * This class is a buffer for the RandomeAccesFile
 * Inspiration:
 * http://www.javaworld.com/javaworld/javatips/jw-javatip26.html
 * 
 * @author Ziver
 */
public class BufferedRandomAccessFile extends RandomAccessFile{
	// The size of the buffer
	private int BUF_SIZE = 256;

	// The Buffer
	byte buffer[];
	// The end of the buffer
	int buf_end = 0;
	// The position in the buffer
	int buf_pos = 0;
	// The real file pointer position where the buffer starts
	long real_pos = 0;

	
	public BufferedRandomAccessFile(String filename, String mode) throws IOException{
		this(new File(filename), mode);
	}
	
	public BufferedRandomAccessFile(File file, String mode) throws IOException{
		super(file,mode);
		invalidate();
		buffer = new byte[BUF_SIZE];    
	}
	
	public BufferedRandomAccessFile(String filename, String mode, int bufsize) throws IOException{
		this(new File(filename), mode, bufsize);  
	}
	
	public BufferedRandomAccessFile(File file, String mode, int bufsize) throws IOException{
		super(file,mode);
		invalidate();
		BUF_SIZE = bufsize;
		buffer = new byte[BUF_SIZE];    
	}

	/**
	 * Reads the next byte in the buffer
	 * 
	 */
	public final int read() throws IOException{
		if(buf_pos >= buf_end) {
			if(fillBuffer() < 0)
				return -1;
		}
		if(buf_end == 0) {
			return -1;
		} else {
			buf_pos++;
			return buffer[buf_pos-1];
		}
	}

	/**
	 * Reads in data from the file to the buffer
	 * 
	 * @return The buffer
	 * @throws IOException
	 */
	private int fillBuffer() throws IOException {
		int n = super.read(buffer, 0, BUF_SIZE );
		if(n >= 0) {
			real_pos +=n;
			buf_end = n;
			buf_pos = 0;
		}
		return n;
	}

	/**
	 * Resets the buffer
	 * 
	 * @throws IOException
	 */
	private void invalidate() throws IOException {
		buf_end = 0;
		buf_pos = 0;
		real_pos = super.getFilePointer();
	}

	/**
	 * This class while read in b.length from the file
	 */
	public int read(byte b[]) throws IOException {
		return read(b, 0, b.length);
	}
	
	/**
	 * Reads a given length of bytes from the buffer
	 * 
	 */
	public int read(byte b[], int off, int len) throws IOException {
		int leftover = buf_end - buf_pos;
		if(len <= leftover) {
			System.arraycopy(buffer, buf_pos, b, off, len);
			buf_pos += len;
			return len;
		}
		for(int i = 0; i < len; i++) {
			int c = this.read();
			if(c != -1)
				b[off+i] = (byte)c;
			else {
				return i;
			}
		}
		return len;
	}

	/**
	 * Returns the file pointer in the file
	 */
	public long getFilePointer() throws IOException{
		long l = real_pos;
		return (l - buf_end + buf_pos) ;
	}
	
	/**
	 * Changes the file pointer to another position
	 * 
	 * @param pos The position to move the pointer to
	 */
	public void seek(long pos) throws IOException {
		int n = (int)(real_pos - pos);
		if(n >= 0 && n <= buf_end) {
			buf_pos = buf_end - n;
		} else {
			super.seek(pos);
			invalidate();
		}
	}

	/**
	 * Returns the next line in the file
	 * This method is a replacement for readLine() 
	 */
	 public final String readNextLine() throws IOException {
		String str = null;
		if(buf_end-buf_pos <= 0) {
			if(fillBuffer() < 0) {
				throw new IOException("Error filling buffer!");
			}
		}
		int lineend = -1;
		for(int i = buf_pos; i < buf_end; i++) {
			if(buffer[i] == '\n') {
				lineend = i;
				break;
			}
		}
		if(lineend < 0) {
			StringBuffer input = new StringBuffer(256);
			int c;
			while (((c = read()) != -1) && (c != '\n')) {
				input.append((char)c);
			}
			if ((c == -1) && (input.length() == 0)) {
				return null;
			}
			return input.toString();
		}
		
		if(lineend > 0 && buffer[lineend-1] == '\r'){
			str = new String(buffer, buf_pos, lineend - buf_pos -1);
		}
		else {
			str = new String(buffer, buf_pos, lineend - buf_pos);
		}
		buf_pos = lineend +1;
		return str;
	 }

}
