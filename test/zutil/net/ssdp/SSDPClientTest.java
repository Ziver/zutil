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
import zutil.net.ssdp.SSDPClient;

import java.io.IOException;
import java.util.logging.Level;


/**
 * Created by Ziver on 2015-09-29.
 */
public class SSDPClientTest {

    public static void main(String[] args) throws IOException {
        System.out.println(LogUtil.getCallingClass());
        LogUtil.setGlobalLevel(Level.FINEST);
        SSDPClient ssdp = new SSDPClient();
        ssdp.requestService("upnp:rootdevice");
        ssdp.requestService("urn:schemas-wifialliance-org:device:WFADevice:1");
        ssdp.requestService("urn:dial-multiscreen-org:service:dial:1"); // Chromecast
        ssdp.requestService("urn:schemas-upnp-org:device:InternetGatewayDevice:1"); // Routers
        ssdp.start();

        ssdp.setListener(new SSDPClient.SSDPServiceListener() {
            @Override
            public void serviceDiscovered(StandardSSDPInfo service) {
                System.out.println("*********** DISCOVERY *************" );
                System.out.println("" + service);
            }

            @Override
            public void serviceLost(StandardSSDPInfo service) {
                System.out.println("*********** LOST *************" );
                System.out.println("" + service);
            }
        });
    }
}
