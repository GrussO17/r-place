package place.test;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServer {
    public static void main(String args[]){
        try{
            ServerSocket me = new ServerSocket(1234);
            while(true){
                Socket client = me.accept();

            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
