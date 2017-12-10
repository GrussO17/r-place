package place.server;

import place.PlaceException;
import place.PlaceTile;
import place.network.PlaceExchange;
import place.network.PlaceRequest;
import place.server.model.ServerModel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Observable;
import java.util.Observer;

public class PlaceClientThread extends Thread implements Observer {

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ServerModel board;
    private String username;

    public PlaceClientThread(ObjectInputStream in, ObjectOutputStream out, String username, ServerModel board) {
        this.board = board;
        this.board.addObserver(this);
        this.in = in;
        this.out = out;
        this.username = username;
    }

    private void close() {
        try {
            in.close();
            out.close();
        } catch (IOException e) {
            System.err.println("Failed closing client socket");
        }
    }

    public void update(Observable obs, Object o) {
        try {
            PlaceExchange.send(out, (new PlaceRequest<>(PlaceRequest.RequestType.TILE_CHANGED, (PlaceTile) o)));
        } catch (PlaceException e) {
            System.err.println("error sending updated move");
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
                    } else {
                        out.writeUnshared(new PlaceRequest<>(PlaceRequest.RequestType.ERROR, "Invalid move"));
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        System.out.println(username + "'s client thread sleep interrupted");
                    }
                } else {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            PlaceServer.logoff(username);
            close();
        }

    }
}
