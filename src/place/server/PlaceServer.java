package place.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

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
                Socket client = sS.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader( client.getInputStream()));
                String user = reader.readLine();
                new PlaceClientThread(client, user).start();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }

    }
}
