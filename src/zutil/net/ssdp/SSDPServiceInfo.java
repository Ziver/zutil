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

/**
 * This class contains information about a service from
 * or through the SSDP protocol
 * 
 * @author Ziver
 */
public interface SSDPServiceInfo {
	
	/**
	 * @return the URL to the Service, e.g. "http://192.168.0.1:80/index.html"
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
