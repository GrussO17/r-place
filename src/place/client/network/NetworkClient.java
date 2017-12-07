package place.client.network;

import place.PlaceBoard;
import place.PlaceException;
import place.PlaceTile;
import place.client.model.ClientModel;
import place.network.PlaceExchange;
import place.network.PlaceRequest;

import java.io.*;
import java.net.Socket;

import static place.network.PlaceRequest.RequestType.LOGIN;

public class NetworkClient extends Thread {
    private Socket sock;
    private ObjectInputStream networkIn;
    private ObjectOutputStream networkOut;
    private ClientModel model;

    /**
     * Hook up with a Place server. Because of the nature of the server
     * protocol, this constructor actually blocks waiting for the first
     * message from the server that tells it how big the board will be.
     * Afterwards a thread that listens for server messages and forwards
     * them to the game object is started.
     *
     * @param hostname the name of the host running the server program
     * @param port     the port of the server socket on which the server is
     *                 listening
     * @param model    the local object holding the state of the game that
     *                 must be updated upon receiving server messages
     * @throws PlaceException If there is a problem opening the connection
     */
    public NetworkClient(String hostname, int port, String username, ClientModel model)
            throws PlaceException {
        try {
            this.sock = new Socket(hostname, port);
            this.networkOut = new ObjectOutputStream(sock.getOutputStream());
            this.networkIn = new ObjectInputStream(sock.getInputStream());
            this.model = model;

            PlaceExchange.send(new PlaceRequest<>(LOGIN, username), networkOut);

            PlaceRequest b = PlaceExchange.receive(networkIn);
            if (b.getType() == PlaceRequest.RequestType.BOARD)
                board((PlaceBoard) b.getData());

        } catch (IOException e) {
            throw new PlaceException(e);
        }
    }

    public void run() {
        try {
            MAINLOOP:
            while (true) {
                PlaceRequest input = (PlaceRequest) this.networkIn.readUnshared();
                PlaceRequest.RequestType type = input.getType();
                switch (type) {
                    case LOGIN_SUCCESS:
                        loginSuccess();
                        break;
                    case TILE_CHANGED:
                        tileChanged((PlaceTile) input.getData());
                        break;
                    default:
                        System.err.println("Unrecognized request: " + input.toString());
                        break MAINLOOP;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void loginSuccess() {
        System.out.println("Successfully connected to server");
    }

    public void tileChanged(PlaceTile tile) {
        model.setTile(tile);
    }

    public void board(PlaceBoard board) {
        model.createBoard(board.DIM);
    }


    public void close() {
        try {
            this.sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendMove(PlaceTile tile) throws PlaceException {
        PlaceExchange.send(new PlaceRequest<>(PlaceRequest.RequestType.CHANGE_TILE, tile), networkOut);
    }
}
