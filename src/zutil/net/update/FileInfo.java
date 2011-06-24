package zutil.net.update;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import zutil.Hasher;
import zutil.io.file.FileUtil;

/**
 * This class is used to store the files
 * and there hashes
 * 
 * @author Ziver
 */
public class FileInfo implements Serializable{
	private static final long serialVersionUID = 1L;

	private transient File file;
	private String path;
	private String hash;
	private long size;

	public FileInfo(String root, File file) throws IOException{
		path = FileUtil.relativePath(file, root);		
		hash = Hasher.MD5(file);
		size = file.length();
		this.file = file;
	}


	public String getPath() {
		return path;
	}
	public String getHash() {
		return hash;
	}
	public long getSize() {
		return size;
	}
	public File getFile(){
		return file;
	}


	public boolean equals(Object comp){
		if(comp instanceof FileInfo){
			FileInfo tmp = (FileInfo)comp;
			return path.equals(tmp.path) && hash.equals(tmp.hash);
		}
		return false;
	}

	public String toString(){
		return path;
	}
}