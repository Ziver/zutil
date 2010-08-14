package zutil.network.torrent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import zutil.FileUtil;

public class Torrent {
	// Name of the torrent
	private String name;
	// Comment
	private String comment;
	// Creation date as unix timestamp
	private long date;
	// Files in the torrent
	private ArrayList<String> file_list;
	// Size of of the full torrent (after download)
	private long size;
	// Signature of the software which created the torrent
	private String created_by;
	// tracker (the tracker the torrent has been received from)
	private String main_tracker;
	// List of known trackers for the torrent
	private ArrayList<String> tracker_list;
	private HashMap<String,Object> info_hash;
	// Torrent is marked as 'private'.
	private boolean is_private;
	
	public Torrent(File torrent) throws IOException{
		this(FileUtil.getFileContent(	torrent ));
	}

	public Torrent(String data){
		reset();
		decode(data);
	}

	private void reset(){
		// Reset
		name = "";
		comment = "";
		date = 0;
		file_list = new ArrayList<String>();
		size = 0;
		created_by = "";
		main_tracker = "";
		tracker_list = new ArrayList<String>();
		info_hash = new HashMap<String,Object>();
		is_private = false;
	}
	
	@SuppressWarnings("unchecked")
	private void decode(String data){
		HashMap<?,?> dataMap = (HashMap<?,?>)TorrentParser.decode(data);
		
		name = (String)dataMap.get("name");
		comment = (String)dataMap.get("comment");
		date = (Long)dataMap.get("creation date"); 
		file_list = new ArrayList<String>();
		size = (Long)dataMap.get("length");
		created_by = (String)dataMap.get("created by");
		main_tracker = (String)dataMap.get("announce");
		tracker_list = (ArrayList<String>)dataMap.get("announce-list");
		info_hash = (HashMap<String, Object>)dataMap.get("info");
		is_private = (((Integer)dataMap.get("private")) != 0);
	}
	
	// ************** GETTER **************
	public String getName(){
		return name;
	}	
	public String getComments(){
		return comment;
	}	
	public long getDate(){
		return date;
	}
	public ArrayList<String> getFileList(){
		return file_list;
	}
	public long getSize(){
		return size;
	}
	public String getAuthor(){
		return created_by;
	}
	public String getMainTracker(){
		return main_tracker;
	}
	public ArrayList<String> getTrackerList(){
		return tracker_list;
	}
	public HashMap<String,Object> getInfoHash(){
		return info_hash;
	}
	public boolean isPrivate(){
		return is_private;
	}
	// ************************************
}
