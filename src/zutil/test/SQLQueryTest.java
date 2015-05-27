/*
 * Copyright (c) 2015 ezivkoc
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

import org.junit.*;
import static org.junit.Assert.*;

import zutil.db.SQLQuery;

public class SQLQueryTest {

	@Test
	public void selectTest() {
		assertEquals( "SELECT * FROM test1", 
				""+SQLQuery.SELECT().FROM("test1") );
		assertEquals( "SELECT * FROM test1", 
				""+SQLQuery.SELECT("*").FROM("test1") );
		assertEquals( "SELECT test1,test2 FROM test1", 
				""+SQLQuery.SELECT("test1","test2").FROM("test1") );
	}
	@Test
	public void selectJoinTest() {
		assertEquals( "SELECT * FROM test1 JOIN test2", 
				""+SQLQuery.SELECT("*").FROM("test1").JOIN("test2") );
		assertEquals( "SELECT * FROM test1 NATURAL JOIN test2", 
				""+SQLQuery.SELECT("*").FROM("test1").NATURAL_JOIN("test2") );
		assertEquals( "SELECT * FROM test1 UNION test2", 
				""+SQLQuery.SELECT("*").FROM("test1").UNION("test2") );
		assertEquals( "SELECT * FROM test1 JOIN test2 NATURAL JOIN test3 UNION test4", 
				""+SQLQuery.SELECT("*").FROM("test1").JOIN("test2").NATURAL_JOIN("test3").UNION("test4") );
		
		assertEquals( "SELECT * FROM test1 NATURAL JOIN test2 NATURAL JOIN test3 NATURAL JOIN test4", 
				""+SQLQuery.SELECT("*").FROM().NATURAL_JOIN("test1","test2","test3","test4") );
		assertEquals( "SELECT * FROM test1 JOIN test2 JOIN test3 JOIN test4", 
				""+SQLQuery.SELECT("*").FROM().JOIN("test1","test2","test3","test4") );
		assertEquals( "SELECT * FROM test1 UNION test2 UNION test3 UNION test4", 
				""+SQLQuery.SELECT("*").FROM().UNION("test1","test2","test3","test4") );
	}
	@Test
	public void selectWhereTest() {
		assertEquals( "SELECT * FROM test1 WHERE arg=value", 
				""+SQLQuery.SELECT("*").FROM("test1").WHERE().EQ("arg","value") );
	}
	@Test
	public void selectGroupByTest() {
		assertEquals( "SELECT * FROM test1 GROUP BY col1", 
				""+SQLQuery.SELECT("*").FROM("test1").GROUP_BY("col1") );
		assertEquals( "SELECT * FROM test1 WHERE arg=value GROUP BY col1", 
				""+SQLQuery.SELECT("*").FROM("test1").WHERE().EQ("arg","value").GROUP_BY("col1") );
		assertEquals( "SELECT * FROM test1 WHERE arg=value GROUP BY col1 ASC", 
				""+SQLQuery.SELECT("*").FROM("test1").WHERE().EQ("arg","value").GROUP_BY("col1").ASC() );
		assertEquals( "SELECT * FROM test1 WHERE arg=value GROUP BY col1 DESC", 
				""+SQLQuery.SELECT("*").FROM("test1").WHERE().EQ("arg","value").GROUP_BY("col1").DESC() );
	}
	@Test
	public void selectOrderByTest() {
		assertEquals( "SELECT * FROM test1 WHERE arg=value ORDER BY col1", 
				""+SQLQuery.SELECT("*").FROM("test1").WHERE().EQ("arg","value").ORDER_BY("col1") );
		assertEquals( "SELECT * FROM test1 WHERE arg=value ORDER BY col1 ASC", 
				""+SQLQuery.SELECT("*").FROM("test1").WHERE().EQ("arg","value").ORDER_BY("col1").ASC() );
		assertEquals( "SELECT * FROM test1 WHERE arg=value ORDER BY col1 DESC", 
				""+SQLQuery.SELECT("*").FROM("test1").WHERE().EQ("arg","value").ORDER_BY("col1").DESC() );
		assertEquals( "SELECT * FROM test1 WHERE arg=value GROUP BY col1 ORDER BY col2 DESC", 
				""+SQLQuery.SELECT("*").FROM("test1").WHERE().EQ("arg","value").GROUP_BY("col1").ORDER_BY("col2").DESC() );
		assertEquals( "SELECT * FROM test1 WHERE arg=value GROUP BY col1 ASC ORDER BY col2 DESC", 
				""+SQLQuery.SELECT("*").FROM("test1").WHERE().EQ("arg","value").GROUP_BY("col1").ASC().ORDER_BY("col2").DESC() );
	}
	@Test
	public void selectLimitTest() {
		assertEquals( "SELECT * FROM test1 LIMIT 1", 
				""+SQLQuery.SELECT("*").FROM("test1").LIMIT(1) );
		assertEquals( "SELECT * FROM test1 LIMIT 1 4", 
				""+SQLQuery.SELECT("*").FROM("test1").LIMIT(1).TO(4) );
		assertEquals( "SELECT * FROM test1 WHERE arg=value LIMIT 1", 
				""+SQLQuery.SELECT("*").FROM("test1").WHERE().EQ("arg","value").LIMIT(1) );
		assertEquals( "SELECT * FROM test1 WHERE arg=value ORDER BY col1 DESC LIMIT 1", 
				""+SQLQuery.SELECT("*").FROM("test1").WHERE().EQ("arg","value").ORDER_BY("col1").DESC().LIMIT(1) );
		assertEquals( "SELECT * FROM test1 WHERE arg=value GROUP BY col1 ORDER BY col2 DESC LIMIT 1", 
				""+SQLQuery.SELECT("*").FROM("test1").WHERE().EQ("arg","value").GROUP_BY("col1").ORDER_BY("col2").DESC().LIMIT(1) );
	}
	
	
	@Test
	public void updateTest() {
		
	}

	
	@Test
	public void deleteTest() {
		
	}
	
	@Test
	public void createTest() {
		
	}
}
