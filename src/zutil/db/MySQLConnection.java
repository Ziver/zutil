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
package zutil.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLConnection {
	Connection conn = null;
	
	/**
	 * Connects to a MySQL server
	 * 
	 * @param url is the URL of the MySQL server
	 * @param db is the database to connect to
	 * @param user is the user name
	 * @param password is the password
	 */
    public MySQLConnection(String url, String db, String user, String password) 
    		throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		Class.forName ("com.mysql.jdbc.Driver").newInstance();
		DriverManager.setLoginTimeout(10);
        conn = DriverManager.getConnection ("jdbc:mysql://"+url+"/"+db, user, password);
    }
    
    /**
     * Runs a query and returns the result.<br>
     * <b>NOTE:</b> Don't forget to close the ResultSet and the Statement or it 
     * can lead to memory leak: rows.getStatement().close();
     * 
     * @param sql is the query to execute
     * @return the data that the DB returned
     */
    public synchronized ResultSet query(String sql) throws SQLException{
    	Statement s = conn.createStatement ();
    	s.executeQuery(sql);
    	return s.getResultSet();    	
    }
    
    /**
     * Returns the first cell of the first row of the query
     * 
     * @param sql is the SQL query to run, preferably with the LIMIT 1 at the end
     * @return A SQL row if it exists or else null
     */
    public synchronized String simpleQuery(String sql) throws SQLException{
    	Statement s = conn.createStatement ();
    	s.executeQuery(sql);
    	ResultSet result = s.getResultSet();
    	if(result.next()){
    		String tmp = result.getString(1);
    		result.close();
    		return tmp;
    	}
    	return null;    	
    }
    
    /**
     * Runs a query in the MySQL server and returns effected rows
     * 
     * @param sql is the query to execute
     * @return the number of rows effected
     */
    public synchronized int updateQuery(String sql) throws SQLException{
    	Statement s = conn.createStatement ();
    	int ret = s.executeUpdate(sql);
    	s.close();
    	return ret;
    }
    
    /**
     * @return the last inserted id or -1 if there was an error
     * @throws SQLException
     */
    public int getLastInsertID() throws SQLException{
    	Statement s = conn.createStatement ();
    	s.executeQuery("SELECT LAST_INSERT_ID()");
    	ResultSet result = s.getResultSet();
    	if(result.next()){
    		int tmp = result.getInt(1);
    		result.close();
    		return tmp;
    	}
    	return -1;   
    }
    
    /**
     * Runs a Prepared Statement.<br>
     * <b>NOTE:</b> Don't forget to close the PreparedStatement or it can lead to memory leak
     * 
     * @param sql is the SQL query to run
     * @return The PreparedStatement
     */
    public synchronized PreparedStatement prepareStatement(String sql) throws SQLException{
    	return conn.prepareStatement(sql);
    }
    
    /**
     * Disconnects from the database
     */
    public synchronized void close() throws SQLException{
    	if (conn != null){
            conn.close ();
        }
    }
}
