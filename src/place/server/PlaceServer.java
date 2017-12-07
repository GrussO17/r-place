package place.server;

import place.PlaceException;
import place.network.PlaceExchange;
import place.network.PlaceRequest;
import place.server.model.ServerModel;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class PlaceServer {
    private static Set<String> users = new HashSet<>();

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java PlaceServer <port> <size>");
            System.exit(1);
        }
        int portNum = Integer.parseInt(args[0]);
        int size = Integer.parseInt(args[1]);
        ServerModel model = new ServerModel(size);
        try (ServerSocket serverSocket = new ServerSocket(portNum)) {
            while (true) {
                Socket client = serverSocket.accept();

                ObjectInputStream in = new ObjectInputStream(client.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                PlaceRequest loginRequest = PlaceExchange.receive(in);
                if (loginRequest.getType() == PlaceRequest.RequestType.LOGIN) {
                    String username = (String) loginRequest.getData();
                    if (!users.contains(username)) {
                        users.add(username);
                        new PlaceClientThread(in, out, username, model).start();
                        System.out.println("Started thread for user " + username);
                    } else {
                        System.out.println("Rejected socket for user " + username);
                    }
                }

            }
        } catch (PlaceException e) {
            System.err.println("Failed receiving login request from new client");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logoff(String username) {
        users.remove(username);
        System.out.println(username + " logged off");
    }
}
