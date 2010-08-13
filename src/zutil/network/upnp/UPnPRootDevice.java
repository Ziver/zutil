package zutil.network.upnp;

import zutil.network.http.HttpPage;
import zutil.network.ssdp.SSDPServiceInfo;
/**
 * This class is a UPnP Server class that will be extended 
 * by all root devices handles all the other UPnP services
 * 
 * @author Ziver
 */
public abstract class UPnPRootDevice implements HttpPage, SSDPServiceInfo{

}
