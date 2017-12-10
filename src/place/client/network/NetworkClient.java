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

/**
 * NetworkClient class; the controller in MVC, does network operations
 */
public class NetworkClient extends Thread {
    private Socket sock;
    private ObjectInputStream networkIn;
    private ObjectOutputStream networkOut;
    private ClientModel model;

    /**
     * Constructor for NetworkClient; connects with a Place server.
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

            PlaceExchange.send(networkOut, new PlaceRequest<>(LOGIN, username));

            PlaceRequest req = PlaceExchange.receive(networkIn);
            if (!handleRequest(req)) {
                close();
                System.exit(1);
            }
        } catch (IOException e) {
            throw new PlaceException(e);
        }
    }

    /**
     * The main loop; receive network requests and handle them
     */
    public void run() {
        try {
            while (true) {
                PlaceRequest req = PlaceExchange.receive(networkIn);
                if (!handleRequest(req)) {
                    break;
                }
            }
        } catch (PlaceException e) {
            e.printStackTrace();
            System.out.println("Error reading from server");
        } finally {
            close();
        }
        System.exit(1);
    }

    /**
     * Handle a PlaceRequest from the server and takes required action
     *
     * @param req the request to handle
     * @return true if the main loop should continue to run, false if not (error condition)
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted") //easier to understand
    private boolean handleRequest(PlaceRequest req) {
        switch (req.getType()) {
            case BOARD:
                model.createBoard((PlaceBoard) req.getData());
                return true;
            case LOGIN_SUCCESS:
                System.out.println("Successfully connected to server");
                return true;
            case TILE_CHANGED:
                model.setTile((PlaceTile) req.getData());
                return true;
            case ERROR:
                System.out.println("Error: " + req.getData());
                return false;
            default:
                System.out.println("Unrecognized request: " + req.toString());
                return false;
        }
    }

    /**
     * Close all the network connections
     */
    public void close() {
        try {
            networkIn.close();
            networkOut.close();
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send a move to the server
     *
     * @param tile the new tile representing the move made
     */
    public void sendMove(PlaceTile tile) {
        try {
            PlaceExchange.send(networkOut, new PlaceRequest<>(PlaceRequest.RequestType.CHANGE_TILE, tile));
        } catch (PlaceException e) {
            System.err.println("Error sending move");
        }
    }
}
