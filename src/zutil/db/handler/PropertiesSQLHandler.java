package zutil.db.handler;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Properties;

import zutil.db.SQLResultHandler;

/**
 * Adds the result of the query to a Properties object,
 * 
 * The handler sets the first column of the result as 
 * the key and the second column as the value
 * 
 * @author Ziver
 */
public class PropertiesSQLHandler implements SQLResultHandler<Properties> {
	
	private Properties prop;
	
	/**
	 * Creates a new Properties object to be filled
	 */
	public PropertiesSQLHandler(){
		this.prop = new Properties();
	}
	
	/**
	 * Adds data to a existing Properties object
	 */
	public PropertiesSQLHandler(Properties p){
		this.prop = p;
	}
	
	
	/**
	 * Is called to handle an result from an query.
	 */
	public Properties handleQueryResult(Statement stmt, ResultSet result) throws SQLException{
		while( result.next() )
			prop.setProperty(result.getString(0), result.getString(1));
		return prop;
	}
}
