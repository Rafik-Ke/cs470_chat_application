package main;

public class help {

	public static void main(String[] args) {
		System.out.println(
				"|*******************************************HELP MENU*****************************************|");
		System.out.printf("%s%5s%80s\n","| 1)", "help","|");
		System.out.printf("|\tDescription: Display the command options and their description.               |");
		System.out.println(
				"|---------------------------------------------------------------------------------------------|");
		System.out.println(
				"| 2) myip                                                                                     |");
		System.out.println("|\tDescription: Display the IP address.                                          |");
		System.out.println(
				"|---------------------------------------------------------------------------------------------|");
		System.out.println(
				"| 3) myport                                                                                   |");
		System.out.println("|\t\tDescription: Display listening port.                                          |");
		System.out.println(
				"|---------------------------------------------------------------------------------------------|");
		System.out.println(
				"| 4) connect                                                                                  |");
		System.out.println("|\t\tDescription: Establish connection with <destination IP> using <port number>.  |");
		System.out.println(
				"|---------------------------------------------------------------------------------------------|");
		System.out.println(
				"| 5) list                                                                                     |");
		System.out.println("|\t\tDescription: Display list of connections.                                     |");
		System.out.println(
				"|---------------------------------------------------------------------------------------------|");
		System.out.println(
				"| 6) terminate                                                                                |");
		System.out.println("|\t\tDescription: End connection with IP address of <connection id>.               |");
		System.out.println(
				"|---------------------------------------------------------------------------------------------|");
		System.out.println(
				"| 7) send                                                                                     |");
		System.out.println("|\t\tDescription: Send <message> to IP address of <connection id>.                 |");
		System.out.println(
				"|---------------------------------------------------------------------------------------------|");
		System.out.println(
				"| 8) exit                                                                                     |");
		System.out.println("|\t\tDescription: Exit program.                                                    |");
		System.out.println(
				"|*********************************************************************************************|");
	

	}

}
