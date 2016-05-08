package main;

import java.io.*;
import java.net.*;
import java.util.*;

public class Test {

	private int portNumber = 50001;
	private String ipAddress;
	private volatile boolean exit = false;
	int i = 0;
	public List<Socket> socketList = new ArrayList<Socket>();
	public List<String> portList = new ArrayList<String>();
	ServerSocket srvSocket;
	Socket socket;

	public static void main(String[] args) throws Exception {

		Test test = new Test();

	//	test.serverRunner();
		
		test.connect("172.20.10.7", "40001");

	//	test.takeInput();

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

	public void server() throws Exception {
		// tcp socket
		srvSocket = new ServerSocket(50001);
		socket = null;
		try {
			while (!exit) {
				boolean conExists = false;
				socket = srvSocket.accept();
				
				InputStreamReader instrReader = new InputStreamReader(socket.getInputStream());
				BufferedReader br = new BufferedReader(instrReader);

				String msg = br.readLine();

				for (Socket i : socketList)
					if (i.getRemoteSocketAddress().equals(socket.getRemoteSocketAddress())) {
						System.out.println("The connection exists");
						conExists = true;
					}
				if (myip().equals(socket.getRemoteSocketAddress())){
					conExists = true;
					System.out.println("The connection from the same computer");
				}
				if (msg != null && msg.equals("client") && !conExists) {
					socketList.add(socket);
					portList.add("" + 50001);
					System.out.println("New connection from: " + socket.getRemoteSocketAddress());
					PrintStream printStream = new PrintStream(socket.getOutputStream());
					printStream.println("server");
				}
			}
		} catch (Exception e) {

		} finally {
			if (socket != null && !socket.isClosed())
				socket.close();
		}
	}
	
	public void send(String conId, String msg) throws IOException {
		System.out.println("send");
		
		int id = Integer.parseInt(conId);
		PrintStream printStream = new PrintStream(socketList.get(id).getOutputStream());
		printStream.println(msg);
		
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

	public void list() {
		for (int i = 0; i < socketList.size(); i++)
			System.out.println((i + 1) + " " + socketList.get(i).getInetAddress() + " " + portList.get(i));
	}

	public void connect(String ip, String port) throws Exception {
		Socket socket = null;
		PrintStream ps = null;
		int timeout = 1000;
		try {
			// int p = Integer.parseInt(port);
			// socket = new Socket(ip, p );

			socket = new Socket();

			// limiting the time to establish a connection
			socket.connect(new InetSocketAddress(ip, Integer.parseInt(port)), timeout);

			// stop the request after connection succeeds
			socket.setSoTimeout(timeout);

			ps = new PrintStream(socket.getOutputStream());

			// manual connection verification
			ps.println("client");

			InputStreamReader isr = new InputStreamReader(socket.getInputStream());
			BufferedReader br = new BufferedReader(isr);

			String msg = br.readLine();
			if (msg != null && msg.equals("server")) {
				System.out.println("The connection to peer " + ip + " is successfully established;");
				socketList.add(socket);
				portList.add(port);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("something wrong");
		} finally {
			if (socket != null && !socket.isClosed())
				socket.close();
		}
	}
}
