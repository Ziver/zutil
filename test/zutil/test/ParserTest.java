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
import zutil.parser.BBCodeParser;

import static org.junit.Assert.assertEquals;


public class ParserTest{

	@Test
	public void BBCodeParser() {
		BBCodeParser parser = new BBCodeParser();
		
		assertEquals("1234", parser.read("1234"));
		assertEquals("<i>1234</i>", parser.read("[i]1234[/i]"));
		assertEquals("[apa]lol[/apa]", parser.read("[apa]lol[/apa]"));
		assertEquals("jshdkj <u>lol [apa]lol[/apa]</u>", parser.read("jshdkj [u]lol [apa]lol[/apa]"));
		//assertEquals("jshdkj [m]lol[/k] <i>lol</i>", parser.read("jshdkj [m]lol[/k] [i]lol[/i]"));
		assertEquals("jshdkj [m", parser.read("jshdkj [m"));
		assertEquals("jshdkj [/m", parser.read("jshdkj [/m"));
		assertEquals("jshdkj m]", parser.read("jshdkj m]"));
		assertEquals("jshdkj <br />", parser.read("jshdkj <br />"));
	}
}
