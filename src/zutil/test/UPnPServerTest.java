package zutil.test;

import java.io.File;
import java.io.IOException;

import javax.wsdl.WSDLException;

import zutil.io.MultiPrintStream;
import zutil.net.http.HttpServer;
import zutil.net.http.soap.SOAPHttpPage;
import zutil.net.ssdp.SSDPServer;
import zutil.net.upnp.UPnPMediaServer;
import zutil.net.upnp.services.UPnPContentDirectory;

public class UPnPServerTest {
	
	public static void main(String[] args) throws IOException, WSDLException{		
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
