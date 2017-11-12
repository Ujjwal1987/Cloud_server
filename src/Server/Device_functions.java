package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class Device_functions extends Thread {
	
	private Thread Device_functions, run;
	ServerSocket df;
	protected BlockingQueue queue = null;
	
	
	public Device_functions(BlockingQueue queue) {
		this.queue = queue;
		try {
			df = new ServerSocket(8885);
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
		Socket Device_func = null;
		try {
			Device_func = df.accept();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Device_functions(Device_func);
		run();
}

	private void Device_functions(final Socket device_func) {
		// TODO Auto-generated method stub
		Device_functions = new Thread("Device_functions"){
			public void run(){
				while(true){
					Device_data dd = new Device_data(queue);
					dd.functions(device_func);
				}
			}
		};
		Device_functions.start();
	}
}
