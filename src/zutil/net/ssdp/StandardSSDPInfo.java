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

package zutil.net.ssdp;

import zutil.net.http.HttpPrintStream;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * This class contains information about a service from
 * or through the SSDP protocol
 * 
 * @author Ziver
 */
public class StandardSSDPInfo implements SSDPServiceInfo, SSDPCustomInfo{
	private String location;
	private String st;
	private String usn;
	private long expiration_time;
	// All header parameters
	private HashMap<String, String> headers;
	private InetAddress inetAddress;

	/**
	 * @param l is the value to set the Location variable
	 */
	public void setLocation(String l) {
		location = l;
	}
	
	/**
	 * @param st is the value to set the SearchTarget variable
	 */
	public void setST(String st) {
		this.st = st;
	}	

	/**
	 * @param usn is the value to set the USN variable
	 */
	protected void setUSN(String usn) {
		this.usn = usn;
	}
	
	/**
	 * @param time sets the expiration time of values in this object
	 */
	protected void setExpirationTime(long time) {
		expiration_time = time;
	}
	
	/**
	 * @return The URL to the Service, e.g. "http://192.168.0.1:80/index.html"
	 */
	public String getLocation(){
		return location;
	}
	
	/**
	 * @return the Search Target, e.g. "upnp:rootdevice"
	 */
	public String getSearchTarget(){
		return st;
	}
	
	/**
	 * @return the expiration time for the values in this object
	 */
	public long getExpirationTime(){
		return expiration_time;
	}
	
	/**
	 * @return the USN value, e.g. "uuid:abcdefgh-7dec-11d0-a765-00a0c91e6bf6 "
	 */
	public String getUSN(){
		if( usn==null )
			usn = genUSN();
		return usn+"::"+st;
	}
	
	/**
	 * @return only the USN UUID String
	 */
	public String getUUID(){
		if( usn==null )
			usn = genUSN();
		return usn;
	}

	/**
	 * Generates an unique USN for the service
	 * 
	 * @return an unique string that corresponds to the service
	 */
	private String genUSN(){
		return "uuid:" + UUID.nameUUIDFromBytes( (st+location+Math.random()).getBytes() );
	}
	
	public String toString(){
		return "USN: "+usn+"\nLocation: "+location+"\nST: "+st+"\nExpiration-Time: "+new Date(expiration_time);
	}

	public void setHeaders(HashMap<String, String> headers) {
		this.headers = headers;
	}

	public String getHeader(String header){
		return headers.get(header.toUpperCase());
	}


	@Override
	public void setHeaders(HttpPrintStream http) {
		try {
			if (headers != null) {
				for (String key : headers.keySet()) {
					http.setHeader(key, headers.get(key));
				}
			}
		}catch(IOException e){

		}
	}

	public InetAddress getInetAddress(){
		return inetAddress;
	}

	public void setInetAddress(InetAddress inetAddress) {
		this.inetAddress = inetAddress;
	}
}
