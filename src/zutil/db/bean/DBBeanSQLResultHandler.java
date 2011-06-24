package zutil.db.bean;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import zutil.db.DBConnection;
import zutil.db.SQLResultHandler;
import zutil.db.bean.DBBean.DBBeanConfig;
import zutil.db.bean.DBBean.DBLinkTable;
import zutil.log.LogUtil;

public class DBBeanSQLResultHandler<T> implements SQLResultHandler<T>{
	public static final Logger logger = LogUtil.getLogger();
	/** This is the time to live for the cached items **/
	public static final long CACHE_TTL = 1000*60*1; // 1 min in ms
	/** A cache for detecting recursion **/
	protected static Map<Class<?>, Map<Long,DBBeanCache>> cache =
		Collections.synchronizedMap(new HashMap<Class<?>, Map<Long,DBBeanCache>>());
	/**
	 * A cache container that contains a object and last read time
	 */
	protected static class DBBeanCache{
		public long timestamp;
		public DBBean bean;
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
		this.bean_config = DBBean.getBeanConfig( cl );
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
			logger.fine("Loading new DBBean List");
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
			logger.fine("Creating new DBBean("+bean_class.getName()+") with id: "+id);
			obj = bean_class.newInstance();
			cacheDBBean(obj, id);
			
			// Set id field
			obj.id = id;
			
			// Update fields
			updateBean( result, obj );
			return obj;
		} catch (Exception e) {
			throw new SQLException(e);
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
			logger.fine("Updating DBBean("+bean_class.getName()+") with id: "+obj.id);
			obj.processing_update = true;
			// Get the rest
			for( Field field : bean_config.fields ){
				String name = field.getName();

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
						DBBeanConfig subConfig = DBBean.getBeanConfig( linkTable.beanClass() );
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
		} catch (Exception e) {
			throw new SQLException(e);
		} finally{
			obj.processing_update = false;
		}
		
		obj.updatePerformed();
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
			throw new RuntimeException("This exception sould not be thrown, Somting is realy wrong!", e);
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
			DBBeanCache item = cache.get(c).get(id);
			// Check if the cache is valid
			if( item != null ){
				// The cache is old, update and return it
				if( item.timestamp+CACHE_TTL > System.currentTimeMillis() ){
					// There is no ResultSet to update from
					if( result == null )
						return null;
					// Only update object if there is no update running now
					if( !item.bean.processing_update ){
						logger.finer("Cache to old: "+c.getName()+" ID: "+id);
						updateBean( result, item.bean );
					}
				}
				return item.bean;
			}
			// The cache is null
			cache.get(c).remove(id);
		}
		logger.finer("Cache miss: "+c.getName()+" ID: "+id);
		return null;
	}
	
	/**
	 * Adds the given object to the cache
	 * 
	 * @param 		obj		is the object to cache
	 * @param 		id		is the id object of the bean
	 */
	protected static synchronized void cacheDBBean(DBBean obj, Long id) {
		DBBeanCache item = new DBBeanCache();
		item.timestamp = System.currentTimeMillis();
		item.bean = obj;
		if( cache.containsKey(obj.getClass()) )
			cache.get(obj.getClass()).put(id, item);
		else{
			HashMap<Long, DBBeanCache> map = new HashMap<Long, DBBeanCache>();
			map.put(id, item);
			cache.put(obj.getClass(), map);
		}
	}
	
}
