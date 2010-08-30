package zutil.db.bean;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

import zutil.db.SQLResultHandler;
import zutil.db.bean.DBBean.DBBeanConfig;

public class DBBeanResultHandler<T> implements SQLResultHandler<T>{

	private Class<? extends DBBean> bean_class;
	private DBBeanConfig bean_config;
	private boolean list;

	/**
	 * Creates a new instance of this class that returns only one bean
	 * 
	 * @param 	cl		is the DBBean class that will be parsed from the SQL result
	 * @return			a new instance of this class
	 */
	public static <C extends DBBean> DBBeanResultHandler<C> create(Class<C> cl){		
		return new DBBeanResultHandler<C>(cl, false);
	}

	/**
	 * Creates a new instance of this class that returns a list of beans
	 * 
	 * @param 	cl		is the DBBean class that will be parsed from the SQL result
	 * @return			a new instance of this class
	 */
	public static <C extends DBBean> DBBeanResultHandler<List<C>> createList(Class<C> cl){
		return new DBBeanResultHandler<List<C>>(cl, true);
	}

	/**
	 * Creates a new instance of this class
	 * 
	 * @param 	cl		is the DBBean class that will be parsed from the SQL result
	 */
	protected DBBeanResultHandler(Class<? extends DBBean> cl, boolean list) {
		this.bean_class = cl;
		this.list = list;
		this.bean_config = DBBean.getBeanConfig( cl );
	}

	/**
	 * Is called to handle an result from an query.
	 * 
	 * @param stmt 		is the query
	 * @param result 	is the ResultSet
	 */
	@SuppressWarnings("unchecked")
	public T handle(Statement stmt, ResultSet result) throws SQLException{
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
				if( DBBean.class.isAssignableFrom( field.getDeclaringClass() )){
					DBBean subobj = DBBean.load(null, (Class<? extends DBBean>)field.getDeclaringClass(), result.getObject(name));
					obj.setFieldValue(field, subobj);
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
