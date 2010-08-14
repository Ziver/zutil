package zutil.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DBConnection{
	public enum DBMS{
		MySQL
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
			Class.forName ("com.mysql.jdbc.Driver").newInstance();
			DriverManager.setLoginTimeout(10);
			return "mysql";
		}
		return null;
	}

	/**
	 * @return the last inserted id or -1 if there was an error
	 */
	public int getLastInsertID(){
		try{
			return exec("SELECT LAST_INSERT_ID()", new SQLResultHandler<Integer>(){
				public Integer handle(Statement stmt, ResultSet result) throws SQLException {
					if(result.next())
						return result.getInt(1);
					return -1;
				}
			});
		}catch(SQLException e){
			return -1;
		}	
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
		return exec(stmt, new SQLResultHandler<Integer>(){
			public Integer handle(Statement stmt, ResultSet result) {
				try {
					return stmt.getUpdateCount();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return -1;
			}			
		});
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
					result = stmt.getResultSet();
					return handler.handle(stmt, result);		
				}finally{
					if(result != null){
						try {
							result.close();
						} catch (SQLException e) { }
						result = null;
					}
				}
			}
		// Cleanup
		} finally {		
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlex) { }
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
				conn.close();
			} catch (SQLException sqlex) {
				sqlex.printStackTrace();
			}
			conn = null;
		}
	}

	public boolean equals(Object o){
		return conn.equals(o);
	}
}