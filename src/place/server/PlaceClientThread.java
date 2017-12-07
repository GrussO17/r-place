package place.server;

import place.PlaceException;
import place.PlaceTile;
import place.network.PlaceExchange;
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
        try {
            out.writeUnshared(new PlaceRequest<>(PlaceRequest.RequestType.BOARD, board.getPlaceBoard()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            while (true) {
                PlaceRequest input = (PlaceRequest) this.in.readUnshared();
                if (input.getType() == PlaceRequest.RequestType.CHANGE_TILE) {
                    PlaceTile tile = (PlaceTile) input.getData();
                    if (board.isValid(tile)) {
                        board.setTile(tile);
                        System.out.println("move made: " + tile.toString());
                        PlaceExchange.send((new PlaceRequest<>(PlaceRequest.RequestType.TILE_CHANGED, tile)), out);
                    } else {
                        out.writeUnshared(new PlaceRequest<>(PlaceRequest.RequestType.ERROR, "Invalid move"));
                    }
                } else {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException | PlaceException e) {
            e.printStackTrace();
        } finally {
            close();
            PlaceServer.logoff(username);
        }

    }
}
