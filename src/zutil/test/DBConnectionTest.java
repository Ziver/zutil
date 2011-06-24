package zutil.test;

import java.sql.PreparedStatement;

import zutil.db.DBConnection;
import zutil.db.handler.SimpleSQLHandler;

public class DBConnectionTest {

	public static void main(String[] args){
		try {
			DBConnection db = new DBConnection("koc.se","db","user","password");
			
			// Query 1
			PreparedStatement sql = db.getPreparedStatement("SELECT ?");
			sql.setInt(1, 1);
			DBConnection.exec(sql);
			
			// Query 2
			db.exec("UPDATE ...");
			
			// Query 3
			String s = db.exec("SELECT hello", new SimpleSQLHandler<String>());
			System.out.println( s );
			
			// Query 4
			PreparedStatement sql2 = db.getPreparedStatement("SELECT ?");
			sql2.setString(1, "hello");
			String s2 = DBConnection.exec(sql2, new SimpleSQLHandler<String>());
			System.out.println( s2 );
			
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
