package zutil.db;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public interface SQLResultHandler<T> {
	/**
	 * Is called to handle an result from an query.
	 * 
	 * @param stmt is the query
	 * @param result is the ResultSet
	 */
	public T handle(Statement stmt, ResultSet result) throws SQLException;
}
