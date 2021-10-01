package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class JDBC {
	public static Connection con = null;
	
	public JDBC(String url, String db, String user, String pw) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String connectionString = "jdbc:mysql://"+url+"/"+db;
		
		Connection con = null;
		try {
			con = DriverManager.getConnection(connectionString, user, pw);
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		this.con = con;
	}
}

