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

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import zutil.db.DBConnection.DBMS;

/**
 * This class is an connection pool
 * 
 * @author Ziver
 */
public class DBConnectionPool extends TimerTask {
	public static final long DEFAULT_TIMEOUT = 10*60*60*1000; // 10 minutes;
	public static final int DEFAULT_MAX_SIZE = 5;
	
	// DB details
	private DBMS dbms;
	private String url;
	private String db;
	private String user;
	private String password;

	// Pool details
	private int max_conn;
	private long timeout;
	private Timer timeout_timer;

	protected class PoolItem{
		public DBConnection conn;
		public long timestamp;

		public boolean equals(Object o){
			return conn.equals(o);
		}
	}

	// The pool
	private LinkedList<PoolItem> inusePool;
	private LinkedList<PoolItem> readyPool;

	/**
	 * Creates a new pool of DB connections
	 * 
	 * @param dbms is the DB type
	 * @param url is the URL to the DB
	 * @param db is the name of the database
	 * @param user is the user name to the DB
	 * @param password is the password to the DB
	 */
	public DBConnectionPool(DBMS dbms, String url, String db, String user, String password) throws Exception{
		this.dbms = dbms;
		this.url = url;
		this.db = db;
		this.user = user;
		this.password = password;

		inusePool = new LinkedList<PoolItem>();
		readyPool = new LinkedList<PoolItem>();
		
		this.setTimeout(DEFAULT_TIMEOUT);
		this.setMaxSize(DEFAULT_MAX_SIZE);
	}

	/**
	 * Registers a Connection to the pool
	 * 
	 * @param conn is the Connection to register
	 */
	protected void addConnection(DBConnection conn){
		PoolItem item = new PoolItem();
		item.conn = conn;
		readyPool.addLast(item);
	}

	/**
	 * Removes an connection from the pool
	 * 
	 * @param conn is the connection to remove
	 */
	protected void removeConnection(DBConnection conn){
		inusePool.remove(conn);
		readyPool.remove(conn);
	}

	/**
	 * Lease one connection from the pool
	 * 
	 * @return an DB connection or null if the pool is empty
	 */
	public synchronized DBConnection getConnection() throws Exception{
		if(readyPool.isEmpty()){
			if( size() < max_conn ){
				DBConnection conn = new DBConnection(dbms, url, db, user, password);
				conn.setPool( this );
				addConnection( conn );
				return conn;
			}
			return null;
		}
		else{
			PoolItem item = readyPool.poll();
			inusePool.addLast(item);
			item.timestamp = System.currentTimeMillis();
			return item.conn;
		}
	}

	/**
	 * Registers the Connection as not used
	 * 
	 * @param conn is the connection that is not used anymore
	 */
	protected synchronized void releaseConnection(DBConnection conn){
		int index = inusePool.indexOf(conn);
		PoolItem item = inusePool.remove(index);
		readyPool.addLast(item);
	}

	/**
	 * @return the current size of the pool
	 */
	public int size(){
		return inusePool.size() + readyPool.size();
	}

	/**
	 * Closes all the connections
	 */
	public synchronized void close() throws SQLException{
		for( PoolItem item : inusePool ){
			item.conn.forceClose();
		}
		inusePool.clear();
		for( PoolItem item : readyPool ){
			item.conn.forceClose();
		}
		readyPool.clear();
	}

	/**
	 * Set the max size of the pool
	 */
	public void setMaxSize(int max){
		this.max_conn = max;
	}

	/**
	 * Sets the timeout of the Connections
	 */
	public synchronized void setTimeout(long timeout){
		this.timeout = timeout;
		if(timeout_timer!=null)
			timeout_timer.cancel();
		timeout_timer = new Timer();
		timeout_timer.schedule(this, 0, timeout / 2);
	}

	/**
	 * Checks every DB connection if they are valid and has not timed out
	 */
	public void run(){
		long stale = System.currentTimeMillis() - timeout;

		for(PoolItem item : inusePool){
			if( !item.conn.valid() && stale > item.timestamp ) {
				removeConnection(item.conn);
				item.conn.forceClose();
			}
		}
	}
}
