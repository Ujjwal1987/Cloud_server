package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Notifications implements Runnable {
	ServerSocket Not_sock;
	Thread run, Notifications;
	public static Boolean bl = false;
	public Semaphore semaphore = new Semaphore(1);
	public BufferedWriter bw;
	public static List<Notif_sock_details> Android_Users_notification = new ArrayList<Notif_sock_details>();
	
	public void add_var_notif(String UID, Socket Notif_sock){
		try {
				while(bl == true){
					Thread.currentThread();
					Thread.sleep(1);
				}
				bl = true;
				Android_Users_notification.add(new Notif_sock_details(UID, Notif_sock));
				setAndroid_Users_notification(Android_Users_notification);
				System.out.println("notification size:" + Android_Users_notification.size());
				bl = false;
				
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}
	
	
	
	public static List<Notif_sock_details> getAndroid_Users_notification() {
		return Android_Users_notification;
	}



	public static void setAndroid_Users_notification(
		List<Notif_sock_details> android_Users_notification) {
		Android_Users_notification = android_Users_notification;
	}



	public Notifications() {
		try {
			Not_sock = new ServerSocket(8884);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		run = new Thread(this, "running");
		run.start();
	}
	
	public Notifications(int i){
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Socket Notif = null;
		try {
			Notif = Not_sock.accept();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Notifications_functions(Notif);
		run();
}
	
	public void Notifications_functions(Socket notif){
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new InputStreamReader(notif.getInputStream()));
			bw = new BufferedWriter(new OutputStreamWriter(notif.getOutputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String UID = null;
		try {
			UID = br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		add_var_notif(UID, notif);
		Thread.currentThread().interrupt();
	}
	
	public void transmit_notification(String UID, String Device_id){
		for(int i=0;i<Android_Users_notification.size();i++){
			Notif_sock_details nsd = Android_Users_notification.get(i);
			System.out.println(nsd.getUID());
			if(UID.equals(nsd.getUID())){
				Socket notif = nsd.getNotif_sock();
				try {
					bw = new BufferedWriter(new OutputStreamWriter(notif.getOutputStream()));
					bw.write(Device_id +" online");
					bw.newLine();
					bw.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
	}
}
	public void remove_user(String UID, Socket user_sock){
		while(bl == true){
			
			try {
				Thread.currentThread();
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		bl = true;
		Iterator <Notif_sock_details> it = Android_Users_notification.iterator();
		while(it.hasNext()){
			Notif_sock_details nsd = it.next();
			if(nsd.getUID().equals(UID)){
				if(nsd.getNotif_sock().getInetAddress().equals(user_sock.getInetAddress())){
					it.remove();
				}
			}
		}
		System.out.println(Android_Users_notification.size());
		bl=false;
		return;
	}
	
}
