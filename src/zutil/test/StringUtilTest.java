package zutil.test;

import org.junit.*;
import static org.junit.Assert.*;

import zutil.StringUtil;

public class StringUtilTest {

	@Test
	public void formatBytesToStringTest() {
		assertEquals( StringUtil.formatBytesToString( 100 ), 	"100.0 B" );
		assertEquals( StringUtil.formatBytesToString( 10000 ), 	"9.7 kB" );
	}

	@Test
	public void trimTest() {
		assertEquals( StringUtil.trim("", 			' '),		"" );
		assertEquals( StringUtil.trim(" aa ", 		' '), 		"aa" );
		assertEquals( StringUtil.trim("aa ", 		' '), 		"aa" );
		assertEquals( StringUtil.trim(" aa", 		' '), 		"aa" );
		assertEquals( StringUtil.trim(" aa ", 		'a'),  		"" );
		assertEquals( StringUtil.trim("\u0010 aa ", ' '), 		"aa" );
		assertEquals( StringUtil.trim("\n\naa\n\t", ' '),		"aa" );
		assertEquals( StringUtil.trim("\"aa\"", 	'\"'), 		"aa" );
	}

	@Test
	public void trimQuotesTest() {
		assertEquals( StringUtil.trimQuotes(""),			"" );
		assertEquals( StringUtil.trimQuotes("\""),			"\"" );
		assertEquals( StringUtil.trimQuotes("\"\""),		"" );
		assertEquals( StringUtil.trimQuotes("\"aa"),		"\"aa" );
		assertEquals( StringUtil.trimQuotes("aa\""),		"aa\"" );
		assertEquals( StringUtil.trimQuotes("\"aa\""), 		"aa" );
	}
}
