package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Test {

	private int portNumber = 50001;
	private String ipAddress;
	private volatile boolean exit = false;
	int i = 0;
	public List<Socket> socketList = new ArrayList<Socket>();
	public List<String> CurrentUsers = new ArrayList<String>();
	ServerSocket srvSocket;
	Socket socket;

	public static void main(String[] args) throws Exception {

		Test test = new Test();

		test.serverRunner();
		System.out.println(test.i);

		test.takeInput();

		System.out.println(test.i);

		System.out.println(test.exit);

	}

	public void takeInput() throws IOException {

		Scanner keyboard;
		String input;
		System.out.println("enter something");
		while (!exit) {
			keyboard = new Scanner(System.in);
			input = keyboard.nextLine();
			if (input.equals("exit")) {
				exit = true;
				// socket.close();
				srvSocket.close();
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

	@SuppressWarnings("resource")
	public void server() throws Exception {
		// tcp socket
		srvSocket = new ServerSocket(50001);
		socket = null;
		try {
			while (!exit) {
				socket = srvSocket.accept();
				socketList.add(socket);
				System.out.println("New connection from: " + socket.getLocalAddress().getHostName());

				InputStreamReader instrReader = new InputStreamReader(socket.getInputStream());
				BufferedReader br = new BufferedReader(instrReader);

				String msg = br.readLine();
				System.out.println(msg + " printed in server");
				if (msg != null) {
					PrintStream prntStream = new PrintStream(socket.getOutputStream());
					prntStream.println("msg reseived");
				}
			}
		} catch (Exception e) {

		} finally {
			if (socket != null && !socket.isClosed())
				socket.close();
		}
	}

}
