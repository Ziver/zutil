package zutil.net.ssdp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import zutil.io.StringOutputStream;
import zutil.log.LogUtil;
import zutil.net.http.HTTPHeaderParser;
import zutil.net.http.HttpPrintStream;
import zutil.net.threaded.ThreadedUDPNetwork;
import zutil.net.threaded.ThreadedUDPNetworkThread;

/**
 * An SSDP client class that will request
 * service information.
 * 
 * @author Ziver
 */
public class SSDPClient extends ThreadedUDPNetwork implements ThreadedUDPNetworkThread{
	public static final Logger logger = LogUtil.getLogger();
	// Contains all the received services
	private HashMap<String, LinkedList<SSDPServiceInfo>> services_st;
	private HashMap<String, SSDPServiceInfo> 			 services_usn;
	
	
	public static void main(String[] args) throws IOException{
		System.out.println(LogUtil.getCalingClass());
		LogUtil.setGlobalLevel(Level.FINEST);
		SSDPClient ssdp = new SSDPClient();
		ssdp.requestService("upnp:rootdevice");
		ssdp.start();
		
		for(int i=0; true ;++i){
			while( i==ssdp.getServicesCount("upnp:rootdevice") ){ try{Thread.sleep(100);}catch(Exception e){} }
			logger.log(Level.FINEST, "************************" );	
			logger.log(Level.FINEST, ""+ssdp.getServices("upnp:rootdevice").get(i) );	
		}
	}
	
	/**
	 * Creates new instance of this class. An UDP
	 * listening socket at the SSDP port.
	 * 
	 * @throws IOException
	 */
	public SSDPClient() throws IOException{
		super( null );
		super.setThread( this );
		
		services_st = new HashMap<String, LinkedList<SSDPServiceInfo>>();
		services_usn = new HashMap<String, SSDPServiceInfo>();
	}
	
	/**
	 * Sends an request for an service
	 * 
	 * @param st is the SearchTarget of the service
	 * 
	 * ***** REQUEST: 
	 * M-SEARCH * HTTP/1.1 
	 * Host: 239.255.255.250:reservedSSDPport 
	 * Man: "ssdp:discover" 
	 * ST: ge:fridge 
	 * MX: 3 
	 * 
	 */
	public void requestService(String st){
		try {
			services_st.put( st, new LinkedList<SSDPServiceInfo>() );
			
			// Generate an SSDP discover message
			StringOutputStream msg = new StringOutputStream();
			HttpPrintStream http = new HttpPrintStream( msg, HttpPrintStream.HTTPMessageType.REQUEST );
			http.setRequestType("M-SEARCH");
			http.setRequestURL("*");
			http.setHeader("Host", SSDPServer.SSDP_MULTICAST_ADDR+":"+SSDPServer.SSDP_PORT );
			http.setHeader("ST", st );
			http.setHeader("Man", "\"ssdp:discover\"" );
			http.setHeader("MX", "3" );

			http.close();
			logger.log(Level.FINEST, "***** REQUEST: \n"+msg);
			byte[] data = msg.toString().getBytes();
			DatagramPacket packet = new DatagramPacket( 
					data, data.length, 
					InetAddress.getByName( SSDPServer.SSDP_MULTICAST_ADDR ), 
					SSDPServer.SSDP_PORT );
			super.send( packet );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns a list of received services by 
	 * the given search target. 
	 * 
	 * @param st is the search target
	 * @return a list of received services
	 */
	public LinkedList<SSDPServiceInfo> getServices(String st){
		return services_st.get( st );
	}
	
	/**
	 * Returns the amount of services in the search target
	 * 
	 * @param st is the search target
	 * @return the amount of services
	 */
	public int getServicesCount(String st){
		if( services_st.containsKey( st ) ){
			return services_st.get( st ).size();
		}
		return 0;
	}
	
	/**
	 * Returns a service with the given USN.
	 * 
	 * @param usn is the unique identifier for the service
	 * @return an service, null if there is no such service
	 */
	public SSDPServiceInfo getService(String usn){
		return services_usn.get( usn );
	}
	
	/**
	 * Clears all the received information of the services
	 */
	public void clearServices(){
		services_usn.clear();
		services_st.clear();
	}
	
	/**
	 * Waits for responses
	 * 
	 * ***** RESPONSE; 
	 * HTTP/1.1 200 OK 
	 * Ext: 
	 * Cache-Control: no-cache="Ext", max-age = 5000 
	 * ST: ge:fridge 
	 * USN: uuid:abcdefgh-7dec-11d0-a765-00a0c91e6bf6 
	 * Location: http://localhost:80
	 */
	public void receivedPacket(DatagramPacket packet, ThreadedUDPNetwork network) {
		HTTPHeaderParser header = new HTTPHeaderParser( new String( packet.getData() ) );
		logger.log(Level.FINEST, "*********** Recived\n"+header);
		
		String usn = header.getHeader("USN");
		String st = header.getHeader("ST");
		StandardSSDPInfo service;
		// Get existing service
		if( services_usn.containsKey( usn )){
			service = (StandardSSDPInfo)services_usn.get( usn );
		}
		// Add new service
		else{
			service = new StandardSSDPInfo();
			services_usn.put( usn, service);
			if( !services_st.containsKey(st) )
				services_st.put( st, new LinkedList<SSDPServiceInfo>() );
			services_st.get( header.getHeader("ST") ).add( service );
		}
		
		service.setLocation( header.getHeader("LOCATION") );
		service.setST( st );
		service.setUSN( usn );
		service.setExpirationTime( 
				System.currentTimeMillis() +
				1000 * getCacheTime(header.getHeader("Cache-Control")) );
		logger.log(Level.FINEST, "*********** Recived\n"+service);
	}
	
	private long getCacheTime(String cache_control){
		long ret = 0;
		String[] tmp = cache_control.split(",");
		for( String element : tmp ){
			element = element.replaceAll("\\s", "").toLowerCase();
			if( element.startsWith("max-age=") ){
				ret = Long.parseLong( element.substring( "max-age=".length() ) );
			}
		}
		return ret;
	}

}
