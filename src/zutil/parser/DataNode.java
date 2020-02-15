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

package zutil.parser;

import java.util.*;


/**
 * This is a data node used in JSON and BEncoding and other types
 *
 * @author Ziver
 */
public class DataNode implements Iterable<DataNode> {
    public enum DataType {
        Map, List, String, Number, Boolean
    }

    private Map<String, DataNode> map = null;
    private List<DataNode> list = null;
    private String value = null;
    private DataType type;


    /**
     * Creates an instance with an Boolean value
     */
    public DataNode(boolean value) {
        this.type = DataType.Boolean;
        this.value = "" + value;
    }

    /**
     * Creates an instance with an int value
     */
    public DataNode(int value) {
        this.type = DataType.Number;
        this.value = "" + value;
    }

    /**
     * Creates an instance with an double value
     */
    public DataNode(double value) {
        this.type = DataType.Number;
        this.value = "" + value;
    }

    /**
     * Creates an instance with an long value
     */
    public DataNode(long value) {
        this.type = DataType.Number;
        this.value = "" + value;
    }

    /**
     * Creates an instance with an String value
     */
    public DataNode(String value) {
        this.type = DataType.String;
        this.value = value;
    }

    /**
     * Creates an instance with a specific type
     */
    public DataNode(DataType type) {
        this.type = type;
        switch (type) {
            case Map:
                map = new HashMap<>();
                break;
            case List:
                list = new LinkedList<>();
                break;
            default:
                break;
        }
    }

    /**
     * @param index is the index of the List or Map
     * @return an JSONNode that contains the next level of the List or Map
     */
    public DataNode get(int index) {
        if (map != null)
            return map.get("" + index);
        else if (list != null)
            return list.get(index);
        return null;
    }

    /**
     * @param index is the key in the Map
     * @return an JSONNode that contains the next level of the Map
     */
    public DataNode get(String index) {
        if (map != null)
            return map.get(index);
        return null;
    }

    /**
     * @return a iterator for the Map or List or null if the node contains a value
     */
    public Iterator<DataNode> iterator() {
        if (map != null)
            return map.values().iterator();
        else if (list != null)
            return list.iterator();
        return null;
    }

    /**
     * @return a iterator for the keys in the Map or null if the node contains a value or List
     */
    public Iterator<String> keyIterator() {
        if (map != null)
            return map.keySet().iterator();
        return null;
    }

    /**
     * @return the size of the Map or List or -1 if it is a value
     */
    public int size() {
        if (map != null)
            return map.size();
        else if (list != null)
            return list.size();
        return -1;
    }


    /**
     * Adds a node to the List
     */
    public void add(DataNode node) {
        list.add(node);
    }

    public void add(boolean value) {
        list.add(new DataNode(value));
    }

    public void add(int value) {
        list.add(new DataNode(value));
    }

    public void add(double value) {
        list.add(new DataNode(value));
    }

    public void add(long value) {
        list.add(new DataNode(value));
    }

    public void add(String value) {
        list.add(new DataNode(value));
    }

    /**
     * Adds a node to the Map
     */
    public void set(String key, DataNode node) {
        map.put(key, node);
    }

    public void set(String key, boolean value) {
        map.put(key, new DataNode(value));
    }

    public void set(String key, int value) {
        map.put(key, new DataNode(value));
    }

    public void set(String key, double value) {
        map.put(key, new DataNode(value));
    }

    public void set(String key, long value) {
        map.put(key, new DataNode(value));
    }

    public void set(String key, String value) {
        map.put(key, new DataNode(value));
    }

    /**
     * Sets the value of the node
     *
     * @throws NullPointerException if the node is setup as anything other than a DataType.Number
     */
    public void set(int value) {
        if (this.type != DataType.Number)
            throw new NullPointerException("The node is not setup as a DataType.Number");

        this.value = "" + value;
    }

    /**
     * Sets the value of the node
     *
     * @throws NullPointerException if the node is setup as anything other than a DataType.Number
     */
    public void set(double value) {
        if (this.type != DataType.Number)
            throw new NullPointerException("The node is not setup as a DataType.Number");

        this.value = "" + value;
    }

    /**
     * Sets the value of the node
     *
     * @throws NullPointerException if the node is setup as anything other than a DataType.Boolean
     */
    public void set(boolean value) {
        if (this.type != DataType.Boolean)
            throw new NullPointerException("The node is not setup as a DataType.Boolean");

        this.value = "" + value;
    }

    /**
     * Sets the value of the node, but only
     *
     * @throws NullPointerException if the node is setup as anything other than a DataType.Number
     */
    public void set(long value) {
        if (this.type != DataType.Number)
            throw new NullPointerException("The node is not setup as a DataType.Number");

        this.value = "" + value;
    }

    /**
     * Sets the value of the node
     *
     * @throws NullPointerException if the method DataType.isValue() returns false
     */
    public void set(String value) {
        if (!this.isValue())
            throw new NullPointerException("The node is not setup as a value node");

        this.value = value;
    }


    /**
     * @return if this node contains an Map
     */
    public boolean isMap() {
        return type == DataType.Map;
    }

    /**
     * @return if this node contains an List
     */
    public boolean isList() {
        return type == DataType.List;
    }

    /**
     * @return if this node contains an value
     */
    public boolean isValue() {
        return type != DataType.Map && type != DataType.List;
    }

    /**
     * @return the type of the node
     */
    public DataType getType() {
        return type;
    }


    /**
     * @return the String value in this map or null or if key does not exist
     * @throws NullPointerException if the node is not of type map
     */
    public String getString(String key) {
        if (!this.isMap())
            throw new NullPointerException("The node is not setup as a map");
        if (!this.map.containsKey(key))
            return null;

        return this.get(key).getString();
    }

    /**
     * @return the boolean value in this map
     * @throws NullPointerException if the node is not of type map or if key does not exist
     */
    public boolean getBoolean(String key) {
        if (!this.isMap())
            throw new NullPointerException("The node is not setup as a map");
        if (!this.map.containsKey(key))
            throw new NullPointerException("No such key '" + key + "' in map");

        return this.get(key).getBoolean();
    }

    /**
     * @return the integer value in this map
     * @throws NullPointerException if the node is not of type map or if key does not exist
     */
    public int getInt(String key) {
        if (!this.isMap())
            throw new NullPointerException("The node is not setup as a map");
        if (!this.map.containsKey(key))
            throw new NullPointerException("No such key '" + key + "' in map");

        return this.get(key).getInt();
    }

    /**
     * @return the double value in this map
     * @throws NullPointerException if the node is not of type map or if key does not exist
     */
    public double getDouble(String key) {
        if (!this.isMap())
            throw new NullPointerException("The node is not setup as a map");
        if (!this.map.containsKey(key))
            throw new NullPointerException("No such key '" + key + "' in map");

        return this.get(key).getDouble();
    }

    /**
     * @return the long value in this map
     * @throws NullPointerException if the node is not of type map or if key does not exist
     */
    public long getLong(String key) {
        if (!this.isMap())
            throw new NullPointerException("The node is not setup as a map");
        if (!this.map.containsKey(key))
            throw new NullPointerException("No such key '" + key + "' in map");

        return this.get(key).getLong();
    }

    /**
     * @return the long value in this map
     * @throws NullPointerException if the node is not of type map
     */
    public List getList(String key) {
        if (!this.isMap())
            throw new NullPointerException("The node is not setup as a map");

        return this.get(key).getList();
    }

    /**
     * @return the long value in this map
     * @throws NullPointerException if the node is not of type map
     */
    public Map<String,?> getMap(String key) {
        if (!this.isMap())
            throw new NullPointerException("The node is not setup as a map");

        return this.get(key).getMap();
    }


    /**
     * @return the String value in this node, null if its a Map or List
     */
    public String getString() {
        return value;
    }

    /**
     * @return the boolean value in this node
     */
    public boolean getBoolean() {
        return Boolean.parseBoolean(value);
    }

    /**
     * @return the integer value in this node
     */
    public int getInt() {
        return Integer.parseInt(value);
    }

    /**
     * @return the double value in this node
     */
    public double getDouble() {
        return Double.parseDouble(value);
    }

    /**
     * @return the long value in this node
     */
    public long getLong() {
        return Long.parseLong(value);
    }

    /**
     * @return a list with the items in the node
     * @throws NullPointerException if the node is not of type list
     */
    public List<?> getList() {
        if (!this.isList()) throw new NullPointerException("The node is not setup as a list");

        List output = new ArrayList();
        for (DataNode node : this) {
            switch (node.getType()) {
                case Map:     output.add(node.getMap());     break;
                case List:    output.add(node.getList());    break;
                case Number:  output.add(node.getDouble());  break;
                case Boolean: output.add(node.getBoolean()); break;
                case String:  output.add(node.getString());  break;
            }
        }
        return output;
    }

    /**
     * @return a map with the items in the node
     * @throws NullPointerException if the node is not of type map
     */
    public Map<String,?> getMap() {
        if (!this.isMap()) throw new NullPointerException("The node is not setup as a map");

        Map<String,Object> output = new HashMap<>();
        for (String key : map.keySet()) {
            DataNode node = get(key);
            switch (node.getType()) {
                case Map:     output.put(key, node.getMap());     break;
                case List:    output.put(key, node.getList());    break;
                case Number:  output.put(key, node.getDouble());  break;
                case Boolean: output.put(key, node.getBoolean()); break;
                case String:  output.put(key, node.getString());  break;
            }
        }
        return output;
    }


    public String toString() {
        if (this.isMap())
            return map.toString();
        else if (this.isList())
            return list.toString();
        return value;
    }
}
