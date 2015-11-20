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
