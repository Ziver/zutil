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

package zutil.struct;

import java.util.AbstractSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * This is a timed HashSet. Each entry has a limited time to live.
 */
public class TimedHashSet<T> {

    private HashMap<T, Long> map;
    private long ttl;

    /**
     * @param   ttl     milliseconds the entries will live
     */
    public TimedHashSet(long ttl){
        this.ttl = ttl;
        this.map = new HashMap<>();
    }


    /**
     * @return true if the object already existed in the set which will reset the TTL.
     */
    public boolean add(T o){
        return map.put(o, System.currentTimeMillis()) != null;
    }

    public boolean contains(Object o){
        if(map.containsKey(o)){
            if(map.get(o) + ttl < System.currentTimeMillis()) // entry to old
                map.remove(o);
            else
                return true;
        }
        return false;
    }


    /**
     * Iterates through the Set and removes all entries that has passed the TTL
     */
    public void garbageCollect(){
        for(T o : map.keySet()){
            if(map.get(o) + ttl < System.currentTimeMillis()) // entry to old
                map.remove(o);
        }
    }
}
