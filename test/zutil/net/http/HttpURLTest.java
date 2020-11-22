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

package zutil.net.http;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;


public class HttpURLTest {

    @Test
    public void fullURLTest() {
        HttpURL url = new HttpURL();
        url.setProtocol("http");
        assertEquals( "http://127.0.0.1/", url.getURL() );

        url.setHost("koc.se");
        assertEquals( "http://koc.se/", url.getURL() );

        url.setPort( 80 );
        assertEquals( "http://koc.se:80/", url.getURL() );

        url.setPath("test/index.html");
        assertEquals( "http://koc.se:80/test/index.html", url.getURL() );

        url.setParameter("key", "value");
        assertEquals( "http://koc.se:80/test/index.html?key=value", url.getURL() );

        url.setAnchor( "anch" );
        assertEquals( "http://koc.se:80/test/index.html?key=value#anch", url.getURL() );
    }

    @Test
    public void urlParameterTest() {
        HttpURL url = new HttpURL();
        url.setParameter("key1", "value1");
        assertEquals( "key1=value1", url.getParameterString() );

        url.setParameter("key1", "value1");
        assertEquals( "key1=value1", url.getParameterString() );

        url.setParameter("key2", "value2");
        assertThat(url.getParameterString(), allOf(containsString("key2=value2"), containsString("key1=value1")));

    }


}
