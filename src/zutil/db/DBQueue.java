/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Ziver Koc
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

package zutil.db;

import zutil.converter.Converter;
import zutil.io.MultiPrintStream;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

/**
 * This class creates a queue that stores the
 * data in a mysql table.
 * The table should look like this:
 * <PRE>
 * CREATE TABLE `queue` (
 * 		`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
 * 		`data` BINARY NOT NULL
 * 	);
 * 	</PRE>
 * @author Ziver
 *
 */
public class DBQueue<E> implements Queue<E>{
    private DBConnection db;
    private String table;

    /**
     * Initiates the queue.<br>
     *
     * @param   db      is the connection to the DB
     * @param   table   is the name of the table
     */
    public DBQueue(DBConnection db, String table) {
        this.db = db;
        this.table = table;
    }

    public boolean add(Object arg0) {
        try {
            PreparedStatement sql = db.getPreparedStatement("INSERT INTO ? (data) VALUES(?)");
            sql.setObject(1, table);
            sql.setObject(2, arg0);
            DBConnection.exec(sql);
        } catch (Exception e) {
            e.printStackTrace(MultiPrintStream.out);
            return false;
        }
        return true;
    }

    public E element() {
        return peek();
    }

    public boolean offer(Object arg0) {
        return add(arg0);
    }

    public synchronized E peek() {
        try {
            return db.exec("SELECT * FROM " + table + " LIMIT 1", new SQLResultHandler<E>() {
                public E handleQueryResult(Statement stmt, ResultSet rs) throws SQLException{
                    if (rs.next())
                        try {
                            return (E) Converter.toObject(rs.getBytes("data"));
                        } catch (Exception e) {
                            e.printStackTrace(MultiPrintStream.out);
                        }
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace(MultiPrintStream.out);
        }
        return null;
    }

    public synchronized E poll() {
        try {
            return db.exec("SELECT * FROM " + table + " LIMIT 1", new SQLResultHandler<E>() {
                public E handleQueryResult(Statement stmt, ResultSet rs) {
                    try {
                        if (rs.next()) {
                            db.exec("DELETE FROM " + table + " WHERE id=" + rs.getInt("id") + " LIMIT 1");
                            return (E) Converter.toObject(rs.getBytes("data"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace(MultiPrintStream.out);
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace(MultiPrintStream.out);
        }
        return null;
    }

    public E remove() {
        return poll();
    }

    public boolean addAll(Collection<? extends E> arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    public void clear() {
        try {
            db.exec("TRUNCATE TABLE `" + table + "`");
        } catch (SQLException e) {
            e.printStackTrace(MultiPrintStream.out);
        }
    }

    public boolean contains(Object arg0) {
        try {
            return db.exec("SELECT data FROM " + table + " WHERE data='" + Converter.toBytes(arg0) + "' LIMIT 1", new SQLResultHandler<Boolean>() {
                public Boolean handleQueryResult(Statement stmt, ResultSet rs) throws SQLException{
                    return rs.next();
                }
            });
        } catch (Exception e) {
            e.printStackTrace(MultiPrintStream.out);
        }
        return false;
    }

    public boolean containsAll(Collection<?> arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isEmpty() {
        return (peek() != null);
    }

    public Iterator<E> iterator() {
        // TODO: Auto-generated method stub
        return null;
    }

    public synchronized boolean remove(Object arg0) {
        try {
            db.exec("DELETE FROM " + table + " WHERE data='" + Converter.toBytes(arg0) + "' LIMIT 1");
            return true;
        } catch (Exception e) {
            e.printStackTrace(MultiPrintStream.out);
        }
        return false;
    }

    public synchronized boolean removeAll(Collection<?> arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean retainAll(Collection<?> arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    public int size() {
        try {
            return db.exec("SELECT count(*) FROM " +table, new SQLResultHandler<Integer>() {
                public Integer handleQueryResult(Statement stmt, ResultSet rs) throws SQLException{
                    if (rs.next())
                        return rs.getInt(1);
                    return 0;
                }
            });
        } catch (Exception e) {
            e.printStackTrace(MultiPrintStream.out);
        }
        return 0;
    }

    public E[] toArray() {
        // TODO Auto-generated method stub
        return null;
    }

    public E[] toArray(Object[] arg0) {
        // TODO Auto-generated method stub
        return null;
    }

}
