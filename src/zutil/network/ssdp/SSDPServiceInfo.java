package zutil.network.ssdp;

/**
 * This class contains information about a service from
 * or through the SSDP protocol
 * 
 * @author Ziver
 */
public interface SSDPServiceInfo {
	
	/**
	 * @return The URL to the Service, e.g. "http://192.168.0.1:80/index.html"
	 */
	public String getLocation();
	
	/**
	 * @return the Search Target, e.g. "upnp:rootdevice"
	 */
	public String getSearchTarget();
	
	/**
	 * @return the expiration time for the values in this object
	 */
	public long getExpirationTime();
	
	/**
	 * @return the USN value, e.g. "uuid:abcdefgh-7dec-11d0-a765-00a0c91e6bf6 "
	 */
	public String getUSN();
	
	/**
	 * @return only the USN UUID String
	 */
	public String getUUID();
}
