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
import zutil.parser.DataNode;
import zutil.parser.DataNode.DataType;
import zutil.parser.json.JSONParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class JSONParserTest {

    @Test
    public void nullString(){
        DataNode data = JSONParser.read(null);
        assertNull(data);
    }

    @Test
    public void emptyString(){
        DataNode data = JSONParser.read("");
        assertNull(data);
    }

    @Test
    public void emptyMap(){
        DataNode data = JSONParser.read("{}");
        assertEquals(DataType.Map, data.getType());
        assertEquals( 0, data.size());
    }

    @Test
    public void emptyList(){
        DataNode data = JSONParser.read("[]");
        assertEquals(DataType.List, data.getType());
        assertEquals( 0, data.size());
    }

    @Test
    public void valueInt(){
        DataNode data = JSONParser.read("1234");
        assert(data.isValue());
        assertEquals( DataType.Number, data.getType());
        assertEquals( 1234, data.getInt());
    }

    @Test
    public void valueDouble(){
        DataNode data = JSONParser.read("12.34");
        assert(data.isValue());
        assertEquals( DataType.Number, data.getType());
        assertEquals( 12.34, data.getDouble(), 0);
    }
    
    @Test
    public void valueBoolean(){
        DataNode data = JSONParser.read("false");
        assert(data.isValue());
        assertEquals( DataType.Boolean, data.getType());
        assertEquals( false, data.getBoolean());
    }
    
    @Test
    public void valueBooleanUpperCase(){
        DataNode data = JSONParser.read("TRUE");
        assert(data.isValue());
        assertEquals( DataType.Boolean, data.getType());
        assertEquals( true, data.getBoolean());
    }
    
    @Test
    public void valueStringNoQuotes(){
        DataNode data = JSONParser.read("teststring");
        assert(data.isValue());
        assertEquals( DataType.String, data.getType());
        assertEquals( "teststring", data.getString());
    }
    
    @Test
    public void toManyCommasInList(){
        DataNode data = JSONParser.read("[1,2,3,]");
        assertEquals(DataType.List, data.getType());
        assertEquals( 1, data.get(0).getInt());
        assertEquals( 2, data.get(1).getInt());
        assertEquals( 3, data.get(2).getInt());
    }

    @Test
    public void toManyCommasInMap(){
        DataNode data = JSONParser.read("{1:1,2:2,3:3,}");
        assertEquals(DataType.Map, data.getType());
        assertEquals( 1, data.get("1").getInt());
        assertEquals( 2, data.get("2").getInt());
        assertEquals( 3, data.get("3").getInt());
    }

    @Test
    public void nullValueInList(){
        DataNode data = JSONParser.read("[1,null,3,]");
        assertEquals(DataType.List, data.getType());
        assertEquals(3, data.size());
        assertEquals( "1", data.get(0).getString());
        assertNull(data.get(1));
        assertEquals( "3", data.get(2).getString());
    }

    @Test
    public void nullValueInMap(){
        DataNode data = JSONParser.read("{1:1,2:null,3:3,}");
        assertEquals(DataType.Map, data.getType());
        assertEquals(3, data.size());
        assertEquals( "1", data.getString("1"));
        assertNull(data.get("2"));
        assertEquals( "3", data.getString("3"));
    }

    @Test
    public void emptyStringInMap(){
        DataNode data = JSONParser.read("{1:{2:\"\"}}");
        assertEquals(DataType.Map, data.getType());
        assertEquals(1, data.size());
        assertEquals( "", data.get("1").getString("2"));
    }


	@Test
	public void complexMap() {
		String json = "{" +
				"\"test1\": 1234," +
				"\"TEST1\": 5678," +
				"\"test3\": 1234.99," +
				"\"test4\": \"91011\"," +
				"\"test5\": [12,13,14,15]," +
				"\"test6\": [\"a\",\"b\",\"c\",\"d\"]" +
				"}";
		
		DataNode data = JSONParser.read(json);
		assert( data.isMap() );
		assert( 1234 == data.get("test1").getInt() );
		assert( 5678 == data.get("TEST1").getInt() );
		assert( 1234.99 == data.get("test3").getDouble() );
		assertEquals( "91011", data.get("test4").getString() );
		
		assert( data.get("test5").isList() );
		assertEquals( 4,  data.get("test5").size() );
		assertEquals( 12, data.get("test5").get(0).getInt() );
		assertEquals( 13, data.get("test5").get(1).getInt() );
		assertEquals( 14, data.get("test5").get(2).getInt() );
		assertEquals( 15, data.get("test5").get(3).getInt() );
		
		assert( data.get("test6").isList() );
		assertEquals( 4,   data.get("test6").size() );
		assertEquals( "a", data.get("test6").get(0).getString() );
		assertEquals( "b", data.get("test6").get(1).getString() );
		assertEquals( "c", data.get("test6").get(2).getString() );
		assertEquals( "d", data.get("test6").get(3).getString() );
	}
}
