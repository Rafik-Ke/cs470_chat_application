package main;

import java.io.*;
import java.net.*;

public class TestClient {
	int i = 0;

	public static void main(String[] args) throws Exception {

		TestClient tc = new TestClient();

		tc.client();
	}

	@SuppressWarnings("resource")
	public void client() throws Exception {
		Socket socket  = null;
		OutputStream os;
		PrintWriter pw;
		socket = new Socket("localhost", 50001);
		os = socket.getOutputStream();
		while (i < 100){
			
			
			
			os.write(i++);
			pw = new PrintWriter(os);
			pw.println("hello to server from client , message : " + ++i);
		}
		
		InputStreamReader instrReader = new InputStreamReader(socket.getInputStream());
		BufferedReader br = new BufferedReader(instrReader);

		String msg = br.readLine();
		System.out.println(msg);
	}
}
