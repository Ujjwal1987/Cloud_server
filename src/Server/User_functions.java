package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class User_functions implements Runnable{
	
	private Thread User_functions, run;
	protected BlockingQueue queue = null;
	ServerSocket uf;
	public volatile boolean running = true;
	
	public void terminate(){
		running = false;
	}
	
	public User_functions(BlockingQueue queue) {
		this.queue = queue;
		try {
			uf = new ServerSocket(8887);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		run = new Thread(this, "running");
		run.start();
	}



	@Override
	public void run() {
		// TODO Auto-generated method stub
				Socket User_func = null;
				try {
					User_func = uf.accept();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				User_functions(User_func,queue);
				run();
}
	
	private void User_functions(final Socket User_func, final BlockingQueue queue){
		User_functions = new Thread("User_functions"){
			public void run(){
				while(running){
					User usr = new User(queue);
					usr.usr_auth(User_func);
				}
			}
		};
		User_functions.start();
	}
}
