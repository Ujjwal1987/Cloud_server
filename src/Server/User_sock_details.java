package Server;

import java.io.Serializable;
import java.net.Socket;

public class User_sock_details implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private final String UID;
	private Socket Socket_android;
	public User_sock_details(String uID, Socket socket_android) {
		super();
		UID = uID;
		Socket_android = socket_android;

	}
	public Socket getSocket_android() {
		return Socket_android;
	}
	public void setSocket_android(Socket socket_android) {
		Socket_android = socket_android;
	}
/*	public Socket getNotif_sock() {
		return Notif_sock;
	}
	public void setNotif_sock(Socket notif_sock) {
		Notif_sock = notif_sock;
	}*/
	public String getUID() {
		return UID;
	}


	
	

}
