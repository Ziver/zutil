package zutil.db.bean;

import zutil.log.LogUtil;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class contains a cache of all DBBeans that are allocated
 */
class DBBeanCache {
    private static final Logger logger = LogUtil.getLogger();
    /** This is the time to live for the cached items **/
    public static final long CACHE_DATA_TTL = 1000*60*5; // 5 min in ms
    /** A cache for detecting recursion **/
    private static Map<Class<?>, Map<Long, CacheItem>> cache =
            new ConcurrentHashMap<>();
    private static ScheduledExecutorService executor;


    static {
        enableBeanGBC(true); // Initiate DBBeanGarbageCollector
    }

    /**
     * A cache container that contains a bean and its last filed update time
     */
    private static class CacheItem{
        public long updateTimestamp;
        public WeakReference<DBBean> bean;
    }



    /**
     * This function cancels the internal cache garbage collector in DBBean.
     * GBC is enabled by default
     */
    public static synchronized void enableBeanGBC(boolean enable){
        if(enable){
            if( executor == null ){
                executor = Executors.newSingleThreadScheduledExecutor();
                executor.scheduleAtFixedRate( new DBBeanGarbageCollector(), CACHE_DATA_TTL, CACHE_DATA_TTL *2, TimeUnit.MILLISECONDS );
                logger.fine("Bean garbage collection daemon enabled");
            }
        }
        else {
            if (executor != null) {
                executor.shutdown();
                executor = null;
                logger.fine("Bean garbage collection daemon disabled");
            }
        }
    }

    /**
     * This class acts as an garbage collector that removes old DBBeans
     */
    private static class DBBeanGarbageCollector implements Runnable {
        public void run() {
            try {
                if (cache == null) {
                    logger.severe("DBBeanSQLResultHandler not initialized, stopping DBBeanGarbageCollector timer.");
                    enableBeanGBC(false);
                    return;
                }

                int removed = 0;
                for (Object classKey : cache.keySet()) {
                    if (classKey == null) continue;

                    Map<Long, CacheItem> class_cache = cache.get(classKey);
                    for (Iterator<Map.Entry<Long, CacheItem>> it = class_cache.entrySet().iterator(); it.hasNext(); ) {
                        Map.Entry<Long, CacheItem> entry = it.next();
                        if (entry.getKey() == null) continue;
                        // Check if session is still valid
                        if (entry.getValue().bean.get() == null) {
                            it.remove();
                            removed++;
                        }
                    }
                }
                if (removed > 0)
                    logger.info("DBBean GarbageCollector has cleared " + removed + " beans from cache.");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "DBBeanGarbageCollector thread has crashed", e);
            }
        }
    }



    public static boolean contains(DBBean obj){
        if (obj == null)
            return false;
        return contains(obj.getClass(), obj.getId());
    }
    public static boolean contains(Class<?> c, Long id){
        if( cache.containsKey(c) ){
            CacheItem cacheItem = cache.get(c).get(id);
            // Check if the cache is valid
            if( cacheItem != null && cacheItem.bean.get() != null ) {
                return true;
            } else {
                // The bean has been deallocated
                cache.get(c).remove(id);
            }
        }
        return false;
    }

    /**
     * Will check the cache if the given bean exists
     *
     * @param 		c			is the class of the bean
     * @param 		id			is the id of the bean
     * @return					a cached DBBean object, null if there is a cache miss
     */
    public static DBBean get(Class<?> c, Long id) {
        if(contains(c, id)){
            CacheItem cacheItem = cache.get(c).get(id);
            return cacheItem.bean.get();
        }
        logger.finer("Bean("+c.getName()+") cache miss for id: "+id);
        return null;
    }

    /**
     * @return true if the bean data is outdated, false if the data is current or if the bean was not found in the cache
     */
    public static boolean isOutDated(DBBean obj){
        if(contains(obj)) {
            CacheItem cacheItem = cache.get(obj.getClass()).get(obj.getId());
            return cacheItem.updateTimestamp + CACHE_DATA_TTL < System.currentTimeMillis();
        }
        return false;
    }

    /**
     * Will add a bean to the cache. If the bean already is in
     * the cache then its TTL timer will be reset
     */
    public synchronized static void add(DBBean obj) {
        if (contains(obj)) {
            cache.get(obj.getClass()).get(obj.getId()).updateTimestamp = System.currentTimeMillis();
            return;
        }
        CacheItem cacheItem = new CacheItem();
        cacheItem.updateTimestamp = System.currentTimeMillis();
        cacheItem.bean = new WeakReference<>(obj);
        if( cache.containsKey(obj.getClass()) )
            cache.get(obj.getClass()).put(obj.getId(), cacheItem);
        else{
            Map<Long, CacheItem> map = new ConcurrentHashMap<>();
            map.put(obj.getId(), cacheItem);
            cache.put(obj.getClass(), map);
        }
    }

    public static void remove(DBBean obj){
        if (obj != null)
            if( cache.containsKey(obj.getClass()) )
                cache.get(obj.getClass()).remove(obj.getId());
    }
}
