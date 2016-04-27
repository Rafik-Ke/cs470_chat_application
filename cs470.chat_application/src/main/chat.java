package main;

import java.io.*;
import java.net.*;
import java.nio.channels.ServerSocketChannel;
import java.util.Enumeration;
import java.util.Scanner;

public class chat {
	private int portNumber = 50001;
	private String ipAddress;

	public static void main(String[] args) throws Exception {

		// socket.getRemoteSocketAddress().toString();

		// System.out.println("The IP address is " +
		// InetAddress.getLocalHost().getHostAddress());

		chat chatApp = new chat();

		chatApp.takeInput();

	}

	public void takeInput() throws Exception {
		boolean exit = false;
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
			case "help":
				if (command.length > 1)
					System.out.println("Too many arguments");
				else
					help();
				break;
			case "myip":
				if (command.length > 1)
					System.out.println("Too many arguments");
				else
					myip();
				break;
			case "myport":
				if (command.length > 1)
					System.out.println("Too many arguments");
				else
					myport();
				break;
			case "connect":
				if (command.length == 1)
					printErrorMsg("The destination is not specified");
				else if (command.length == 2)
					printErrorMsg("The port number is not specified");
				else if (command.length > 3)
					printErrorMsg("Too many arguments");
				else
					connect(command[1], command[2]);
				break;
			case "list":
				if (command.length > 1)
					System.out.println("Too many arguments");
				else
					list();
				break;
			case "terminate":
				if (command.length == 1)
					printErrorMsg("The connection ID is not specified");
				else
					terminate(command[1]);
				break;
			case "send":
				if (command.length == 1)
					printErrorMsg("The connection ID is not specified");
				if (command.length == 2)
					printErrorMsg("There is no message.");
				else if (command.length > 3)
					printErrorMsg("Too many arguments");
				else
					send(command[1], command[2]);
				break;
			case "exit":
				System.out.println(command.length);
				if (command.length > 1) {

					System.out.println("Too many arguments");
				} else {
					exit();
					exit = true;
				}
				break;
			default:
				printErrorMsg("command");
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

	}

	// print the ip address
	public void myip() throws UnknownHostException, SocketException {
		System.out.println("The IP address is " + Inet4Address.getLocalHost().getHostAddress());

		// this is the fake ip will be commented
		System.out.println("the fake " + NetworkInterface.getNetworkInterfaces().nextElement().getInetAddresses()
				.nextElement().getHostAddress()); // returns "127.0.0.1"
	}

	public void myport() {
		System.out.println(portNumber);
	}

	public void connect(String dest, String port) {
		/*
		 * ServerSocketChannel serverSocket; serverSocket =
		 * ServerSocketChannel.open(); serverSocket.socket().bind();
		 * serverSocket.socket().accept();
		 */

		System.out.println("connect");
	}

	public void list() {
		System.out.println("list");
	}

	public void terminate(String conId) {
		System.out.println("terminate");
	}

	public void send(String conId, String msg) {
		System.out.println("send");
	}

	public void exit() {
		// close the connection
		System.out.println("exit");
	}

	public void setPort(int port) {
		this.portNumber = port;
	}

	public int getPort() {
		return this.portNumber;
	}

	public void printErrorMsg(String msg) {
		System.out.println("You Entered a wrong " + msg);
		System.out.println("Please enter again");
	}
}
