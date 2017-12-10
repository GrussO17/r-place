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

/**
 * PlaceClientThread class; each time the PlaceServer gets a new connection,
 * it starts a thread to handle the client actions
 */
public class PlaceClientThread extends Thread implements Observer {

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ServerModel board;
    private String username;

    /**
     * Construct a client thread
     *
     * @param in       the input stream to receive on
     * @param out      the output stream to send on
     * @param username the username of the client associated with this thread
     * @param board    the model to use
     */
    public PlaceClientThread(ObjectInputStream in, ObjectOutputStream out, String username, ServerModel board) {
        this.board = board;
        this.board.addObserver(this);
        this.in = in;
        this.out = out;
        this.username = username;
    }

    /**
     * Close all the connections of this thread
     */
    private void close() {
        try {
            in.close();
            out.close();
        } catch (IOException e) {
            System.err.println("Failed closing client socket");
        }
    }

    /**
     * Update the client for this thread when the server model has changed
     *
     * @param obs the server model which changed
     * @param o   the specific tile which was changed
     */
    public void update(Observable obs, Object o) {
        try {
            PlaceExchange.send(out, (new PlaceRequest<>(PlaceRequest.RequestType.TILE_CHANGED, (PlaceTile) o)));
        } catch (PlaceException e) {
            System.err.println("error sending updated move");
        }
    }

    /**
     * Main loop method, waits for client to send messages and
     * enforces the wait time before another tile can be changed.
     * Ends if the client sends anything other than a CHANGE_TILE
     */
    public void run() {
        try {
            out.writeUnshared(new PlaceRequest<>(PlaceRequest.RequestType.LOGIN_SUCCESS,
                    "Login for " + username + " accepted"));
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
            System.err.println("Connection error with user " + username);
        } finally {
            PlaceServer.logoff(username);
            close();
            this.board.deleteObserver(this);
        }

    }
}
