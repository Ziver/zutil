/*******************************************************************************
 * Copyright (c) 2011 Ziver Koc
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
package zutil.net.torrent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import zutil.io.MultiPrintStream;
import zutil.io.file.FileUtil;
import zutil.parser.BEncodedParser;
import zutil.parser.DataNode;

public class TorrentMetainfo {
	/** Comment (optional) **/
	private String comment;
	/** Signature of the software which created the torrent (optional) **/
	private String created_by;
	/** Creation date as Unix timestamp (optional) **/
	private long creation_date;
	/** The main Tracker (the tracker the torrent has been received from) **/
	private String announce;
	/** List of known trackers for the torrent (optional) **/
	private ArrayList<String> announce_list;
	/** The encoding of the Strings (optional) **/
	private String encoding;

	/** Size of of the full torrent (after download) **/
	private long size;
	/** Number of bytes in each piece **/
	private long piece_length;
	/** String consisting of the concatenation of all 20-byte SHA1 hash values, one per piece **/
	private ArrayList<String> piece_hashes;
	/** Torrent is private. No other trackers allowed other then those listed. (optional) **/
	private boolean is_private;

	// Files in the torrent
	private ArrayList<TorrentFile> file_list;



	public TorrentMetainfo(File torrent) throws IOException{
		this(FileUtil.getFileContent(	torrent ));
	}

	public TorrentMetainfo(String data){
		decode(data);
	}


	private void decode(String data){
		DataNode metainfo = BEncodedParser.read(data);
		
		// Main values
		announce 	= metainfo.getString("announce");
		created_by 	= metainfo.getString("created by");
		comment 	= metainfo.getString("comment");
		encoding 	= metainfo.getString("encoding");
		if( metainfo.get("creation date") != null )
			creation_date = metainfo.getLong("creation date");
		if( metainfo.get("announce-list") != null ){
			DataNode tmp = metainfo.get("announce-list");
			announce_list = new ArrayList<String>();
			for( DataNode tracker : tmp )
				announce_list.add( tracker.getString() );
		}

		// info data
		DataNode info 	= metainfo.get("info");
		String name 	= info.getString("name");
		piece_length 	= info.getLong("piece length");
		if( info.get("private") != null )
			is_private = (info.getInt("private") != 0);
		// Split the hashes
		String hashes = info.getString("pieces");
		piece_hashes = new ArrayList<String>();
		for(int i=0; i<hashes.length() ;i++){
			String hash = "";
			for(; i%20!=0 ;i++)
				hash += hashes.charAt(i);
			piece_hashes.add(hash);
		}

		// File data
		file_list = new ArrayList<TorrentFile>();
		// Single-file torrent
		if( info.get("files") == null ){
			Long length = info.getLong("length");
			file_list.add( new TorrentFile(name, length) );
		}
		// Multi-file torrent
		else{
			DataNode files = info.get("files");
			for( DataNode file : files ){
				String filename = "";
				DataNode tmp = file.get("path");
				// File in subdir
				if( tmp.isList() ){
					Iterator<DataNode> it = tmp.iterator();
					while( it.hasNext() ){
						filename += it.next().getString();
						if(it.hasNext()) filename += File.separator;
					}
				}
				// File in root dir
				else
					filename = tmp.getString();
				Long length = file.getLong("length");
				filename = name + File.separator + filename;
				file_list.add( new TorrentFile(filename, length) );
			}
		}
	}



	public String getComment() {
		return comment;
	}
	public String getCreated_by() {
		return created_by;
	}
	public long getCreation_date() {
		return creation_date;
	}
	public String getAnnounce() {
		return announce;
	}
	public ArrayList<String> getAnnounce_list() {
		return announce_list;
	}
	public String getEncoding() {
		return encoding;
	}
	public long getSize() {
		return size;
	}
	public long getPiece_length() {
		return piece_length;
	}
	public ArrayList<String> getPiece_hashes() {
		return piece_hashes;
	}
	public boolean isIs_private() {
		return is_private;
	}
	public ArrayList<TorrentFile> getFile_list() {
		return file_list;
	}

	/**
	 * Example use
	 */
	public static void main(String[] args){
		try {
			TorrentMetainfo tmp = new TorrentMetainfo(FileUtil.find("C:\\Users\\Ziver\\Desktop\\test.torrent"));
			MultiPrintStream.out.dump(tmp);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
