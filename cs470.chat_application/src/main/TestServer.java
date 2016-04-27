package main;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.*;
import java.net.*;

public class TestServer 
{
	public static void main(String args[]) throws Exception 
	{
		byte[] send_data = new byte[256];
	    byte[] receive_data = new byte[256];
	    int recv_port = 5000;

		
		// getting the user input and save it in a BufferedReader
		BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

		// creating a socket
		DatagramSocket client_socket = new DatagramSocket();
	   

		// Creating the destination IP address 
		InetAddress IPAddress = InetAddress.getByName("localhost");

		
		 DatagramSocket server_socket = new DatagramSocket( recv_port, IPAddress);
		// printing out a message showing that the client in ready
		System.out.println("Client - Initialized the client...");

		
		// Keeping the client working until the user enter q to quit
		while (true) 
		{
			
			// printing out the if the user wants to quit then enter q
			System.out.print("Client - Type Something (q or Q to quit): ");
		
			// reading user input
			String data = userInput.readLine();

			
			// if user entered 'q' then exit and the client socket will be closed
			if (data.equals("q") || data.equals("Q")) 
			{
				System.out.println("Client - Exited ! ");
				client_socket.close();
				break;
			} 
			
			
			// if not then 
			else
			{
				
				// send_data will save the data entered by the user from data readLine
				send_data = data.getBytes();
				

				
				// creating a sending packet and print it out
				DatagramPacket send_packet = new DatagramPacket(send_data, send_data.length, IPAddress, 5000);
				System.out.println("Client - Sending data : <" + data + ">" + IPAddress);
				client_socket.send(send_packet);
				
				
				// creating a receive packet and print it out
		        DatagramPacket receive_packet = new DatagramPacket(receive_data, receive_data.length);
		        server_socket.receive(receive_packet);
		        String data_recevied = new String(receive_packet.getData());
		        InetAddress IPAddressHost = receive_packet.getAddress();
		        recv_port = receive_packet.getPort();

	            System.out.println("Server - Client from IP " + IPAddressHost + " @ port " + recv_port + " said : " + data_recevied + " (length: " + receive_packet.getLength() + ")");

			}
			
			
		} // end while loop 

	} // end main
	
}// end class