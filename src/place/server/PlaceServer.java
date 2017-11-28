package place.server;

import java.io.IOException;
import java.net.ServerSocket;

public class PlaceServer {
    public static void main(String[] args)  {
        if(args.length != 2){
            System.err.println("Usage: java PlaceServer <port> <size>");
            System.exit(1);
        }
        int portNum = Integer.parseInt(args[0]);
        int size = Integer.parseInt(args[1]);
        try(ServerSocket sS = new ServerSocket(portNum)){
            while(true){
                new PlaceClientThread(sS.accept()).start();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }

    }
}
