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

package zutil;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;


public class StringUtilTest {

    @Test
    public void formatByteSizeToStringTest() {
        assertEquals( "100.0 B", StringUtil.formatByteSizeToString(100) );
        assertEquals(  "9.7 kB",  StringUtil.formatByteSizeToString(10000) );
    }

    @Test
    public void formatTimeToStringTest() {
        assertEquals( "1 sec ", StringUtil.formatTimeToString( 1000 ) );
        assertEquals( "1 month 1 day 1 hour 1 min 1 sec 1 milisec ",
                StringUtil.formatTimeToString(2629743830L +86400000+3600000+60000+1000+1 ) );
        assertEquals( "2 months 2 days 2 hours 2 min 2 sec 2 milisec ",
                StringUtil.formatTimeToString( (2629743830L +86400000+3600000+60000+1000+1)*2 ) );
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

    @Test
    public void joinTest(){
        assertEquals("", StringUtil.join(",", Collections.emptyList()));
        assertEquals("1,2,3,4,5", StringUtil.join(",", 1,2,3,4,5));
        assertEquals("1,2,3,4,5", StringUtil.join(",", Arrays.asList(1,2,3,4,5)));
        assertEquals("animal,monkey,dog", StringUtil.join(",", "animal", "monkey", "dog"));
        assertEquals("animal,monkey,dog", StringUtil.join(",", Arrays.asList("animal", "monkey", "dog")));
        assertEquals("12345", StringUtil.join("", 1,2,3,4,5));
    }
}
