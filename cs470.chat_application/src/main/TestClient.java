package main;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TestClient implements Hello{
    private TestClient() {}
    public static void main(String[] args) {
        String host = (args.length < 1) ? null : args[0];
        try {

            Registry registry = LocateRegistry.getRegistry(host);
            Hello stub = (Hello) registry.lookup("Hello");

            String response = stub.sayHello();
           // String response2 = stub.display();

            System.out.println("response: " + response);
            //System.out.println("response2: " + response2);


        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
    public String sayHello() {
        return "Hello!";
    }
}