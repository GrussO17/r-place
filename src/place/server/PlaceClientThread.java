package place.server;

import place.PlaceTile;
import place.network.PlaceRequest;
import place.server.model.ServerModel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PlaceClientThread extends Thread {

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ServerModel board;
    private String username;

    public PlaceClientThread(ObjectInputStream in, ObjectOutputStream out, String username, ServerModel board) {
        this.board = board;
        this.in = in;
        this.out = out;
        this.username = username;
    }

    public void close() {
        try {
            in.close();
            out.close();
        } catch (IOException e) {
            System.err.println("Failed closing client socket");
        }
    }

    public void run() {
        System.out.println("asdf asdf asdfa");
        try {
            out.writeUnshared(new PlaceRequest<>(PlaceRequest.RequestType.BOARD, board.getPlaceBoard()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true)
            try {
                PlaceRequest input = (PlaceRequest) this.in.readUnshared();
                if (input.getType() == PlaceRequest.RequestType.CHANGE_TILE) {
                    if (board.isValid((PlaceTile) input.getData())) {
                        board.setTile((PlaceTile) input.getData());
                    } else {
                        out.writeUnshared(new PlaceRequest<>(PlaceRequest.RequestType.ERROR, "Invalid move"));
                    }
                } else {
                    break;
                }

            } catch (IOException | ClassNotFoundException e) {
                break;
            } finally {
                close();
                PlaceServer.logoff(username);
            }
    }
}
