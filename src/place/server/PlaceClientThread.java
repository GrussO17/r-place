package place.server;

import java.net.Socket;

public class PlaceClientThread extends Thread{

    Socket connection;
    String userName;

    public PlaceClientThread(Socket connection, String userName){
        this.connection = connection;
        this.userName = userName;
    }
}
