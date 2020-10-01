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
import zutil.db.bean.DBBeanConfig.DBBeanFieldConfig;
import zutil.db.bean.DBBeanConfig.DBBeanSubBeanConfig;
import zutil.log.LogUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;


public class DBBeanSQLResultHandler<T> implements SQLResultHandler<T> {
    private static final Logger logger = LogUtil.getLogger();

    private Class<? extends DBBean> beanClass;
    private DBBeanConfig beanConfig;
    private DBConnection db;
    private boolean list;


    /**
     * Creates a new instance of this class that returns only one bean
     *
     * @param <C> is the class type that should be instantiated
     * @param cl  is the DBBean class that will be parsed from the SQL result
     * @return a new instance of this class
     */
    public static <C extends DBBean> DBBeanSQLResultHandler<C> create(Class<C> cl) {
        return new DBBeanSQLResultHandler<>(cl, null, false);
    }

    /**
     * Creates a new instance of this class that returns a bean with all its containing beans
     *
     * @param <C> is the class type that should be instantiated
     * @param cl  is the DBBean class that will be parsed from the SQL result
     * @param db  is the DB connection for loading internal beans
     * @return a new instance of this class
     */
    public static <C extends DBBean> DBBeanSQLResultHandler<C> create(Class<C> cl, DBConnection db) {
        return new DBBeanSQLResultHandler<>(cl, db, false);
    }

    /**
     * Creates a new instance of this class that returns a list of beans
     *
     * @param <C> is the class type that should be instantiated
     * @param cl  is the DBBean class that will be parsed from the SQL result
     * @return a new instance of this class
     */
    public static <C extends DBBean> DBBeanSQLResultHandler<List<C>> createList(Class<C> cl) {
        return new DBBeanSQLResultHandler<>(cl, null, true);
    }

    /**
     * Creates a new instance of this class that returns a list of beans with all the internal beans
     *
     * @param <C> is the class type that should be instantiated
     * @param cl  is the DBBean class that will be parsed from the SQL result
     * @param db  is the DB connection for loading internal beans
     * @return a new instance of this class
     */
    public static <C extends DBBean> DBBeanSQLResultHandler<List<C>> createList(Class<C> cl, DBConnection db) {
        return new DBBeanSQLResultHandler<>(cl, db, true);
    }


    /**
     * Creates a new instance of this class
     *
     * @param cl   is the DBBean class that will be parsed from the SQL result
     * @param db   is the DB connection for loading internal beans, may be null to disable internal beans
     * @param list is if the handler should return a list of beans instead of one
     */
    protected DBBeanSQLResultHandler(Class<? extends DBBean> cl, DBConnection db, boolean list) {
        this.beanClass = cl;
        this.list = list;
        this.db = db;
        this.beanConfig = DBBeanConfig.getBeanConfig(cl);
    }


    /**
     * Is called to handle a result from a query.
     *
     * @param stmt   is the query
     * @param result is the ResultSet
     */
    @SuppressWarnings("unchecked")
    public T handleQueryResult(Statement stmt, ResultSet result) throws SQLException {
        if (list) {
            List<DBBean> bean_list = new LinkedList<>();
            while (result.next()) {
                DBBean obj = createBean(result);
                bean_list.add(obj);
            }
            return (T) bean_list;
        } else {
            if (result.next()) {
                return (T) createBean(result);
            }
            return null;
        }

    }


    /**
     * Instantiates a new bean and assigns field values from the ResultSet
     *
     * @param result is where the field values for the bean will bee read from, the cursor should be in front of the data
     * @return a new instance of the bean
     */
    private DBBean createBean(ResultSet result) throws SQLException {
        try {
            Long id = result.getLong("id");
            // Check cache first
            DBBean obj = DBBeanCache.get(beanClass, id);
            if (obj == null) {
                // Cache miss create a new bean
                logger.fine("Creating new Bean(" + beanClass.getName() + ") with id: " + id);
                obj = beanClass.newInstance();
                obj.setId(id);
                updateBean(result, obj);
            } else if (DBBeanCache.isOutDated(obj)) {
                // Update fields
                logger.finer("Bean(" + beanClass.getName() + ") cache to old for id: " + id);
                updateBean(result, obj);
            }
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
     * @param result is where the field values for the bean will be read from, the cursor should be in front of the data
     * @param obj    is the bean that will be updated
     */
    @SuppressWarnings("unchecked")
    private void updateBean(ResultSet result, DBBean obj) throws SQLException {
        if (obj.readLock.tryLock()) {
            try {
                logger.fine("Updating Bean(" + beanClass.getName() + ") with id: " + obj.getId());
                // Read fields
                for (DBBeanFieldConfig field : beanConfig.getFields()) {
                    String name = field.getName();

                    // Inline DBBean class
                    if (DBBean.class.isAssignableFrom(field.getType())) {
                        if (db != null) {
                            Long subId = result.getLong(name);
                            DBBean subObj = DBBeanCache.get(field.getType(), subId);
                            if (subObj == null)
                                subObj = DBBean.load(db, (Class<? extends DBBean>) field.getType(), subId);
                            field.setValue(obj, subObj);
                        } else
                            logger.warning("No DB available to read sub beans");
                    }
                    // Normal field
                    else {
                        field.setValue(obj, result.getObject(name));
                    }
                }
                // Update cache
                DBBeanCache.add(obj);

                // Read sub beans
                if (db != null) {
                    for (DBBeanSubBeanConfig subBeanField : beanConfig.getSubBeans()) {
                        DBBeanConfig subBeanConfig = subBeanField.getSubBeanConfig();

                        // Load List from link table
                        String subSql = "SELECT subBeanTable.* FROM " +
                                subBeanField.getLinkTableName() + " as linkTable, " +
                                subBeanConfig.getTableName() + " as subBeanTable " +
                                "WHERE linkTable." + subBeanField.getParentIdColumnName() + "=? AND " +
                                "linkTable." + subBeanConfig.getIdColumnName() + "=subBeanTable." + subBeanConfig.getIdColumnName();
                        logger.finest("List Load Query: " + subSql);
                        PreparedStatement subStmt = db.getPreparedStatement(subSql);
                        subStmt.setObject(1, obj.getId());
                        List<? extends DBBean> list = DBConnection.exec(subStmt,
                                DBBeanSQLResultHandler.createList(subBeanField.getSubBeanClass(), db));
                        subBeanField.setValue(obj, list);
                    }
                } else
                    logger.warning("No DB available to read sub beans");

                // Call post listener
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
