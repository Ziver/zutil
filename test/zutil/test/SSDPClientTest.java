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
        ssdp.start();

        for(int i=0; true ;++i){
            while( i==ssdp.getServicesCount("upnp:rootdevice") ){ try{Thread.sleep(100);}catch(Exception e){} }
            System.out.println("************************" );
            System.out.println("" + ssdp.getServices("upnp:rootdevice").get(i));
        }
    }
}
