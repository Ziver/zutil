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

import zutil.log.LogUtil;
import zutil.net.http.HttpHeader;
import zutil.net.http.HttpHeaderParser;
import zutil.net.http.HttpPrintStream;
import zutil.net.threaded.ThreadedUDPNetwork;
import zutil.net.threaded.ThreadedUDPNetworkThread;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static zutil.net.ssdp.SSDPServer.SSDP_MULTICAST_ADDR;
import static zutil.net.ssdp.SSDPServer.SSDP_PORT;

/**
 * An SSDP client class that will request
 * service information.
 * 
 * @author Ziver
 */
public class SSDPClient extends ThreadedUDPNetwork implements ThreadedUDPNetworkThread{
    private static final Logger logger = LogUtil.getLogger();
    public static final String USER_AGENT = "Zutil SSDP Client";
    /** Mapping of search targets and list of associated services **/
    private HashMap<String, LinkedList<StandardSSDPInfo>> services_st;
    /** Map of all unique services received **/
    private HashMap<String, StandardSSDPInfo> 			  services_usn;

    private SSDPServiceListener listener;


    /**
     * Creates new instance of this class. An UDP
     * listening socket at the SSDP port.
     *
     * @throws IOException
     */
    public SSDPClient() throws IOException{
        super( SSDP_MULTICAST_ADDR, SSDP_PORT );
        super.setThread(this);

        services_st = new HashMap<>();
        services_usn = new HashMap<>();
    }

    /**
     * Sends an request for an service
     *
     * @param   searchTarget    is the SearchTarget of the service
     *
     * ***** REQUEST:
     * M-SEARCH * HTTP/1.1
     * Host: 239.255.255.250:reservedSSDPport
     * Man: "ssdp:discover"
     * ST: ge:fridge
     * MX: 3
     *
     */
    public void requestService(String searchTarget){
        requestService(searchTarget, null);
    }
    public void requestService(String searchTarget, HashMap<String,String> headers){
        try {
            // Generate an SSDP discover message
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            HttpPrintStream http = new HttpPrintStream( buffer, HttpPrintStream.HttpMessageType.REQUEST );
            http.setRequestType("M-SEARCH");
            http.setRequestURL("*");
            http.setHeader("Host", SSDP_MULTICAST_ADDR +":"+ SSDP_PORT );
            http.setHeader("ST", searchTarget );
            http.setHeader("Man", "\"ssdp:discover\"" );
            http.setHeader("MX", "3" );
            http.setHeader("USER-AGENT", USER_AGENT );
            if(headers != null) {
                for (String key : headers.keySet()) {
                    http.setHeader(key, headers.get(key));
                }
            }
            logger.log(Level.FINEST, "Sending Multicast: "+ http);
            http.flush();

            byte[] data = buffer.toByteArray();
            //System.out.println(new String(data)+"****************");
            DatagramPacket packet = new DatagramPacket(
                    data, data.length,
                    InetAddress.getByName( SSDP_MULTICAST_ADDR ),
                    SSDP_PORT );
            super.send( packet );
            http.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set a listener that will be notified when new services are detected
     */
    public void setListener(SSDPServiceListener listener){
        this.listener = listener;
    }

    /**
     * Returns a list of received services by
     * the given search target.
     *
     * @param   searchTarget    is the search target
     * @return a list of received services
     */
    public LinkedList<StandardSSDPInfo> getServices(String searchTarget){
        if(services_st.get(searchTarget) == null)
            return new LinkedList<>();
        return services_st.get(searchTarget);
    }

    /**
     * Returns the amount of services in the search target
     *
     * @param   searchTarget      is the search target
     * @return the amount of services cached
     */
    public int getServicesCount(String searchTarget){
        if(services_st.containsKey(searchTarget)){
            return services_st.get(searchTarget).size();
        }
        return 0;
    }

    /**
     * Returns a service with the given USN.
     *
     * @param   usn     is the unique identifier for a service
     * @return an service, null if there is no such service
     */
    public StandardSSDPInfo getService(String usn){
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
     * Clears all services matching the search target
     */
    public void clearServices(String st){
        if(services_st.get(st) != null) {
            for (StandardSSDPInfo service : services_st.get(st)) {
                services_usn.remove(service.getUSN());
            }
        }
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
        try {
            String msg = new String(packet.getData(), packet.getOffset(), packet.getLength());
            HttpHeaderParser headerParser = new HttpHeaderParser(msg);
            HttpHeader header = headerParser.read();

            String usn = header.getHeader("USN");
            String st = header.getHeader("ST");
            StandardSSDPInfo service = null;
            // Get existing service
            if (services_usn.containsKey(usn)) {
                service = services_usn.get(usn);
            }

            // Remove service
            if ("NOTIFY".equals(header.getRequestType()) && "ssdp:byebye".equalsIgnoreCase(header.getHeader("NTS"))){
                logger.log(Level.FINER, "Received NOTIFY:byebye (from: " + packet.getAddress() + "): " + header);
                if (service != null) {
                    services_usn.remove(usn);
                    if (services_st.containsKey(st))
                        services_st.get(st).remove(service);
                    if (listener != null)
                        listener.serviceLost(service);
                }
            }
            // Existing or new service update
            else if (header.isResponse() || "NOTIFY".equals(header.getRequestType())) {
                logger.log(Level.FINER, "Received service update (from: " + packet.getAddress() + "): " + header);
                boolean newService = false;

                // Add new service
                if (service == null){
                    newService = true;
                    service = new StandardSSDPInfo();
                    services_usn.put(usn, service);
                    if (!services_st.containsKey(st))
                        services_st.put(st, new LinkedList<StandardSSDPInfo>());
                    services_st.get(header.getHeader("ST")).add(service);
                }

                service.setLocation(header.getHeader("LOCATION"));
                service.setST(st);
                service.setUSN(usn);
                service.setInetAddress(packet.getAddress());
                if (header.getHeader("Cache-Control") != null) {
                    service.setExpirationTime(
                            System.currentTimeMillis() + 1000 * getCacheTime(header.getHeader("Cache-Control")));
                }
                service.readHeaders(header);

                if (listener != null && newService)
                    listener.serviceDiscovered(service);
            }
            else {
                logger.log(Level.FINEST, "Ignored (from: " + packet.getAddress() + "): " + header);
            }
        } catch (IOException e){
            logger.log(Level.SEVERE, null, e);
        }
    }

    private long getCacheTime(String cache_control){
        long ret = 0;
        HashMap<String,String> tmpMap = new HashMap<>();
        HttpHeaderParser.parseHeaderValues(tmpMap, cache_control, ",");
        if(tmpMap.containsKey("max-age"))
            ret = Long.parseLong( tmpMap.get("max-age") );
        return ret;
    }

    public interface SSDPServiceListener{
        /**
         * Is called when a new service is discovered. Will only be called once per service.
         */
        void serviceDiscovered(StandardSSDPInfo service);

        /**
         * Is called when a service goes down and is not available anymore.
         */
        void serviceLost(StandardSSDPInfo service);
    }
}
