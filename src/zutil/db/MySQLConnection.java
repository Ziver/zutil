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
    	if(result.next())
    		return result.getString(0);
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
