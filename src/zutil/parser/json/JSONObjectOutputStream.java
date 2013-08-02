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

import sun.misc.ClassLoaderUtil;
import zutil.ClassUtil;
import zutil.parser.DataNode;
import zutil.parser.DataNode.DataType;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.UnsupportedDataTypeException;

public class JSONObjectOutputStream extends OutputStream implements ObjectOutput, Closeable{
    private boolean generateMetaData;

    private HashMap<Object,Integer> objectCache;
    private JSONWriter out;

    public JSONObjectOutputStream(OutputStream out) {
        this.generateMetaData = true;
        this.objectCache = new HashMap<Object, Integer>();
        this.out = new JSONWriter(out);
    }

    public void writeBoolean(boolean v) throws IOException {
        out.write(new DataNode(v));
    }

    public void writeByte(int v) throws IOException {
        // TODO:
    }

    public void writeShort(int v) throws IOException {
        out.write(new DataNode(v));
    }

    public void writeChar(int v) throws IOException {
        out.write(new DataNode((char)v));
    }

    public void writeInt(int v) throws IOException {
        out.write(new DataNode(v));
    }

    public void writeLong(long v) throws IOException {
        out.write(new DataNode(v));
    }

    public void writeFloat(float v) throws IOException {
        out.write(new DataNode(v));
    }

    public void writeDouble(double v) throws IOException {
        out.write(new DataNode(v));
    }

    public void writeBytes(String s) throws IOException {
        // TODO:
    }

    public void writeChars(String s) throws IOException {
        out.write(new DataNode(s));
    }

    @Override
    public void write(int b) throws IOException {
        out.write(new DataNode(b));
    }

    public void writeUTF(String s) throws IOException {
        out.write(new DataNode(s));
    }

    public void writeObject(Object obj) throws IOException{
        try{
            out.write(getDataNode(obj));
        } catch (IllegalAccessException e) {
            throw new IOException("Unable to serialize object", e);
        } finally {
            objectCache.clear();
        }
    }

    protected DataNode getDataNode(Object obj) throws IOException, IllegalArgumentException, IllegalAccessException {
        DataNode root = null;

        // Check if the object is a primitive
        if(ClassUtil.isPrimitive(obj.getClass()) ||
                ClassUtil.isWrapper(obj.getClass())){
            root = getPrimitiveDataNode(obj.getClass(), obj);
        }
        // Object is a complex data type
    	else {
            root = new DataNode(DataNode.DataType.Map);
            // Generate meta data
            if(generateMetaData){
                // Cache
                if(objectCache.containsKey(obj)){ // Hit
                    root.set("@object_id", objectCache.get(obj));
                    return root;
                }
                else{ // Miss
                    objectCache.put(obj, objectCache.size()+1);
                    root.set("@object_id", objectCache.size());
                }
                root.set("@class", obj.getClass().getName());
            }
            // Add all the fields to the DataNode
            for(Field field : obj.getClass().getDeclaredFields()){
                if((field.getModifiers() & Modifier.STATIC) == 0 &&
                        (field.getModifiers() & Modifier.TRANSIENT) == 0){
                    field.setAccessible(true);

                    // Add basic type (int, float...)
                    if(ClassUtil.isPrimitive(field.getType()) ||
                            ClassUtil.isWrapper(field.getType())){
                        root.set(field.getName(), getPrimitiveDataNode(field.getType(), field.get(obj)));
                    }
                    // Add an array
                    else if(field.getType().isArray()){
                        DataNode arrayNode = new DataNode(DataNode.DataType.List);
                        Object array = field.get(obj);
                        for(int i=0; i< Array.getLength(array) ;i++){
                            arrayNode.add(getDataNode(Array.get(array, i)));
                        }
                        root.set(field.getName(), arrayNode);
                    }
                    else if(List.class.isAssignableFrom(field.getType())){
                        // TODO Add List Support
                    }
                    else if(Map.class.isAssignableFrom(field.getType())){
                        // TODO Add Map Support
                    }
                    else{
                        root.set(field.getName(), getDataNode(field.get(obj)));
                    }
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
        
    	node.set(value.toString());
		return node;
	}

    /**
     * Enable or disables the use of meta data in the JSON
     * stream for class def and caching.
     * Should only be disabled if the input is not a JSONObjectInputStream.
     * All the meta-data tags will start with a '@'
     */
    public void generateMetaData(boolean generate){
        generateMetaData = generate;
    }

}
