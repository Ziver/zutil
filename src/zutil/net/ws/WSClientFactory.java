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

package zutil.net.ws;

import zutil.log.LogUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is an factory that generates clients for an unspecified web services
 *
 * @author Ziver
 */
public class WSClientFactory {
    private static Logger logger = LogUtil.getLogger();

    /**
     * Generates a Client Object for the web service.
     *
     * @param 	<T> 	is the class of the web service definition
     * @param 	intf 	is the class of the web service definition
     * @param 	handler is the handler that will execute the calls to the web service
     * @return a client Object
     */
    public static <T extends WSInterface> T createClient(Class<T> intf, InvocationHandler handler) {
        try {
            T obj = (T) Proxy.newProxyInstance(
                    WSClientFactory.class.getClassLoader(),
                    new Class[] { intf },
                    handler);

            return obj;
        } catch (Exception e) {
            logger.log(Level.SEVERE, null, e);
        }

        return null;
    }

}
