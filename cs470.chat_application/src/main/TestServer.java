package main;

import java.io.*;
import java.net.*;

public class TestServer {

	public static void main(String[] args) throws Exception {

		TestServer ca = new TestServer();

		ca.server();
	}

	@SuppressWarnings("resource")
	public void server() throws Exception {
		ServerSocket srvSocket = new ServerSocket(50001);
		Socket socket = null;
		while (true) {
			socket = srvSocket.accept();
			InputStreamReader instrReader = new InputStreamReader(socket.getInputStream());
			BufferedReader br = new BufferedReader(instrReader);

			String msg = br.readLine();
			System.out.println(msg + " printed in server");
			if (msg != null) {
				PrintStream prntStream = new PrintStream(socket.getOutputStream());
				prntStream.println("msg reseived");
			}
		}
	}

}
