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


http://rox-xmlrpc.sourceforge.net/niotut/#The server



public class Test2 {

	private int myPortNumber = 40001;
	private String ipAddress;
	private volatile boolean exit = false;
	List<SocketChannel> socketChannelList = new ArrayList<SocketChannel>();
	List<String> ipList = new ArrayList<String>();
	List<Integer> portList = new ArrayList<Integer>();
	Selector socketSelector;
	ServerSocketChannel serverSocketChannel;
	SocketChannel socketChannel;
	private ByteBuffer readBuffer = ByteBuffer.allocate(9000);

	public static void main(String[] args) throws Exception {
		Test2 test = new Test2(); 
		test.initiate();
		test.serverRunner();
		test.takeInput();
	}

	public void initiate() throws IOException {
		socketSelector = SelectorProvider.provider().openSelector();
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);

		serverSocketChannel.register(socketSelector, SelectionKey.OP_ACCEPT);

		serverSocketChannel.socket().bind(new InetSocketAddress(myPortNumber));
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
		boolean conExists;
		try {
			while (!exit) {
				conExists = false;
				try {
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
						// Check what event is available and deal with it
						if (key.isAcceptable()) {
							this.accept(key);
						} else if (key.isReadable()) {
							this.read(key);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
		}
	}

	private void accept(SelectionKey key) throws IOException {
		// For an accept to be pending the channel must be a server socket
		// channel.
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

		// Accept the connection and make it non-blocking
		SocketChannel socketChannel = serverSocketChannel.accept();
		Socket socket = socketChannel.socket();
		socketChannel.configureBlocking(false);

		// Register the new SocketChannel with our Selector, indicating
		// we'd like to be notified when there's data waiting to be read
		socketChannel.register(socketSelector, SelectionKey.OP_READ);
	}
	
	private void read(SelectionKey key) throws IOException {
	    SocketChannel socketChannel = (SocketChannel) key.channel();

	    // Clear out our read buffer so it's ready for new data
	    readBuffer.clear();
	    
	    // Attempt to read off the channel
	    int numRead;
	    try {
	      numRead = socketChannel.read(readBuffer);
	    } catch (IOException e) {
	      // The remote forcibly closed the connection, cancel
	      // the selection key and close the channel.
	      key.cancel();
	      socketChannel.close();
	      return;
	    }

	    if (numRead == -1) {
	      // Remote entity shut the socket down cleanly. Do the
	      // same from our end and cancel the channel.
	      key.channel().close();
	      key.cancel();
	      return;
	    }
	    
	    byte[] data = new byte[numRead];
		System.arraycopy(readBuffer.array(), 0, data, 0, numRead);
		
		System.out.println(new String(data));

	    // Hand the data off to our worker thread
	//    this.worker.processData(this, socketChannel, this.readBuffer.array(), numRead); 
	  }
	
	
	
	
	
	
	
	
	
	

	public void connect(String destIp, String p) throws Exception {
		SocketChannel socketChannel = null;
		PrintStream ps = null;
		int timeout = 1000;
		boolean conExists = false;
		try {
			int port = Integer.parseInt(p);
			socketChannel = SocketChannel.open();

			/*
			 * if (destIp.equals(myip()) ||
			 * destIp.toLowerCase().equals("localhost") ||
			 * destIp.equals("127.0.0.1")) { System.out.println(
			 * "The connection request is from the same computer"); conExists =
			 * true; } else { for (int i = 0; i < socketChannelList.size(); i++)
			 * { if (destIp.equals(ipList.get(i))) { System.out.println(
			 * "The connection already exists"); conExists = true; } } }
			 */
			// socketChannel.socket().setSoTimeout(timeout);
			// limiting the time to establish a connection
			if (!conExists)
				socketChannel.socket().connect(new InetSocketAddress(destIp, port), timeout);

			byte[] message = new String("client").getBytes();
			ByteBuffer buffer = ByteBuffer.wrap(message);
			socketChannel.write(buffer);
			buffer.clear();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("something wrong");
		} finally {

		}
	}

	public void send(String conId, String msg) throws IOException {

		int id = Integer.parseInt(conId);

		byte[] message = new String(msg).getBytes();
		ByteBuffer buffer = ByteBuffer.wrap(message);

		socketChannelList.get(id).write(buffer);

		System.out.println(socketChannelList.get(id).isConnected());

		buffer.clear();

	}

	// print the ip address
	public String myip() throws UnknownHostException, SocketException {
		String myIp = Inet4Address.getLocalHost().getHostAddress();
		System.out.println("The IP address is " + myIp);
		// this is the fake ip will be commented
		/*
		 * System.out.println("the fake " +
		 * NetworkInterface.getNetworkInterfaces().nextElement().
		 * getInetAddresses() .nextElement().getHostAddress()); // returns
		 * "127.0.0.1"
		 */ return myIp;
	}

	public void list() throws IOException {
		for (int i = 0; i < socketChannelList.size(); i++)
			System.out.println((i + 1) + " " + socketChannelList.get(i).getRemoteAddress() + " " + portList.get(i));
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
				// connect(command[1], command[2]);
				connect("192.168.1.67", "40001");
				connect("localhost", "40001");
				connect("127.0.0.1", "40001");
				break;
			case "exit":
				exit = true;
				// socket.close();
				break;
			case "list":
				list();
				break;
			case "send":
				send(command[1], command[2]);
				break;
			case "myip":
				myip();
				break;
			}
		}
	}

}
