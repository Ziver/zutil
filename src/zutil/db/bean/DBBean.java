/*******************************************************************************
 * Copyright (c) 2011 Ziver Koc
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
 ******************************************************************************/
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import zutil.db.DBConnection;
import zutil.log.LogUtil;

/**
 * <XMP>
 * The class that extends this will be able to save its state to a DB.
 * Fields that are transient will be ignored, and fields that extend 
 * DBBean will be replaced with the id field of that class in the database.
 * 
 * Supported fields:
 * 	*Boolean
 * 	*Integer
 * 	*Short
 * 	*Float
 * 	*Double
 * 	*String
 * 	*Character
 * 	*DBBean
 * 	*java.sql.Timestamp
 * 	*List<DBBean>
 * 
 * </XMP>
 * @author Ziver
 */
public abstract class DBBean {
	private static final Logger logger = LogUtil.getLogger();

	/** The id of the bean **/
	protected Long id;

	/**
	 * Sets the name of the table in the database
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface DBTable {
		/** This is the name of the table, SQL rules apply should not contain any strange characters or spaces **/
		String value();
		/** Change the id column name of the bean, default column name is "id", SQL rules apply should not contain any strange characters or spaces **/
		String idColumn() default "id";
		/** Sets if the fields in the super classes is also part of the bean **/
		boolean superBean() default false;
	}

	/**
	 * Sets the name of the table that links different DBBeans together
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface DBLinkTable {
		/** The name of the Link table, SQL rules apply should not contain any strange characters or spaces */
		String table();
		/** The class of the linked bean */
		Class<? extends DBBean> beanClass();
		/** The name of the column that contains the main objects id, SQL rules apply should not contain any strange characters or spaces */
		String idColumn() default "";
	}

	/**
	 * A Class that contains information about a bean
	 */
	protected static class DBBeanConfig{
		/** The name of the table in the DB **/
		protected String tableName;
		/** The name of the id column **/
		protected String idColumn;
		/** All the fields in the bean **/
		protected ArrayList<Field> fields;

		protected DBBeanConfig(){
			fields = new ArrayList<Field>();
		}
	}

	/** This is a cache of all the initialized beans */	 
	private static HashMap<String,DBBeanConfig> beanConfigs = new HashMap<String,DBBeanConfig>();
	/** This value is for preventing recursive loops when saving */
	protected boolean processing_save;
	/** This value is for preventing recursive loops when updating */	
	protected boolean processing_update;

	protected DBBean(){
		if( !beanConfigs.containsKey( this.getClass() ) )
			initBeanConfig( this.getClass() );
		processing_save = false;
		processing_update = false;
	}

	/**
	 * @return all the fields except the ID field
	 */
	public static ArrayList<Field> getFields(Class<? extends DBBean> c){
		if( !beanConfigs.containsKey( c.getName() ) )
			initBeanConfig( c );
		return beanConfigs.get( c.getName() ).fields;		
	}

	/**
	 * @return the configuration object for the specified class
	 */
	protected static DBBeanConfig getBeanConfig(Class<? extends DBBean> c){
		if( !beanConfigs.containsKey( c.getName() ) )
			initBeanConfig( c );
		return beanConfigs.get( c.getName() );	
	}

	/**
	 * Caches the fields
	 */
	private static void initBeanConfig(Class<? extends DBBean> c){
		logger.fine("Initiating new DBBeanConfig( "+c.getName()+" )");
		DBBeanConfig config = new DBBeanConfig();
		// Find the table name
		DBTable tableAnn = c.getAnnotation(DBTable.class);
		if( tableAnn != null ){
			config.tableName = tableAnn.value();
			config.idColumn  = tableAnn.idColumn();
		}
		else{
			config.tableName = c.getSimpleName();
			config.idColumn  = "id";
		}
		// Add the fields in the bean and all the super classes fields
		for(Class<?> cc = c; cc != DBBean.class ;cc = cc.getSuperclass()){
			Field[] fields = cc.getDeclaredFields();
			for( Field field : fields ){
				int mod = field.getModifiers();
				if( !Modifier.isTransient( mod ) &&
						!Modifier.isAbstract( mod ) &&
						!Modifier.isFinal( mod ) &&
						!Modifier.isStatic( mod ) &&
						!Modifier.isInterface( mod ) &&
						!Modifier.isNative( mod ) &&
						!config.fields.contains( field )){
					config.fields.add( field );
				}
			}
			if( tableAnn == null || !tableAnn.superBean() )
				break;
		}

		beanConfigs.put(c.getName(), config);
	}

	/**
	 * Saves the object and all the sub objects to the DB
	 * 
	 * @param		db				is the DBMS connection
	 */
	public void save(DBConnection db) throws SQLException{
		save( db, true );
	}
	
	/**
	 * Saves the Object to the DB
	 * 
	 * @param		db				is the DBMS connection
	 * @param		recursive		is if the method should save all sub objects
	 */
	@SuppressWarnings("unchecked")
	public void save(DBConnection db, boolean recursive) throws SQLException{
		if( processing_save )
			return;
		processing_save = true;
		Class<? extends DBBean> c = this.getClass();
		DBBeanConfig config = getBeanConfig( c );
		try {
			Long id = this.getId();
			// Generate the SQL
			StringBuilder query = new StringBuilder();
			if( id == null )
				query.append("INSERT INTO ");
			else query.append("UPDATE ");
			query.append( config.tableName );

			StringBuilder params = new StringBuilder();
			for( Field field : config.fields ){
				if( !List.class.isAssignableFrom(field.getType()) ){
					params.append(" ");
					params.append(field.getName());
					params.append("=?,");
				}
			}
			if( params.length() > 0 ){
				params.delete( params.length()-1, params.length());
				query.append( " SET" );
				query.append( params );
				if( id != null )
					query.append(" WHERE ").append(config.idColumn).append("=?");
			}
			logger.finest("Save query("+c.getName()+" id:"+this.getId()+"): "+query.toString());
			PreparedStatement stmt = db.getPreparedStatement( query.toString() );
			// Put in the variables in the SQL
			int index = 1;
			for(Field field : config.fields){

				// Another DBBean class
				if( DBBean.class.isAssignableFrom( field.getType() )){
					DBBean subobj = (DBBean)getFieldValue(field);
					if(subobj != null){
						if( recursive || subobj.getId() == null )
							subobj.save(db);
						stmt.setObject(index, subobj.getId() );
					}
					else
						stmt.setObject(index, null);
					index++;
				}
				// A list of DBBeans
				else if( List.class.isAssignableFrom( field.getType() ) && 
						field.getAnnotation( DBLinkTable.class ) != null){
					// DO NOTING
				}
				// Normal field
				else{
					Object value = getFieldValue(field);
					stmt.setObject(index, value);
					index++;
				}
			}
			if( id != null )
				stmt.setObject(index, id);

			// Execute the SQL
			DBConnection.exec(stmt);
			if( id == null )
				this.id = (Long) db.getLastInsertID();
			
			// Save the list, after we get the object id
			for(Field field : config.fields){
				if( List.class.isAssignableFrom( field.getType() ) && 
						field.getAnnotation( DBLinkTable.class ) != null){
					List<DBBean> list = (List<DBBean>)getFieldValue(field);
					if( list != null ){
						DBLinkTable linkTable = field.getAnnotation( DBLinkTable.class );
						String subtable = linkTable.table();
						String idcol = (linkTable.idColumn().isEmpty() ? config.tableName : linkTable.idColumn() );
						String sub_idcol = "id";

						DBBeanConfig subConfig = null;
						for(DBBean subobj : list){
							// Save the sub bean
							if( recursive || subobj.getId() == null )
								subobj.save(db);
							if( subobj.getId() == null ){
								logger.severe("Unable to save field "+c.getSimpleName()+"."+field.getName()+" with "+subobj.getClass().getSimpleName());
								continue;
							}
							// Get the Sub object configuration
							if(subConfig == null){
								subConfig = getBeanConfig( subobj.getClass() );
								sub_idcol = subConfig.idColumn;
							}
							// Save links in link table
							String subsql = "";
							if( subtable.equals(subConfig.tableName) )
								subsql = "UPDATE "+subtable+" SET "+idcol+"=? WHERE "+sub_idcol+"=?";
							else
								subsql = "REPLACE INTO "+subtable+" SET "+idcol+"=?, "+sub_idcol+"=?";
							logger.finest("List Save query("+c.getName()+" id:"+subobj.getId()+"): "+subsql);
							PreparedStatement subStmt = db.getPreparedStatement( subsql );
							subStmt.setLong(1, this.getId() );
							subStmt.setLong(2, subobj.getId() );
							DBConnection.exec(subStmt);
						}
					}
				}
			}
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw new SQLException(e);
		} finally{
			processing_save = false;
		}		
	}

	/**
	 * Deletes the object from the DB, WARNING will not delete sub beans
	 * 
	 * @throws SQLException 
	 */
	public void delete(DBConnection db) throws SQLException{
		Class<? extends DBBean> c = this.getClass();
		DBBeanConfig config = getBeanConfig( c );
		if( this.getId() == null )
			throw new NoSuchElementException("ID field is null( Has the bean been saved?)!");

		String sql = "DELETE FROM "+config.tableName+" WHERE "+config.idColumn+"=?";
		logger.fine("Delete query("+c.getName()+" id:"+this.getId()+"): "+sql);
		PreparedStatement stmt = db.getPreparedStatement( sql );
		// Put in the variables in the SQL
		stmt.setObject(1, this.getId() );

		// Execute the SQL
		DBConnection.exec(stmt);
		this.id = null;
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
		DBBeanConfig config = getBeanConfig( c );
		// Generate query
		String sql = "SELECT * FROM "+config.tableName;
		logger.fine("Load All query("+c.getName()+"): "+sql);
		PreparedStatement stmt = db.getPreparedStatement( sql );
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
	public static <T extends DBBean> T load(DBConnection db, Class<T> c, long id) throws SQLException {
		// Initiate a BeanConfig if there is non
		DBBeanConfig config = getBeanConfig( c );
		// Generate query
		String sql = "SELECT * FROM "+config.tableName+" WHERE "+config.idColumn+"=? LIMIT 1";
		logger.fine("Load query("+c.getName()+" id:"+id+"): "+sql);
		PreparedStatement stmt = db.getPreparedStatement( sql );
		stmt.setObject(1, id );
		// Run query
		T obj = DBConnection.exec(stmt, DBBeanSQLResultHandler.create(c, db) );
		return obj;
	}

	/**
	 * Creates a specific table for the given Bean,
	 * WARNING: Experimental
	 */
	public static void create(DBConnection sql, Class<? extends DBBean> c) throws SQLException{
		DBBeanConfig config = getBeanConfig( c );

		// Generate the SQL
		StringBuilder query = new StringBuilder();
		query.append("CREATE TABLE "+config.tableName+" (  ");

		// ID
		query.append(" ").append(config.idColumn).append(" ");
		query.append( classToDBName( Long.class ) );
		query.append(" PRIMARY KEY AUTO_INCREMENT, ");

		for( Field field : config.fields ){
			query.append(" ");
			query.append( field.getName() );
			query.append( classToDBName(c) );
			query.append(", ");
		}
		query.delete( query.length()-2, query.length());
		query.append(")");
		logger.fine("Create query("+c.getName()+"): "+sql.toString());
		PreparedStatement stmt = sql.getPreparedStatement( sql.toString() );

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
		else if(c == Timestamp.class)	return "DATETIME";
		else if(DBBean.class.isAssignableFrom(c))
			return classToDBName(Long.class);
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
			if( !Modifier.isPublic( field.getModifiers()))
				field.setAccessible(true);
			
			return field.get(this);
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
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
			if( !Modifier.isPublic( field.getModifiers()))
				field.setAccessible(true);
			
			// Set basic datatype
			if( o == null && !Object.class.isAssignableFrom( field.getType() ) ){
				logger.fine("Trying to set primitive data type to null!");
				if( 	 field.getType() == Integer.TYPE )	field.setInt(this, 0);
				else if( field.getType() == Character.TYPE )field.setChar(this, (char) 0);
				else if( field.getType() == Byte.TYPE )		field.setByte(this, (byte) 0);
				else if( field.getType() == Short.TYPE )	field.setShort(this, (short) 0);
				else if( field.getType() == Long.TYPE )		field.setLong(this, 0l);
				else if( field.getType() == Float.TYPE )	field.setFloat(this, 0f);
				else if( field.getType() == Double.TYPE )	field.setDouble(this, 0d);
				else if( field.getType() == Boolean.TYPE )	field.setBoolean(this, false);
			}
			else
				field.set(this, o);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * @return the object id or null if the bean has not bean saved yet
	 */
	public Long getId(){
		return id;
	}
	
	/**
	 * This function cancels the internal cache garbage collector in DBBean
	 */
	public static void cancelGBC(){
		DBBeanSQLResultHandler.cancelGBC();
	}
	
	/**
	 * Will be called whenever the bean has been updated from the database.
	 */
	protected void updatePerformed(){}
}
