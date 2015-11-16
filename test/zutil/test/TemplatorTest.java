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

import org.junit.Test;
import zutil.parser.Templator;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by Ziver on 2015-03-23.
 */
public class TemplatorTest {
    class TestClass{
        public String attr;
    }
    class TestFuncClass{
        public boolean isTrue(){
            return true;
        }
        public boolean isFalse(){
            return false;
        }
    }


    @Test
    public void tagIncorrectTest(){
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
    public void attributeEmptyTest(){
        Templator tmpl = new Templator("<HTML>{{test}}</HTML>");
        assertEquals("<HTML>{{test}}</HTML>", tmpl.compile());
    }
    @Test
    public void attributeSimpleTest() {
        Templator tmpl = new Templator("<HTML>{{test}}</HTML>");
        tmpl.set("test", "1234");
        assertEquals("<HTML>1234</HTML>", tmpl.compile());
    }
    @Test
    public void attributeObjectTest(){
        Templator tmpl = new Templator("<HTML>{{test.attr}}</HTML>");
        TestClass obj = new TestClass();
        obj.attr = "1234";
        tmpl.set("test", obj);
        assertEquals("<HTML>1234</HTML>", tmpl.compile());
    }



    @Test
    public void conditionIncompleteTest(){
        assertEquals("<HTML>{{#key}}</HTML>",
                new Templator("<HTML>{{#key}}</HTML>").compile());
        assertEquals("<HTML>{{/key}}</HTML>",
                new Templator("<HTML>{{/key}}</HTML>").compile());
        assertEquals("",
                new Templator("{{#key}}{{/key}}").compile());
        assertEquals("<HTML></HTML>",
                new Templator("<HTML>{{#key}}{{/key}}</HTML>").compile());
    }
    @Test
    public void conditionEmptyTest(){
        Templator tmpl = new Templator(
                "<HTML>{{#key}}123456789{{/key}}</HTML>");
        assertEquals(
                "<HTML></HTML>",
                tmpl.compile());
    }
    @Test
    public void conditionSimpleTest(){
        Templator tmpl = new Templator(
                "<HTML>{{#key}}123456789{{/key}}</HTML>");
        tmpl.set("key", "set");
        assertEquals(
                "<HTML>123456789</HTML>",
                tmpl.compile());
    }
    @Test
    public void conditionObjectTest(){
        Templator tmpl = new Templator("<HTML>{{#test.attr}}5678{{/test.attr}}</HTML>");
        TestClass obj = new TestClass();
        obj.attr = "1234";
        tmpl.set("test", obj);
        assertEquals("<HTML>5678</HTML>", tmpl.compile());

        tmpl.clear();
        tmpl.set("test", new TestClass());
        assertEquals("<HTML></HTML>", tmpl.compile());
    }
    @Test
    public void conditionBooleanTest(){
        Templator tmpl = new Templator(
                "<HTML>{{#key}}123456789{{/key}}</HTML>");
        tmpl.set("key", true);
        assertEquals(
                "<HTML>123456789</HTML>", tmpl.compile());

        tmpl.set("key", false);
        assertEquals(
                "<HTML></HTML>", tmpl.compile());
    }



    @Test
    public void conditionIteratorEmptyTest(){
        Templator tmpl = new Templator("<HTML>{{#list}}1234 {{.}} {{/list}}</HTML>");
        tmpl.set("list", new ArrayList());
        assertEquals(
                "<HTML></HTML>", tmpl.compile());
    }
    @Test
    public void conditionIteratorTest(){
        Templator tmpl = new Templator("<HTML>{{#list}}{{.}}{{/list}}</HTML>");
        tmpl.set("list", Arrays.asList(1,2,3,4,5,6,7,8,9));
        assertEquals(
                "<HTML>123456789</HTML>", tmpl.compile());
    }
    @Test
    public void conditionArrayTest(){
        Templator tmpl = new Templator("<HTML>{{#list}}{{.}}{{/list}}</HTML>");
        tmpl.set("list", new int[]{1,2,3,4,5,6,7,8,9});
        assertEquals(
                "<HTML>123456789</HTML>", tmpl.compile());
    }
    @Test
    public void conditionArrayLength(){
        Templator tmpl = new Templator("<HTML>{{#list.length}}run once{{/list.length}}</HTML>");
        tmpl.set("list", new int[]{});
        assertEquals(
                "<HTML></HTML>", tmpl.compile());
        tmpl.set("list", new int[]{1});
        assertEquals(
                "<HTML>run once</HTML>", tmpl.compile());
        tmpl.set("list", new int[]{1,2,3,4,5,6,7,8,9});
        assertEquals(
                "<HTML>run once</HTML>", tmpl.compile());
    }


    @Test
    public void negativeConditionEmptyTest(){
        Templator tmpl = new Templator(
                "<HTML>{{^key}}123456789{{/key}}</HTML>");
        assertEquals(
                "<HTML>123456789</HTML>", tmpl.compile());
    }
    @Test
    public void negativeConditionSetTest(){
        Templator tmpl = new Templator(
                "<HTML>{{^key}}123456789{{/key}}</HTML>");
        tmpl.set("key", "set");
        assertEquals(
                "<HTML></HTML>", tmpl.compile());
    }
    @Test
    public void negativeConditionObjectTest(){
        Templator tmpl = new Templator("<HTML>{{^test.attr}}5678{{/test.attr}}</HTML>");
        TestClass obj = new TestClass();
        obj.attr = "1234";
        tmpl.set("test", obj);
        assertEquals("<HTML></HTML>", tmpl.compile());

        tmpl.clear();
        tmpl.set("test", new TestClass());
        assertEquals("<HTML>5678</HTML>", tmpl.compile());
    }
    @Test
    public void negativeConditionIteratorTest(){
        Templator tmpl = new Templator(
                "<HTML>{{^key}}123456789{{/key}}</HTML>");
        tmpl.set("key", Arrays.asList(1,2,3,4,5,6,7,8,9));
        assertEquals(
                "<HTML></HTML>", tmpl.compile());
    }
    @Test
    public void negativeConditionIteratorEmptyTest(){
        Templator tmpl = new Templator(
                "<HTML>{{^key}}123456789{{/key}}</HTML>");
        tmpl.set("key", new ArrayList());
        assertEquals(
                "<HTML>123456789</HTML>", tmpl.compile());
    }
    @Test
    public void negativeConditionBooleanTest(){
        Templator tmpl = new Templator(
                "<HTML>{{^key}}123456789{{/key}}</HTML>");
        tmpl.set("key", true);
        assertEquals(
                "<HTML></HTML>", tmpl.compile());

        tmpl.set("key", false);
        assertEquals(
                "<HTML>123456789</HTML>", tmpl.compile());
    }



    @Test
    public void commentTest(){
        Templator tmpl = new Templator(
                "<HTML>{{! This is a comment}}</HTML>");
        assertEquals(
                "<HTML></HTML>", tmpl.compile());
    }


    @Test
    public void functionCallTest(){
        Templator tmpl = new Templator(
                "<HTML>{{#obj.isTrue()}}it is true{{/obj.isTrue()}}</HTML>");
        tmpl.set("obj", new TestFuncClass());
        assertEquals(
                "<HTML>it is true</HTML>", tmpl.compile());

        tmpl = new Templator(
                "<HTML>{{#obj.isFalse()}}it is true{{/obj.isFalse()}}</HTML>");
        tmpl.set("obj", new TestFuncClass());
        assertEquals(
                "<HTML></HTML>", tmpl.compile());
    }
    @Test
    public void functionInArrayTest(){
        Templator tmpl = new Templator(
                "<HTML>{{#list}}{{#.isTrue()}}1{{/.isTrue()}}{{/list}}</HTML>");
        tmpl.set("list", new TestFuncClass[]{
                new TestFuncClass(),new TestFuncClass(),new TestFuncClass()
        });
        assertEquals(
                "<HTML>111</HTML>", tmpl.compile());
    }


    @Test
    public void recursiveTemplateorTest(){
        Templator tmpl2 = new Templator(
                "{{value1}},{{value2}}");
        tmpl2.set("value1", "sub1");
        tmpl2.set("value2", "sub2");
        Templator tmpl = new Templator(
                "<HTML>{{parent}}:{{child}}</HTML>");
        tmpl.set("parent", "super");
        tmpl.set("child", tmpl2);

        assertEquals(
                "<HTML>super:sub1,sub2</HTML>", tmpl.compile());
    }
}
