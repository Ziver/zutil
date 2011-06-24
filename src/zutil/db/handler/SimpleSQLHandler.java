package zutil.db.handler;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import zutil.db.SQLResultHandler;

/**
 * Returns the first column of the first row from the query
 * 
 * @author Ziver
 */
public class SimpleSQLHandler<T> implements SQLResultHandler<T> {
	/**
	 * Is called to handle an result from an query.
	 * 
	 * @param stmt is the query
	 * @param result is the ResultSet
	 */
	@SuppressWarnings("unchecked")
	public T handleQueryResult(Statement stmt, ResultSet result) throws SQLException{
		if( result.next() )
			return (T) result.getObject(1);
		return null;
	}
}
