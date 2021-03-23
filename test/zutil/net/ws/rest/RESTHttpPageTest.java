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

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Created by Ziver on 2016-09-27.
 */
public class RESTHttpPageTest {

    public static class TestClass implements WSInterface{
        public String hello() {
            return "hello world";
        }
    }

    @Test
    public void noInput() throws Throwable {
        RESTHttpPage rest = new RESTHttpPage(new TestClass());

        HashMap<String,String> input = new HashMap<>();
        String output = rest.execute("hello", input);
        assertEquals("\"hello world\"", output);
    }


    public static class TestEchoClass implements WSInterface{
        public String echo(@WSParamName("input") String input) {
            return "echo: "+input;
        }
    }

    @Test
    public void oneInput() throws Throwable {
        RESTHttpPage rest = new RESTHttpPage(new TestEchoClass());

        HashMap<String,String> input = new HashMap<>();
        input.put("input", "test input");
        String output = rest.execute("echo", input);
        assertEquals("\"echo: test input\"", output);
    }

}