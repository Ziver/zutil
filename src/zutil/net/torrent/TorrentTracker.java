package zutil.net.torrent;

import java.io.IOException;
import java.net.URL;

import zutil.net.http.HttpClient;
import zutil.net.http.HttpHeaderParser;

/**
 * This tracker represents a tracker client 
 * that connects to a tracker
 * 
 * @author Ziver
 */
public class TorrentTracker {
	/** The address to the tracker **/
	private URL trackerURL;
	
	
	
	private void update() throws IOException {
		HttpClient request = HttpClient.GET();
		request.setURL( trackerURL );
		HttpHeaderParser response = request.send();
	}
}
