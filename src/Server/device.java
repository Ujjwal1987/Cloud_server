package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;

public class device {
	
	private String device_ID;
	private String device_ip_address;
	private String device_app_id;
	private String UID, GID;
	public device() {

	}

	
	public void register(Socket sock_reg_client){
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(sock_reg_client.getInputStream()));
			String line  = null;
			line = br.readLine();
			String []temp = line.split(":");
			device_ID = temp[0];
			device_ip_address = temp[1];
			device_app_id = temp[2];
			UID = temp[3];
			GID = temp[4];

			String My_sql_query = "Select* from USER_IOT where (UID = '"+UID+"')";
			SQL_query sql1 = new SQL_query();
			ResultSet rs = null;
			rs = sql1.query_exec(My_sql_query);
			try {
				if(rs.next()){
					My_sql_query = "Select* from GROUP_IOT where (GROUP_ID = '"+GID+"')";
					rs = null;
					rs = sql1.query_exec(My_sql_query);
					if(rs.next()){
						My_sql_query = "INSERT INTO DEVICE (DEVICE_ID, DEVICE_IP_ADDRESS, DEVICE_APP_ID, UID, GROUP_ID) VALUE('"+device_ID+"','"+device_ip_address+"','"+device_app_id+"', '"+UID+"', '"+GID+"')";
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
					}
					else{
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
