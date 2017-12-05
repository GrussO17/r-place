package place.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class PlaceClientThread extends Thread {

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String username;

    public PlaceClientThread(Socket socket, String username){
        try {
            this.in = (ObjectInputStream)socket.getInputStream();
            this.out = (ObjectOutputStream)socket.getOutputStream();
        } catch (IOException e) {
            System.err.println("IO exception creating client-thread streams");
            return;
        }
        this.username = username;
    }

    public void run() {

    }
}
