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

package zutil.parser.json;

import zutil.parser.Base64Decoder;
import zutil.parser.DataNode;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

public class JSONObjectInputStream extends InputStream implements ObjectInput, Closeable{
    private JSONParser parser;

	public JSONObjectInputStream(Reader in) {
		this.parser = new JSONParser(in);
	}

    public Object readObject() throws ClassNotFoundException, IOException {
        try{
            DataNode root = parser.read();
            if(root != null){
                readObject(root);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected static Object readObject(DataNode json) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        Class objClass = Class.forName(json.getString("@class"));
        Object obj = objClass.newInstance();

        // Read all fields from the new object instance
        for(Field field : obj.getClass().getDeclaredFields()){
            if((field.getModifiers() & Modifier.STATIC) == 0 &&
                    (field.getModifiers() & Modifier.TRANSIENT) == 0 &&
                    json.get(field.getName()) != null){
                // Parse field
                field.setAccessible(true);
                field.set(obj, readValue(
                        field.getDeclaringClass(),
                        json.get(field.getName())));
            }
        }
        return obj;
    }

    protected static Object readValue(Class type, DataNode json) throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        // Field type is a primitive?
        if(type.isPrimitive()){
            return readPrimitive(type, json);
        }
        else if(type.isArray()){
            if(type.getComponentType() == Byte.class)
                return Base64Decoder.decodeToByte(json.getString());
            else{
                Object array = Array.newInstance(type.getComponentType(), json.size());
                for(int i=0; i<json.size(); i++){
                    Array.set(array, i, readValue(type.getComponentType(), json.get(i)));
                }
                return array;
            }
        }
        else if(List.class.isAssignableFrom(type)){
            // TODO Add List Support
            List list = (List)type.newInstance();
            for(int i=0; i<json.size(); i++){
                list.add(readValue(null, json.get(i)));
            }
            return list;
        }
        else if(Map.class.isAssignableFrom(type)){
            // TODO Add Map Support
            Map map = (Map)type.newInstance();
            for(int i=0; i<json.size(); i++){
                map.put(
                        readValue(null, json.get(i)),
                        readValue(null, json.get(i)));
            }
            return map;
        }
        // Field is a new Object
        else{
            return readObject(json);
        }
    }

    protected static Object readPrimitive(Class<?> type, DataNode json){
        //if     (type == Short.class)  return json.getShort();
        if     (type == Integer.class) return json.getInt();
        else if(type == Long.class)    return json.getLong();

        //else if(type == Float.class) field.setFloat(obj, json.getFloat());
        else if(type == Double.class)  return json.getDouble();

        else if(type == Boolean.class) return json.getBoolean();
        //else if(type == Character.class) return json.getChar();
        else if(type == String.class)  return json.getString();
        //else if(type == Byte.class) return Base64Decoder.decodeToByte(json.getString());
        return null;
    }


	public void readFully(byte[] b) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void readFully(byte[] b, int off, int len) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public int skipBytes(int n) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean readBoolean() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	public byte readByte() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int readUnsignedByte() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	public short readShort() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int readUnsignedShort() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	public char readChar() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int readInt() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	public long readLong() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	public float readFloat() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	public double readDouble() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String readLine() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public String readUTF() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int read() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	
}
