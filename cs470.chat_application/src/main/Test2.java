package main;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class Test2 {

	private int myPortNumber = 50002;
	private String ipAddress;
	private volatile boolean exit = false;
	int i = 0;
	public List<SocketChannel> socketChannelList = new ArrayList<SocketChannel>();
	public List<String> portList = new ArrayList<String>();
	ServerSocket srvSocket;
	Socket socket;
	  public static final String GREETING = "Hello I must be going.\r\n";
	public static void main(String[] args) throws Exception {

		
		int port = 40001; // default
	    ByteBuffer buffer = ByteBuffer.wrap(GREETING.getBytes());
	    ServerSocketChannel ssc = ServerSocketChannel.open();
	    ssc.socket().bind(new InetSocketAddress(port));
	    ssc.configureBlocking(false);
	    while (true) {
	      System.out.println("Waiting for connections");
	      SocketChannel sc = ssc.accept();
	      if (sc == null) {
	        Thread.sleep(2000);
	      } else {
	        System.out.println("Incoming connection from: " + sc.socket().getRemoteSocketAddress());
	        buffer.rewind();
	        sc.write(buffer);
	        sc.close();
	      }
	    }

		
		
		
/*		Test2 test = new Test2();
		// test.serverRunner();
		test.newS();
		test.takeInput();*/

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
				connect(command[1], command[2]);
				break;
			case "exit":
				exit = true;
				// socket.close();
				srvSocket.close();
				break;
			case "list":
				list();
				break;
			case "send":
				send(command[1], command[2]);
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

	public void newS() throws IOException {
		while (true) {
			ServerSocketChannel ssChannel = ServerSocketChannel.open();
			ssChannel.configureBlocking(false);
			int port = 40001;
			ssChannel.socket().bind(new InetSocketAddress(port));
			int localPort = ssChannel.socket().getLocalPort();
			SocketChannel sChannel = ssChannel.accept();
			if (sChannel == null) {
			} else {
			}
		}
	}

	public void server() throws Exception {
		ServerSocketChannel serverSocketChannel = null;
		SocketChannel socketChannel = null;
		try {
			// while (!exit) {
			boolean conExists = false;
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.socket().bind(new InetSocketAddress(40001));
			// this is handshake with client
			socketChannel = serverSocketChannel.accept();

			for (SocketChannel i : socketChannelList)
				if (i.getRemoteAddress().equals(socketChannel.getRemoteAddress())) {
					System.out.println("The connection exists");
					conExists = true;
				}

			if (socketChannel != null)
				if (myip().equals(socketChannel.getRemoteAddress())) {
					conExists = true;
					System.out.println("The connection from the same computer");
				}

			if (!conExists)
				socketChannelList.add(socketChannel);

			// }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// if (socketChannel != null && !socketChannel.isClosed())
			// socketChannel.close();
		}
	}

	public void send(String conId, String msg) throws IOException {
		System.out.println("send");

		int id = Integer.parseInt(conId);
		// PrintStream printStream = new
		// PrintStream(socketChannelList.get(id).getOutputStream());
		// printStream.println(msg);

	}

	// print the ip address
	public String myip() throws UnknownHostException, SocketException {
		String myIp = Inet4Address.getLocalHost().getHostAddress();
		System.out.println("The IP address is " + myIp);
		// this is the fake ip will be commented
		System.out.println("the fake " + NetworkInterface.getNetworkInterfaces().nextElement().getInetAddresses()
				.nextElement().getHostAddress()); // returns "127.0.0.1"
		return myIp;
	}

	public void list() throws IOException {
		for (int i = 0; i < socketChannelList.size(); i++)
			System.out.println((i + 1) + " " + socketChannelList.get(i).getRemoteAddress() + " " + portList.get(i));
	}

	public void connect(String ip, String port) throws Exception {
		SocketChannel socketChannel = null;
		PrintStream ps = null;
		int timeout = 200;
		try {
			// int p = Integer.parseInt(port);
			// socket = new Socket(ip, p );

			socketChannel = SocketChannel.open();

			// limiting the time to establish a connection
			socketChannel.connect(new InetSocketAddress(ip, Integer.parseInt(port)));

			if (socketChannel.isConnected())
				socketChannelList.add(socketChannel);

			// stop the request after connection succeeds
			// socket.setSoTimeout(timeout);
			/*
			 * ByteBuffer buf = ByteBuffer.allocate(48);
			 * 
			 * int bytesRead = socketChannel.read(buf);
			 * 
			 * ps = new PrintStream(socketChannel.getOutputStream());
			 * 
			 * // manual connection verification ps.println("client");
			 * 
			 * InputStreamReader isr = new
			 * InputStreamReader(socket.getInputStream()); BufferedReader br =
			 * new BufferedReader(isr);
			 * 
			 * String msg = br.readLine(); if (msg != null &&
			 * msg.equals("server")) { System.out.println(
			 * "The connection to peer " + ip + " is successfully established;"
			 * ); socketChannelList.add(socket); portList.add(port); }
			 */
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("something wrong");
		} finally {
			if (socket != null && !socket.isClosed())
				socket.close();
		}
	}
}
