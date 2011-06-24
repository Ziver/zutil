package zutil.net.torrent;

import zutil.Dumpable;

/**
 * This class represents a File for download
 * 
 * @author Ziver
 */
public class TorrentFile implements Dumpable{
	private String filename;
	private long length;
	
	public TorrentFile(String filename, long length){
		this.filename = filename;
		this.length = length;
	}
	

	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public long getLength() {
		return length;
	}
	public void setLength(long length) {
		this.length = length;
	}
}