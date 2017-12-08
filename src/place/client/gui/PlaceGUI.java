package place.client.gui;

import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import place.PlaceColor;
import place.PlaceException;
import place.PlaceTile;
import place.client.model.ClientModel;
import place.client.network.NetworkClient;

import javax.swing.*;
import javax.swing.plaf.IconUIResource;
import java.io.PrintWriter;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

public class PlaceGUI extends Application implements Observer {
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


    public static void main(String[] args) {Application.launch( args );}

    public synchronized void go(Scanner userIn, PrintWriter userOut) {
        this.userIn = userIn;
        this.userOut = userOut;
        this.refresh();
        while (true) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update(Observable t, Object o) {
        assert t == this.model : "Update from non-model Observable";
        this.refresh();
    }

    private void refresh() {
        if (model.getBoard() == null) {
            return;
        }
        try {
            for (PlaceTile[] row : model.getBoard()) {
                for (PlaceTile tile : row) {
                    userOut.print(tile.getColor());
                }
                userOut.println();
            }
            userOut.println();
            PlaceColor color = PlaceColor.WHITE;
            String[] response;
            while (true) {
                userOut.print("Enter your move [row] [col] [color]: ");
                userOut.flush();
                try {
                    response = userIn.nextLine().split(" ");
                    int row = Integer.parseInt(response[0]);
                    int col = Integer.parseInt(response[1]);
                    int colorNum = Integer.parseInt(response[2]);
                    for (PlaceColor c : PlaceColor.values()) {
                        if (c.getNumber() == colorNum) {
                            color = c;
                        }
                    }
                    serverConn.sendMove(new PlaceTile(row, col, username, color));
                    break;
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.out.println("Invalid input, try again");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start(Stage s) {
        BorderPane border = new BorderPane();

        for(PlaceTile[] row: model.getBoard() ) {
            for (PlaceTile tile : row) {

            }
        }

        HBox bottom = new HBox();
        for(int a = 1; a <= 16; a++) {
            JRadioButton button = new JRadioButton(a, new IconUIResource())
        }

    }


    public void init(){
        try {
            List<String> params = super.getParameters().getRaw();
            String host = params.get(0);
            int port = Integer.parseInt(params.get(1));
            username = params.get(2);

            this.model = new ClientModel();
            this.model.addObserver(this);
            this.serverConn = new NetworkClient(host, port, username, model);
            serverConn.start();
        } catch (PlaceException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        this.userIn.close();
        this.userOut.close();
        this.serverConn.close();
    }
}

}
