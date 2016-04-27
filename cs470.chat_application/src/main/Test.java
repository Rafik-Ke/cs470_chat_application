package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Test {

	private int portNumber = 50001;
	private String ipAddress;
	private volatile boolean exit = false;
	int i = 0;
	public List<Socket> socketList = new ArrayList<Socket>();
	public List<String> CurrentUsers = new ArrayList<String>();
	ServerSocket srvSocket;
	Socket socket;

	public static void main(String[] args) throws Exception {

		Test test = new Test();

		test.serverRunner();

		System.out.println(test.i);

		test.takeInput();

		System.out.println(test.i);

		System.out.println(test.exit);

	}

	public void takeInput() throws Exception {

		Scanner keyboard;
		String input;
		String[] command;
		System.out.println("enter something");
		while (!exit) {
			keyboard = new Scanner(System.in);
			input = keyboard.nextLine();
			input = input.toLowerCase().trim();
			command = input.split("\\s+");
			switch (command[0]) {
			
			case "connect":
				client();
				break;

			case "exit":
				exit = true;
				// socket.close();
				srvSocket.close();
				break;
				
				

			}
		}
	}

	public void serverRunner() {
		Thread t = new Thread() {
			public void run() {
				try {
					server();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
	}
	
	public void server() throws Exception {
		// tcp socket
		srvSocket = new ServerSocket(50001);
		socket = null;
		try {
			while (!exit) {
				socket = srvSocket.accept();
				socketList.add(socket);
				
				System.out.println("New connection from: " + socket.getRemoteSocketAddress());

				InputStreamReader instrReader = new InputStreamReader(socket.getInputStream());
				BufferedReader br = new BufferedReader(instrReader);

				String msg = br.readLine();
				System.out.println(msg + " printed in server");
				if (msg != null) {
					PrintStream prntStream = new PrintStream(socket.getOutputStream());
					prntStream.println("msg reseived");
				}
			}
		} catch (Exception e) {

		} finally {
			if (socket != null && !socket.isClosed())
				socket.close();
		}
	}
	
	@SuppressWarnings("resource")
	public void client() throws Exception {
		Socket socket  = null;
		OutputStream os;
		PrintWriter pw;
		socket = new Socket("192.168.1.67", 50001);
		
		PrintStream ps = new PrintStream(socket.getOutputStream());
		ps.println("hello to server from client , message : " + ++i);
		
		
		InputStreamReader instrReader = new InputStreamReader(socket.getInputStream());
		BufferedReader br = new BufferedReader(instrReader);

		String msg = br.readLine();
		System.out.println(msg);
	}



}
