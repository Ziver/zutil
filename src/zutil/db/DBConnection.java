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

package zutil.db;

import zutil.db.handler.SimpleSQLHandler;
import zutil.log.LogUtil;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.Closeable;
import java.math.BigInteger;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection implements Closeable{
	private static final Logger logger = LogUtil.getLogger();
	
	public enum DBMS{
		MySQL,
		SQLite
	}
	// The connection
	private Connection conn = null;
	// The pool that this connection belongs to
	private DBConnectionPool pool;


	/**
	 * Creates an Connection from JNDI
	 * 
	 * @param jndi the name of the connection, e.g. "jdbc/mysql"
	 */
	public DBConnection(String jndi) throws NamingException, SQLException{
		InitialContext ctx = new InitialContext();
		DataSource ds = (DataSource)ctx.lookup("java:comp/env/"+jndi);
		conn = ds.getConnection();
	}

	/**
	 * Creates an Connection to a MySQL server
	 * 
	 * @param url is the URL of the MySQL server
	 * @param db is the database to connect to
	 * @param user is the user name
	 * @param password is the password
	 */
	public DBConnection(String url, String db, String user, String password) throws Exception{
		this(DBMS.MySQL, url, db, user, password);
	}

	/**
	 * Creates an Connection to a DB server
	 * 
	 * @param dbms is the DB type
	 * @param url is the URL of the MySQL server
	 * @param db is the database to connect to
	 * @param user is the user name
	 * @param password is the password
	 */
	public DBConnection(DBMS dbms, String url, String db, String user, String password) throws Exception{
		String dbms_name = initDriver(dbms);
		conn = DriverManager.getConnection ("jdbc:"+dbms_name+"://"+url+"/"+db, user, password);
	}
	
	/**
	 * Creates an Connection to a DB file
	 * 
	 * @param dbms is the DB type
	 * @param db is the database to connect to
	 */
	public DBConnection(DBMS dbms, String db) throws Exception{
		String dbms_name = initDriver(dbms);
		conn = DriverManager.getConnection ("jdbc:"+dbms_name+":"+db);
	}

	/**
	 * @return the underlying connection
	 */
	public Connection getConnection(){
		return conn;
	}

	/**
	 * Initiates the DB driver and returns its protocol name.
	 * 
	 * @param db is the DB type
	 * @return the protocol name of the DBMS
	 */
	public String initDriver(DBMS db) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		switch(db){
		case MySQL:
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			DriverManager.setLoginTimeout(10);
			return "mysql";
		case SQLite:
			Class.forName("org.sqlite.JDBC");
			return "sqlite";
		default:
			return null;
		}
	}

	/**
	 * @return the last inserted id or -1 if there was an error
	 */
	public long getLastInsertID(){
		try{
			return exec("SELECT LAST_INSERT_ID()", new SimpleSQLHandler<BigInteger>()).longValue();
		}catch(SQLException e){
			logger.log(Level.WARNING, null, e);
		}
		return -1;
	}

	/**
	 * Runs a Prepared Statement.<br>
	 * <b>NOTE:</b> Don't forget to close the PreparedStatement or it can lead to memory leaks
	 * 
	 * @param sql is the SQL query to run
	 * @return An PreparedStatement
	 */
	public PreparedStatement getPreparedStatement(String sql) throws SQLException{
		return conn.prepareStatement(sql);
	}

	/**
	 * <b>NOTE:</b> Don't forget to close the Statement or it can lead to memory leaks
	 * 
	 * @return an Statement for the DB
	 */
	public Statement getStatement() throws SQLException{
		return conn.createStatement();
	}

	/**
	 * Executes an query and cleans up after itself.
	 * 
	 * @param query is the query
	 * @return update count or -1 if the query is not an update query
	 */
	public int exec(String query) throws SQLException {
		PreparedStatement stmt = getPreparedStatement( query );
		return exec(stmt);
	}
	
	/**
	 * Executes an query and cleans up after itself.
	 * 
	 * @param stmt is the query
	 * @return update count or -1 if the query is not an update query
	 */
	public static int exec(PreparedStatement stmt) throws SQLException {
		Integer ret = exec(stmt, new SQLResultHandler<Integer>(){
			public Integer handleQueryResult(Statement stmt, ResultSet result) {
				try {
					if(stmt != null)
						return stmt.getUpdateCount();
					else
						return -1;
				} catch (SQLException e) {
					logger.log(Level.WARNING, null, e);
				}
				return -1;
			}			
		});
		
		if(ret != null)
			return ret;
		return -1;
	}
	
	/**
	 * Executes an query and cleans up after itself.
	 * @param <T> 
	 * 
	 * @param query is the query
	 * @param handler is the result handler
	 * @return update count or -1 if the query is not an update query
	 */
	public <T> T exec(String query, SQLResultHandler<T> handler) throws SQLException {
		PreparedStatement stmt = getPreparedStatement( query );
		return exec(stmt, handler);
	}

	/**
	 * Executes an query and cleans up after itself.
	 * 
	 * @param stmt is the query
	 * @param handler is the result handler
	 * @return the object from the handler
	 */
	public static <T> T exec(PreparedStatement stmt, SQLResultHandler<T> handler) throws SQLException{
		try{
			// Execute
			stmt.execute();

			// Handle result
			if( handler != null ){
				ResultSet result = null;
				try{
					if(stmt.getMoreResults()){
						result = stmt.getResultSet();
						return handler.handleQueryResult(stmt, result);
					}
					else
						return null;
				}catch(SQLException sqlex){
					logger.throwing(null, null, sqlex);
				}finally{
					if(result != null){
						try {
							result.close();
						} catch (SQLException sqlex) { 
							logger.throwing(null, null, sqlex);
						}
						result = null;
					}
				}
			}
		}catch(SQLException sqlex){
			logger.throwing(null, null, sqlex);
		// Cleanup
		} finally {		
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlex) { 
					logger.throwing(null, null, sqlex);
				}
				stmt = null;
			}
		}
		return null;
	}

	/**
	 * Sets the pool that this connection belongs to
	 * 
	 * @param pool is the pool
	 */
	protected void setPool(DBConnectionPool pool){
		if( pool != null )
			pool.removeConnection(this);
		this.pool = pool;
	}

	/**
	 * Checks if the DB Connection is valid and functioning
	 * 
	 * @return true or false depending on the validity of the connection
	 */
	public boolean valid(){
		try {
			conn.getMetaData();
			return !conn.isClosed();
		}catch (Exception e) {
			return false;
		}
	}

	/**
	 * Disconnects from the database or releases the connection back to the pool
	 */
	public void close(){
		if(pool!=null){
			pool.releaseConnection(this);
			conn = null;
		}
		else{
			forceClose();
		}
	}

	/**
	 * Disconnects from the database
	 */
	public void forceClose(){
		if (conn != null) {
			try {
				if( !conn.isClosed() )
					conn.close();
			} catch (SQLException e) {
				logger.log(Level.WARNING, null, e);
			}
			conn = null;
		}
	}

	public boolean equals(Object o){
		return conn.equals(o);
	}
}
