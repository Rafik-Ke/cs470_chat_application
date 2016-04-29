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
	public List<String> ipList = new ArrayList<String>();
	public List<Integer> portList = new ArrayList<Integer>();
	ServerSocket srvSocket;
	Socket socket;

	public static void main(String[] args) throws Exception {
		Test2 test = new Test2(); //
		test.serverRunner();// test.newS();
		test.takeInput();
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
			case "myip":
				myip();
				break;
			}
		}
	}

	public void serverRunner() {
		Thread t = new Thread() {
			public void run() {
				try {
					server();
					// newS();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
	}

	public void newS() throws IOException {

		ServerSocketChannel ssChannel = ServerSocketChannel.open();
		ssChannel.configureBlocking(false);
		int port = 40001;
		ssChannel.socket().bind(new InetSocketAddress(port));
		int localPort = ssChannel.socket().getLocalPort();
		while (!exit) {
			SocketChannel sChannel = ssChannel.accept();
			if (sChannel == null) {
			} else {
			}
		}
	}

	public void server() throws Exception {
		ServerSocketChannel serverSocketChannel = null;
		SocketChannel socketChannel = null;
		boolean conExists;
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.socket().bind(new InetSocketAddress(40001));
			while (!exit) {
				conExists = false;
				// this is handshake with client
				socketChannel = serverSocketChannel.accept();

				if (socketChannel != null) {
					// these removes extras
					String localAddress = socketChannel.getLocalAddress().toString().split(":")[0];
					localAddress = localAddress.replaceAll("[/:]", "");
					String remoteAddress = socketChannel.getRemoteAddress().toString().split(":")[0];
					remoteAddress = remoteAddress.replaceAll("[/:]", "");

					if (myip().equals(remoteAddress) || localAddress.equals(remoteAddress)) {
						System.out.println("The connection request is from the same computer");
						conExists = true;
					} else {
						for (int i = 0; i < socketChannelList.size(); i++) {
							if (ipList.get(i).equals(remoteAddress)) {
								System.out.println("The connection already exists");
								conExists = true;
							}
						}
					}

					if (!conExists) {
						socketChannelList.add(socketChannel);
						ipList.add(remoteAddress);
						portList.add(myPortNumber);
					}
				}
			}
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

	public void connect(String destIp, String p) throws Exception {
		SocketChannel socketChannel = null;
		PrintStream ps = null;
		int timeout = 200;
		boolean conExists = false;
		try {
			int port = Integer.parseInt(p);
			socketChannel = SocketChannel.open();

			if (destIp.equals(myip()) || destIp.toLowerCase().equals("localhost") || destIp.equals("127.0.0.1")) {
				System.out.println("The connection request is from the same computer");
				conExists = true;
			} else {
				for (int i = 0; i < socketChannelList.size(); i++) {
					if (destIp.equals(ipList.get(i))) {
						System.out.println("The connection already exists");
						conExists = true;
					}
				}
			}

			// limiting the time to establish a connection
			if (!conExists)
				socketChannel.connect(new InetSocketAddress(destIp, port));


		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("something wrong");
		} finally {
			if (socket != null && !socket.isClosed())
				socket.close();
		}
	}
}
