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

import zutil.Timer;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This is a Object cache where objects can be stored with {@link java.lang.ref.WeakReference} and a time limit.
 * 
 * Created by Ziver on 2016-07-29.
 */
public class ObjectCache<K, V> {

    private static class CacheEntry<V>{
        public Timer timer;
        public WeakReference<V> value;
    }

    private HashMap<K, CacheEntry<V>> cache = new HashMap<>();
    private long ttl;


    public ObjectCache(long ttl){
        this.ttl = ttl;
    }


    /**
     * Stores a key and value pair in the cache.
     */
    public void put(K key, V value){
        CacheEntry<V> entry = new CacheEntry<>();
        entry.timer = new Timer(ttl).start();
        entry.value = new WeakReference<>(value);
        cache.put(key, entry);
    }


    /**
     * Checks if the specific key is available in
     * the cache and that it is valid.
     */
    public boolean containsKey(Object key){
        if(cache.containsKey(key)){
            CacheEntry<V> entry = cache.get(key);
            if (entry.timer.hasTimedOut() || entry.value.get() == null) // entry to old or not valid
                cache.remove(key);
            else
                return true;
        }
        return false;
    }


    public V get(Object key){
        if (containsKey(key))
            return cache.get(key).value.get();
        return null;
    }



    /**
     * This method will return the number of stored entries(valid and timed out entries) in the cache.
     */
    public int size() {
        return cache.size();
    }



    /**
     * Iterates through the Set and removes all entries that has passed its TTL.
     *
     * @return the number of objects removed from the Set
     */
    public int garbageCollect(){
        int count = 0;
        for(Iterator<Map.Entry<K, CacheEntry<V>>> it = cache.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<K, CacheEntry<V>> mapEntry = it.next();
            if (mapEntry.getValue().timer.hasTimedOut() || mapEntry.getValue().value.get() == null) { // entry to old or not valid
                it.remove();
                ++count;
            }
        }
        return count;
    }
}
