package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class User {
	
	private String username;
	private String password;
	private String UID;
	//public User_sock_details Android_Users;
	protected BlockingQueue queue = null;
	public List<User_sock_details> Android_users = new ArrayList<User_sock_details>();
	
	public User(){
		
	}
	
	public User(BlockingQueue queue) {
		this.queue = queue;
	}
	
	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getUID() {
		return UID;
	}


	public void setUID(String uID) {
		UID = uID;
	}
	
	public void reg_newusr_android(String UID, Socket sock, String Group_ID){
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			String line  = null;
			line = br.readLine();
			if(line.equals("exit")){				//sync
				bw.write("1");
				bw.newLine();
				bw.flush();
				Group grp = new Group(queue);
				grp.group_admin_functions(UID, sock, Group_ID);
			}else{
			String []temp = line.split(":");
			System.out.println(line);
			username = temp[0];
			password = temp[1];
			UID = temp[2];

			String My_sql_query = "INSERT INTO USER_IOT (USERNAME, PASSWORD, UID) VALUE('"+username+"','"+password+"','"+UID+"')";
			SQL_Update_query sql = new SQL_Update_query();
			int result;
			result = sql.query_exec(My_sql_query);
			if(result == 1){
				My_sql_query = "INSERT INTO USER_GROUP(GROUP_ID, UID) VALUE('"+Group_ID+"','"+UID+"')";
				result = sql.query_exec(My_sql_query);
				if(result==1){
					
					bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
					bw.write("1");
					bw.newLine();
					bw.flush();
				}
				else{
					
					bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
					bw.write("0");
					bw.newLine();
					bw.flush();
				}
			}else{
				
				bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
				bw.write("0");
				bw.newLine();
				bw.flush();
			}
			
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void reg_existusr_android(String UID, Socket sock, String GID){
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			String line  = null;
			line = br.readLine();
			if(line.equals("exit")){				//sync
				bw.write("1");
				bw.newLine();
				bw.flush();
				Group grp = new Group(queue);
				grp.group_admin_functions(UID, sock, GID);
			}else{
			line = br.readLine();
			UID = line;

			String My_sql_query = "INSERT INTO USER_GROUP(GROUP_ID, UID) VALUE('"+GID+"','"+UID+"')";
			SQL_Update_query sql = new SQL_Update_query();
			int result;
			result = sql.query_exec(My_sql_query);
			if(result == 1){
					
					bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
					bw.write("1");
					bw.newLine();
					bw.flush();
				}
				else{
					bw = null;
					bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
					bw.write("0");
					bw.newLine();
					bw.flush();
				}
			}
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		} 


	public void usr_auth(Socket auth_sock){
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(auth_sock.getInputStream()));
			String line  = null;
			line = br.readLine();
			String []temp = line.split(":");
			username = temp[0];
			password = temp[1];
			System.out.println(line);

			String My_sql_query = "SELECT* FROM USER_IOT WHERE (USERNAME = '"+username+"' AND PASSWORD = '"+password+"')";
			SQL_query sql = new SQL_query();
			ResultSet result;
			result = sql.query_exec(My_sql_query);
			try {
				if(result.next()){
					BufferedWriter bw = null;
					String UID  =result.getString("UID");
					bw = new BufferedWriter(new OutputStreamWriter(auth_sock.getOutputStream()));
					bw.write("1" + ":" + UID);
					bw.newLine();
					bw.flush();
//					ServerSocket not_sock = new ServerSocket (8884);
//					Socket notif_sock = not_sock.accept();
					Device_data dd = new Device_data();
					dd.add_var(result.getString("UID"),auth_sock);
					
					//User.this.Android_users.add(new User_sock_details(username, auth_sock.getInetAddress(), auth_sock.getPort(),result.getString("UID"),auth_sock));
					/*Android_Users = new User_sock_details(username, auth_sock.getInetAddress(), auth_sock.getPort(),result.getString("UID"),auth_sock);
					try {
						Android_Users.save_Obj(result.getString("UID"), Android_Users);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
					Group grp = new Group(queue);
//					String UID = result.getString("UID");
					grp.extract_groups(UID,auth_sock);
					
/*					String line1 = br.readLine();
						Group grp = new Group();
						String UID = result.getString("UID");
						grp.extract_groups(UID,auth_sock);
					}*/
				}else{
					BufferedWriter bw = null;
					bw = new BufferedWriter(new OutputStreamWriter(auth_sock.getOutputStream()));
					bw.write("0");
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
	
	public void register(Socket sock_reg_client){
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(sock_reg_client.getInputStream()));
			String line  = null;
			line = br.readLine();
			String []temp = line.split(":");
			username = temp[0];
			password = temp[1];
			UID = temp[2];

			String My_sql_query = "INSERT INTO USER_IOT (USERNAME, PASSWORD, UID) VALUE('"+username+"','"+password+"','"+UID+"')";
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}


