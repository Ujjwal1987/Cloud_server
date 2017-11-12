package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Agreegator{
	
	private String Agreegator_ID;
	private String UID, GID;
	public Agreegator() {
		
	}
	public String getAgreegator_ID() {
		return Agreegator_ID;
	}
	public void setAgreegator_ID(String agreegator_ID) {
		Agreegator_ID = agreegator_ID;
	}
	public String getUID() {
		return UID;
	}
	public void setUID(String uID) {
		UID = uID;
	}
	public String getGID() {
		return GID;
	}
	public void setGID(String gID) {
		GID = gID;
	}


	public void register(Socket sock_reg_client){
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(sock_reg_client.getInputStream()));
			String line  = null;
			line = br.readLine();
			String []temp = line.split(":");
			Agreegator_ID = temp[0];
			UID = temp[1];
			GID = temp[2];
			
			String My_sql_query = "Select* from USER_IOT where (UID = '"+temp[1]+"')";
			SQL_query sql1 = new SQL_query();
			ResultSet rs = null;
			rs = sql1.query_exec(My_sql_query);
			try {
				if(rs.next()){
					My_sql_query = "Select* from GROUP_IOT where (GROUP_ID = '"+temp[2]+"')";
					rs = null;
					rs = sql1.query_exec(My_sql_query);
					if(rs.next()){
						My_sql_query = "INSERT INTO AGREEGATOR (AGREEGATOR_ID, UID, GROUP_ID) VALUE('"+temp[0]+"','"+temp[1]+"','"+temp[2]+"')";
						SQL_Update_query sql = new SQL_Update_query();
						int result;
						result = sql.query_exec(My_sql_query);
						if(result == 1){
							BufferedWriter bw = null;
							bw = new BufferedWriter(new OutputStreamWriter(sock_reg_client.getOutputStream()));
							bw.write("1");
							bw.newLine();
							bw.flush();
						}else{
							BufferedWriter bw = null;
							bw = new BufferedWriter(new OutputStreamWriter(sock_reg_client.getOutputStream()));
							bw.write("0");
							bw.newLine();
							bw.flush();
						}	
					}else{
						BufferedWriter bw = null;
						bw = new BufferedWriter(new OutputStreamWriter(sock_reg_client.getOutputStream()));
						bw.write("3");
						bw.newLine();
						bw.flush();
					}
					
				} 
				else
				{
					BufferedWriter bw = null;
					bw = new BufferedWriter(new OutputStreamWriter(sock_reg_client.getOutputStream()));
					bw.write("2");
					bw.newLine();
					bw.flush();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}
