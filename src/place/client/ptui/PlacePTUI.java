package place.client.ptui;

import place.PlaceException;
import place.client.model.ClientModel;
import place.client.network.NetworkClient;

import java.io.PrintWriter;
import java.util.List;
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


    public synchronized void go(Scanner userIn, PrintWriter userOut){
        this.userOut = userOut;
        this.model.addObserver(this);

        this.refresh();

        while ( this.model.getStatus() == Board.Status.NOT_OVER ) {
            try {
                this.wait();
            }
            catch( InterruptedException ie ) {}
        }



    }



    public void init() {
        try {
            List<String> args = super.getArguments();
            String host = args.get(0);
            int port = Integer.parseInt(args.get(1));
            String username = args.get(2);

            this.model = new ClientModel();
            this.serverConn = new NetworkClient(host, port, username, model);
        }
        catch(PlaceException e){
            e.printStackTrace();
        }
    }

    public void stop() {
        this.userIn.close();
        this.userOut.close();
        this.serverConn.close();
    }




    public static void main(String[] args) {
        ConsoleApplication.launch(PlacePTUI.class, args);
    }
}
