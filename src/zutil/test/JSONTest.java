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
