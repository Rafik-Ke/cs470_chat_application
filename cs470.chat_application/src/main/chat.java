package main;

import java.io.*;
import java.net.*;

public class chat {

	public static void main(String[] args) throws Exception {
		
		
		chat chatApp = new chat();
		chatApp.help();
		chatApp.myip();
	}
	
	public void help(){
		System.out.println("help");
		
		
	}
	
	//print the ip address
	public void myip() throws UnknownHostException{
		System.out.println("The IP address is " + Inet4Address.getLocalHost().getHostAddress());
	}
	
	public void myport(){
		
	}
	
	public void connect(){
		
	}

}
