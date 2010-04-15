package zutil.test;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import javax.wsdl.WSDLException;

import zutil.MultiPrintStream;
import zutil.log.Logger;
import zutil.network.http.HttpServer;
import zutil.network.http.soap.SOAPHttpPage;
import zutil.network.ssdp.SSDPServer;
import zutil.network.upnp.UPnPMediaServer;
import zutil.network.upnp.services.UPnPContentDirectory;

public class UPnPServerTest {
	
	public static void main(String[] args) throws IOException, WSDLException{
		//Logger.setGlobalLogLevel(Level.FINEST);
		
		UPnPMediaServer upnp = new UPnPMediaServer("http://192.168.0.60:8080/");
		MultiPrintStream.out.println("UPNP Server running");
		
		UPnPContentDirectory cds = new UPnPContentDirectory(new File("C:\\Users\\Ziver\\Desktop\\lan"));
		
		HttpServer http = new HttpServer("http://192.168.0.60/", 8080);
		//http.setDefaultPage(upnp);
		http.setPage("/RootDesc", upnp );
		http.setPage("/SCP/ContentDir", cds );
		SOAPHttpPage soap = new SOAPHttpPage("Action/ContentDir", cds);
		soap.enableSession(false);
		http.setPage("/Action/ContentDir", soap );
		http.start();
		MultiPrintStream.out.println("HTTP Server running");
		
		SSDPServer ssdp = new SSDPServer();
		ssdp.addService( upnp );
		ssdp.start();
		MultiPrintStream.out.println("SSDP Server running");
	}
}
