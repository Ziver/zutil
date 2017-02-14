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

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Logger;


public class DBBeanSQLResultHandler<T> implements SQLResultHandler<T>{
	private static final Logger logger = LogUtil.getLogger();
	
	private Class<? extends DBBean> beanClass;
	private DBBeanConfig beanConfig;
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
		return new DBBeanSQLResultHandler<>(cl, db, false);
	}

	/**
	 * Creates a new instance of this class that returns a list of beans
	 * 
	 * @param 	cl		is the DBBean class that will be parsed from the SQL result
	 * @return			a new instance of this class
	 */
	public static <C extends DBBean> DBBeanSQLResultHandler<List<C>> createList(Class<C> cl){
		return new DBBeanSQLResultHandler<>(cl, null, true);
	}

	/**
	 * Creates a new instance of this class that returns a list of beans with all the internal beans
	 * 
	 * @param 	cl		is the DBBean class that will be parsed from the SQL result
	 * @param	db		is the DB connection for loading internal beans
	 * @return			a new instance of this class
	 */
	public static <C extends DBBean> DBBeanSQLResultHandler<List<C>> createList(Class<C> cl, DBConnection db){
		return new DBBeanSQLResultHandler<>(cl, db, true);
	}


	/**
	 * Creates a new instance of this class
	 * 
	 * @param 	cl		is the DBBean class that will be parsed from the SQL result
	 * @param	db		is the DB connection for loading internal beans, may be null to disable internal beans
	 * @param	list	is if the handler should return a list of beans instead of one
	 */
	protected DBBeanSQLResultHandler(Class<? extends DBBean> cl, DBConnection db, boolean list) {
		this.beanClass = cl;
		this.list = list;
		this.db = db;
		this.beanConfig = DBBeanConfig.getBeanConfig( cl );
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
			// Check cache first
			DBBean obj = DBBeanCache.get(beanClass, id, result);
			if ( obj == null ) {
                // Cache miss create a new object
                logger.fine("Creating new Bean(" + beanClass.getName() + ") with id: " + id);
                obj = beanClass.newInstance();
                obj.setId(id);
                DBBeanCache.add(obj);
            }
			
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
		if (obj.readLock.tryLock()) {
            try {
                logger.fine("Updating Bean(" + beanClass.getName() + ") with id: " + obj.getId());
                // Get the rest
                for (Field field : beanConfig.fields) {
                    String name = DBBeanConfig.getFieldName(field);

                    // Another DBBean class
                    if (DBBean.class.isAssignableFrom(field.getType())) {
                        if (db != null) {
                            Long subid = result.getLong(name);
                            DBBean subobj = DBBeanCache.get(field.getType(), subid);
                            if (subobj == null)
                                subobj = DBBean.load(db, (Class<? extends DBBean>) field.getType(), subid);
                            obj.setFieldValue(field, subobj);
                        } else
                            logger.warning("No DB available to read sub beans");
                    }
                    // A list of DBBeans
                    else if (List.class.isAssignableFrom(field.getType()) &&
                            field.getAnnotation(DBLinkTable.class) != null) {
                        if (db != null) {
                            DBLinkTable linkTable = field.getAnnotation(DBLinkTable.class);
                            DBBeanConfig subConfig = DBBeanConfig.getBeanConfig(linkTable.beanClass());
                            String linkTableName = linkTable.table();
                            String subTable = subConfig.tableName;
                            String idcol = (linkTable.idColumn().isEmpty() ? beanConfig.tableName : linkTable.idColumn());

                            // Load list from link table
                            String subsql = "SELECT subObjTable.* FROM " + linkTableName + " as linkTable, " + subTable + " as subObjTable WHERE linkTable." + idcol + "=? AND linkTable." + subConfig.idColumn + "=subObjTable." + subConfig.idColumn;
                            logger.finest("List Load Query: " + subsql);
                            PreparedStatement subStmt = db.getPreparedStatement(subsql);
                            subStmt.setObject(1, obj.getId());
                            List<? extends DBBean> list = DBConnection.exec(subStmt,
                                    DBBeanSQLResultHandler.createList(linkTable.beanClass(), db));
                            obj.setFieldValue(field, list);
                        } else
                            logger.warning("No DB available to read sub beans");
                    }
                    // Normal field
                    else {
                        obj.setFieldValue(field, result.getObject(name));
                    }
                }

                obj.postUpdateAction();
            } finally {
                obj.readLock.unlock();
            }
        } else {
            obj.readLock.lock();
            obj.readLock.unlock();
        }
	}

	
}
