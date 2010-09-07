package zutil.db.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import zutil.db.DBConnection;

/**
 * <XMP>
 * The class that extends this will be able to save its state to a DB.
 * Fields that are transient will be ignored, and fields that extend 
 * DBBean will be replaced by the id field of that class.
 * 
 * Supported fields:
 * *Boolean
 * *Integer
 * *Short
 * *Float
 * *Double
 * *String
 * *Character
 * *DBBean
 * *List<DBBean>
 * 
 * </XMP>
 * @author Ziver
 */
public abstract class DBBean {
	
	/**
	 * Sets the name of the table in the database
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface DBTable {
	    String value();
	}
	
	/**
	 * Sets the name of the table that links different DBBeans together
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface DBLinkTable {
		/** The name of the Link table */
	    String name();
	    /** The name of the column that contains this objects id */
	    String column() default "";
	}
	
	/**
	 * Sets the field as the id column in the table
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface DBTableID {}

	/**
	 * A Class that contains information about a bean
	 */
	protected static class DBBeanConfig{
		/** The name of the table in the DB */
		protected String tableName;
		/** The id field */
		protected Field id_field;
		/** All the fields in the bean */
		protected ArrayList<Field> fields;
		
		protected DBBeanConfig(){
			fields = new ArrayList<Field>();
		}
	}
	
	/** This is a cache of all the initialized beans */	 
	private static HashMap<Class<? extends DBBean>,DBBeanConfig> beanConfigs = new HashMap<Class<? extends DBBean>,DBBeanConfig>();
	/** This value is for preventing recursive loops */	 
	private boolean processing;
	
	protected DBBean(){
		if( !beanConfigs.containsKey( this.getClass() ) )
			initBeanConfig( this.getClass() );
		processing = false;
	}
	
	/**
	 * @return the ID field of the bean or null if there is non
	 */
	public static Field getIDField(Class<? extends DBBean> c){
		if( !beanConfigs.containsKey( c ) )
			initBeanConfig( c );
		return beanConfigs.get( c ).id_field;
	}
		
	/**
	 * @return all the fields except the ID field
	 */
	public static ArrayList<Field> getFields(Class<? extends DBBean> c){
		if( !beanConfigs.containsKey( c ) )
			initBeanConfig( c );
		return beanConfigs.get( c ).fields;		
	}
	
	/**
	 * @return the configuration object for the specified class
	 */
	protected static DBBeanConfig getBeanConfig(Class<? extends DBBean> c){
		if( !beanConfigs.containsKey( c ) )
			initBeanConfig( c );
		return beanConfigs.get( c );	
	}
	
	/**
	 * Caches the fields
	 */
	private static void initBeanConfig(Class<? extends DBBean> c){
		Field[] fields = c.getDeclaredFields();
		DBBeanConfig config = new DBBeanConfig();
		// Find the table name
		if( c.getAnnotation(DBTable.class) != null )
				config.tableName = c.getAnnotation(DBTable.class).value().replace('\"', ' ');
		// Add the fields in the bean
		for( Field field : fields ){
			if( !Modifier.isTransient(field.getModifiers()) ){
				if(field.getAnnotation(DBBean.DBTableID.class) != null)			
					config.id_field = field;		
				config.fields.add( field );
			}
		}
		
		beanConfigs.put(c, config);
	}
	
	/**
	 * Saves the Object to the DB
	 */
	@SuppressWarnings("unchecked")
	public void save(DBConnection sql) throws SQLException{
		if(processing)
			return;
		processing = true;
		Class<? extends DBBean> c = this.getClass();
		DBBeanConfig config = beanConfigs.get(c);
		try {
			// Generate the SQL
			StringBuilder query = new StringBuilder();
			query.append("REPLACE INTO ?  ");
			
			for( Field field : config.fields ){
				query.append(" ");
				query.append(field.getName());
				query.append("=?, ");
			}
			query.delete( query.length()-2, query.length());
			PreparedStatement stmt = sql.getPreparedStatement( sql.toString() );
			// add the table name
			stmt.setObject(1, config.tableName);
			// Put in the variables in the SQL
			for(int i=0; i<config.fields.size() ;++i ){
				Field field = config.fields.get(i);				
				
				// Another DBBean class
				if( DBBean.class.isAssignableFrom( field.getType() )){
					DBBean subobj = (DBBean)field.get(this);
					subobj.save(sql);
					stmt.setObject(i+2, getBeanConfig(subobj.getClass()) );
				}
				// A list of DBBeans
				else if( List.class.isAssignableFrom( field.getType() ) && 
						field.getAnnotation( DBLinkTable.class ) != null){
					List<DBBean> list = (List<DBBean>)field.get(this);
					DBLinkTable linkTable = field.getAnnotation( DBLinkTable.class );
					String subtable = linkTable.name();
					String idcol = (linkTable.column().isEmpty() ? config.tableName : linkTable.column() );

					DBBeanConfig subConfig = null;
					for(DBBean subobj : list){
						if(subConfig == null)
							subConfig = beanConfigs.get( subobj.getClass() );
						// Save links in link table
						PreparedStatement subStmt = sql.getPreparedStatement("REPLACE INTO ? ?=? ?=?");
						subStmt.setString(1, subtable);
						subStmt.setString(2, idcol);
						subStmt.setObject(3, config.id_field);
						subStmt.setString(4, subConfig.tableName);
						subStmt.setObject(5, subConfig.id_field.get(subobj));
						DBConnection.exec(subStmt);
						// Save the sub bean
						subobj.save(sql);
					}
				}
				// Normal field
				else
					stmt.setObject(i+2, field.get(this));
			}
			
			// Execute the SQL
			DBConnection.exec(stmt);
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	/**
	 * Deletes the object from the DB, WARNING will not delete sub beans
	 */
	public void delete(DBConnection sql){
		Class<? extends DBBean> c = this.getClass();
		DBBeanConfig config = beanConfigs.get(c);
		if( config.id_field == null )
			throw new NoSuchElementException("DBTableID annotation missing in bean!");
		try {		
			PreparedStatement stmt = sql.getPreparedStatement(
					"DELETE FROM ? WHERE "+ config.id_field +"=?");
			// Put in the variables in the SQL		
			stmt.setObject(1, config.tableName );
			stmt.setObject(2, config.id_field.get(this) );
			
			// Execute the SQL
			DBConnection.exec(stmt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads all the rows in the table into a LinkedList
	 * 
	 * @param 	<T> 	is the class of the bean
	 * @param 	c 		is the class of the bean
	 * @return			a LinkedList with all the Beans in the DB
	 */
	public static <T extends DBBean> List<T> load(DBConnection db, Class<T> c) throws SQLException {
		// Initiate a BeanConfig if there is non
		if( !beanConfigs.containsKey( c ) )
			initBeanConfig( c );
		DBBeanConfig config = beanConfigs.get(c);
		// Generate query
		PreparedStatement stmt = db.getPreparedStatement( "SELECT * FROM ?" );
		stmt.setString(1, config.tableName);
		// Run query
		List<T> list = DBConnection.exec(stmt, DBBeanSQLResultHandler.createList(c, db) );
		return list;
	}
	
	/**
	 * Loads all the rows in the table into a LinkedList
	 * 
	 * @param 	<T> 	is the class of the bean
	 * @param 	c 		is the class of the bean
	 * @param	id		is the id value of the bean
	 * @return			a DBBean Object with the specific id or null
	 */
	public static <T extends DBBean> T load(DBConnection db, Class<T> c, Object id) throws SQLException {
		// Initiate a BeanConfig if there is non
		if( !beanConfigs.containsKey( c ) )
			initBeanConfig( c );
		DBBeanConfig config = beanConfigs.get(c);
		// Generate query
		PreparedStatement stmt = db.getPreparedStatement( "SELECT * FROM ? WHERE ?=? LIMIT 1" );
		stmt.setString(1, config.tableName);
		stmt.setString(2, config.id_field.getName());
		stmt.setObject(3, id );
		// Run query
		T obj = DBConnection.exec(stmt, DBBeanSQLResultHandler.create(c, db) );
		return obj;
	}
	
	/**
	 * Creates a specific table for the given Bean
	 */
	public static void create(DBConnection sql, Class<? extends DBBean> c) throws SQLException{
		if( !beanConfigs.containsKey( c ) )
			initBeanConfig( c );
		DBBeanConfig config = beanConfigs.get(c);

		// Generate the SQL
		StringBuilder query = new StringBuilder();
		query.append("CREATE TABLE ? (  ");
		
		for( Field field : config.fields ){
			query.append(" ");
			query.append( field.getName() );
			query.append( classToDBName(c) );
			if( config.id_field.equals( field ) )
				query.append(" PRIMARY KEY");
			query.append(", ");
		}
		query.delete( query.length()-2, query.length());
		query.append(")");
		PreparedStatement stmt = sql.getPreparedStatement( sql.toString() );
		// add the table name
		stmt.setObject(1, config.tableName);
		
		// Execute the SQL
		DBConnection.exec(stmt);
	}
	
	private static String classToDBName(Class<?> c){
		if(     c == String.class) 		return "CLOB"; // TEXT
		else if(c == Short.class) 		return "SMALLINT";
		else if(c == short.class) 		return "SMALLINT";
		else if(c == Integer.class) 	return "INTEGER";
		else if(c == int.class) 		return "INTEGER";
		else if(c == BigInteger.class)	return "BIGINT";
		else if(c == Long.class) 		return "DECIMAL";
		else if(c == long.class) 		return "DECIMAL";
		else if(c == Float.class) 		return "DOUBLE";
		else if(c == float.class) 		return "DOUBLE";
		else if(c == Double.class) 		return "DOUBLE";
		else if(c == double.class) 		return "DOUBLE";
		else if(c == BigDecimal.class) 	return "DECIMAL";
		else if(c == Boolean.class) 	return "BOOLEAN";
		else if(c == boolean.class) 	return "BOOLEAN";
		else if(c == Byte.class) 		return "BINARY(1)";
		else if(c == byte.class) 		return "BINARY(1)";
		return null;
	}
	
	/**
	 * This is a workaround if the field is not visible to other classes
	 * 
	 * @param 	field	is the field
	 * @return 			the value of the field
	 */
	protected Object getFieldValue(Field field){
		try {
			field.setAccessible(true);
			return field.get(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * This is a workaround if the field is not visible to other classes
	 * 
	 * @param 	field	is the field
	 * @return 			the value of the field
	 */
	protected void setFieldValue(Field field, Object o){
		try {
			field.setAccessible(true);
			field.set(this, o);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
