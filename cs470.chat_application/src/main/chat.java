package main;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;

public class chat {
	private int myPortNumber = 50001;
	private String myIp;
	private boolean exit = false;
	ServerSocketChannel serverSocketChannel;
	List<SocketChannel> socketChannelList = new ArrayList<SocketChannel>();
	List<String> ipList = new ArrayList<String>();
	List<Integer> portList = new ArrayList<Integer>();
	Selector socketSelector;

	// SocketChannel socketChannel;
	private ByteBuffer readBuffer = ByteBuffer.allocate(9000);

	public static void main(String[] args) throws Exception {
		chat chatApp = new chat();
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
		socketSelector = SelectorProvider.provider().openSelector();
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
		try {
			while (!exit) {
				// conExists = false;
				try {
					terminated = false;

				
						// Wait for an event one of the registered channels
						socketSelector.select();

						// Iterate over the set of keys for which events are
						// available
						Iterator<SelectionKey> selectedKeys = socketSelector.selectedKeys().iterator();
						
						for (int i = 0; i < socketChannelList.size(); i++)
							if (!socketChannelList.get(i).isOpen()) {
								socketChannelList.get(i).close();
								socketChannelList.remove(i);
							
								selectedKeys.remove();
								selectedKeys = null;
							}
						
						while (selectedKeys.hasNext()) {
							SelectionKey key = (SelectionKey) selectedKeys.next();
							selectedKeys.remove();
							if (!key.isValid()) {
								continue;
							}
							// check the request is a new connection or reading
							// from
							// a connection
							// new connection request
							if (key.isAcceptable()) {
								this.accept(key);
								// connection already exists reading message
							} else if (key.isReadable()) {
								this.read(key);
								// handles the terminated socket
							} else if (key.isConnectable())
								System.out.println("socket closed");
					}
				} catch (Exception e) {
					System.out.println("oops");
					e.printStackTrace();
					break;
				}
			}
		} catch (Exception e) {
			System.out.println("oops 2");
		}
	}

	// creates a new connection by using the selector key
	private void accept(SelectionKey key) {
		try {
			ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
			SocketChannel socketChannel = serverSocketChannel.accept();
			// Socket socket = socketChannel.socket();
			socketChannel.configureBlocking(false);

			// Register the new SocketChannel in the selector for waiting for
			// the client
			socketChannel.register(socketSelector, SelectionKey.OP_READ);

			System.out.println("New connection from: " + getRemoteIP(socketChannel));

			socketChannelList.add(socketChannel);
			portList.add(myPortNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// reading the message using the key of the socketchannel
	private void read(SelectionKey key) throws IOException {

		SocketChannel socketChannel = (SocketChannel) key.channel();
		readBuffer.clear();

		int numRead;
		try {
			numRead = socketChannel.read(readBuffer);
			byte[] data = new byte[numRead];
			System.arraycopy(readBuffer.array(), 0, data, 0, numRead);
			System.out.println("Message received from " + getRemoteIP(socketChannel) + ": " + new String(data));

			// client closed the socketchannel, close in the server side
		} catch (IOException e) {
			key.cancel();
			socketChannel.close();
			return;
		}

		// no message is sent discard the key
		if (numRead == -1) {
			key.channel().close();
			key.cancel();
			return;
		}
	}

	// print the ip address
	public String myip() throws UnknownHostException, SocketException {
		System.out.println("The IP address is " + getMyIp());
		// this is the fake ip will be commented
		System.out.println("the fake " + NetworkInterface.getNetworkInterfaces().nextElement().getInetAddresses()
				.nextElement().getHostAddress()); // returns "127.0.0.1"
		return myIp;
	}

	public void connect(String destIp, String p) throws Exception {
		SocketChannel socketChannel = null;
		InetSocketAddress isa = null;
		PrintStream ps = null;
		int timeout = 20000;
		boolean conExists = false;
		try {
			int port = Integer.parseInt(p);
			socketChannel = SocketChannel.open();

			/*
			 * if (destIp.equals(getMyIp()) ||
			 * destIp.toLowerCase().equals("localhost") ||
			 * destIp.equals("127.0.0.1")) { System.out.println(
			 * "The connection request is from the same computer"); conExists =
			 * true; } else { for (int i = 0; i < socketChannelList.size(); i++)
			 * { if (destIp.equals(ipList.get(i)) && portNumber.equals(..)) {
			 * System.out.println( "The connection already exists"); conExists =
			 * true; } } }
			 */

			if (!conExists) {
				// limiting the time to establish a connection
				socketChannel.socket().setSoTimeout(timeout);

				isa = new InetSocketAddress(destIp, port);

				// socketChannel.socket().connect(isa, timeout);
				socketChannel.connect(isa);
				socketChannel.configureBlocking(false);
				System.out.println("The connection to peer " + destIp + " is successfully established;");
				socketChannelList.add(socketChannel);
				portList.add(port);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("something is wrong");
		} finally {

		}
	}

	public void send(String conId, String msg) throws IOException {

		int id = Integer.parseInt(conId);

		byte[] message = new String(msg).getBytes();
		ByteBuffer buffer = ByteBuffer.wrap(message);

		if (socketChannelList.get(id).isConnected())
			socketChannelList.get(id).write(buffer);

		// System.out.println(socketChannelList.get(id).isConnected());

		buffer.clear();
	}

	public void list() throws IOException {
		for (int i = 0; i < socketChannelList.size(); i++) {
			System.out.println((i + 1) + " " + getRemoteIP(socketChannelList.get(i)) + " " + portList.get(i));
		}
	}

	public void terminate(String conId) {
		System.out.println("terminate");
		try {
			int id = Integer.parseInt(conId);
			socketChannelList.get(id).close();
			socketChannelList.remove(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void exit() throws IOException {
		// close the connection
		serverSocketChannel.close();
		this.exit = true;
		System.out.println("exit");
	}

	public void setMyPortNumber(int port) {
		this.myPortNumber = port;
	}

	public int getMyPortNumber() {
		return this.myPortNumber;
	}

	public String getMyIp() throws UnknownHostException {
		return this.myIp;
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
					myip();
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
				 * else
				 */
				connect(command[1], command[2]);
				// connect("localhost", ""+myPortNumber);
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
