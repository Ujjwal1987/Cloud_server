package Server;

import java.net.Socket;

public class Notif_sock_details {
	
	private final String UID;
	private Socket Notif_sock;
//	private Socket user_sock;
	public Notif_sock_details(String uID, Socket Notif_sock) {
		super();
		UID = uID;
		this.Notif_sock = Notif_sock;
//		this.user_sock = user_sock;
	}
	public Socket getNotif_sock() {
		return Notif_sock;
	}
	public void setNotif_sock(Socket notif_sock) {
		Notif_sock = notif_sock;
	}
	public String getUID() {
		return UID;
	}
/*	public Socket getUser_sock() {
		return user_sock;
	}
	public void setUser_sock(Socket user_sock) {
		this.user_sock = user_sock;
	}*/
	
}
