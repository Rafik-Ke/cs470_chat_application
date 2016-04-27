package main;

import java.io.*;
import java.net.*;
import java.nio.channels.ServerSocketChannel;
import java.util.Enumeration;
import java.util.Scanner;

public class chat {

	public static void main(String[] args) throws Exception {

		// csocket.getRemoteSocketAddress().toString();

		System.out.println("The IP address is " + InetAddress.getLocalHost().getHostAddress());

		chat chatApp = new chat();
		chatApp.help();
		chatApp.myip();

	}

	public void takeInput() throws Exception {
		boolean exit = false;
		Scanner keyboard;
		String input;
		String[] selection;
		while (!exit) {
			keyboard = new Scanner(System.in);
			input = keyboard.next();
			input = input.toLowerCase().trim();
			selection = input.split(" ");
			switch (selection[0]){
			case "help": help();
				break;
			case "myip": myip();
				break;
			case "myport": myport();
				break;
			case "connect": connect(selection[1], selection[2]);
				break;
			case "list": list();
				break;
			case "terminate":
				break;
			case "send":
				break;
			case "exit": exit = true;
				break;
			default:
				break;
				
			}

		}
	}

	public void createSocket() throws IOException {
		// Create an unbound server socket
		ServerSocket serverSocket = new ServerSocket();

		// Create a socket address object
		InetSocketAddress endPoint = new InetSocketAddress("localhost", 12900);

		// Set the wait queue size to 100
		int waitQueueSize = 100;

		// Bind the server socket to localhost and at port 12900 with
		// a wait queue size of 100
		serverSocket.bind(endPoint, waitQueueSize);
	}

	public void help() throws Exception {
		System.out.println("help");
		System.out.println("Your Host addr: " + Inet4Address.getLocalHost().getHostAddress());


	}

	// print the ip address
	public void myip() throws UnknownHostException, SocketException {
		System.out.println("The IP address is " + Inet4Address.getLocalHost().getHostAddress());
		
		//this is the fake ip will be commented
		System.out.println("the fake " + NetworkInterface.getNetworkInterfaces().nextElement().getInetAddresses()
				.nextElement().getHostAddress()); // returns "127.0.0.1"
	}

	public void myport() {

	}

	public void connect(String dest, String port) {
		/*
		 * ServerSocketChannel serverSocket; serverSocket =
		 * ServerSocketChannel.open(); serverSocket.socket().bind();
		 * serverSocket.socket().accept();
		 */

	}
	
	public void list(){
		
	}
	
	public void terminate(String conId){
		
	}
	
	public void send(String conId, String msg){
		
	}
	
	public void exit(){
		//close the connection
	}

}
