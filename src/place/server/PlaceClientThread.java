package place.server;

import place.PlaceBoard;
import place.PlaceTile;
import place.network.PlaceRequest;
import place.server.model.ServerModel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class PlaceClientThread extends Thread {

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ServerModel board;
    private String username;

    public PlaceClientThread(Socket socket, String username, ServerModel board){
        try {
            this.board = board;
            this.in = (ObjectInputStream)socket.getInputStream();
            this.out = (ObjectOutputStream)socket.getOutputStream();
        } catch (IOException e) {
            System.err.println("IO exception creating client-thread streams");
            e.printStackTrace();
            return;
        }
        this.username = username;
    }

    public void run() {
        boolean go = true;
        try {
            out.writeUnshared(new PlaceRequest(PlaceRequest.RequestType.BOARD, board.getPlaceBoard()));
        }
        catch(IOException e){
            e.printStackTrace();
        }
        while(go)
            try{
                PlaceRequest input = (PlaceRequest) this.in.readUnshared();
                if(input.getType() == PlaceRequest.RequestType.CHANGE_TILE){
                    if(board.isValid((PlaceTile)input.getData())){
                        board.setTile((PlaceTile)input.getData());
                    }
                    else{
                        out.writeUnshared(new PlaceRequest(PlaceRequest.RequestType.ERROR, "Invalid move"));
                    }
                }
                else{
                    go = false;
                }

            }
            catch(IOException | ClassNotFoundException e){
                e.printStackTrace();
            }
    }
}
