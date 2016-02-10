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
import zutil.parser.CSVParser;
import zutil.parser.DataNode;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

/**
 * Created by ezivkoc on 2015-07-30.
 */
public class CSVParserTest {


    @Test
    public void emptyTest(){
        DataNode node = CSVParser.read("");
        assertEquals(null, node);
    }

    @Test
    public void simpleTest(){
        DataNode node = CSVParser.read("hello,world,you");
        assertEquals(3, node.size());
        assertEquals("hello", node.get(0).getString());
        assertEquals("world", node.get(1).getString());
        assertEquals("you",   node.get(2).getString());
    }

    @Test
    public void simpleHeaderTest() throws IOException {
        CSVParser parser = new CSVParser(new StringReader("where,what,who\nhello,world,you"), true);
        DataNode node = parser.read();
        assertEquals(3, node.size());
        assertEquals("hello", node.get(0).getString());
        assertEquals("world", node.get(1).getString());
        assertEquals("you",   node.get(2).getString());
        node = parser.getHeaders();
        assertEquals("where", node.get(0).getString());
        assertEquals("what", node.get(1).getString());
        assertEquals("who",   node.get(2).getString());
    }

    @Test
    public void simpleMultilineTest() throws IOException {
        CSVParser parser = new CSVParser(
                new StringReader("hello,world,you\nhello,world,you\nhello,world,you"));
        int rows=0;
        for(DataNode node = parser.read(); node != null; node=parser.read(), ++rows) {
            assertEquals(3, node.size());
            assertEquals("hello", node.get(0).getString());
            assertEquals("world", node.get(1).getString());
            assertEquals("you", node.get(2).getString());
        }
        assertEquals(3, rows);
    }

    @Test
    public void quotedTest(){
        DataNode node = CSVParser.read("\"hello\",\"world\",\"you\"");
        assertEquals(3, node.size());
        assertEquals("hello", node.get(0).getString());
        assertEquals("world", node.get(1).getString());
        assertEquals("you",   node.get(2).getString());
    }

    @Test
    public void quotedIncorrectlyTest(){
        DataNode node = CSVParser.read("hello\",wo\"rl\"d,\"you\"");
        assertEquals(3, node.size());
        assertEquals("hello\"", node.get(0).getString());
        assertEquals("wo\"rl\"d", node.get(1).getString());
        assertEquals("you",   node.get(2).getString());
    }

    @Test
    public void quotedCommaTest(){
        DataNode node = CSVParser.read("hello,\"world,you\"");
        assertEquals(2, node.size());
        assertEquals("hello", node.get(0).getString());
        assertEquals("world,you", node.get(1).getString());
    }
}
