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

package zutil.parser.json;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import zutil.ClassUtil;
import zutil.parser.Base64Decoder;
import zutil.parser.DataNode;

import javax.activation.UnsupportedDataTypeException;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JSONObjectInputStream extends InputStream implements ObjectInput, Closeable{
    protected static final String MD_OBJECT_ID = "@object_id";
    protected static final String MD_CLASS = "@class";

    private JSONParser parser;
    private HashMap<String, Class> registeredClasses;
    private HashMap<Integer, Object> objectCache;


	public JSONObjectInputStream(Reader in) {
		this.parser = new JSONParser(in);
        this.registeredClasses = new HashMap<>();
		this.objectCache = new HashMap<>();
	}


    /**
     * If no metadata is available in the stream then this
     * class will be instantiated and assigned data from the received JSON.
     *
     * @param   c       the Class that will be instantiated for the root JSON.
     */
    public void registerRootClass(Class<?> c){
        registeredClasses.put(null, c);
    }

    /**
     * A object instance of the given class will be created if the specific key is seen.
     * This can be used for streams that do not have any class metadata.
     * NOTE: any meta-data in the stream will override the registered classes.
     *
     * @param   key     a String key that will be looked for in the InputStream
     * @param   c       the Class that will be instantiated for the specific key.
     */
    public void registerClass(String key, Class<?> c){
        registeredClasses.put(key, c);
    }


    public synchronized Object readObject() throws IOException {
        try{
            DataNode root = parser.read();
            if(root != null){
                return readObject(null, root);
            }
        // TODO: Fix Exceptions
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			objectCache.clear();
		}
        return null;
    }

    protected Object readObject(String key, DataNode json) throws IllegalAccessException, InstantiationException, ClassNotFoundException, IllegalArgumentException, UnsupportedDataTypeException {
        // See if the Object id is in the cache before continuing
    	if(json.getString("@object_id") != null && objectCache.containsKey(json.getInt(MD_OBJECT_ID)))
        	return objectCache.get(json.getInt(MD_OBJECT_ID));

        // Resolve the class
        Object obj = null;
        // Try using metadata
        if(json.getString(MD_CLASS) != null) {
            Class<?> objClass = Class.forName(json.getString(MD_CLASS));
            obj = objClass.newInstance();
        }
        // Search for registered classes
        else if(registeredClasses.containsKey(key)){
            Class<?> objClass = registeredClasses.get(key);
            obj = objClass.newInstance();
        }
        // Unknown Class
        else return null;

        // Read all fields from the new object instance
        for(Field field : obj.getClass().getDeclaredFields()){
            if((field.getModifiers() & Modifier.STATIC) == 0 &&
                    (field.getModifiers() & Modifier.TRANSIENT) == 0 &&
                    json.get(field.getName()) != null){
                // Parse field
                field.setAccessible(true);
                field.set(obj, readField(
                        field.getType(),
                        field.getName(),
                        json.get(field.getName())));
            }
        }
        // Add object to the cache
        if(json.getString(MD_OBJECT_ID) != null)
        	objectCache.put(json.getInt(MD_OBJECT_ID), obj);
        return obj;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Object readField(Class<?> type, String key, DataNode json) throws IllegalAccessException, ClassNotFoundException, InstantiationException, UnsupportedDataTypeException {
        // Field type is a primitive?
        if(type.isPrimitive() || String.class.isAssignableFrom(type)){
            return readPrimitive(type, json);
        }
        else if(type.isArray()){
            if(type.getComponentType() == Byte.class)
                return Base64Decoder.decodeToByte(json.getString());
            else{
                Object array = Array.newInstance(type.getComponentType(), json.size());
                for(int i=0; i<json.size(); i++){
                    Array.set(array, i, readField(type.getComponentType(), key, json.get(i)));
                }
                return array;
            }
        }
        else if(List.class.isAssignableFrom(type)){
			List list = (List)type.newInstance();
            for(int i=0; i<json.size(); i++){
                list.add(readObject(key, json.get(i)));
            }
            return list;
        }
        else if(Map.class.isAssignableFrom(type)){
            Map map = (Map)type.newInstance();
            for(Iterator<String> it=json.keyIterator(); it.hasNext();){
                String subKey = it.next();
                map.put(
                		subKey,
                		readObject(subKey, json.get(subKey)));
            }
            return map;
        }
        // Field is a new Object
        else{
            return readObject(key, json);
        }
    }
    
    protected static Object readPrimitive(Class<?> type, DataNode json){
        if     (type == int.class ||
        		type == Integer.class) return json.getInt();
        else if(type == long.class ||
        		type == Long.class)    return json.getLong();
        
        else if(type == double.class ||
        		type == Double.class)  return json.getDouble();

        else if(type == boolean.class || 
        		type == Boolean.class) return json.getBoolean();
        else if(type == String.class)  return json.getString();
        return null;
    }




    @Override public void readFully(byte[] b) throws IOException {
		throw new NotImplementedException();
	}
    @Override public void readFully(byte[] b, int off, int len) throws IOException {
        throw new NotImplementedException();
	}
    @Override public int skipBytes(int n) throws IOException {
        throw new NotImplementedException();
	}
    @Override public boolean readBoolean() throws IOException {
        throw new NotImplementedException();
	}
    @Override public byte readByte() throws IOException {
        throw new NotImplementedException();
	}
    @Override public int readUnsignedByte() throws IOException {
        throw new NotImplementedException();
	}
    @Override public short readShort() throws IOException {
        throw new NotImplementedException();
	}
    @Override public int readUnsignedShort() throws IOException {
        throw new NotImplementedException();
	}
    @Override public char readChar() throws IOException {
        throw new NotImplementedException();
	}
    @Override public int readInt() throws IOException {
        throw new NotImplementedException();
	}
    @Override public long readLong() throws IOException {
        throw new NotImplementedException();
	}
    @Override public float readFloat() throws IOException {
        throw new NotImplementedException();
	}
    @Override public double readDouble() throws IOException {
        throw new NotImplementedException();
	}
    @Override public String readLine() throws IOException {
        throw new NotImplementedException();
	}
    @Override public String readUTF() throws IOException {
        throw new NotImplementedException();
	}
	@Override public int read() throws IOException {
        throw new NotImplementedException();
	}

}
