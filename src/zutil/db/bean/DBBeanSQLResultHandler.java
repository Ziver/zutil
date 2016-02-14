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

package zutil.db.bean;

import zutil.db.DBConnection;
import zutil.db.SQLResultHandler;
import zutil.db.bean.DBBean.DBLinkTable;
import zutil.log.LogUtil;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class DBBeanSQLResultHandler<T> implements SQLResultHandler<T>{
	private static final Logger logger = LogUtil.getLogger();
	/** This is the time to live for the cached items **/
	public static final long CACHE_DATA_TTL = 1000*60*5; // 5 min in ms
	/** A cache for detecting recursion **/
	protected static Map<Class<?>, Map<Long,DBBeanCache>> cache =
		new ConcurrentHashMap<Class<?>, Map<Long,DBBeanCache>>();
	private static Timer timer;

    static {
        enableBeanGBC(true); // Initiate DBBeanGarbageCollector
    }

	/**
	 * A cache container that contains a object and last read time
	 */
	private static class DBBeanCache{
		public long updateTimestamp;
		public WeakReference<DBBean> bean;
	}
	
	private Class<? extends DBBean> bean_class;
	private DBBeanConfig bean_config;
	private DBConnection db;
	private boolean list;

	/**
	 * Creates a new instance of this class that returns only one bean
	 * 
	 * @param 	cl		is the DBBean class that will be parsed from the SQL result
	 * @return			a new instance of this class
	 */
	public static <C extends DBBean> DBBeanSQLResultHandler<C> create(Class<C> cl){		
		return new DBBeanSQLResultHandler<C>(cl, null, false);
	}

	/**
	 * Creates a new instance of this class that returns a bean with all its containing beans
	 * 
	 * @param 	cl		is the DBBean class that will be parsed from the SQL result
	 * @param	db		is the DB connection for loading internal beans
	 * @return			a new instance of this class
	 */
	public static <C extends DBBean> DBBeanSQLResultHandler<C> create(Class<C> cl, DBConnection db){		
		return new DBBeanSQLResultHandler<C>(cl, db, false);
	}

	/**
	 * Creates a new instance of this class that returns a list of beans
	 * 
	 * @param 	cl		is the DBBean class that will be parsed from the SQL result
	 * @return			a new instance of this class
	 */
	public static <C extends DBBean> DBBeanSQLResultHandler<List<C>> createList(Class<C> cl){
		return new DBBeanSQLResultHandler<List<C>>(cl, null, true);
	}

	/**
	 * Creates a new instance of this class that returns a list of beans with all the internal beans
	 * 
	 * @param 	cl		is the DBBean class that will be parsed from the SQL result
	 * @param	db		is the DB connection for loading internal beans
	 * @return			a new instance of this class
	 */
	public static <C extends DBBean> DBBeanSQLResultHandler<List<C>> createList(Class<C> cl, DBConnection db){
		return new DBBeanSQLResultHandler<List<C>>(cl, db, true);
	}

	/**
	 * Creates a new instance of this class
	 * 
	 * @param 	cl		is the DBBean class that will be parsed from the SQL result
	 * @param	db		is the DB connection for loading internal beans, may be null to disable internal beans
	 * @param	list	is if the handler should return a list of beans instead of one
	 */
	protected DBBeanSQLResultHandler(Class<? extends DBBean> cl, DBConnection db, boolean list) {
		this.bean_class = cl;
		this.list = list;
		this.db = db;
		this.bean_config = DBBeanConfig.getBeanConfig( cl );
	}

	/**
	 * This function cancels the internal cache garbage collector in DBBean.
     * GBC is enabled by default
	 */
	private static void enableBeanGBC(boolean enable){
        if(enable){
            if( timer == null ){
                timer = new Timer( true ); // Run as daemon
                timer.schedule( new DBBeanGarbageCollector(), CACHE_DATA_TTL, CACHE_DATA_TTL *2 );
				logger.info("Bean garbage collection daemon enabled");
            }
        }
        else {
            if (timer != null) {
                timer.cancel();
                timer = null;
				logger.info("Bean garbage collection daemon disabled");
            }
        }
	}

	/**
	 * This class acts as an garbage collector that removes old DBBeans
	 * 
	 * @author Ziver
	 */
	private static class DBBeanGarbageCollector extends TimerTask {
		public void run(){
			if( cache == null ){
				logger.severe("DBBeanSQLResultHandler not initialized, stopping DBBeanGarbageCollector timer.");
				this.cancel();
				return;
			}
			
			int removed = 0;
			long time = System.currentTimeMillis();
			for(Object classKey : cache.keySet()){
				if( classKey == null ) continue;
					
				Map<Long,DBBeanCache> class_cache = cache.get(classKey);
				for(Object objKey : class_cache.keySet()){
					if( objKey == null ) continue;
					
					DBBeanCache beanCache = class_cache.get(objKey);
					// Check if session is still valid
					if( beanCache.bean.get() == null ){
						class_cache.remove(objKey);
						removed++;
					}
				}
			}

            logger.info("DBBean GarbageCollector has cleared "+removed+" beans from cache.");
		}
	}
	
	/**
	 *  Is called to handle a result from a query.
	 * 
	 * @param stmt 		is the query
	 * @param result 	is the ResultSet
	 */
	@SuppressWarnings("unchecked")
	public T handleQueryResult(Statement stmt, ResultSet result) throws SQLException{
		if( list ){
			LinkedList<DBBean> bean_list = new LinkedList<DBBean>();
			while( result.next() ){
				DBBean obj = createBean(result);
				bean_list.add( obj );
			}
			return (T) bean_list;
		}
		else{
			if( result.next() ){
				return (T) createBean(result);
			}
			return null;
		}

	}
	
	
	/**
	 * Instantiates a new bean and assigns field values from the ResultSet
	 * 
	 * @param 		result		is where the field values for the bean will bee read from, the cursor should be in front of the data
	 * @return					a new instance of the bean
	 */
	protected DBBean createBean(ResultSet result) throws SQLException{
		try {
			Long id = result.getLong( "id" );
			DBBean obj = getCachedDBBean(bean_class, id, result);
			if( obj != null )
				return obj;
			logger.fine("Creating new Bean("+bean_class.getName()+") with id: "+id);
			obj = bean_class.newInstance();
            obj.id = id; // Set id field
			cacheDBBean(obj);
			
			// Update fields
			updateBean( result, obj );
			return obj;
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Updates an existing bean and assigns field values from the ResultSet
	 * 
	 * @param 		result		is where the field values for the bean will bee read from, the cursor should be in front of the data
	 * @param		obj			is the object that will be updated
	 */
	@SuppressWarnings("unchecked")
	protected void updateBean(ResultSet result, DBBean obj) throws SQLException{
		try {
			logger.fine("Updating Bean("+bean_class.getName()+") with id: "+obj.id);
			obj.processing_update = true;
			// Get the rest
			for( Field field : bean_config.fields ){
				String name = DBBeanConfig.getFieldName(field);

				// Another DBBean class
				if( DBBean.class.isAssignableFrom( field.getType() )){
					if(db != null){
						Long subid = result.getLong( name );
						DBBean subobj = getCachedDBBean( field.getType(), subid );
						if( subobj == null )
							subobj = DBBean.load(db, (Class<? extends DBBean>)field.getType(), subid);
						obj.setFieldValue(field, subobj);
					}
				}
				// A list of DBBeans
				else if( List.class.isAssignableFrom( field.getType() ) && 
						field.getAnnotation( DBLinkTable.class ) != null){
					if(db != null){
						DBLinkTable linkTable = field.getAnnotation( DBLinkTable.class );
						DBBeanConfig subConfig = DBBeanConfig.getBeanConfig( linkTable.beanClass() );
						String linkTableName = linkTable.table();
						String subTable = subConfig.tableName;
						String idcol = (linkTable.idColumn().isEmpty() ? bean_config.tableName : linkTable.idColumn() );

						// Load list from link table
						//String subsql = "SELECT * FROM "+linkTableName+" NATURAL JOIN "+subConfig.tableName+" WHERE "+idcol+"=?";
						String subsql = "SELECT obj.* FROM "+linkTableName+" as link, "+subTable+" as obj WHERE obj."+idcol+"=? AND obj."+bean_config.idColumn+"=link.id";
						logger.finest("List Load Query: "+subsql);
						PreparedStatement subStmt = db.getPreparedStatement( subsql );
						subStmt.setObject(1, obj.getId() );
						List<? extends DBBean> list = DBConnection.exec(subStmt, 
								DBBeanSQLResultHandler.createList(linkTable.beanClass(), db));
						obj.setFieldValue(field, list);
					}
				}
				// Normal field
				else				
					obj.setFieldValue(field, result.getObject(name));
			}
		} finally{
			obj.processing_update = false;
		}
		
		obj.postUpdateAction();
	}

	/**
	 * Will check the cache if the given object exists
	 * 
	 * @param 		c			is the class of the bean
	 * @param 		id			is the id of the bean
	 * @return					a cached DBBean object, or null if there is no cached object or if the cache is to old
	 */
	protected DBBean getCachedDBBean(Class<?> c, Long id){
		try{
			return getCachedDBBean( c, id, null );
		}catch(SQLException e){
			throw new RuntimeException("This exception should not be thrown, Something ent ready wrong!", e);
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
	protected DBBean getCachedDBBean(Class<?> c, Long id, ResultSet result) throws SQLException{
		if( cache.containsKey(c) ){
			DBBeanCache cacheItem = cache.get(c).get(id);
			// Check if the cache is valid
			if( cacheItem != null){
                DBBean bean = cacheItem.bean.get();
                if( bean != null) {
                    // The cache is old, update and return it
                    if (cacheItem.updateTimestamp + CACHE_DATA_TTL < System.currentTimeMillis()) {
                        // There is no ResultSet to update from
                        if (result == null)
                            return null;
                        // Only update object if there is no update running now
                        if (!bean.processing_update) {
                            logger.finer("Bean(" + c.getName() + ") cache to old for id: " + id);
                            updateBean(result, bean);
                        }
                    }
                    return bean;
                }
			}
			// The cache is null
			cache.get(c).remove(id);
		}
		logger.finer("Bean("+c.getName()+") cache miss for id: "+id);
		return null;
	}
	
	/**
	 * Adds the given object to the cache
	 * 
	 * @param 		obj		is the object to cache
	 */
	protected static synchronized void cacheDBBean(DBBean obj) {
		DBBeanCache cacheItem = new DBBeanCache();
		cacheItem.updateTimestamp = System.currentTimeMillis();
		cacheItem.bean = new WeakReference<DBBean>(obj);
		if( cache.containsKey(obj.getClass()) )
			cache.get(obj.getClass()).put(obj.getId(), cacheItem);
		else{
			Map<Long, DBBeanCache> map = new ConcurrentHashMap<Long, DBBeanCache>();
			map.put(obj.getId(), cacheItem);
			cache.put(obj.getClass(), map);
		}
	}
	
}
