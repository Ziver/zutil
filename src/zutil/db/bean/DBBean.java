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
import zutil.db.bean.DBBeanConfig.DBBeanFieldConfig;
import zutil.db.bean.DBBeanConfig.DBBeanSubBeanConfig;
import zutil.log.LogUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

/**
 * The class that extends this will be able to save its state to a database.
 * Fields that are transient will be ignored, and fields that extend
 * DBBean will be replaced with the an id which corresponds to the field
 * of that object.
 *
 * <p>
 * Supported fields:
 * <ul>
 * <li>Boolean</li>
 * <li>Integer</li>
 * <li>Short</li>
 * <li>Float</li>
 * <li>Double</li>
 * <li>String</li>
 * <li>Character</li>
 * <li>java.sql.Timestamp</li>
 * <li>DBBean (A Integer reference to another Bean in another table)</li>
 * <li>List&lt;DBBean&gt; (A reference table is used to associate Beans into the list)</li>
 * </ul>
 * @author Ziver
 */
public abstract class DBBean {
    private static final Logger logger = LogUtil.getLogger();

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
        /** The name of the column that contains the parent beans id, SQL rules apply, should not contain any strange characters or spaces */
        String idColumn() default "";
    }



    /** A unique id of the bean **/
    private Long id;

    /** This lock is for preventing recursive loops and concurrency when saving */
    protected ReentrantLock saveLock;
    /** This value is for preventing recursive loops when updating */
    protected ReentrantLock readLock;


    protected DBBean(){
        DBBeanConfig.getBeanConfig(this.getClass());
        saveLock = new ReentrantLock();
        readLock = new ReentrantLock();
    }



    /**
     * Saves the bean and all its sub beans to the DB
     *
     * @param		db				is the DBMS connection
     */
    public void save(DBConnection db) throws SQLException{
        save( db, true );
    }

    /**
     * Saves the bean to the DB
     *
     * @param		db				the DBMS connection
     * @param		recursive		if all sub beans should be saved also
     */
    @SuppressWarnings("unchecked")
    public void save(DBConnection db, boolean recursive) throws SQLException{
        if (saveLock.isHeldByCurrentThread()) // If the current thread already has a lock then this
            return;                           // is a recursive call and we do not need to do anything
        else if (saveLock.tryLock()) {
            Class<? extends DBBean> c = this.getClass();
            DBBeanConfig config = DBBeanConfig.getBeanConfig(c);
            try {
                // Generate the SQL
                StringBuilder query = new StringBuilder();
                if (this.id == null) {
                    query.append("INSERT INTO ").append(config.getTableName());
                    StringBuilder sqlCols = new StringBuilder();
                    StringBuilder sqlValues = new StringBuilder();
                    for (DBBeanFieldConfig field : config.getFields()) {
                        if (sqlCols.length() > 0)
                            sqlCols.append(", ");
                        sqlCols.append(field.getName());

                        if (sqlValues.length() > 0)
                            sqlValues.append(", ");
                        sqlValues.append("?");
                    }
                    if (config.getFields().size() > 0) { // is there any fields?
                        query.append(" (").append(sqlCols).append(")");
                        query.append(" VALUES(").append(sqlValues).append(")");
                    } else
                        query.append(" DEFAULT VALUES");
                }
                else if (config.getFields().size() > 0) { // Is there any fields to update?
                    query.append("UPDATE ").append(config.getTableName());
                    StringBuilder sqlSets = new StringBuilder();
                    for (DBBeanFieldConfig field : config.getFields()) {
                        if (sqlSets.length() > 0)
                            sqlSets.append(", ");
                        sqlSets.append(field.getName());
                        sqlSets.append("=?");
                    }
                    query.append(" SET ").append(sqlSets);
                    query.append(" WHERE ").append(config.getIdColumnName()).append("=?");
                }

                // Check if we have a valid query to run, skip otherwise
                if (query.length() > 0) {
                    String sql = query.toString();
                    logger.finest("Save Bean(" + c.getName() + ", id: " + this.getId() + ") query: " + sql);
                    PreparedStatement stmt = db.getPreparedStatement(sql);
                    // Put in the variables in the SQL
                    int index = 1;
                    for (DBBeanFieldConfig field : config.getFields()) {
                        // Another DBBean class
                        if (DBBean.class.isAssignableFrom(field.getType())) {
                            DBBean subObj = (DBBean) field.getValue(this);
                            if (subObj != null) {
                                if (recursive || subObj.getId() == null)
                                    subObj.save(db);
                                stmt.setObject(index, subObj.getId());
                            } else
                                stmt.setObject(index, null);
                            index++;
                        }
                        // Normal field
                        else {
                            Object value = field.getValue(this);
                            stmt.setObject(index, value);
                            index++;
                        }
                    }
                    if (this.id != null)
                        stmt.setObject(index, this.id);

                    // Execute the SQL
                    DBConnection.exec(stmt);
                    if (this.id == null)
                        this.id = db.getLastInsertID(stmt);
                }
                // Update cache
                DBBeanCache.add(this);

                // Save sub beans, after we get the parent beans id
                if (recursive){
                    for (DBBeanSubBeanConfig subBeanField : config.getSubBeans()) {
                        if (this.id == null)
                            throw new SQLException("Unknown parent bean id");

                        List<DBBean> list = (List<DBBean>) subBeanField.getValue(this);
                        if (list != null) {
                            for (DBBean subObj : list) {
                                // Save the sub bean
                                subObj.save(db);
                                if (subObj.getId() == null) {
                                    logger.severe("Unable to save field " + c.getSimpleName() + "." + subBeanField.getName() +
                                            " with " + subObj.getClass().getSimpleName() + " because sub bean id is null");
                                    continue;
                                }
                                // Get the Sub bean configuration
                                String subIdCol = subBeanField.getSubBeanConfig().getIdColumnName();

                                // Save links in link table
                                String sql;
                                if (!subBeanField.isStandaloneLinkTable()) // Sub Bean and link table is the same table
                                    sql = "UPDATE "+ subBeanField.getLinkTableName() +" SET "+ subBeanField.getParentIdColumnName() +"=? WHERE "+ subIdCol +"=?";
                                else
                                    sql = "INSERT INTO " + subBeanField.getLinkTableName() + " (" + subBeanField.getParentIdColumnName() + ", " + subIdCol + ") SELECT ?,? " +
                                            "WHERE NOT EXISTS(SELECT 1 FROM " + subBeanField.getLinkTableName() + " WHERE " + subBeanField.getParentIdColumnName() + "=? AND " + subIdCol + "=?);";
                                logger.finest("Save sub Bean(" + c.getName() + ", id: " + subObj.getId() + ") query: " + sql);
                                PreparedStatement subStmt = db.getPreparedStatement(sql);
                                subStmt.setLong(1, this.id);
                                subStmt.setLong(2, subObj.getId());
                                if (subStmt.getParameterMetaData().getParameterCount() > 2) {
                                    subStmt.setLong(3, this.id);
                                    subStmt.setLong(4, subObj.getId());
                                }
                                DBConnection.exec(subStmt);
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                throw e;
            } catch (Exception e) {
                throw new SQLException(e);
            } finally {
                saveLock.unlock();
            }
        } else {
            // If we have concurrent saves, only save once and skip the other threads
            saveLock.lock();
            saveLock.unlock();
        }
    }


    /**
     * Deletes the bean from the DB and all its sub beans and links.
     */
    public void delete(DBConnection db) throws SQLException{
        delete(db, true);
    }
    /**
     * Deletes the bean from the DB and the links to sub beans.
     *
     * @param		recursive		if all sub beans should be deleted also
     */
    public void delete(DBConnection db, boolean recursive) throws SQLException{
        Class<? extends DBBean> c = this.getClass();
        DBBeanConfig config = DBBeanConfig.getBeanConfig( c );
        if( this.getId() == null )
            throw new NullPointerException("ID field is null! (Has the bean been saved?)");

        // Delete sub beans
        for (DBBeanSubBeanConfig subBeanField : config.getSubBeans()) {
            List<DBBean> list = (List<DBBean>) subBeanField.getValue(this);
            if (list != null) {
                for (DBBean subObj : list) {
                    // Delete links
                    if (subBeanField.isStandaloneLinkTable()) {
                        String sql = "DELETE FROM "+subBeanField.getLinkTableName()+" WHERE "+subBeanField.getParentIdColumnName()+"=?";
                        logger.finest("Delete link, query: "+sql);
                        PreparedStatement stmt = db.getPreparedStatement( sql );
                        stmt.setLong(1, this.getId() );
                        DBConnection.exec(stmt);
                    }
                    // Delete sub beans
                    if (recursive)
                        subObj.delete(db);
                }
            }
        }

        // Delete this bean from DB
        String sql = "DELETE FROM "+config.getTableName()+" WHERE "+config.getIdColumnName()+"=?";
        logger.finest("Delete Bean("+c.getName()+", id: "+this.getId()+") query: "+sql);
        PreparedStatement stmt = db.getPreparedStatement( sql );
        stmt.setLong(1, this.getId() );
        DBConnection.exec(stmt);

        // Clear cache and reset id
        DBBeanCache.remove(this);
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
        String sql = "SELECT * FROM "+config.getTableName();
        logger.finest("Load all Beans("+c.getName()+") query: "+sql);
        PreparedStatement stmt = db.getPreparedStatement( sql );
        // Run query
        return DBConnection.exec(stmt, DBBeanSQLResultHandler.createList(c, db) );
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
        String sql = "SELECT * FROM "+config.getTableName()+" WHERE "+config.getIdColumnName()+"=? LIMIT 1";
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
        query.append("CREATE TABLE ").append(config.getTableName()).append(" (  ");

        // ID
        query.append(" ").append(config.getIdColumnName()).append(" ");
        query.append( classToDBType( Long.class ) );
        query.append(" PRIMARY KEY AUTO_INCREMENT, ");

        for( DBBeanFieldConfig field : config.getFields() ){
            query.append(" ");
            query.append(field.getName());
            query.append(classToDBType(c));
            query.append(", ");
        }
        query.delete(query.length()-2, query.length());
        query.append(")");

        logger.finest("Create Bean("+c.getName()+") query: "+sql.toString());
        PreparedStatement stmt = sql.getPreparedStatement( sql.toString() );

        // Execute the SQL
        DBConnection.exec(stmt);
    }

    private static String classToDBType(Class<?> c){
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
            return classToDBType(Long.class);
        return null;
    }

    /**
     * @return the bean id or null if the bean has not bean saved yet
     */
    public final Long getId(){
        return id;
    }

    final void setId(Long id){
        this.id = id;
    }



    ////////////////// EXTENDABLE METHODS /////////////////////////

    /**
     * Will be called whenever the bean has been updated from the database.
     */
    protected void postUpdateAction(){}
}
