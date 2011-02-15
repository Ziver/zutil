package zutil.net.upnp;

import zutil.net.http.HttpPage;
import zutil.net.ssdp.SSDPServiceInfo;
/**
 * This class is a UPnP Server class that will be extended 
 * by all root devices handles all the other UPnP services
 * 
 * @author Ziver
 */
public abstract class UPnPRootDevice implements HttpPage, SSDPServiceInfo{

}
