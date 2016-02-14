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
import zutil.log.LogUtil;

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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** 
 * The class that extends this will be able to save its state to a database.
 * Fields that are transient will be ignored, and fields that extend 
 * DBBean will be replaced with the an id which corresponds to the field 
 * of that object.
 * 
 * <XMP>
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
	 * Can be used if the column name is different from the field name.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface DBColumn {
		/** This is the name of the column in the database for the specified field. SQL rules apply, should not contain any strange characters or spaces **/
		String value();
	}

	/**
	 * Should be used for fields with lists of DBBeans.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface DBLinkTable {
		/** The name of the Link table, SQL rules apply, should not contain any strange characters or spaces */
		String table();
		/** The class of the linked bean */
		Class<? extends DBBean> beanClass();
		/** The name of the column that contains the main objects id, SQL rules apply, should not contain any strange characters or spaces */
		String idColumn() default "";
	}


	/** This value is for preventing recursive loops when saving */
	protected boolean processing_save;
	/** This value is for preventing recursive loops when updating */	
	protected boolean processing_update;

	protected DBBean(){
		DBBeanConfig.getBeanConfig(this.getClass());
		processing_save = false;
		processing_update = false;
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
		DBBeanConfig config = DBBeanConfig.getBeanConfig( c );
		try {
			Long id = this.getId();
			// Generate the SQL
			StringBuilder query = new StringBuilder();
			if( id == null ) {
                query.append("INSERT INTO ").append( config.tableName );
                query.append( " (" );
                for( Field field : config.fields ){
                    if( !List.class.isAssignableFrom(field.getType()) ){
                        query.append(" ");
                        query.append(DBBeanConfig.getFieldName(field));
                        query.append(",");
                    }
                }
                if(query.charAt(query.length()-1) == ',')
                    query.deleteCharAt(query.length()-1);
                query.append( ") VALUES(" );
                for( Field field : config.fields ){
                    query.append( "?," );
                }
                if(query.charAt(query.length()-1) == ',')
                    query.deleteCharAt(query.length()-1);
                query.append( ")" );
            }
			else{
                query.append("UPDATE ").append( config.tableName );
                query.append( " SET" );
                for( Field field : config.fields ){
                    if( !List.class.isAssignableFrom(field.getType()) ){
                        query.append(" ");
                        query.append(DBBeanConfig.getFieldName(field));
                        query.append("=?,");
                    }
                }
                if(query.charAt(query.length()-1) == ',')
                    query.deleteCharAt(query.length()-1);
                query.append(" WHERE ").append(config.idColumn).append("=?");
            }

            String sql = query.toString();
			logger.finest("Save Bean("+c.getName()+", id: "+this.getId()+") query: "+ sql);
			PreparedStatement stmt = db.getPreparedStatement( sql );
			// Put in the variables in the SQL
			int index = 1;
			for(Field field : config.fields){

				// Another DBBean class
				if( DBBean.class.isAssignableFrom( field.getType() )){
					DBBean subObj = (DBBean)getFieldValue(field);
					if(subObj != null){
						if( recursive || subObj.getId() == null )
							subObj.save(db);
						stmt.setObject(index, subObj.getId() );
					}
					else
						stmt.setObject(index, null);
					index++;
				}
				// A list of DBBeans
				else if( List.class.isAssignableFrom( field.getType() ) && 
						field.getAnnotation( DBLinkTable.class ) != null){
					// Do stuff later
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
				this.id = db.getLastInsertID(stmt);
			
			// Save the list, after we get the object id
			for(Field field : config.fields){
				if( List.class.isAssignableFrom( field.getType() ) && 
						field.getAnnotation( DBLinkTable.class ) != null){
					List<DBBean> list = (List<DBBean>)getFieldValue(field);
					if( list != null ){
						DBLinkTable linkTable = field.getAnnotation( DBLinkTable.class );
						String subTable = linkTable.table();
						String idCol = (linkTable.idColumn().isEmpty() ? config.tableName : linkTable.idColumn() );
						String subIdCol = "id";

						DBBeanConfig subConfig = null;
						for(DBBean subObj : list){
							// Save the sub bean
							if( recursive || subObj.getId() == null )
								subObj.save(db);
							if( subObj.getId() == null ){
								logger.severe("Unable to save field "+c.getSimpleName()+"."+field.getName()+" with "+subObj.getClass().getSimpleName());
								continue;
							}
							// Get the Sub object configuration
							if(subConfig == null){
								subConfig = DBBeanConfig.getBeanConfig( subObj.getClass() );
								subIdCol = subConfig.idColumn;
							}
							// Save links in link table
							sql = "";
							if( subTable.equals(subConfig.tableName) )
								sql = "UPDATE "+subTable+" SET "+idCol+"=? WHERE "+subIdCol+"=?";
							else
								sql = "REPLACE INTO "+subTable+" SET "+idCol+"=?, "+subIdCol+"=?";
							logger.finest("Save Bean("+c.getName()+", id: "+subObj.getId()+") query: "+sql);
							PreparedStatement subStmt = db.getPreparedStatement( sql );
							subStmt.setLong(1, this.getId() );
							subStmt.setLong(2, subObj.getId() );
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
		DBBeanConfig config = DBBeanConfig.getBeanConfig( c );
		if( this.getId() == null )
			throw new NoSuchElementException("ID field is null? (Has the bean been saved?)");

		String sql = "DELETE FROM "+config.tableName+" WHERE "+config.idColumn+"=?";
		logger.finest("Delete Bean("+c.getName()+", id: "+this.getId()+") query: "+sql);
		PreparedStatement stmt = db.getPreparedStatement( sql );
		// Put in the variables in the SQL
		stmt.setObject(1, this.getId() );

		// Execute the SQL
		DBConnection.exec(stmt);
		this.id = null;
	}

	/**
	 * Loads all rows from the table into a LinkedList
	 * 
	 * @param 	<T> 	is the class of the bean
	 * @param 	c 		is the class of the bean
	 * @return			a LinkedList with all the beans in the DB
	 */
	public static <T extends DBBean> List<T> load(DBConnection db, Class<T> c) throws SQLException {
		// Initiate a BeanConfig if there is non
		DBBeanConfig config = DBBeanConfig.getBeanConfig( c );
		// Generate query
		String sql = "SELECT * FROM "+config.tableName;
		logger.finest("Load all Beans("+c.getName()+") query: "+sql);
		PreparedStatement stmt = db.getPreparedStatement( sql );
		// Run query
		List<T> list = DBConnection.exec(stmt, DBBeanSQLResultHandler.createList(c, db) );
		return list;
	}

	/**
	 * Loads a specific instance of the bean from the table  with the specific id
	 * 
	 * @param 	<T> 	is the class of the bean
	 * @param 	c 		is the class of the bean
	 * @param	id		is the id value of the bean
	 * @return			a DBBean Object with the specific id or null if the id was not found
	 */
	public static <T extends DBBean> T load(DBConnection db, Class<T> c, long id) throws SQLException {
		// Initiate a BeanConfig if there is non
		DBBeanConfig config = DBBeanConfig.getBeanConfig( c );
		// Generate query
		String sql = "SELECT * FROM "+config.tableName+" WHERE "+config.idColumn+"=? LIMIT 1";
		logger.finest("Load Bean("+c.getName()+", id: "+id+") query: "+sql);
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
		DBBeanConfig config = DBBeanConfig.getBeanConfig( c );

		// Generate the SQL
		StringBuilder query = new StringBuilder();
		query.append("CREATE TABLE "+config.tableName+" (  ");

		// ID
		query.append(" ").append(config.idColumn).append(" ");
		query.append( classToDBName( Long.class ) );
		query.append(" PRIMARY KEY AUTO_INCREMENT, ");

		for( Field field : config.fields ){
			query.append(" ");
			query.append( DBBeanConfig.getFieldName(field) );
			query.append( classToDBName(c) );
			query.append(", ");
		}
		query.delete( query.length()-2, query.length());
		query.append(")");

		logger.finest("Create Bean("+c.getName()+") query: "+sql.toString());
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

			// Set basic data type
			if( o == null && !Object.class.isAssignableFrom( field.getType() ) ){
				if( 	 field.getType() == Integer.TYPE )	field.setInt(this, 0);
				else if( field.getType() == Character.TYPE )field.setChar(this, (char) 0);
				else if( field.getType() == Byte.TYPE )		field.setByte(this, (byte) 0);
				else if( field.getType() == Short.TYPE )	field.setShort(this, (short) 0);
				else if( field.getType() == Long.TYPE )		field.setLong(this, 0l);
				else if( field.getType() == Float.TYPE )	field.setFloat(this, 0f);
				else if( field.getType() == Double.TYPE )	field.setDouble(this, 0d);
				else if( field.getType() == Boolean.TYPE )	field.setBoolean(this, false);
			}
			else {
				// Some special cases
				if(field.getType() == Boolean.TYPE && o instanceof Integer)
					field.setBoolean(this, ((Integer)o) > 0 ); // Convert an Integer to boolean
				else
					field.set(this, o);
			}
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
	


    ////////////////// EXTENDABLE METHODS /////////////////////////

	/**
	 * Will be called whenever the bean has been updated from the database.
	 */
	protected void postUpdateAction(){}
}
