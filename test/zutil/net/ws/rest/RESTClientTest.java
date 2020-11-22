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

package zutil.net.ws.rest;

import org.junit.Test;
import zutil.net.ws.WSInterface;
import zutil.net.ws.WebServiceDef;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Ziver on 2016-09-27.
 */
public class RESTClientTest {

    public interface OpenWeartherMap extends WSInterface {

        @WSNamespace("")
        int weather(@WSParamName("q") String city);
    }


    //@Test
    public void testREST() throws MalformedURLException {
        OpenWeartherMap restObj = RESTClientFactory.createClient(
                new URL("http://samples.openweathermap.org/data/2.5/"),
                OpenWeartherMap.class);

        assertNotNull(restObj.weather("London,uk"));
    }

}