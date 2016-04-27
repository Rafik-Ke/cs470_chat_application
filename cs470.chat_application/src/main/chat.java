package main;

import java.io.*;
import java.net.*;
import java.nio.channels.ServerSocketChannel;
import java.util.Enumeration;
import java.util.Scanner;

public class chat {
	private int portNumber = 50001;
	private String ipAddress;
	private boolean exit = false;

	public static void main(String[] args) throws Exception {


		// socket.getRemoteSocketAddress().toString();

			chat chatApp = new chat();
			try {
		//		chatApp.portNumber = Integer.parseInt(args[0]);
				chatApp.takeInput();
/*				chatApp.server();
				chatApp.client();*/
				
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Please run the program with this format: chat <port number>");
			}
			
			
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

	public void connect(String dest, String port) throws IOException {

		ServerSocketChannel serverSocket;
		serverSocket = ServerSocketChannel.open();
		// serverSocket.socket().bind();
		serverSocket.socket().accept();

		System.out.println("connect");
	}

	public void client() throws UnknownHostException, IOException {
		String sentece;
		String mSentence;

		// create input stream
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

		// create cllient socket connect to server
		Socket clientSocket = new Socket(ipAddress, portNumber);

		// create output stream attached to socket
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

		// create input stream
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

		sentece = inFromUser.readLine();

		// send line to server
		outToServer.writeChars(sentece);

		// read line from server
		mSentence = inFromServer.readLine();

		// client socket close
		clientSocket.close();

	}

	public void server() throws UnknownHostException, IOException {
		String clientSentece;
		String modSentence;

		ServerSocket serverSocket = new ServerSocket(portNumber);

		while (true) {

			// create cllient socket connect to server
			Socket connectionSocket = new Socket(getIp(), portNumber);

			// create input stream
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

			// create output stream attached to socket
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

			clientSentece = inFromClient.readLine();

			modSentence = clientSentece.toUpperCase() + '\n';

			// send line to server
			outToClient.writeChars(modSentence);

		}

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
	//	srvSocket.close();
		System.out.println("exit");
	}

	public void setPort(int port) {
		this.portNumber = port;
	}

	public int getPort() {
		return this.portNumber;
	}
	
	public String getIp() throws UnknownHostException{
		return Inet4Address.getLocalHost().getHostAddress();
	}

	public void help() throws Exception {
		System.out.println(
				"|*******************************************HELP MENU*****************************************|");
		System.out.println(
				"| 1) help                                                                                     |");
		System.out.println("|\t\tDescription: Display the command options and their description.               |");
		System.out.println(
				"|---------------------------------------------------------------------------------------------|");
		System.out.println(
				"| 2) myip                                                                                     |");
		System.out.println("|\t\tDescription: Display the IP address.                                          |");
		System.out.println(
				"|---------------------------------------------------------------------------------------------|");
		System.out.println(
				"| 3) myport                                                                                   |");
		System.out.println("|\t\tDescription: Display listening port.                                          |");
		System.out.println(
				"|---------------------------------------------------------------------------------------------|");
		System.out.println(
				"| 4) connect                                                                                  |");
		System.out.println("|\t\tDescription: Establish connection with [destination IP] using [port number].  |");
		System.out.println(
				"|---------------------------------------------------------------------------------------------|");
		System.out.println(
				"| 5) list                                                                                     |");
		System.out.println("|\t\tDescription: Display list of connections.                                     |");
		System.out.println(
				"|---------------------------------------------------------------------------------------------|");
		System.out.println(
				"| 6) terminate                                                                                |");
		System.out.println("|\t\tDescription: End connection with IP address of [connection id].               |");
		System.out.println(
				"|---------------------------------------------------------------------------------------------|");
		System.out.println(
				"| 7) send                                                                                     |");
		System.out.println("|\t\tDescription: Send [message] to IP address of [connection id].                 |");
		System.out.println(
				"|---------------------------------------------------------------------------------------------|");
		System.out.println(
				"| 8) exit                                                                                     |");
		System.out.println("|\t\tDescription: Exit program.                                                    |");
		System.out.println(
				"|*********************************************************************************************|");
	}

	public void printErrorMsg(String msg) {
		System.out.println("You Entered a wrong " + msg);
		System.out.println("Please enter again");
	}
}
