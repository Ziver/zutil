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

import zutil.ClassUtil;
import zutil.parser.Base64Encoder;
import zutil.parser.DataNode;
import zutil.parser.DataNode.DataType;

import javax.activation.UnsupportedDataTypeException;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static zutil.parser.json.JSONObjectInputStream.MD_CLASS;
import static zutil.parser.json.JSONObjectInputStream.MD_OBJECT_ID;

public class JSONObjectOutputStream extends OutputStream implements ObjectOutput, Closeable{
    /** If the generated JSON should contain class def meta-data **/
    private boolean generateMetaData = true;
    /** If fields that are null should be included in the json **/
    private boolean ignoreNullFields = true;

    /** Cache of parsed objects **/
    private HashMap<Object,Integer> objectCache;
    private JSONWriter out;

    private JSONObjectOutputStream() {
        this.objectCache = new HashMap<Object, Integer>();
    }
    public JSONObjectOutputStream(OutputStream out) {
        this();
        this.out = new JSONWriter(out);
    }
    public JSONObjectOutputStream(Writer out) {
        this();
        this.out = new JSONWriter(out);
    }



    public synchronized void writeObject(Object obj) throws IOException{
        try{
            out.write(getDataNode(obj));
        } catch (IllegalAccessException e) {
            throw new IOException("Unable to serialize object", e);
        } finally {
            objectCache.clear();
        }
    }

    protected DataNode getDataNode(Object obj) throws IOException, IllegalArgumentException, IllegalAccessException {
        if(obj == null)
            return null;
        Class objClass = obj.getClass();
        DataNode root;

        // Check if the object is a primitive
        if(ClassUtil.isPrimitive(obj.getClass()) ||
                ClassUtil.isWrapper(obj.getClass())){
            root = getPrimitiveDataNode(obj.getClass(), obj);
        }
        // Add an array
        else if(objClass.isArray()){
            // Special case for byte arrays
            if(objClass.getComponentType() == byte.class) {
                root = new DataNode(DataType.String);
                root.set(Base64Encoder.encode((byte[])obj));
            }
            // Other arrays
            else {
                root = new DataNode(DataType.List);
                for (int i = 0; i < Array.getLength(obj); i++) {
                    root.add(getDataNode(Array.get(obj, i)));
                }
            }
        }
        // List
        else if(List.class.isAssignableFrom(objClass)){
            root = new DataNode(DataNode.DataType.List);
            List list = (List)obj;
            for(Object item : list){
                root.add(getDataNode(item));
            }
        }
        // Map
        else if(Map.class.isAssignableFrom(objClass)){
            root = new DataNode(DataNode.DataType.Map);
            Map map = (Map)obj;
            for(Object key : map.keySet()){
                root.set(
                        getDataNode(key).getString(),
                        getDataNode(map.get(key)));
            }
        }
        // Object is a complex data type
    	else {
            root = new DataNode(DataNode.DataType.Map);
            // Generate meta data
            if(generateMetaData){
                // Cache
                if(objectCache.containsKey(obj)){ // Hit
                    root.set(MD_OBJECT_ID, objectCache.get(obj));
                    return root;
                }
                else{ // Miss
                    objectCache.put(obj, objectCache.size()+1);
                    root.set(MD_OBJECT_ID, objectCache.size());
                }
                root.set(MD_CLASS, obj.getClass().getName());
            }
            // Add all the fields to the DataNode
            for(Field field : obj.getClass().getDeclaredFields()){
                if((field.getModifiers() & Modifier.STATIC) == 0 &&
                        (field.getModifiers() & Modifier.TRANSIENT) == 0){
                    field.setAccessible(true);
                    Object fieldObj = field.get(obj);

                    // has object a value?
                    if(ignoreNullFields && fieldObj == null)
                        continue;
                    else
                        root.set(field.getName(), getDataNode(fieldObj));
                }
            }
        }
        return root;
    }

    private DataNode getPrimitiveDataNode(Class<?> type, Object value) throws UnsupportedDataTypeException, IllegalArgumentException, IllegalAccessException {
    	DataNode node = null;
        if     (type == int.class ||
        		type == Integer.class ||
        		type == long.class ||
        		type == Long.class ||
		        type == double.class ||
        		type == Double.class)
        	node = new DataNode(DataType.Number);

        else if(type == boolean.class || 
        		type == Boolean.class)
        	node = new DataNode(DataType.Boolean);
        
        else if(type == String.class ||
        		type == char.class ||
        		type == Character.class)
        	node = new DataNode(DataType.String);
        else
        	throw new UnsupportedDataTypeException("Unsupported primitive data type: "+type.getName());

        if(value != null)
    	    node.set(value.toString());
		return node;
	}

    /**
     * Enable or disables the use of meta data in the JSON
     * stream for class definitions and caching.
     * If meta data is disabled then all parsed classes need to
     * be registered in JSONObjectInputStream to be able to decode objects.
     *
     * All the meta-data tags will start with a '@'
     */
    public void enableMetaData(boolean generate){
        generateMetaData = generate;
    }

    /**
     * Defines if null fields in objects should be included
     * in the JSON output.
     */
    public void ignoreNullFields(boolean enable) {
        ignoreNullFields = enable;
    }

    public void flush() throws IOException {
        super.flush();
        out.flush();
    }



    @Override public void writeBoolean(boolean v) throws IOException {
        out.write(new DataNode(v));
    }
    @Override public void writeByte(int v) throws IOException {
        throw new UnsupportedOperationException ();
    }
    @Override public void writeShort(int v) throws IOException {
        out.write(new DataNode(v));
    }
    @Override public void writeChar(int v) throws IOException {
        out.write(new DataNode((char)v));
    }
    @Override public void writeInt(int v) throws IOException {
        out.write(new DataNode(v));
    }
    @Override public void writeLong(long v) throws IOException {
        out.write(new DataNode(v));
    }
    @Override public void writeFloat(float v) throws IOException {
        out.write(new DataNode(v));
    }
    @Override public void writeDouble(double v) throws IOException {
        out.write(new DataNode(v));
    }
    @Override public void writeBytes(String s) throws IOException {
        throw new UnsupportedOperationException ();
    }
    @Override public void writeChars(String s) throws IOException {
        out.write(new DataNode(s));
    }
    @Override public void write(int b) throws IOException {
        out.write(new DataNode(b));
    }
    @Override public void writeUTF(String s) throws IOException {
        out.write(new DataNode(s));
    }


}
