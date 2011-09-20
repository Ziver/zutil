package zutil.test;

import static org.junit.Assert.assertEquals;


import org.junit.Test;

import zutil.parser.BBCodeParser;


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
