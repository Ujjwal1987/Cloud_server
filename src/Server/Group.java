package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;

public class Group {
	
	String GID;
	String Group_name;
	String Admin_UID;
	protected BlockingQueue queue = null;
	public Group() {

	}
	public Group(BlockingQueue queue){
		this.queue = queue;
	}
	public String getGID() {
		return GID;
	}
	public void setGID(String gID) {
		GID = gID;
	}
	public String getGroup_name() {
		return Group_name;
	}
	public void setGroup_name(String group_name) {
		Group_name = group_name;
	}
	
	public void extract_groups(String UID, Socket sock){
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader (sock.getInputStream()));
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		String line1 = null;
		try {
			line1 = br.readLine();
			System.out.println(line1);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		if(line1.equals("send_grp")){
		String My_sql_query = "SELECT* FROM USER_GROUP WHERE (UID = '"+UID+"')";
		SQL_query sql = new SQL_query();
		ResultSet result;
		result = sql.query_exec(My_sql_query);
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String GID = null;
		try {
			while(result.next()){
				GID = result.getString("GROUP_ID");
				try {
					My_sql_query = "SELECT* FROM GROUP_IOT WHERE (GROUP_ID = '"+GID+"')";
					ResultSet result_temp;
					result_temp = sql.query_exec(My_sql_query);
					while(result_temp.next()){
					bw.write(result_temp.getString("GROUP_NAME"));
					bw.newLine();
					bw.flush();
					bw.write(GID);
					bw.newLine();
					bw.flush();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			try {
				bw.write("exit");
				bw.newLine();
				bw.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			String Group_ID;
			Group_ID = br.readLine();
			if(Group_ID.equals("exit")){
				//Sync function
				bw.write("exit");
				bw.newLine();
				bw.flush();
				bw.close();
				br.close();
				sock.close();
				Device_data dd = new Device_data();
				dd.remove_user(UID, sock);
				Notifications not = new Notifications(1);
				not.remove_user(UID,sock);
				User_functions uf = null;
				uf.terminate();
			}else{
			
			My_sql_query = "SELECT* FROM GROUP_IOT WHERE (GROUP_ID = '"+Group_ID+"')";
			ResultSet result_temp = sql.query_exec(My_sql_query);
			try {
				while(result_temp.next()){
					if(result_temp.getString("UID").equals(UID)){
						bw.write("1");
						bw.newLine();
						bw.flush();
						group_admin_functions(UID, sock, Group_ID);
/*						while(true){
							if(temp.equals("exit")){
								bw.write("1");
								bw.newLine();
								bw.flush();
								extract_groups(UID, sock); //sync task
								break;
							}else{
								switch(temp){
								case "reg_usr_grp":
									User usr1 = new User();
									usr1.reg_existusr_android(sock, Group_ID);
									break;
									
								case "cntrl_dev":
									break;
									
								case "reg_new_usr_grp":
									User usr = new User();
									usr.reg_newusr_android(sock, Group_ID);
									break;
									
								default:
									break;
								}	
							}
							
						}*/
					}
					else{
						bw.write("0");
						bw.newLine();
						bw.flush();
						group_non_admin_functions(UID, sock, Group_ID);
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		}
		
	}
	
	public void group_admin_functions(String UID, Socket sock, String Group_ID){
//		System.out.println("I am here");
		BufferedReader br = null;
		
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String temp  = null;
		while(true){
			try {
				temp = br.readLine();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(temp.equals("exit")){
				try {
					bw.write("1");
					bw.newLine();
					bw.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				extract_groups(UID, sock); //sync task
				break;
			}else{
				switch(temp){
				case "reg_usr_grp":
					User usr1 = new User();
					usr1.reg_existusr_android(UID, sock, Group_ID);
					break;
					
				case "cntrl_dev":
					Device_data dd1 = new Device_data();
					dd1.extract_device_list(Group_ID, sock);
					Device_control dc = new Device_control(queue);
					dc.control_device(UID, sock, Group_ID,"1");
					break;
					
				case "reg_new_usr_grp":
					User usr = new User();
					usr.reg_newusr_android(UID, sock, Group_ID);
					break;
					
				default:
					break;
				}	
			}
			
		}
	}
	
	public void group_non_admin_functions(String UID, Socket sock, String Group_ID){
//		System.out.println("I am here");
		BufferedReader br = null;
		
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String temp  = null;
		while(true){
			try {
				temp = br.readLine();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println(temp);
			if(temp.equals("exit")){
				try {
					bw.write("1");
					bw.newLine();
					bw.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				extract_groups(UID, sock); //sync task
				break;
			}else{
				switch(temp){
					
				case "cntrl_dev":
					
					Device_data dd1 = new Device_data();
					dd1.extract_device_list(Group_ID, sock);
					Device_control dc = new Device_control();
					dc.control_device(UID, sock, Group_ID,"0");
					break;
					
						
				default:
					break;
				}	
			}
			
		}
	}
	
	public void register(Socket sock_reg_client){
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(sock_reg_client.getInputStream()));
			String line  = null;
			line = br.readLine();
			String []temp = line.split(":");
			Group_name = temp[0];
			GID = temp[1];
			Admin_UID = temp[2];
			
			String My_sql_query = "Select* from USER_IOT where (UID = '"+Admin_UID+"')";
			SQL_query sql1 = new SQL_query();
			ResultSet rs = null;
			rs = sql1.query_exec(My_sql_query);
			try {
				if(rs.next()){
				My_sql_query = "INSERT INTO GROUP_IOT (GROUP_NAME, GROUP_ID, UID) VALUE('"+Group_name+"','"+GID+"','"+Admin_UID+"')";
				SQL_Update_query sql = new SQL_Update_query();
				int result;
				result = sql.query_exec(My_sql_query);
				if(result == 1){
					BufferedWriter bw = null;
					bw = new BufferedWriter(new OutputStreamWriter(sock_reg_client.getOutputStream()));
					bw.write("1");
					bw.newLine();
					bw.flush();
					My_sql_query = "INSERT INTO USER_GROUP(GROUP_ID, UID) VALUE('"+GID+"','"+Admin_UID+"')";
					sql.query_exec(My_sql_query);
					
				}else{
					BufferedWriter bw = null;
					bw = new BufferedWriter(new OutputStreamWriter(sock_reg_client.getOutputStream()));
					bw.write("0");
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
