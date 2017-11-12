package Server;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class SQL_query {
	
	public String SQL_URL = "jdbc:mysql://localhost/SERVER_VER_2";
	public String SQL_USER = "root";
	public String SQL_PASSWORD = "dolby-digital";
	public Connection DB_conn = null;
	public Statement stmt = null;
	
	
	
	public SQL_query() {
	}



	public ResultSet query_exec(String Sql_query){
		try {
			DB_conn = (Connection) DriverManager.getConnection(SQL_URL,SQL_USER,SQL_PASSWORD);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	try {
			stmt = (Statement) DB_conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	ResultSet result_temp = null;
    	try {
			result_temp = stmt.executeQuery(Sql_query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return result_temp;
	}

}
