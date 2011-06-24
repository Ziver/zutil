package zutil.net.update;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import zutil.io.file.FileUtil;

/**
 * This class is used to store the files
 * and there hashes
 * 
 * @author Ziver
 */
class FileListMessage implements Serializable{
	private static final long serialVersionUID = 1L;

	private ArrayList<FileInfo> fileList;
	private long totalSize;

	private FileListMessage(){}
	
	/**
	 * Returns a ArrayList of FileInfo object for all the files in the specified folder
	 * 
	 * @param 		path		is the path to scan
	 **/
	public FileListMessage(String path) throws IOException{
		fileList = new ArrayList<FileInfo>();

		List<File> files = FileUtil.search(FileUtil.find(path));
		long totalSize = 0;
		for(File file : files){
			FileInfo fileInfo = new FileInfo(path, file);
			fileList.add( fileInfo );
			totalSize += fileInfo.getSize();
		}
		this.totalSize = totalSize;
	}


	public long getTotalSize() {
		return totalSize;
	}
	
	public ArrayList<FileInfo> getFileList(){
		return fileList;
	}
	
	/**
	 * Compares the files and returns the files that differ from this file list
	 * 
	 * @param 		comp		is the file list to compare with
	 * @return
	 */
	public FileListMessage getDiff( FileListMessage comp){
		FileListMessage diff = new FileListMessage();

		long diffSize = 0;
		diff.fileList = new ArrayList<FileInfo>();
		for(FileInfo file : this.fileList){
			if( !comp.fileList.contains( file)){
				diff.fileList.add( file );
				diffSize += file.getSize();
			}
		}
		diff.totalSize = diffSize;
		
		return diff;
	}


	public boolean equals(Object comp){
		if(comp instanceof FileListMessage){
			FileListMessage tmp = (FileListMessage)comp;
			return fileList.equals(tmp.fileList) && totalSize == tmp.totalSize;
		}
		return false;
	}
}