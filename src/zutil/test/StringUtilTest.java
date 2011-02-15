package zutil.test;

import org.junit.*;
import static org.junit.Assert.*;

import zutil.StringUtil;

public class StringUtilTest {

	@Test
	public void formatBytesToStringTest() {
		assertEquals( "100.0 B", StringUtil.formatBytesToString( 100 ) );
		assertEquals(  "9.7 kB",  StringUtil.formatBytesToString( 10000 ) );
	}

	@Test
	public void formatTimeToStringTest() {
		assertEquals( "1 sec ", StringUtil.formatTimeToString( 1000 ) );
		assertEquals( "1 month 1 day 1 hour 1 min 1 sec 1 milisec ", 
				StringUtil.formatTimeToString( 2629743830l+86400000+3600000+60000+1000+1 ) );
		assertEquals( "2 months 2 days 2 hours 2 min 2 sec 2 milisec ", 
				StringUtil.formatTimeToString( (2629743830l+86400000+3600000+60000+1000+1)*2 ) );
	}
	
	@Test
	public void trimTest() {
		assertEquals( "",   StringUtil.trim("", 			' ') );
		assertEquals( "aa", StringUtil.trim(" aa ", 		' ') );
		assertEquals( "aa", StringUtil.trim("aa ", 			' ') );
		assertEquals( "aa", StringUtil.trim(" aa", 			' ') );
		assertEquals( "",   StringUtil.trim(" aa ", 		'a') );
		assertEquals( "aa", StringUtil.trim("\u0010 aa ", 	' ') );
		assertEquals( "aa", StringUtil.trim("\n\naa\n\t", 	' ') );
		assertEquals( "aa", StringUtil.trim("\"aa\"", 		'\"') );
	}

	@Test
	public void trimQuotesTest() {
		assertEquals( "", 		StringUtil.trimQuotes("") );
		assertEquals( "\"", 	StringUtil.trimQuotes("\"") );
		assertEquals( "", 		StringUtil.trimQuotes("\"\"") );
		assertEquals( "\"aa", 	StringUtil.trimQuotes("\"aa") );
		assertEquals( "aa\"", 	StringUtil.trimQuotes("aa\"") );
		assertEquals( "aa", 	StringUtil.trimQuotes("\"aa\"") );
	}
}
