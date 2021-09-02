/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Ziver Koc
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

package zutil.net.upnp;

import zutil.net.http.HttpHeader;
import zutil.net.http.HttpPage;
import zutil.net.http.HttpPrintStream;
import zutil.net.ssdp.SSDPServiceInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

/**
 * This class is a UPnP Root Server that can host multiple UPnP services.
 */
public class UPnPServer implements HttpPage, SSDPServiceInfo {
    public static final String RELATIVE_URL = "upnp/rootdev";
    private static final String DEVICE_TYPE = "urn:schemas-upnp-org:device:MediaServer:1";

    private String url;
    private String nameFriendly = "ZupNp Server";
    private String manufacturer = "Ziver Koc";
    private String manufacturerUrl = "http://example.com";
    private String modelName = nameFriendly;
    private String modelDescription = "UPnP AV Media Server";
    private float modelNumber = 1.0f;
    private final String uuid = "uuid:" + UUID.nameUUIDFromBytes(this.getClass().toString().getBytes());
    private ArrayList<UPnPService> services = new ArrayList<>();


    public UPnPServer(String location) {
        url = location;
    }


    public void respond(HttpPrintStream out,
                        HttpHeader headers,
                        Map<String, Object> session,
                        Map<String, String> cookie,
                        Map<String, String> request) throws IOException {

        out.enableBuffering(true);
        out.setHeader(HttpHeader.HEADER_CONTENT_TYPE, "text/xml");

        out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        out.println("<root xmlns=\"urn:schemas-upnp-org:service:ContentDirectory:1\">");
        out.println("	<specVersion>");
        out.println("		<major>1</major>");
        out.println("		<minor>0</minor>");
        out.println("	</specVersion>");
        out.println("	<URLBase>" + url + "</URLBase>"); //" + ssdp.getLocation() + "
        out.println("	<device>");
        out.println("		<deviceType>" + DEVICE_TYPE + "</deviceType>");
        out.println("		<friendlyName>" + nameFriendly + "</friendlyName>");
        out.println("		<manufacturer>" + manufacturer + "</manufacturer>");
        out.println("		<manufacturerURL>" + manufacturerUrl + "</manufacturerURL>");
        out.println("		<modelName>ZupNP Server</modelName>");
        out.println("		<modelDescription>" + modelDescription + "</modelDescription>");
        out.println("		<modelNumber>" + modelNumber + "</modelNumber>");
        out.println("");
        out.println("		<UDN>" + getUUID() + "</UDN>");
        out.println("		<serviceList>");
        out.println("		    <service>");
        out.println("			    <serviceType>urn:schemas-upnp-org:service:ConnectionManager:1</serviceType>");
        out.println("			    <serviceId>urn:upnp-org:serviceId:CMGR_1-0</serviceId>");
        out.println("			    <SCPDURL>CMGR_Control/GetServDesc</SCPDURL>");
        out.println("			    <controlURL>CMGR_Control</controlURL>");
        out.println("			    <eventSubURL>CMGR_Event</eventSubURL>");
        out.println("		    </service>");
        out.println("		    <service>");
        out.println("			    <serviceType>urn:schemas-upnp-org:service:ContentDirectory:1</serviceType>");
        out.println("			    <serviceId>urn:upnp-org:serviceId:CDS_1-0</serviceId>");
        out.println("			    <SCPDURL>SCP/ContentDir</SCPDURL>");
        out.println("			    <controlURL>Action/ContentDir</controlURL>");
        out.println("			    <eventSubURL>Event/ContentDir</eventSubURL>");
        out.println("		    </service>");
        out.println("		</serviceList>");
        out.println("	</device>");
        out.println("</root>");
        out.flush();
    }


    public long getExpirationTime() {
        return 60 * 30; // 30min
    }
    public String getLocation() {
        return url + "RootDesc";
    }
    public String getSearchTarget() {
        return "upnp:rootdevice";
    }
    public String getUSN() {
        return getUUID() + "::upnp:rootdevice";
    }
    public String getUUID() {
        return uuid;
    }

}
