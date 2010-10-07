package zutil.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

import zutil.MultiPrintStream;
import zutil.converters.Converter;

/**
 * This class creates a queue that stors the 
 * data in a mysql table.
 * The table should look like this:
 * CREATE TABLE `queue` (
 * 		`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
 * 		`data` BINARY NOT NULL
 * 	);
 * @author Ziver
 *
 */
public class DBQueue<E> implements Queue<E>{
	// GO TO KNOW = SELECT LAST_INSERT_ID() as pos_id
	private DBConnection db;
	private String table;
	
	/**
	 * Initiates the queue.<br>
	 * <b>WARNING!!<b> this will erase all rows in the table
	 * 
	 * @param db is the connection to the DB
	 * @param table is the name of the table
	 */
	public DBQueue(DBConnection db, String table){
		this.db = db;
		this.table = table;
	}

	public boolean add(Object arg0){
		try {
			PreparedStatement sql = db.getPreparedStatement("INSERT INTO "+table+" (data) VALUES(?)");
			sql.setObject(1, arg0);
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

	@SuppressWarnings("unchecked")
	public synchronized E peek() {
		try {
			return db.exec("SELECT * FROM "+table+" LIMIT 1", new SQLResultHandler<E>(){
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

	@SuppressWarnings("unchecked")
	public synchronized E poll() {
		try {
			return db.exec("SELECT * FROM "+table+" LIMIT 1", new SQLResultHandler<E>(){
				public E handleQueryResult(Statement stmt, ResultSet rs) {
					try{
					if (rs.next()) {
						db.exec("DELETE FROM "+table+" WHERE id="+rs.getInt("id")+" LIMIT 1");
						return (E) Converter.toObject(rs.getBytes("data"));
					}
					}catch(Exception e){
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
			db.exec("TRUNCATE TABLE `"+table+"`");
		} catch (SQLException e) {
			e.printStackTrace(MultiPrintStream.out);
		}		
	}

	public boolean contains(Object arg0) {
		try {
			return db.exec("SELECT data FROM "+table+" WHERE data='"+Converter.toBytes(arg0)+"' LIMIT 1", new SQLResultHandler<Boolean>(){
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
		// TODO Auto-generated method stub
		return null;
	}

	public synchronized boolean remove(Object arg0) {
		try {
			db.exec("DELETE FROM "+table+" WHERE data='"+Converter.toBytes(arg0)+"' LIMIT 1");
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
			return db.exec("SELECT count(*) FROM "+table, new SQLResultHandler<Integer>(){
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

	@SuppressWarnings("unchecked")
	public E[] toArray(Object[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
