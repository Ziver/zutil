/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Ziver Koc
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
 */

package zutil.test;

import zutil.io.MultiPrintStream;
import zutil.net.http.HttpServer;
import zutil.net.ssdp.SSDPServer;
import zutil.net.upnp.UPnPMediaServer;
import zutil.net.upnp.services.UPnPContentDirectory;
import zutil.net.ws.WebServiceDef;
import zutil.net.ws.soap.SOAPHttpPage;

import java.io.File;
import java.io.IOException;

public class UPnPServerTest {
	
	public static void main(String[] args) throws IOException{		
		UPnPMediaServer upnp = new UPnPMediaServer("http://192.168.0.60:8080/");
		MultiPrintStream.out.println("UPNP Server running");
		
		UPnPContentDirectory cds = new UPnPContentDirectory(new File("C:\\Users\\Ziver\\Desktop\\lan"));
		WebServiceDef ws = new WebServiceDef( UPnPContentDirectory.class );
		
		HttpServer http = new HttpServer(8080);
		//http.setDefaultPage(upnp);
		http.setPage("/RootDesc", upnp );
		http.setPage("/SCP/ContentDir", cds );
		SOAPHttpPage soap = new SOAPHttpPage(ws);
		soap.setObject( cds );
		soap.enableSession( false );
		http.setPage("/Action/ContentDir", soap );
		http.start();
		MultiPrintStream.out.println("HTTP Server running");
		
		SSDPServer ssdp = new SSDPServer();
		ssdp.addService( upnp );
		ssdp.start();
		MultiPrintStream.out.println("SSDP Server running");
	}
}
