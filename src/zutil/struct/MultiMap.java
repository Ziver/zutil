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

package zutil.struct;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * A Map wrapping object that allows supports for multiple values per key.
 *
 * @param <K> – the type of keys maintained by this map
 * @param <V> – the type of mapped values
 */
public class MultiMap<K,V> implements Map<K,V> {

    private Map<K, LinkedList<V>> internalMap;


    /**
     * Creates a MultiMap
     */
    public MultiMap() {
         this.internalMap = new HashMap<>();
    }

    /**
     * Creates a MultiMap
     *
     * @param internalMapClass   Provide the type of the internal map.
     */
    public MultiMap(Class<? extends Map> internalMapClass) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        this.internalMap = internalMapClass.getDeclaredConstructor().newInstance();
    }


    /**
     * @return the number of keys registered in the Map. Note that the number
     *          of values contained might be more than the value returned here.
     */
    @Override
    public int size() {
        return internalMap.size();
    }

    @Override
    public boolean isEmpty() {
        return internalMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return internalMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        for (K key : internalMap.keySet()) {
            for (V keyValue : internalMap.get(key)){
                if (Objects.equals(value, keyValue))
                    return true;
            }
        }

        return false;
    }


    @Override
    public V put(K key, V value) {
        if (value == null) {
            remove(key);
            return null;
        }

        LinkedList<V> valueList = internalMap.get(key);

        if (valueList == null) {
            valueList = new LinkedList<V>();
            internalMap.put(key, valueList);
        }

        V prevValue = (valueList.isEmpty() ? null : valueList.getLast());
        valueList.add(value);

        return prevValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        if (m instanceof MultiMap) {
            for (K key : m.keySet()) {
                for (V value : ((MultiMap<? extends K, ? extends V>) m).getAll(key)){
                    put(key, value);
                }
            }
        } else {
            for (Entry<? extends K, ? extends V> e : m.entrySet()) {
                put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public V remove(Object key) {
        if (key == null) {
            return null;
        }

        LinkedList<V> valueList = internalMap.remove(key);

        if (valueList != null)
            return valueList.getFirst();
        else
            return null;
    }

    @Override
    public void clear() {
        internalMap.clear();
    }


    @Override
    public V get(Object key) {
        LinkedList<V> valueList = internalMap.get(key);

        if (valueList != null)
            return valueList.getLast();
        else
            return null;
    }

    /**
     * @return a list of all values associated with the specified key. A empty list will be returned if the key does not exist.
     */
    public List<V> getAll(Object key) {
        LinkedList<V> valueList = internalMap.get(key);

        if (valueList != null)
            return Collections.unmodifiableList(valueList);
        else
            return Collections.emptyList();
    }

    @Override
    public Set<K> keySet() {
        return internalMap.keySet();
    }

    @Override
    public Collection<V> values() {
        LinkedList<V> list = new LinkedList<>();

        for(List<V> valueList : internalMap.values()) {
            list.addAll(valueList);
        }

        return list;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }


}
