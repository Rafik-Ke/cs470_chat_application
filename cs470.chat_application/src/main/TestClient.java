package main;

import java.io.*;
import java.net.*;

public class TestClient {
	int i = 0;

	public static void main(String[] args) throws Exception {

		TestClient ca = new TestClient();
		while (true)
			ca.client();
	}

	@SuppressWarnings("resource")
	public void client() throws Exception {
		Socket socket = new Socket("192.168.1.143", 50001);
		PrintStream ps = new PrintStream(socket.getOutputStream());
		ps.println("hello to server from client , message : " + ++i);

		InputStreamReader instrReader = new InputStreamReader(socket.getInputStream());
		BufferedReader br = new BufferedReader(instrReader);

		String msg = br.readLine();
		System.out.println(msg);
	}
}