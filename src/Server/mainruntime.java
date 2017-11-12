package Server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class mainruntime extends Thread{
	public static void main (String[] args){
		BlockingQueue queue = new ArrayBlockingQueue(1024);
		
		Cloud_Administrator ca = new Cloud_Administrator();
		User_functions uf = new User_functions(queue);
		Device_functions df = new Device_functions(queue);
		Notifications not = new Notifications();

		Thread t1 = new Thread(ca);
		Thread t2 = new Thread(uf);
		Thread t3 = new Thread(df);
		Thread t4 = new Thread(not);
		
		t1.start();
		t2.start();
		t3.start();
		t4.start();
	}
}

