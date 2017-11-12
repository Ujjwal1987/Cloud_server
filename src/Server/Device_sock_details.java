package Server;

import java.io.Serializable;
import java.net.Socket;

public class Device_sock_details implements Serializable{
	
	private final String Device_id;
	private Socket Socket_Device;
		
	public Device_sock_details(String uID, Socket socket_Device) {
		super();
		Device_id = uID;
		Socket_Device = socket_Device;
	}

	public Socket getSocket_Device() {
		return Socket_Device;
	}

	public void setSocket_Device(Socket socket_Device) {
		Socket_Device = socket_Device;
	}

	public String getUID() {
		return Device_id;
	}
	
}
