package place.server;

import place.PlaceException;
import place.network.PlaceExchange;
import place.network.PlaceRequest;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PlaceServer {
    private static final Set<String> users = new HashSet<>(Arrays.asList("peter", "owen", "test1", "test2"));

    public static void main(String[] args)  {
        if(args.length != 2){
            System.err.println("Usage: java PlaceServer <port> <size>");
            System.exit(1);
        }
        int portNum = Integer.parseInt(args[0]);
        int size = Integer.parseInt(args[1]);
        try(ServerSocket serverSocket = new ServerSocket(portNum)){
            while(true){
                Socket client = serverSocket.accept();
                try {
                    PlaceRequest loginRequest = PlaceExchange.receive((ObjectInputStream) client.getInputStream());
                    if (loginRequest.getType() == PlaceRequest.RequestType.LOGIN) {
                        String username = (String)loginRequest.getData();
                        if (users.contains(username)) {
                            new PlaceClientThread(client, username).start();
                            System.out.println("Started thread for user " + username);
                        } else {
                            System.out.println("Rejected socket for user " + username);
                        }
                    }
                } catch (PlaceException e) {
                    System.err.println("Failed receiving login request from new client");
                }
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }

    }
}
