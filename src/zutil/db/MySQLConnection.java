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
	 * @param url The URL of the MySQL server
	 * @param db The database to connect to
	 * @param user The user name
	 * @param password The password
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
    public MySQLConnection(String url,String db,String user, String password) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		Class.forName ("com.mysql.jdbc.Driver").newInstance();
        conn = DriverManager.getConnection ("jdbc:mysql://"+url+"/"+db, user, password);
    }
    
    /**
     * Runs a query and returns the result
     * NOTE: Don't forget to close the ResultSet and the Statement or it can lead to memory leak tex: rows.getStatement().close();
     * @param sql The query to execute
     * @return The data that the DB returned
     * @throws SQLException
     */
    public synchronized ResultSet returnQuery(String sql) throws SQLException{
    	Statement s = conn.createStatement ();
    	s.executeQuery (sql);
    	return s.getResultSet();    	
    }
    
    /**
     * Runs a query in the MySQL server and returns effected rows
     * @param sql The query to execute
     * @return Number of rows effected
     * @throws SQLException
     */
    public synchronized int updateQuery(String sql) throws SQLException{
    	Statement s = conn.createStatement ();
    	int ret = s.executeUpdate(sql);
    	s.close();
    	return ret;
    }
    
    /**
     * Runs a Prepared Statement
     * NOTE: Don't forget to close the PreparedStatement or it can lead to memory leak
     * @param sql The SQL to run
     * @return The PreparedStatement
     * @throws SQLException
     */
    public synchronized PreparedStatement prepareStatement(String sql) throws SQLException{
    	return conn.prepareStatement(sql);
    }
    
    /**
     * Disconnects from the database
     * @throws SQLException 
     *
     */
    public synchronized void close() throws SQLException{
    	if (conn != null){
            conn.close ();
        }
    }
}
