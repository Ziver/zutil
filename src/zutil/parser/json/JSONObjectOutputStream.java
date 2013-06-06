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

import zutil.log.LogUtil;
import zutil.parser.DataNode;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JSONObjectOutputStream extends OutputStream implements ObjectOutput, Closeable{
    private boolean generateMetaDeta;

    private HashMap<Object,Integer> objectCache;
    private JSONWriter out;

    public JSONObjectOutputStream(OutputStream out) {
        this.generateMetaDeta = true;
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

    public void writeUTF(String s) throws IOException {
        out.write(new DataNode(s));
    }

    public void writeObject(Object obj) throws IOException{
        try{
            out.write(getDataNode(obj));
        } catch (IllegalAccessException e) {
            throw new IOException("Unable to serialize object", e);
        }
    }

    protected DataNode getDataNode(Object obj) throws IllegalAccessException {
        //if(!(obj instanceof Serializable))
        //    throw new UnSerializable
        DataNode root = new DataNode(DataNode.DataType.Map);
        // Generate meta data
        if(generateMetaDeta){
            root.set("@class", obj.getClass().getName());
            // Cache
            if(objectCache.containsKey(obj)){ // Hit
                root.set("@object_id", objectCache.get(obj));
                return root;
            }
            else{ // Miss
                objectCache.put(obj, objectCache.size()+1);
                root.set("@object_id", objectCache.size());
            }
        }
        // Add all the fields to the DataNode
        for(Field field : obj.getClass().getDeclaredFields()){
            if((field.getModifiers() & Modifier.STATIC) == 0 &&
                    (field.getModifiers() & Modifier.TRANSIENT) == 0){
                field.setAccessible(true);
                // Add an array
                if(field.getType().isArray()){
                    DataNode arrayNode = new DataNode(DataNode.DataType.List);
                    Object array = field.get(obj);
                    for(int i=0; i< Array.getLength(array) ;i++){
                        arrayNode.add(Array.get(array, i).toString());
                    }
                    root.set(field.getName(), arrayNode);
                }
                // Add basic type (int, float...)
                else if(field.getType().isPrimitive() ||
                        field.getType() == String.class){
                    root.set(field.getName(), field.get(obj).toString());
                }
                else{
                    root.set(field.getName(), getDataNode(field.get(obj)));
                }
            }
        }
        return root;
    }

    @Override
    public void write(int b) throws IOException {
        // TODO:
    }

    /**
     * Enable or disables the use of meta data in the JSON
     * stream for class def and caching.
     * Should only be disabled if the input is not a JSONObjectInputStream.
     * All the meta-data tags will start with a '@'
     */
    public void generateMetaDeta(boolean generate){
        generateMetaDeta = generate;
    }

    /**
     * Reset the Object stream (clears the cache)
     */
    public void reset(){
        objectCache.clear();
    }

}
