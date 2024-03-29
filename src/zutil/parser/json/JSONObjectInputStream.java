/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Ziver Koc
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
import zutil.io.StringInputStream;
import zutil.io.StringOutputStream;
import zutil.log.LogUtil;
import zutil.parser.Base64Decoder;
import zutil.parser.DataNode;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class JSONObjectInputStream extends InputStream implements ObjectInput, Closeable{
    private static final Logger logger = LogUtil.getLogger();
    protected static final String MD_OBJECT_ID = "@object_id";
    protected static final String MD_CLASS = "@class";

    private JSONParser parser;
    private HashMap<String, Class> registeredClasses = new HashMap<>();
    private HashMap<Integer, Object> objectCache = new HashMap<>();


    private JSONObjectInputStream() {}
    public JSONObjectInputStream(Reader in) {
        this.parser = new JSONParser(in);
    }
    public JSONObjectInputStream(InputStream in) {
        this.parser = new JSONParser(in);
    }


    /**
     * @return a String containing the JSON representation of the Object
     */
    public static <T> T parse(String json) {
        try {
            StringInputStream in = new StringInputStream(json);
            JSONObjectInputStream reader = new JSONObjectInputStream(in);
            T object = reader.readGenericObject();
            reader.close();
            return object;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * If no metadata is available in the stream then this
     * class will be instantiated and assigned data from the received JSON.
     *
     * @param   c       the Class that will be instantiated for the root JSON.
     */
    public void registerRootClass(Class<?> c) {
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
    public void registerClass(String key, Class<?> c) {
        registeredClasses.put(key, c);
    }


    /**
     * @return  the object read from the stream
     */
    @Override
    public Object readObject() {
        return readObject(null);
    }
    /**
     * @param   <T>     is a simple cast to this type
     * @return  the object read from the stream
     */
    public <T> T readGenericObject() {
        return readObject(null);
    }
    /**
     * @param   c   will override the registered root class and use this value instead
     * @return  the object read from the stream
     */
    public synchronized <T> T readObject(Class<T> c) {
        try {
            DataNode root = parser.read();
            if (root != null) {
                if (root.isList()) { // Handle json that starts as an array
                    ArrayList list = new ArrayList();
                    for (DataNode node : root) {
                        list.add((T) readObject(c, null, node));
                    }
                    return (T) list;
                } else { // Handle json that starts as an object
                    return (T) readObject(c, null, root);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, null, e);
        } finally {
            objectCache.clear();
        }
        return null;
    }


    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Object readType(Class<?> type, Class<?>[] genericType, Object currentValue, String key, DataNode json)
            throws IllegalAccessException, ClassNotFoundException, InstantiationException, NoSuchMethodException, InvocationTargetException {

        if (json == null || type == null)
            return null;

        // Field type is a primitive?
        if (ClassUtil.isPrimitive(type) ||
                ClassUtil.isWrapper(type)) {
            return readPrimitive(type, json);
        }
        else if (type.isEnum()) {
            return Enum.valueOf((Class<? extends Enum>)type, (String) readPrimitive(String.class, json));
        }
        else if (type.isArray()) {
            if (type.getComponentType() == byte.class)
                return Base64Decoder.decodeToByte(json.getString());
            else {
                Object array = Array.newInstance(type.getComponentType(), json.size());
                for (int i=0; i<json.size(); i++) {
                    Array.set(array, i, readType(
                            type.getComponentType(),
                            null,
                            null,
                            key,
                            json.get(i)));
                }
                return array;
            }
        }
        else if (Collection.class.isAssignableFrom(type)) {
            if (genericType == null || genericType.length < 1)
                genericType = ClassUtil.getGenericClasses(type, List.class);

            Collection list = (Collection) currentValue;
            if (list == null) {
                if (type == Set.class)
                    list = new HashSet();
                else if (type == List.class)
                    list = new ArrayList();
                else
                    list = (Collection) type.getDeclaredConstructor().newInstance();
            }

            for (int i=0; i<json.size(); i++) {
                list.add(readType(
                        (genericType.length>=1? genericType[0] : null),
                        null,
                        null,
                        key,
                        json.get(i)));
            }
            return list;
        }
        else if (Map.class.isAssignableFrom(type)) {
            if (genericType == null || genericType.length < 2)
                genericType = ClassUtil.getGenericClasses(type, Map.class);

            Map map = (Map) currentValue;
            if (map == null) {
                if (type == Map.class)
                    map = new HashMap();
                else
                    map = (Map) type.getDeclaredConstructor().newInstance();
            }

            for (Iterator<String> it=json.keyIterator(); it.hasNext();) {
                String subKey = it.next();
                if (json.get(subKey) != null) {
                    map.put(subKey, readType(
                            (genericType.length >= 2 ? genericType[1] : null),
                            null,
                            null,
                            subKey,
                            json.get(subKey)));
                }
            }
            return map;
        }
        // Field is a new Object
        else {
            return readObject(type, key, json);
        }
    }


    protected Object readObject(Class<?> type, String key, DataNode json) throws ClassNotFoundException, IllegalArgumentException {
        // Only parse if json is a map
        if (json == null || !json.isMap())
            return null;
        // See if the Object id is in the cache before continuing
        if (json.getString(MD_OBJECT_ID) != null && objectCache.containsKey(json.getInt(MD_OBJECT_ID)))
            return objectCache.get(json.getInt(MD_OBJECT_ID));

        // ------------------------------------------------
        // Resolve the class
        // ------------------------------------------------

        Class<?> objClass;

        // Try using explicit class from target
        if (type != null) {
            objClass = type;
        }
        // Try using JSON metadata
        else if (json.getString(MD_CLASS) != null) {
            objClass = Class.forName(json.getString(MD_CLASS));
        }
        // Search for registered classes
        else if (registeredClasses.containsKey(key)) {
            objClass = registeredClasses.get(key);
        }
        // Unknown class
        else {
            logger.warning("Unknown type for key: '" + key + "'");
            return null;
        }

        // ------------------------------------------------
        // Instantiate object
        // ------------------------------------------------

        Object obj = null;

        // Date and time objects
        if (Date.class.isAssignableFrom(objClass)) {
            if (json.getString("timestamp") != null) {
                obj = new Date(json.getLong("timestamp"));
            }
        }
        else if (Calendar.class.isAssignableFrom(objClass)) {
            if (json.getString("timestamp") != null) {
                obj = Calendar.getInstance();
                ((Calendar) obj).setTimeInMillis(json.getLong("timestamp"));
            }
        }
        // Instantiate generic object
        else{
            try {
                obj = objClass.getDeclaredConstructor().newInstance();

                // Read all fields from the new object instance
                for (Field field : ClassUtil.getAllDeclaredFields(obj.getClass())) {
                    if ((field.getModifiers() & Modifier.STATIC) == 0 &&
                            (field.getModifiers() & Modifier.TRANSIENT) == 0 &&
                            json.get(field.getName()) != null) {
                        // Parse field
                        field.setAccessible(true);
                        field.set(obj, readType(
                                field.getType(),
                                ClassUtil.getGenericClasses(field),
                                field.get(obj),
                                field.getName(),
                                json.get(field.getName())));
                    }
                }
            } catch (Exception e) {
                logger.warning("Unable to instantiate object(" + key + "): " + e.getMessage());
            }
        }
        // Add object to the cache
        if (json.getString(MD_OBJECT_ID) != null)
            objectCache.put(json.getInt(MD_OBJECT_ID), obj);
        return obj;
    }


    protected static Object readPrimitive(Class<?> type, DataNode json) {
        if      (type == int.class ||
                type == Integer.class) return json.getInt();
        else if (type == long.class ||
                type == Long.class)    return json.getLong();

        else if (type == double.class ||
                type == Double.class)  return json.getDouble();

        else if (type == boolean.class ||
                type == Boolean.class) return json.getBoolean();
        else if (type == String.class)  return json.getString();
        return null;
    }




    @Override public void readFully(byte[] b) {
        throw new UnsupportedOperationException ();
    }
    @Override public void readFully(byte[] b, int off, int len) {
        throw new UnsupportedOperationException ();
    }
    @Override public int skipBytes(int n) {
        throw new UnsupportedOperationException ();
    }
    @Override public boolean readBoolean() {
        throw new UnsupportedOperationException ();
    }
    @Override public byte readByte() {
        throw new UnsupportedOperationException ();
    }
    @Override public int readUnsignedByte() {
        throw new UnsupportedOperationException ();
    }
    @Override public short readShort() {
        throw new UnsupportedOperationException ();
    }
    @Override public int readUnsignedShort() {
        throw new UnsupportedOperationException ();
    }
    @Override public char readChar() {
        throw new UnsupportedOperationException ();
    }
    @Override public int readInt() {
        throw new UnsupportedOperationException ();
    }
    @Override public long readLong() {
        throw new UnsupportedOperationException ();
    }
    @Override public float readFloat() {
        throw new UnsupportedOperationException ();
    }
    @Override public double readDouble() {
        throw new UnsupportedOperationException ();
    }
    @Override public String readLine() {
        throw new UnsupportedOperationException ();
    }
    @Override public String readUTF() {
        throw new UnsupportedOperationException ();
    }
    @Override public int read() throws IOException {
        throw new UnsupportedOperationException ();
    }

}
