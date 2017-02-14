package zutil.db.bean;

import zutil.log.LogUtil;

import java.lang.ref.WeakReference;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
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
    private static Timer timer;


    static {
        enableBeanGBC(true); // Initiate DBBeanGarbageCollector
    }

    /**
     * A cache container that contains a object and last read time
     */
    private static class CacheItem{
        public long updateTimestamp;
        public WeakReference<DBBean> bean;
    }



    /**
     * This function cancels the internal cache garbage collector in DBBean.
     * GBC is enabled by default
     */
    public static void enableBeanGBC(boolean enable){
        if(enable){
            if( timer == null ){
                timer = new Timer( true ); // Run as daemon
                timer.schedule( new DBBeanGarbageCollector(), CACHE_DATA_TTL, CACHE_DATA_TTL *2 );
                logger.fine("Bean garbage collection daemon enabled");
            }
        }
        else {
            if (timer != null) {
                timer.cancel();
                timer = null;
                logger.fine("Bean garbage collection daemon disabled");
            }
        }
    }

    /**
     * This class acts as an garbage collector that removes old DBBeans
     */
    private static class DBBeanGarbageCollector extends TimerTask {
        public void run(){
            if( cache == null ){
                logger.severe("DBBeanSQLResultHandler not initialized, stopping DBBeanGarbageCollector timer.");
                this.cancel();
                return;
            }

            int removed = 0;
            for(Object classKey : cache.keySet()){
                if( classKey == null ) continue;

                Map<Long, CacheItem> class_cache = cache.get(classKey);
                for(Iterator<Map.Entry<Long, CacheItem>> it = class_cache.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<Long, CacheItem> entry = it.next();
                    if( entry.getKey() == null ) continue;
                    // Check if session is still valid
                    if( entry.getValue().bean.get() == null ){
                        it.remove();
                        removed++;
                    }
                }
            }
            if (removed > 0)
                logger.info("DBBean GarbageCollector has cleared "+removed+" beans from cache.");
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
     * Will check the cache if the given object exists
     *
     * @param 		c			is the class of the bean
     * @param 		id			is the id of the bean
     * @return					a cached DBBean object, or null if there is no cached object or if the cache is to old
     */
    public static DBBean get(Class<?> c, Long id){
        try{
            return get( c, id, null );
        }catch(SQLException e){
            throw new RuntimeException("This exception should not be thrown, Something went really wrong!", e);
        }
    }

    /**
     * Will check the cache if the given object exists and will update it if its old
     *
     * @param 		c			is the class of the bean
     * @param 		id			is the id of the bean
     * @param		result		is the ResultSet for this object, the object will be updated from this ResultSet if the object is to old, there will be no update if this parameter is null
     * @return					a cached DBBean object, might update the cached object if its old but only if the ResultSet parameter is set
     */
    public static DBBean get(Class<?> c, Long id, ResultSet result) throws SQLException{
        if(contains(c, id)){
            CacheItem cacheItem = cache.get(c).get(id);
            DBBean bean = cacheItem.bean.get();
            // The cache is old, update and return it
            if (cacheItem.updateTimestamp + CACHE_DATA_TTL < System.currentTimeMillis()) {
                // There is no ResultSet to update from
                if (result == null)
                    return null;
                // Only update object if there is no update running now
                logger.finer("Bean(" + c.getName() + ") cache to old for id: " + id);
                // TODO:updateBean(result, bean);
            }
            return bean;
        }
        logger.finer("Bean("+c.getName()+") cache miss for id: "+id);
        return null;
    }

    /**
     * Will check if the object with the id already exists in the cahce,
     * if not then it will add the given object to the cache.
     *
     * @param 		obj		is the object to cache
     */
    public synchronized static void add(DBBean obj) {
        if (contains(obj))
            return;
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
}
