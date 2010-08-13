package zutil.network.ssdp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import zutil.MultiPrintStream;
import zutil.io.StringOutputStream;
import zutil.log.LogUtil;
import zutil.network.http.HTTPHeaderParser;
import zutil.network.http.HttpPrintStream;
import zutil.network.threaded.ThreadedUDPNetworkThread;
import zutil.network.threaded.ThreadedUDPNetwork;

/**
 * A Server class that announces an service by the SSDP 
 * protocol specified at:
 * http://coherence.beebits.net/chrome/site/draft-cai-ssdp-v1-03.txt
 * ftp://ftp.pwg.org/pub/pwg/www/hypermail/ps/att-0188/01-psi_SSDP.pdf
 * 
 * @author Ziver
 * 
 * ********* Message clarification:
 * ****** Incoming:
 * ST: Search Target, this is object of the discovery request, (e.g., ssdp:all, etc.)
 * HOST: This is the SSDP multicast address
 * MAN: Description of packet type, (e.g., "ssdp:discover", )
 * MX: Wait these few seconds and then send response
 *
 * ****** Outgoing:
 * EXT: required by HTTP - not used with SSDP
 * SERVER: informational
 * LOCATION: This is the URL to request the QueryEndpointsInterface endpoint
 * USN: advertisement UUID
 * CACHE-CONTROL: max-age = seconds until advertisement expires
 * NT: Notify target same as ST
 * NTS: same as Man but for Notify messages
 */
public class SSDPServer extends ThreadedUDPNetwork implements ThreadedUDPNetworkThread{
	public static final Logger logger = LogUtil.getLogger();
	public static final String SERVER_INFO = "SSDP Java Server by Ziver Koc";
	public static final int DEFAULT_CACHE_TIME = 60*30; // 30 min
	public static final int BUFFER_SIZE = 512;
	public static final String SSDP_MULTICAST_ADDR = "239.255.255.250";
	public static final int SSDP_PORT = 1900;

	// instance specific values
	private int cache_time;
	private NotifyTimer notifyTimer = null;
	/** HashMap that contains services as < SearchTargetName, SSDPServiceInfo > */
	private HashMap<String, SSDPServiceInfo> services;


	public static void main(String[] args) throws IOException{
		SSDPServer ssdp = new SSDPServer();
		StandardSSDPInfo service = new StandardSSDPInfo();
		service.setLocation("nowhere");
		service.setST("upnp:rootdevice");
		ssdp.addService(service);
		ssdp.start();
		MultiPrintStream.out.println("SSDP Server running");
	}

	public SSDPServer() throws IOException{
		super( null, SSDP_MULTICAST_ADDR, SSDP_PORT );
		super.setThread( this );

		services = new HashMap<String, SSDPServiceInfo>();

		setChacheTime( DEFAULT_CACHE_TIME );
		enableNotify( true );
	}

	/**
	 * Adds an service that will be announced.
	 * 
	 * @param searchTarget is the ST value in SSDP
	 * @param location is the location of the service
	 */
	public void addService(SSDPServiceInfo service){
		services.put( service.getSearchTarget(), service );
	}
	/**
	 * Remove a service from being announced. This function will
	 * send out an byebye message to the clients that the service is down.
	 * 
	 * @param searchTarget is the ST value in SSDP
	 */
	public void removeService(String searchTarget){
		sendByeBye( searchTarget );
		services.remove( searchTarget );
	}

	/**
	 * Sets the cache time that will be sent to 
	 * the clients. If notification is enabled then an
	 * notification message will be sent every cache_time/2 seconds
	 * 
	 * @param time is the time in seconds
	 */
	public void setChacheTime(int time){
		cache_time = time;
		if( isNotifyEnabled() ){
			enableNotify(false);
			enableNotify(true);
		}
	}

	/**
	 * Enable or disable notification messages to clients
	 * every cache_time/2 seconds
	 */
	public void enableNotify(boolean enable){
		if( enable && notifyTimer==null ){
			notifyTimer = new NotifyTimer();
			Timer timer = new Timer();
			timer.schedule(new NotifyTimer(), 0, cache_time*1000/2);
		}else if( !enable && notifyTimer!=null ){
			notifyTimer.cancel();
			notifyTimer = null;
		}
	}
	/**
	 * @return if notification messages is enabled
	 */
	public boolean isNotifyEnabled(){
		return notifyTimer != null;
	}

	/**
	 * Handles the incoming packets like this:
	 * 
	 * ***** REQUEST: 
	 * M-SEARCH * HTTP/1.1 
	 * Host: 239.255.255.250:reservedSSDPport 
	 * Man: "ssdp:discover" 
	 * ST: ge:fridge 
	 * MX: 3 
	 * 
	 * ***** RESPONSE; 
	 * HTTP/1.1 200 OK 
	 * Ext: 
	 * Cache-Control: no-cache="Ext", max-age = 5000 
	 * ST: ge:fridge 
	 * USN: uuid:abcdefgh-7dec-11d0-a765-00a0c91e6bf6 
	 * Location: http://localhost:80
	 * 
	 */
	public void receivedPacket(DatagramPacket packet, ThreadedUDPNetwork network) {
		try {
			String msg = new String( packet.getData() );

			HTTPHeaderParser header = new HTTPHeaderParser( msg );
			logger.log(Level.FINEST, "**** Received:\n"+header);

			// ******* Respond
			// Check that the message is an ssdp discovery message
			if( header.getRequestType().equalsIgnoreCase("M-SEARCH") ){
				String man = header.getHeader("Man").replace("\"", "");
				String st = header.getHeader("ST");
				// Check that its the correct URL and that its an ssdp:discover message
				if( header.getRequestURL().equals("*") && man.equalsIgnoreCase("ssdp:discover") ){
					// Check if the requested service exists
					if( services.containsKey( st ) ){
						// Generate the SSDP response
						StringOutputStream response = new StringOutputStream();
						HttpPrintStream http = new HttpPrintStream( response );
						http.setStatusCode(200);
						http.setHeader("Server", SERVER_INFO );
						http.setHeader("ST", st );
						http.setHeader("Location", services.get(st).getLocation() );
						http.setHeader("EXT", "" );
						http.setHeader("Cache-Control", "max-age = "+cache_time );
						http.setHeader("USN", services.get(st).getUSN() );

						http.close();
						logger.log(Level.FINEST, "********** Response:\n"+response);
						byte[] data = response.toString().getBytes();
						packet = new DatagramPacket( 
								data, data.length, 
								packet.getAddress(), 
								packet.getPort());
						network.send( packet );
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * This thread is a timer task that sends an 
	 * notification message to the network every 
	 * cache_time/2 seconds.
	 * 
	 * @author Ziver
	 */
	private class NotifyTimer extends TimerTask {
		public void run(){
			sendNotify();
		}
	}
	/**
	 * Sends keepalive messages to update the cache of the clients
	 */
	public void sendNotify(){
		for(String st : services.keySet()){
			sendNotify( st );
		}
	}
	/**
	 * Sends an keepalive message to update the cache of the clients
	 *  
	 * @param searchTarget is the ST value of the service
	 * 
	 * ********** Message ex:
	 * NOTIFY * HTTP/1.1 
	 * Host: 239.255.255.250:reservedSSDPport 
	 * NT: blenderassociation:blender 
	 * NTS: ssdp:alive 
	 * USN: someunique:idscheme3 
	 * Location: http://localhost:80
	 * Cache-Control: max-age = 7393 
	 */
	public void sendNotify(String searchTarget){
		try {
			// Generate the SSDP response
			StringOutputStream msg = new StringOutputStream();
			HttpPrintStream http = new HttpPrintStream( msg, HttpPrintStream.HTTPMessageType.REQUEST );
			http.setRequestType("NOTIFY");
			http.setRequestURL("*");
			http.setHeader("Server", SERVER_INFO );
			http.setHeader("Host", SSDP_MULTICAST_ADDR+":"+SSDP_PORT );
			http.setHeader("NT", searchTarget );
			http.setHeader("NTS", "ssdp:alive" );
			http.setHeader("Location", services.get(searchTarget).getLocation() );
			http.setHeader("Cache-Control", "max-age = "+cache_time );
			http.setHeader("USN", services.get(searchTarget).getUSN() );

			http.close();
			logger.log(Level.FINEST, "******** Notification:\n"+msg);
			byte[] data = msg.toString().getBytes();
			DatagramPacket packet = new DatagramPacket( 
					data, data.length, 
					InetAddress.getByName( SSDP_MULTICAST_ADDR ), 
					SSDP_PORT );
			super.send( packet );

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Shutdown message is sent to the clients that all 
	 * the service is shutting down.
	 */
	public void sendByeBye(){
		for(String st : services.keySet()){
			sendByeBye( st );
		}
	}	
	/**
	 * Shutdown message is sent to the clients that the service is shutting down
	 * 
	 * @param searchTarget is the ST value of the service
	 * 
	 * **********  Message ex:
	 * NOTIFY * HTTP/1.1 
	 * Host: 239.255.255.250:reservedSSDPport 
	 * NT: someunique:idscheme3 
	 * NTS: ssdp:byebye 
	 * USN: someunique:idscheme3 
	 */
	public void sendByeBye(String searchTarget){
		try {
			// Generate the SSDP response
			StringOutputStream msg = new StringOutputStream();
			HttpPrintStream http = new HttpPrintStream( msg, HttpPrintStream.HTTPMessageType.REQUEST );
			http.setRequestType("NOTIFY");
			http.setRequestURL("*");
			http.setHeader("Server", SERVER_INFO );
			http.setHeader("Host", SSDP_MULTICAST_ADDR+":"+SSDP_PORT );
			http.setHeader("NT", searchTarget );
			http.setHeader("NTS", "ssdp:byebye" );
			http.setHeader("USN", services.get(searchTarget).getUSN() );

			http.close();
			logger.log(Level.FINEST, "******** ByeBye:\n"+msg);
			byte[] data = msg.toString().getBytes();
			DatagramPacket packet = new DatagramPacket( 
					data, data.length, 
					InetAddress.getByName( SSDP_MULTICAST_ADDR ), 
					SSDP_PORT );
			super.send( packet );

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
