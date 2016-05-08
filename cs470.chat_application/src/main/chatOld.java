package main;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class chatOld {
	private final int CON_REQUEST = 1, CON_RESPONSE = 2;
	private int myPortNumber = 4444;
	private String myIp;
	private boolean exit = false;
	private List<Connection> connections = new ArrayList<Connection>();
	private ServerSocketChannel serverSocketChannel;
	private Selector socketSelector;
	private ByteBuffer readBuffer;

	public static void main(String[] args) throws Exception {
		chatOld chatApp = new chatOld();
		try {
			// chatApp.setMyPortNumber(Integer.parseInt(args[0]));
			chatApp.serverRunner();
			chatApp.takeInput();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Please run the program with this format:java chat <port number>");
		}
	}

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

	public void server() throws Exception {
		// boolean conExists; //no need because client will not send connect
		boolean terminated;
		while (!exit) {
			// conExists = false;
			try {
				terminated = false;
				// Wait for an event one of the registered channels
				socketSelector.select();
				// Iterate over the set of keys for which events are
				// available
				Iterator<SelectionKey> selectedKeys = socketSelector.selectedKeys().iterator();

				while (selectedKeys.hasNext()) {
					SelectionKey key = (SelectionKey) selectedKeys.next();
					selectedKeys.remove();
					if (!key.isValid()) {
						continue;
					}
					// check the request is a new connection or reading
					// from a connection new connection request
					if (key.isAcceptable()) {
						this.accept(key);
						// connection already exists reading message
					} else if (key.isReadable()) {
						this.read(key);

					} else if (key.isConnectable()) {
						System.out.println("socket closed");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}

	// creates a new connection by using the selector key
	private void accept(SelectionKey key) {
		try {
			ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
			SocketChannel socketChannel = serverSocketChannel.accept();
			// Socket socket = socketChannel.socket();
			socketChannel.configureBlocking(false);

			// Register SocketChannel in the selector and wait for client
			socketChannel.register(socketSelector, SelectionKey.OP_READ);
			String rip = getRemoteIP(socketChannel);
			int listCrntSize = connections.size();
			if (listCrntSize > 0) {
				/*
				 * if they are already connected by the other client shouldn't
				 * display message for new connection
				 */
				if (!connections.get(connections.size() - 1).getConnectionIp().equals(rip))
					System.out.println("New connection from: " + rip);
			} else
				System.out.println("New connection from: " + rip);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// reading the message using the key of the socketchannel
	private void read(SelectionKey key) throws IOException {

		readBuffer = ByteBuffer.allocate(9000);

		SocketChannel socketChannel = (SocketChannel) key.channel();
		String remoteIp = getRemoteIP(socketChannel);
		int numRead = -1;

		try {
			readBuffer.clear();
			numRead = socketChannel.read(readBuffer);
			byte[] data = new byte[numRead];

			System.arraycopy(readBuffer.array(), 0, data, 0, numRead);
			String message = new String(data);

			String trmIp = message.split(" ")[0];
			if (message.contains("terminatess")) {
				System.out.println("Peer " + trmIp + " terminates the connection");

				System.out.println(message + " real port -->" + connections.get(0).getRealPort());

				System.out.println(connections.get(0).getConnectionIp() + " trmIp" + trmIp);
				System.out.println(
						connections.get(0).getRealPort() + " -->port  " + Integer.parseInt(message.split(" ")[2]));

				for (int i = 0; i < connections.size(); i++) {
					// if (connections.get(i).getDisplayPort() ==
					// Integer.parseInt(message.split(" ")[2])){
					// if(connections.get(i).getConnectionIp().equals(remoteIp)
					// || connections.get(i).getConnectionIp().equals(trmIp)){

					System.out.println(" it is terminated ");
					connections.get(i).getSocketChannel().close();
					connections.remove(i);
					key.channel().close();
					key.cancel();
					socketChannel.close();
					return;
					// }
				}
				// send an automatic connection from server to client
				if (message.contains("connectToPort")) {
					String remotePort = message.split(" ")[1];
					connect(getRemoteIP(socketChannel), remotePort, CON_RESPONSE);
					return;
				}
			}
			System.out.println("Message received from " + getRemoteIP(socketChannel) + ": " + message);
		} catch (Exception e) {
			System.out.println(e.getMessage());

			e.printStackTrace();

			System.out.println("Peer " + remoteIp + " terminates the connection");

			key.channel().close();
			key.cancel();
			socketChannel.close();
			for (int i = 0; i < connections.size(); i++)
				if (connections.get(i).getConnectionIp().equals(remoteIp))// ==
																			// Integer.parseInt(message.split("
																			// ")[2]))
					connections.remove(i);
			return;
		}
	}

	public void send(String conId, String msg) throws IOException {
		int id = Integer.parseInt(conId) - 1;
		byte[] message = new String(msg).getBytes();
		ByteBuffer buffer = ByteBuffer.wrap(message);
		if (connections.get(id).getSocketChannel().isConnected()) {
			connections.get(id).getSocketChannel().write(buffer);
		}
		buffer.clear();
	}

	public void connect(String destIp, String dstPrt, int src) throws Exception {
		SocketChannel socketChannel = null;
		InetSocketAddress isa = null;
		int timeout = 20000;
		boolean conExists = false;
		try {
			int destPort = Integer.parseInt(dstPrt);
			socketChannel = SocketChannel.open();
			/*
			 * if (destIp.equals(getMyIp()) ||
			 * destIp.toLowerCase().equals("localhost") ||
			 * destIp.equals("127.0.0.1")) { System.out.println(
			 * "The connection request is from the same computer"); conExists =
			 * true; } else { for (int i = 0; i < connections.size(); i++) { if
			 * (destIp.equals(connections.get(i).getConnectionIp())) {
			 * System.out.println("The connection already exists"); conExists =
			 * true; } } }
			 */
			socketChannel.socket().setSoTimeout(timeout);
			if (!conExists && src == CON_REQUEST) {
				isa = new InetSocketAddress(destIp, destPort);

				// socketChannel.socket().connect(isa, timeout);
				socketChannel.connect(isa);
				socketChannel.configureBlocking(false);

				System.out.println("The connection to peer " + destIp + " is successfully established;");

				Connection con = new Connection(socketChannel, destIp, destPort, destPort);

				connections.add(con);

				// it first makes a connection with the other server and asks
				// the other computer to connect to its server by the other
				// computer client
				send("" + connections.size(), "connectToPort " + getMyPortNumber());

				return;
			}
			// this is a request from the same machine's server making an
			// auto-connection to the client machine
			if (src == CON_RESPONSE) {
				isa = new InetSocketAddress(destIp, destPort);

				// socketChannel.socket().connect(isa, timeout);
				socketChannel.connect(isa);
				socketChannel.configureBlocking(false);
				// myPortNumber is only for display it is not a real port number
				Connection con = new Connection(socketChannel, destIp, myPortNumber, destPort);

				connections.add(con);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("something is wrong");
		} finally {

		}
	}

	public void list() throws IOException {
		System.out.println("id: IP address               Port No.");
		for (int i = 0; i < connections.size(); i++) {
			if (connections.get(i).getSocketChannel().isConnected() && connections.get(i).getSocketChannel().isOpen())
				System.out.println((i + 1) + " " + connections.get(i).getConnectionIp() + " "
						+ connections.get(i).getDisplayPort());
		}
	}

	public void terminate(String conId) {
		try {
			int id = Integer.parseInt(conId) - 1;
			// if(connections.get(id).getRealPort() ==
			// connections.get(id).getDisplayPort())
			send(conId, getMyIp() + " terminatess " + connections.get(id).getRealPort());
			connections.get(id).getSocketChannel().close();
			connections.remove(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void exit() throws IOException {

		this.exit = true;
		// terminate all the connections
		for (int i = 0; i < connections.size(); i++) {
			terminate("" + i);
			connections.get(i).getSocketChannel().close();
		}
		serverSocketChannel.close();
	}

	public int getMyPortNumber() {
		return this.myPortNumber;
	}

	public void setMyPortNumber(int port) {
		this.myPortNumber = port;
	}

	public String getMyIp() throws UnknownHostException {
		return this.myIp = Inet4Address.getLocalHost().getHostAddress();
	}

	public void setMyIp() throws UnknownHostException {
		this.myIp = Inet4Address.getLocalHost().getHostAddress();
	}

	public String getRemoteIP(SocketChannel sc) throws IOException {
		return sc.getRemoteAddress().toString().replace("/", "").split(":")[0];
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
				 * else connect(command[1], command[2], CON_REQUEST);
				 */

				connect("localhost", "1111", CON_REQUEST);

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





/*
class Connection {
	private SocketChannel socketChannel;
	private String connectionIp;
	private int displayPort;
	private int realPort;

	public Connection(SocketChannel socketChannel, String connectionIp, int desplayPort, int realPort) {
		super();
		this.socketChannel = socketChannel;
		this.connectionIp = connectionIp;
		this.displayPort = desplayPort;
		this.realPort = realPort;
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

	public void setDisplayPort(int desplayPort) {
		this.displayPort = desplayPort;
	}

	public int getRealPort() {
		return realPort;
	}

	public void setRealPort(int realPort) {
		this.realPort = realPort;
	}
}*/