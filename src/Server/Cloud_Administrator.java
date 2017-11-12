package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Cloud_Administrator extends Thread {
	
	private Thread Cloud_Administrator, run;
	ServerSocket ca;
	
	
	public Cloud_Administrator() {
		try {
			ca = new ServerSocket(8886);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		run = new Thread(this, "running_admin");
		run.start();
	}



	@Override
	public void run() {
		// TODO Auto-generated method stub
				Socket Admin_func = null;
				try {
					Admin_func = ca.accept();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Cloud_Administrator(Admin_func);
				run();
				
	}
	
	private void Cloud_Administrator(final Socket Admin_func){
		Cloud_Administrator = new Thread("Cloud_Administrator"){
			public void run(){
				while(true){
					Administrator admin = new Administrator();
					admin.authenticate(Admin_func);
				}
			}
		};
		Cloud_Administrator.start();
	}
}
