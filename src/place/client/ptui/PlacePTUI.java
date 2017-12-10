package place.client.ptui;

import place.PlaceColor;
import place.PlaceException;
import place.PlaceTile;
import place.client.model.ClientModel;
import place.client.network.NetworkClient;

import java.io.PrintWriter;
import java.net.ConnectException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

/**
 * The PlacePTUI application is a plain text Place UI.
 */
public class PlacePTUI extends ConsoleApplication implements Observer {
    /**
     * The model for place
     */
    private ClientModel model;

    /**
     * Connection to network interface to server
     */
    private NetworkClient serverConn;

    /**
     * Scanner to read the user's input
     */
    private Scanner userIn;

    /**
     * Where to send output for the user to see
     */
    private PrintWriter userOut;

    /**
     * The username of this client
     */
    private String username;

    /**
     * Main method, just starts this UI with ConsoleApplication
     *
     * @param args hostname, port, and username
     */
    public static void main(String[] args) {
        ConsoleApplication.launch(PlacePTUI.class, args);
    }

    /**
     * Run the UI; keeps going until stopped by closing
     *
     * @param userIn  Scanner to read user's input
     * @param userOut PrintWriter to send user output
     */
    public synchronized void go(Scanner userIn, PrintWriter userOut) {
        this.userIn = userIn;
        this.userOut = userOut;
        this.refresh();
        String[] response;
        PlaceColor color;

        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                response = userIn.nextLine().split(" ");
                int row = Integer.parseInt(response[0]);
                if (row == -1) {
                    userOut.println("Stopping");
                    stop();
                    System.exit(0);
                }
                int col = Integer.parseInt(response[1]);
                int colorNum = Integer.parseInt(response[2]);
                color = PlaceColor.values()[colorNum];
                serverConn.sendMove(new PlaceTile(row, col, username, color, System.currentTimeMillis()));
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                userOut.println("Invalid input, try again");
                userOut.print("Enter your move [row] [col] [color]: ");
                userOut.flush();
            }
        }
    }

    /**
     * Update method; calls refresh() when model has changed
     *
     * @param t the model
     * @param o the updated tile
     */
    public void update(Observable t, Object o) {
        assert t == this.model : "Update from non-model Observable";
        this.refresh();
    }

    /**
     * Print out the updated board and prompt the user to make a move
     */
    private void refresh() {
        if (model.getBoard() == null) {
            return;
        }
        try {
            userOut.println();
            for (PlaceTile[] row : model.getBoard()) {
                for (PlaceTile tile : row) {
                    userOut.print(tile.getColor());
                }
                userOut.println();
            }
            userOut.println();
            userOut.print("Enter your move [row] [col] [color]: ");
            userOut.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set up this view;
     * creates the model and controller, gets username
     */
    public void init() {
        try {
            List<String> args = super.getArguments();
            String host = args.get(0);
            int port = Integer.parseInt(args.get(1));
            username = args.get(2);
            this.model = new ClientModel();
            this.model.addObserver(this);
            this.serverConn = new NetworkClient(host, port, username, model);
            serverConn.start();
        } catch (PlaceException e) {
            if (e.getCause().getClass() == ConnectException.class) {
                System.out.println("Error connecting to server");
            } else {
                System.out.println("Error in initialization");
            }
            stop();
            System.exit(1);
        }
    }

    /**
     * Close all the network connections
     */
    public void stop() {
        if (userIn != null) {
            userIn.close();
        }
        if (userOut != null) {
            userOut.close();
        }
        if (serverConn != null) {
            serverConn.close();
        }
    }
}
