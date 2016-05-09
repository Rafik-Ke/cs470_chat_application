/**
 * CS470 - Project 1
 * Developers:
 * Rafik Keshishians
 * Salem Alharbi
 * 
 * Client Server Chat application working on TCP
 * Each peer can make connection to multiple computer; 
 * each peer initiating the connection becomes the client
 * and can send message to the other peer which is a server peer.
 * Therefore, to send a message to a peer that is already connected as a client,
 * a client initiation should be sent to the destination machine first.
 * 
 * For the project Java NIO non-blocking IO is used that each socket channel remains open after 
 * making a handshake. 
 * 
 * Online training source: Java NIO SocketChannel (non-blocking IO)
 * URL:    			 http://tutorials.jenkov.com/java-nio/index.html
 * 
 */

package main;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class chat {
	private int myPortNumber = 1111;
	private boolean exit = false;
	private List<Connection> connections = new ArrayList<Connection>();
	private ServerSocketChannel serverSocketChannel;
	private Selector socketSelector;
	private ByteBuffer readBuffer;

	public static void main(String[] args) throws Exception {
		chat chatApp = new chat();
		try {
			// chatApp.setMyPortNumber(Integer.parseInt(args[0]));
			chatApp.serverRunner();
			chatApp.takeInput();
		} catch (Exception e) {
			System.out.println("Please run the program with this format:java chat <port number>");
		}
	}

	/**
	 * Returns void.
	 * initiates socketselector by opening a selector
	 * Initiates a serversocketchannel and opens the channel for coming connection
	 * configures the connection to not get closed
	 * binds the connection with the host port number
	 * 
	 *runs the server thread 
	 */
	public void serverRunner() throws IOException {
		socketSelector = Selector.open();
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.register(socketSelector, SelectionKey.OP_ACCEPT);
		serverSocketChannel.socket().bind(new InetSocketAddress(myPortNumber));

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

	/**
	 * Returns void.
	 * runs on a separate thread in a while loop until the user exits the program
	 * waits for a request to either create a new connection or read the request
	 *
	 */
	public void server() throws Exception {
		// boolean conExists; //no need because client will not send connect
		SelectionKey key = null;

		while (!exit) {
			try {
				/**
				 * Wait for an event one of the registered channels
				 */
				socketSelector.select();

				// Iterate over the set of keys for events
				Iterator<SelectionKey> selectedKeys = socketSelector.selectedKeys().iterator();

				while (selectedKeys.hasNext()) {
					key = (SelectionKey) selectedKeys.next();
					selectedKeys.remove();
					if (!key.isValid()) {
						continue;
					}
					// check the request is a new connection or reading
					// from a connection new connection request
					else if (key.isAcceptable()) {
						this.accept(key);
						// connection already exists, reading message
					} else if (key.isReadable()) {
						this.read(key);
					} else if (key.isConnectable()) {
						System.out.println("is connectable");
					}
				}

			} catch (Exception e) {
				System.out.println("Please rerun the program fatal error");
				e.getMessage();
			}
		}
	}
	/**
	 * Returns void.
	 * creates a new connection by using the selector key
	 * @param key Selection key for socket selector passed by a related event
	 */
	private void accept(SelectionKey key) throws IOException {

		ServerSocketChannel serverSocketChannel = null;
		SocketChannel socketChannel = null;
		try {
			serverSocketChannel = (ServerSocketChannel) key.channel();
			socketChannel = serverSocketChannel.accept();

			socketChannel.configureBlocking(false);

			// Register SocketChannel in the selector and wait for client
			socketChannel.register(socketSelector, SelectionKey.OP_READ);
			String rip = getRemoteIP(socketChannel);
			System.out.println("New connection from: " + rip);

			Connection con = new Connection(socketChannel, rip, getMyPortNumber(), "server");
			connections.add(con);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns void.
	 * reading the message using the key of the socketchannel
	 * @param key Selection key for socket selector passed by a related event
	 */
	private void read(SelectionKey key) throws IOException {
		readBuffer = ByteBuffer.allocate(9000);
		SocketChannel socketChannel = null;
		String remoteIp = "";
		int numRead;
		try {
			socketChannel = (SocketChannel) key.channel();
			remoteIp = getRemoteIP(socketChannel);
			readBuffer.clear();
			numRead = socketChannel.read(readBuffer);
			byte[] data = new byte[numRead];

			System.arraycopy(readBuffer.array(), 0, data, 0, numRead);
			String message = new String(data);

			System.out.println("Message received from " + getRemoteIP(socketChannel) + ": " + message);
		} catch (Exception e) {
			System.out.println("Peer " + remoteIp + " terminates the connection");
			key.channel().close();
			key.cancel();
			socketChannel.close();
			for (int i = 0; i < connections.size(); i++)
				if (connections.get(i).getConnectionIp().equals(remoteIp))
					connections.remove(i);
			// e.printStackTrace();
			return;
		}
	}
	
	/**
	 * Returns void.
	 * sends message to the server
	 * @param conId the index of the peer server
	 * @param msg the message that is passed from client to the server
	 */
	public void send(String conId, String msg) throws IOException {
		int id = Integer.parseInt(conId) - 1;
		byte[] message = new String(msg).getBytes();
		ByteBuffer buffer = ByteBuffer.wrap(message);
		if (connections.get(id).getSocketChannel().isConnected()) {
			connections.get(id).getSocketChannel().write(buffer);
		}
		buffer.clear();
	}
	/**
	 * Returns void.
	 * makes a client connection to the destination peer
	 * @param destIp the destination peer IP address
	 * @param dstPrt the destination peer port number
	 */
	public void connect(String destIp, String dstPrt) throws Exception {
		SocketChannel socketChannel = null;
		InetSocketAddress isa = null;
		int timeout = 2000;
		boolean conExists = false;
		try {
			int destPort = Integer.parseInt(dstPrt);
			socketChannel = SocketChannel.open();

			if (destIp.equals(getMyIp()) || destIp.toLowerCase().equals("localhost") || destIp.equals("127.0.0.1")) {
				System.out.println("The connection request is from the same computer");
				conExists = true;
				return;
			} else {
				for (int i = 0; i < connections.size(); i++) {
					if (destIp.equals(connections.get(i).getConnectionIp())
							&& destPort == connections.get(i).getDisplayPort()) {
						System.out.println("The connection already exists");
						conExists = true;
						return;
					}
				}
			}

			socketChannel.socket().setSoTimeout(timeout);
			if (!conExists) {
				isa = new InetSocketAddress(destIp, destPort);
				socketChannel.socket().connect(isa, timeout);
				socketChannel.configureBlocking(false);
				System.out.println("The connection to peer " + destIp + " is successfully established;");
				Connection con = new Connection(socketChannel, destIp, destPort, "client");
				connections.add(con);
				return;
			}
		} catch (Exception e) {
			System.out.println("connection is not made correctly");
		}
	}
	/**
	 * Returns void.
	 * displays the list of the current connections specifying the host peer is a client or a server
	 */
	public void list() throws IOException {
		System.out.printf("%-7s%5s%18s%25s\n", "id:", "IP address", "Port No.", "Connection Type");
		System.out.println("-------------------------------------------------------------");
		for (int i = 0; i < connections.size(); i++) {
			if (connections.get(i).getSocketChannel().isConnected() && connections.get(i).getSocketChannel().isOpen())
				System.out.printf("%-7d%5s%17d%20s\n", (i + 1), connections.get(i).getConnectionIp(),
						connections.get(i).getDisplayPort(), connections.get(i).getType());
		}
	}

	/**
	 * Returns void. 
	 * Implementations of terminate command Terminates connection
	 * with a specific user.
	 * 
	 * @param conId Index of user in list.
	 * 
	 */
	public void terminate(String conId) {
		try {
			int id = Integer.parseInt(conId) - 1;
			connections.get(id).getSocketChannel().socket().close();
			connections.get(id).getSocketChannel().close();
			connections.remove(id);
		} catch (Exception e) {
			System.out.println("Please enter the ID within the list.");
		}
	}

	/**
	 * Returns void. Implementation of exit command means exit from program and
	 * tells other users that connection is terminated.
	 */
	public void exit() throws IOException {
		this.exit = true;
		// terminate all the connections
		for (int i = 0; i < connections.size(); i++) {
			terminate("" + i);
		}
		socketSelector.close();
		serverSocketChannel.close();
	}

	/**
	 * Returns int. Implmenation of myport command will display user's port
	 * number that program is currently running on and also it uses to receive
	 * the connetion requests.
	 */
	public int getMyPortNumber() {
		return this.myPortNumber;
	}

	/**
	 * Returns void. assigns the port number that program is running on to
	 * myPortNumber.
	 * 
	 * @param port a command line argument and it does not change.
	 * 
	 */
	public void setMyPortNumber(int port) {
		this.myPortNumber = port;
	}

	/**
	 * Returns String. Getter that returns the current machine-peer IP address.
	 * and depends on the network and router.
	 */
	public String getMyIp() throws UnknownHostException {
		return Inet4Address.getLocalHost().getHostAddress();
	}

	/**
	 * Returns String. Getter that returns the passing PocketChannel peer IP
	 * address. and depends on the network and router.
	 * 
	 * @param socketChannel
	 *            a SocketChannel object that is asked for its IP address
	 */
	public String getRemoteIP(SocketChannel socketChannel) throws IOException {
		return socketChannel.getRemoteAddress().toString().replace("/", "").split(":")[0];
	}

	/**
	 * Returns void. Takes user's input, checks for correct input and calls the
	 * appropriate method.
	 */
	public void takeInput() throws Exception {
		Scanner keyboard;
		String input;
		String[] command;
		System.out.println("Enter a command or enter \"help\" for list of commands");
		while (!exit) {
			keyboard = new Scanner(System.in);
			input = keyboard.nextLine();
			input = input.trim();
			command = input.split("\\s+");
			switch (command[0].toLowerCase()) {
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
					System.out.println("The IP address is " + getMyIp());
				break;
			case "myport":
				if (command.length > 1)
					System.out.println("Too many arguments");
				else
					System.out.println("The program runs on port number " + getMyPortNumber());
				break;
			case "connect":
				/*
				 * if (command.length == 1) printErrorMsg(
				 * "The destination is not specified"); else if (command.length
				 * == 2) printErrorMsg("The port number is not specified"); else
				 * if (command.length > 3) printErrorMsg("Too many arguments");
				 * else connect(command[1], command[2]);
				 */
				connect(command[1], command[2]);
				// connect("localhost", "1111");

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
				else if (command.length == 2)
					printErrorMsg("There is no message.");
				else
					for (int i = 3; i < command.length; i++)
						command[2] += " " + command[i];
				send(command[1], command[2]);
				break;
			case "exit":
				if (command.length > 1) {
					printErrorMsg("Too many arguments");
				} else {
					exit();
				}
				break;
			default:
				printErrorMsg("!!!!!");
				break;
			}
		}
	}

	/**
	 * Returns void. Implementation of help command will display the available
	 * commands for user which are represented the program's options.
	 */
	public void help() throws Exception {
		System.out.println("|****************************HELP MENU***************************|");
		System.out.println("| 1) help                                                        |");
		System.out.println("|\tDescription: Display the command options and             |"
				+ "\n|\t\t     their description.                          |");
		System.out.println("|----------------------------------------------------------------|");
		System.out.println("| 2) myip                                                        |");
		System.out.println("|\tDescription: Display the IP address.                     |");
		System.out.println("|----------------------------------------------------------------|");
		System.out.println("| 3) myport                                                      |");
		System.out.println("|\tDescription: Display listening port.                     |");
		System.out.println("|----------------------------------------------------------------|");
		System.out.println("| 4) connect                                                     |");
		System.out.println("|\tDescription: Establish connection with <destination IP>  |"
				+"\n|\t\t     using <port number>.                        |");
		System.out.println("|----------------------------------------------------------------|");
		System.out.println("| 5) list                                                        |");
		System.out.println("|\tDescription: Display list of connections.                |");
		System.out.println("|----------------------------------------------------------------|");
		System.out.println("| 6) terminate                                                   |");
		System.out.println("|\tDescription: End connection with IP address              |"
				+ "\n|\t\t     of <connection id>.                         |");
		System.out.println("|----------------------------------------------------------------|");
		System.out.println("| 7) send                                                        |");
		System.out.println("|\tDescription: Send <message> to IP address                |"
				+ "\n|\t\t     of <connection id>.                         |");
		System.out.println("|----------------------------------------------------------------|");
		System.out.println("| 8) exit                                                        |");
		System.out.println("|\tDescription: Exit program.                               |");
		System.out.println("|****************************************************************|");
	}

	public void printErrorMsg(String msg) {
		System.out.println(msg);
		System.out.println("Please enter again");
	}
}

/**
 * Helper class to hold information about the connections
 */
class Connection {
	private SocketChannel socketChannel;
	private String connectionIp;
	private int displayPort;
	private String type;

	public Connection(SocketChannel socketChannel, String connectionIp, int displayPort, String type) {
		super();
		this.socketChannel = socketChannel;
		this.connectionIp = connectionIp;
		this.displayPort = displayPort;
		this.type = type;
	}

	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	public void setSocketChannel(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	public String getConnectionIp() {
		return connectionIp;
	}

	public void setConnectionIp(String connectionIp) {
		this.connectionIp = connectionIp;
	}

	public int getDisplayPort() {
		return displayPort;
	}

	public void setDisplayPort(int displayPort) {
		this.displayPort = displayPort;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}