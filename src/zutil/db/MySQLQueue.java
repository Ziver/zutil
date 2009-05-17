package zutil.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
public class MySQLQueue<E> implements Queue<E>{
	// GO TO KNOW = SELECT LAST_INSERT_ID() as pos_id
	private MySQLConnection db;
	private String table;
	
	/**
	 * Initiats the queue.
	 * WARNING!! this will erase all rows i the table
	 * @param db The connection to the db
	 * @param table The name of the table
	 * @throws SQLException 
	 */
	public MySQLQueue(MySQLConnection db, String table){
		this.db = db;
		this.table = table;
	}

	public boolean add(Object arg0){
		try {
			PreparedStatement sql = db.prepareStatement("INSERT INTO "+table+" (data) VALUES(?)");
			sql.setObject(1, arg0);
			sql.executeUpdate();
			sql.close();
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
		// TODO Auto-generated method stub
		return false;
	}

	@SuppressWarnings("unchecked")
	public synchronized E peek() {
		try {
			ResultSet rs = db.returnQuery("SELECT * FROM "+table+" LIMIT 1");
			if (rs.next()) {
				return (E) Converter.toObject(rs.getBytes("data"));
			}
			rs.getStatement().close();
		} catch (Exception e) {
			e.printStackTrace(MultiPrintStream.out);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public synchronized E poll() {
		try {
			ResultSet rs = db.returnQuery("SELECT * FROM "+table+" LIMIT 1");
			if (rs.next()) {
				db.updateQuery("DELETE FROM "+table+" WHERE id="+rs.getInt("id")+" LIMIT 1");
				return (E) Converter.toObject(rs.getBytes("data"));
			}
			rs.getStatement().close();
		} catch (Exception e) {
			e.printStackTrace(MultiPrintStream.out);
		}
		return null;
	}

	public E remove() {
		return poll();
	}

	public boolean addAll(Collection arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public void clear() {
		try {
			db.updateQuery("TRUNCATE TABLE `"+table+"`");
		} catch (SQLException e) {
			e.printStackTrace(MultiPrintStream.out);
		}		
	}

	public boolean contains(Object arg0) {
		try {
			ResultSet rs = db.returnQuery("SELECT data FROM "+table+" WHERE data='"+Converter.toBytes(arg0)+"' LIMIT 1");
			if (rs.next()) {
				return true;
			}
			rs.getStatement().close();
		} catch (Exception e) {
			e.printStackTrace(MultiPrintStream.out);
		}
		return false;
	}

	public boolean containsAll(Collection arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isEmpty() {
		return (peek() != null);
	}

	public Iterator iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	public synchronized boolean remove(Object arg0) {
		try {
			ResultSet rs = db.returnQuery("DELETE FROM "+table+" WHERE data='"+Converter.toBytes(arg0)+"' LIMIT 1");
			rs.getStatement().close();
			return true;
		} catch (Exception e) {
			e.printStackTrace(MultiPrintStream.out);
		}
		return false;
	}

	public synchronized boolean removeAll(Collection arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean retainAll(Collection arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public int size() {
		try {
			ResultSet rs = db.returnQuery("SELECT count(*) FROM "+table);
			if (rs.next()) {
				return rs.getInt(1);
			}
			rs.getStatement().close();
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
