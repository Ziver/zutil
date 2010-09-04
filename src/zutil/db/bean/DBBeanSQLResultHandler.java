package zutil.db.bean;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

import zutil.db.DBConnection;
import zutil.db.SQLResultHandler;
import zutil.db.bean.DBBean.DBBeanConfig;
import zutil.db.bean.DBBean.DBLinkTable;

public class DBBeanSQLResultHandler<T> implements SQLResultHandler<T>{

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
	 * Is called to handle an result from an query.
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
			if( result.next() )
				return (T) createBean(result);
			return null;
		}

	}

	/**
	 * Instantiates a new bean and assigns field values from the ResultSet
	 * 
	 * @param 	result	is where the field values for the bean will bee read from, the cursor should be in front of the data
	 * @return			a new instance of the bean
	 */
	@SuppressWarnings("unchecked")
	private DBBean createBean(ResultSet result) throws SQLException{
		try {
			DBBean obj = bean_class.newInstance();

			for( Field field : bean_config.fields ){
				String name = field.getName();

				// Another DBBean class
				if( DBBean.class.isAssignableFrom( field.getType() )){
					if(db != null){
						DBBean subobj = DBBean.load(db, (Class<? extends DBBean>)field.getType(), result.getObject(name));
						obj.setFieldValue(field, subobj);
					}
				}
				// A list of DBBeans
				else if( List.class.isAssignableFrom( field.getType() ) && 
						field.getAnnotation( DBLinkTable.class ) != null){
					if(db != null){
						String subtable = field.getAnnotation( DBLinkTable.class ).value().replace("\"", "");

						// Load list from link table
						PreparedStatement subStmt = db.getPreparedStatement("SELECT * FROM \""+subtable+"\" WHERE ?=?");
						List<? extends DBBean> list = DBConnection.exec(subStmt, 
								DBBeanSQLResultHandler.createList((Class<? extends DBBean>)field.getType(), db));
						obj.setFieldValue(field, list);
					}
				}
				// Normal field
				else
					obj.setFieldValue(field, result.getObject(name));
			}
			return obj;

		} catch (InstantiationException e) {
			throw new SQLException(e);
		} catch (IllegalAccessException e) {
			throw new SQLException(e);
		}
	}

}
