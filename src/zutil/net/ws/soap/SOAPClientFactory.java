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

package zutil.net.ws.soap;

import zutil.log.LogUtil;
import zutil.net.ws.WSClientFactory;
import zutil.net.ws.WSInterface;
import zutil.net.ws.WebServiceDef;

import java.net.URL;
import java.util.logging.Logger;

/**
 * This is an factory that generates clients for web services
 *
 * @author Ziver
 */
public class SOAPClientFactory {
    private static Logger logger = LogUtil.getLogger();


    /**
     * Generates a Client Object for the web service.
     *
     * @param 	<T> 	is the class of the web service definition
     * @param 	url 	is the target service serviceUrl
     * @param 	intf 	is the class of the web service definition
     * @return a client Object
     */
    public static <T extends WSInterface> T createClient(URL url, Class<T> intf){
        T obj = WSClientFactory.createClient( intf,
                new SOAPClientInvocationHandler(url, new WebServiceDef(intf)));
        return obj;
    }

}
