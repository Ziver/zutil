/*
 * Copyright (c) 2015 Ziver
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

import org.junit.Test;
import zutil.parser.Templator;

import static org.junit.Assert.*;

/**
 * Created by Ziver on 2015-03-23.
 */
public class TemplatorTest {
    class TestClass{
        public String attr;
    }

    @Test
    public void emptyAttributeTest(){
        Templator tmpl = new Templator("<HTML>{{test}}</HTML>");
        assertEquals("<HTML>{{test}}</HTML>", tmpl.compile());
    }
    @Test
    public void simpleAttributeTest() {
        Templator tmpl = new Templator("<HTML>{{test}}</HTML>");
        tmpl.setData("test", "1234");
        assertEquals("<HTML>1234</HTML>", tmpl.compile());
    }
    @Test
    public void objectAttributeTest(){
        Templator tmpl = new Templator("<HTML>{{test.attr}}</HTML>");
        TestClass obj = new TestClass();
        obj.attr = "1234";
        tmpl.setData("test", obj);
        assertEquals("<HTML>1234</HTML>", tmpl.compile());
    }
    @Test
    public void incorrectTagsTest(){
        assertEquals("<HTML>{{</HTML>",
                new Templator("<HTML>{{</HTML>").compile());
        assertEquals("<HTML>}}</HTML>",
                new Templator("<HTML>}}</HTML>").compile());
        assertEquals("<HTML></HTML>}}",
                new Templator("<HTML></HTML>}}").compile());
        assertEquals("<HTML></HTML>{{",
                new Templator("<HTML></HTML>{{").compile());
        assertEquals("<HTML>{</HTML>",
                new Templator("<HTML>{</HTML>").compile());
        assertEquals("<HTML>}</HTML>",
                new Templator("<HTML>}</HTML>").compile());
        assertEquals("<HTML>{}</HTML>",
                new Templator("<HTML>{}</HTML>").compile());
        assertEquals("<HTML>{test}</HTML>",
                new Templator("<HTML>{test}</HTML>").compile());
    }


    @Test
    public void emptyConditionTest(){
        Templator tmpl = new Templator(
                "<HTML>{{#key}}123456789{{/key}}</HTML>");
        assertEquals(
                "<HTML></HTML>",
                tmpl.compile());
    }
    @Test
    public void simpleConditionTest(){
        Templator tmpl = new Templator(
                "<HTML>{{#key}}123456789{{/key}}</HTML>");
        tmpl.setData("key", true);
        assertEquals(
                "<HTML></HTML>",
                tmpl.compile());
    }
    @Test
    public void incompleteConditionTest(){
        assertEquals("<HTML>{{#key}}</HTML>",
                new Templator("<HTML>{{#key}}</HTML>").compile());
        assertEquals("<HTML>{{/key}}</HTML>",
                new Templator("<HTML>{{/key}}</HTML>").compile());
        assertEquals("",
                new Templator("{{#key}}{{/key}}").compile());
        assertEquals("<HTML></HTML>",
                new Templator("<HTML>{{#key}}{{/key}}</HTML>").compile());
    }

}
