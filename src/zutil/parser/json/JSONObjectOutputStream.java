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
import zutil.parser.DataNode;
import zutil.parser.DataNode.DataType;
import static zutil.parser.json.JSONObjectInputStream.*;

import javax.activation.UnsupportedDataTypeException;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONObjectOutputStream extends OutputStream implements ObjectOutput, Closeable{
    /** If the generated JSON should contain class def meta-data **/
    private boolean generateMetaData;

    /** Cache of parsed objects **/
    private HashMap<Object,Integer> objectCache;
    private JSONWriter out;

    private JSONObjectOutputStream() {
        this.generateMetaData = true;
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
        DataNode root;

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
    	DataNode node;
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
     * stream for class definitions and caching.
     * If meta data is disabled then all parsed classes need to
     * be registered in JSONObjectInputStream to be able to decode objects.
     *
     * All the meta-data tags will start with a '@'
     */
    public void enableMetaData(boolean generate){
        generateMetaData = generate;
    }

    public void flush() throws IOException {
        super.flush();
        out.flush();
    }



    @Override public void writeBoolean(boolean v) throws IOException {
        out.write(new DataNode(v));
    }
    @Override public void writeByte(int v) throws IOException {
        throw new NotImplementedException();
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
        throw new NotImplementedException();
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
