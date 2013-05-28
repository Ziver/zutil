/*******************************************************************************
 * Copyright (c) 2013 Ziver
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
 ******************************************************************************/

package zutil.test;

import static org.junit.Assert.assertEquals;


import org.junit.Test;

import zutil.parser.DataNode;
import zutil.parser.json.JSONParser;


public class JSONTest{

	@Test
	public void JSONParser() {
		JSONParser parser = new JSONParser();
		
		String json = "{" +
				"\"test1\": 1234," +
				"\"TEST1\": 5678," +
				"\"test3\": 1234.99," +
				"\"test4\": \"91011\"," +
				"\"test5\": [12,13,14,15]," +
				"\"test6\": [\"a\",\"b\",\"c\",\"d\"]," +
				"}";
		
		DataNode data = parser.read(json); 
		assert( data.isMap() );
		assert( 1234 == data.get("test1").getInt() );
		assert( 5678 == data.get("TEST1").getInt() );
		assert( 1234.99 == data.get("test3").getDouble() );
		assertEquals( "91011", data.get("test4").getString() );
		
		assert( data.get("test5").isList() );
		assertEquals( 4, data.get("test5").size() );
		assertEquals( 12, data.get("test5").get(0).getInt() );
		assertEquals( 13, data.get("test5").get(1).getInt() );
		assertEquals( 14, data.get("test5").get(2).getInt() );
		assertEquals( 15, data.get("test5").get(3).getInt() );
		
		assert( data.get("test6").isList() );
		assertEquals( 4, data.get("test6").size() );
		assertEquals( "a", data.get("test6").get(0).getString() );
		assertEquals( "b", data.get("test6").get(1).getString() );
		assertEquals( "c", data.get("test6").get(2).getString() );
		assertEquals( "d", data.get("test6").get(3).getString() );
	}
}
