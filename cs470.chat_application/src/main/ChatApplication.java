package main;

import java.io.*;
import java.net.*;

public class ChatApplication {

	public static void main(String[] args) throws Exception {
		
		
		ChatApplication ca = new ChatApplication();
		ca.client();
	//	new ChatApplication().server();
	//	ca.client();
	}
	
	@SuppressWarnings("resource")
	public void server() throws Exception{
		ServerSocket srvSocket = new ServerSocket(50001);
		Socket socket  = srvSocket.accept();
		InputStreamReader instrReader = new InputStreamReader(socket.getInputStream());
		BufferedReader br = new BufferedReader(instrReader);
		
		String msg = br.readLine();
		System.out.println(msg);
		if(msg!= null){
			PrintStream prntStream = new PrintStream(socket.getOutputStream());
			prntStream.println("msg reseived");
		}
	}
	
	public void client() throws Exception{
		Socket socket = new Socket("localhost",50001);
		PrintStream ps = new PrintStream(socket.getOutputStream());
		ps.println("hello to server from client");
		
		InputStreamReader instrReader = new InputStreamReader(socket.getInputStream());
		BufferedReader br = new BufferedReader(instrReader);
		
		String msg = br.readLine();
		System.out.println(msg);
	}
}
