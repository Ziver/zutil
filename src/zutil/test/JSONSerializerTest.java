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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.junit.Test;

import zutil.io.StringInputStream;
import zutil.io.StringOutputStream;
import zutil.parser.json.JSONObjectInputStream;
import zutil.parser.json.JSONObjectOutputStream;

public class JSONSerializerTest{

    @Test
    public void testJSONObjectOutputStream() throws InterruptedException, IOException, ClassNotFoundException{
        TestClass sourceObj = new TestClass();

        StringOutputStream bout = new StringOutputStream();
        JSONObjectOutputStream out = new JSONObjectOutputStream(bout);
        out.writeObject(sourceObj);
        out.flush();
        out.close();
        String data = bout.toString();

        assertEquals(
                "{\"str\": \"1234\", \"@class\": \"zutil.test.JSONSerializerTest$TestClass\", \"obj1\": {\"@class\": \"zutil.test.JSONSerializerTest$TestObj\", \"value\": \"42\", \"@object_id\": 2}, \"obj2\": {\"@class\": \"zutil.test.JSONSerializerTest$TestObj\", \"value\": \"42\", \"@object_id\": 3}, \"@object_id\": 1}",
                data);
    }

	@Test
	public void testJavaObjectInOutSerialize() throws InterruptedException, IOException, ClassNotFoundException{
		TestClass sourceObj = new TestClass();
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bout);
		out.writeObject(sourceObj);
		out.flush();
		out.close();
		byte[] data = bout.toByteArray();
		
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		ObjectInputStream in = new ObjectInputStream(bin);
		TestClass targetObj = (TestClass) in.readObject();
		in.close();
		
		assertEquals( sourceObj, targetObj );
	}
	
	@Test
	public void testJSONObjectInOutSerialize2() throws InterruptedException, IOException, ClassNotFoundException{
		TestClass sourceObj = new TestClass();
		
		StringOutputStream bout = new StringOutputStream();
		JSONObjectOutputStream out = new JSONObjectOutputStream(bout);
		out.writeObject(sourceObj);
		out.flush();
		out.close();
		String data = bout.toString();
		
		StringInputStream bin = new StringInputStream(data);
		JSONObjectInputStream in = new JSONObjectInputStream(bin);
		TestClass targetObj = (TestClass) in.readObject();
		in.close();
		
		assertEquals( sourceObj, targetObj );
	}
	
	
	
	
	
	public static class TestClass implements Serializable{
		private static final long serialVersionUID = 1L;
		String str = "1234";
		TestObj obj1 = new TestObj();
		TestObj obj2 = new TestObj();
		
		public boolean equals(Object obj){
			return obj instanceof TestClass && 
					this.str.equals(((TestClass)obj).str) && 
					this.obj1.equals(((TestClass)obj).obj1) &&
					this.obj2.equals(((TestClass)obj).obj2);
		}
	}
	
	public static class TestObj implements Serializable{
		private static final long serialVersionUID = 1L;
		int value = 42;
		
		public boolean equals(Object obj){
			return obj instanceof TestObj && this.value == ((TestObj)obj).value;
		}
	}
}
