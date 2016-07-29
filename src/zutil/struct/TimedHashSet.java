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

import zutil.Timer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This is a timed HashSet. Each entry has a limited time to live.
 */
public class TimedHashSet<T> {

    private HashMap<T, Timer> map;
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
        return map.put(o, new Timer(ttl).start()) != null;
    }

    public boolean contains(Object o){
        if(map.containsKey(o)){
            if(map.get(o).hasTimedOut()) // entry to old
                map.remove(o);
            else
                return true;
        }
        return false;
    }


    /**
     * This method will return the number of stored entries(valid and timed out entries) in the Set.
     */
    public int size() {
        return map.size();
    }


    /**
     * Iterates through the Set and removes all entries that has passed its TTL.
     *
     * @return the number of objects removed from the Set
     */
    public int garbageCollect(){
        int count = 0;
        for(Iterator<Map.Entry<T, Timer>> it = map.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<T, Timer> entry = it.next();
            if (entry.getValue().hasTimedOut()) { // entry to old
                it.remove();
                ++count;
            }
        }
        return count;
    }

}
