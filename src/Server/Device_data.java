package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Device_data extends User {
	
	public String data;
	public BufferedReader br1,br2;
	public BufferedWriter bw1,bw2;
	public static List<User_sock_details> Android_Users_device = new ArrayList<User_sock_details>();
	protected BlockingQueue queue = null;
	
	public Device_data(){
		
	}
	
	public Device_data(BlockingQueue queue) {
		super(queue);
		this.queue = queue;
		
		// TODO Auto-generated constructor stub
	}
	
	public List<User_sock_details> getAndroid_Users_device() {
		return Android_Users_device;
	}



	public void setAndroid_Users_device(List<User_sock_details> android_Users_device) {
		Android_Users_device = android_Users_device;
	}

	
	public void add_var(String UID, Socket auth_sock){
		Android_Users_device.add(new User_sock_details(UID,auth_sock));
		setAndroid_Users_device(Android_Users_device);
		System.out.println(Device_data.this.Android_Users_device.size());
		return;
	}
	
	public void extract_device_list(String Group_id, Socket user_sock){
		try {
			br1 = new BufferedReader(new InputStreamReader(user_sock.getInputStream()));
			bw1 = new BufferedWriter(new OutputStreamWriter(user_sock.getOutputStream()));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String intrim_string = null;
		try {
			intrim_string = br1.readLine();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(intrim_string.equals("send_dev_data")){
		String sql = "Select * from DEVICE WHERE (GROUP_ID = '"+Group_id+"' and ONLINE = TRUE)";
		SQL_query sqlq = new SQL_query();
		ResultSet rs = sqlq.query_exec(sql);
		
		try {
			if(rs.next()){
				System.out.println("control");
				rs.beforeFirst();
				while(rs.next()){
					try {
						bw1.write(rs.getString("DEVICE_ID"));
						bw1.newLine();
						bw1.flush();
						bw1.write(rs.getString("DEVICE_APP_ID"));
						bw1.newLine();
						bw1.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				try {
					bw1.write("exit");
					bw1.newLine();
					bw1.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				try {
					bw1.write("no device online");
					bw1.newLine();
					bw1.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}

	public void functions(Socket sock){
		try {
			br1 = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			bw1 = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while(true){
			
			try {
				data = br1.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(data);
			if(data.startsWith("/auth/")){
				//System.out.println(Android_Users_device.size());
				//System.out.println(data);
				data = data.replace("/auth/","");
				String []temp = data.split(":");
				String sql = "Select * from DEVICE WHERE (DEVICE_ID = '"+temp[0]+"' and GROUP_ID = '"+temp[2]+"' and UID = '"+temp[3]+"')";
				SQL_query sqlq = new SQL_query();
				ResultSet rs = sqlq.query_exec(sql);
				try {
					if(rs.next()){									//notifications and authentication
						bw1.write("1");
						bw1.newLine();
						bw1.flush();
						String sql1 = "UPDATE DEVICE SET ONLINE = TRUE WHERE (DEVICE_ID = '"+temp[0]+"')";
						SQL_Update_query suq = new SQL_Update_query();
						int a = suq.query_exec(sql1);
						//storing the device socket 
						Device_control dc = new Device_control();
						dc.add_var(temp[0], sock);
						sql = "SELECT * FROM USER_GROUP WHERE GROUP_ID = '"+temp[2]+"'";
						rs = sqlq.query_exec(sql);
						while(rs.next()){
						//rs.beforeFirst();
						Android_Users_device = getAndroid_Users_device();
						//while(rs.next()){
						System.out.println(rs.getString("UID"));
						Notifications nf = new Notifications(1);
						nf.transmit_notification(rs.getString("UID"), temp[0]);
						/*for(int i=0;i<Android_Users_device.size();i++){
							User_sock_details usd = Android_Users_device.get(i);
							System.out.println(usd.getUID());
							if(rs.getString("UID").equals(usd.getUID())){
								Socket sock_android = usd.getNotif_sock();
								bw2 = new BufferedWriter(new OutputStreamWriter(sock_android.getOutputStream()));
								bw2.write(temp[0]+" online");
								bw2.newLine();
								bw2.flush();
							}*/
						}
						}
						/*ResultSet rs2;
						String IP_address;
						while(rs.next()){
							sql = "SELECT * from Anrdoid_User_Online_db WHERE(UID = '"+rs.getString("UID")+"')";
							rs2 = sqlq.query_exec(sql);
							if(rs2.next()){
								IP_address = rs2.getString("IP_address");
								IP_address = IP_address.replace("/", "");
								Socket socket = new Socket(IP_address,6000);
								bw2 = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
								bw2.write(temp[0]+" online");
								bw2.newLine();
								bw2.flush();
							}
						}*/
						
					else{
						bw1.write("0");
						bw1.newLine();
						bw1.flush();
					}
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			else if(data.startsWith("/notif/")){
				try {
					bw1.write("1");
					bw1.newLine();
					bw1.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				data = data.replace("/notif/","");
				System.out.println(data);
				String []temp = data.split(":");
				String Device_id = temp[0];
				String sql = "UPDATE DEVICE SET ONLINE = TRUE WHERE (DEVICE_ID = '"+temp[0]+"')";
				SQL_Update_query suq = new SQL_Update_query();
				int a = suq.query_exec(sql);
				//storing the device socket 
				Device_control dc = new Device_control();
				dc.add_var(temp[0], sock);
				String GID = temp[1];
				sql = "SELECT * FROM USER_GROUP WHERE GROUP_ID = '"+temp[1]+"'";
				SQL_query sqlq = new SQL_query();
				ResultSet rs = null;
				rs = sqlq.query_exec(sql);
				try {
					while(rs.next()){
					Android_Users_device = getAndroid_Users_device();
					System.out.println(rs.getString("UID"));
					Notifications nf = new Notifications(1);
					nf.transmit_notification(rs.getString("UID"), temp[0]);
/*					for(int i=0;i<Android_Users_device.size();i++){
						User_sock_details usd = Android_Users_device.get(i);
						System.out.println(usd.getUID());
						if(rs.getString("UID").equals(usd.getUID())){
							Socket sock_android = usd.getNotif_sock();
							bw2 = new BufferedWriter(new OutputStreamWriter(sock_android.getOutputStream()));
							bw2.write(temp[0]+" online");
							bw2.newLine();
							bw2.flush();
					}
}*/
}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} /*catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
	}
			else if(data.startsWith("/offline/")){
				data = data.replace("/offline/","");
				String Device_id = data;
				String sql = "UPDATE DEVICE SET ONLINE = FALSE WHERE (DEVICE_ID = '"+Device_id+"')";
				SQL_Update_query suq = new SQL_Update_query();
				int a = suq.query_exec(sql);
				System.out.println("i am offline");
				Device_control dc = new Device_control();
				dc.remove_device(Device_id);
				break;
			}
			else{
				try {
					queue.put(data);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
}
		Thread.currentThread().interrupt();
}
	public void remove_user(String UID, Socket user_sock){
		Iterator <User_sock_details> it = Android_Users_device.iterator();
		while(it.hasNext()){
			User_sock_details usd = it.next();
			if(usd.getUID().equals(UID)){
				if(usd.getSocket_android().equals(user_sock)){
					it.remove();
				}
			}
		}
		System.out.println(Android_Users_device.size());
		return;
	}
}
