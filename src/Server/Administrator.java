package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Administrator {
	
	private String admin_name;
	private String admin_password;
	private String admin_id;
	public String getAdmin_name() {
		return admin_name;
	}
	public void setAdmin_name(String admin_name) {
		this.admin_name = admin_name;
	}
	public String getAdmin_password() {
		return admin_password;
	}
	public void setAdmin_password(String admin_password) {
		this.admin_password = admin_password;
	}
	public String getAdmin_id() {
		return admin_id;
	}
	public void setAdmin_id(String admin_id) {
		this.admin_id = admin_id;
	}
	public Administrator() {
		
	}
	
	public void authenticate(Socket sock_reg_client){
		BufferedReader br = null;
		String string = "";
		try {
			br = new BufferedReader(new InputStreamReader(sock_reg_client.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
        	string = br.readLine();
        	String []temp = string.split(":");
        	admin_name = temp[0];
        	admin_password = temp[1];
        	
        	String SQL_query = "SELECT* FROM ADMINISTRATOR WHERE (ADMIN_NAME = '"+admin_name+"' AND ADMIN_PASSWORD = '"+admin_password+"')";
        	ResultSet result = null;
        	SQL_query sq = new SQL_query();
        	result = sq.query_exec(SQL_query);
       	
        	BufferedWriter bw = null;
        	try {
				if(result.next()){
					bw = new BufferedWriter(new OutputStreamWriter(sock_reg_client.getOutputStream()));
					bw.write("1");
					bw.newLine();
					bw.flush();
				}
				else{
					bw = new BufferedWriter(new OutputStreamWriter(sock_reg_client.getOutputStream()));
					bw.write("0");
					bw.newLine();
					bw.flush();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        while(!(string.equals("exit"))){
        	
            try {
				string = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            System.out.println(string);
            switch(string){
            case "user_reg":
            	User us =  new User();
            	us.register(sock_reg_client);
            	break;
            	
            case "grp_reg":
            	Group grp = new Group();
            	grp.register(sock_reg_client);
            	
            	break;
            	
            case "device_reg":
            	device dev = new device();
            	dev.register(sock_reg_client);
            	
            	break;
            	
            case "ag_reg":
            	Agreegator ag = new Agreegator();
            	ag.register(sock_reg_client);
            	
            	break;
            	
            default:
            	
            	break;
            
            }	
            
        }
        
        
	}
	
	

}
