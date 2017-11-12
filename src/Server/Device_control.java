package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Device_control {
	
	Socket device_sock, user_sock;
	String UID, GID;
	protected BlockingQueue queue = null;
	public static List<Device_sock_details> Device_socket = new ArrayList<Device_sock_details>();
	
	
	public Device_control() {
	}
	
	public Device_control(BlockingQueue queue){
		this.queue = queue;
	}

	public static List<Device_sock_details> getDevice_socket() {
		return Device_socket;
	}

	public static void setDevice_socket(List<Device_sock_details> device_socket) {
		Device_socket = device_socket;
	}
	
	public void control_device(String UID, Socket user_sock, String GID, String admin){
		BufferedReader br1 = null;
		BufferedWriter bw1 = null;
		try {
			br1 = new BufferedReader(new InputStreamReader(user_sock.getInputStream()));
			bw1 = new BufferedWriter(new OutputStreamWriter(user_sock.getOutputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String temp = null;
		try {
			temp = br1.readLine();
//			System.out.println(temp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		if(temp.equals("exit")){
			try {
				bw1.write("1");
				bw1.newLine();
				bw1.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(admin.equals("1")){
				
				Group grp = new Group();
				grp.group_admin_functions(UID, user_sock, GID);
			}
			else{
				Group grp = new Group();
				grp.group_non_admin_functions(UID, user_sock, GID);
			}
			
			//ADD REFRESH PROGRAM
		}else{
			temp = temp.replace("/devid/", "");
			Device_sock_details dsd;
			for(int i =0; i<Device_socket.size(); i++){
				dsd = Device_socket.get(i);
				if(dsd.getUID().equals(temp)){
//					System.out.println(dsd.getUID() + "sending command");
					Socket s1 = dsd.getSocket_Device();
					try {
						BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(s1.getOutputStream()));
						BufferedReader br2 = new BufferedReader(new InputStreamReader(s1.getInputStream()));
						temp = "/devid/"+dsd.getUID();
//						System.out.println(temp);
						bw2.write(temp);        //getting device id and sending it to the gateway
						bw2.newLine();
						bw2.flush();
						bw1.write("1");
						bw1.newLine();
						bw1.flush();
//						System.out.println("i am here");
						String temp2 = null;
/*						temp2 = br2.readLine();
						System.out.println("facebook" + ":" + temp2);
						System.out.println("i am here 2");*/
						try {
							temp2 = (String) queue.take();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(temp2.equals("1")){
							System.out.println("Device comm started");
							device_comm(br1,bw1,br2,bw2,UID, user_sock, GID, admin);
						}
						System.out.println("facebook");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			}
			
			try {
				bw1.write("0");
				bw1.newLine();
				bw1.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			control_device(UID, user_sock, GID, admin);			
		}
	}
	
	public void device_comm(BufferedReader br1, BufferedWriter bw1, BufferedReader br2, BufferedWriter bw2, String UID, Socket user_sock, String GID, String admin){
		String recvd_data = null;
		while(true){
		try {
			recvd_data = br1.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(recvd_data);
		if(recvd_data.startsWith("/r/")){
//			recvd_data = recvd_data.replace("/r/", "");
			try {
				bw2.write(recvd_data);
				bw2.newLine();
				bw2.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else{
				try {
					bw2.write("exit");				// send to gateway
					bw2.newLine();
					bw2.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				control_device(UID, user_sock, GID, admin);
		}
		}
			
	}
	
	public void add_var(String Device_id, Socket dev_sock){
		Device_socket.add(new Device_sock_details(Device_id,dev_sock));
		setDevice_socket(Device_socket);
		System.out.println(Device_socket.size());
		return;
	}
	
	public void remove_device(String Device_id){
		Iterator <Device_sock_details> it = Device_socket.iterator();
		while(it.hasNext()){
			Device_sock_details dsd = it.next();
			if(dsd.getUID().equals(Device_id)){
				it.remove();
			}
		}
		System.out.println(Device_socket.size());
		return;
	}
}
