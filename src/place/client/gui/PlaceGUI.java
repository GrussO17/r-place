package place.client.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import place.PlaceColor;
import place.PlaceException;
import place.PlaceTile;
import place.client.model.ClientModel;
import place.client.network.NetworkClient;

import java.net.ConnectException;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Main Place GUI class; a view in the MVC pattern
 */
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
     * The username of this client
     */
    private String username;

    /**
     * 2D array of Rectangles for the UI to use
     */
    private Rectangle[][] rectangles;

    private Tooltip[][] tooltips;


    /**
     * The main method; start this as an Application
     *
     * @param args hostname, port, and username
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    /**
     * Update method; run when the model calls notifyObservers
     *
     * @param t the observable which has changed
     * @param o the specific tile which has been updates
     */
    public void update(Observable t, Object o) {
        assert t == this.model : "Update from non-model Observable";
        PlaceTile tile = (PlaceTile) o;
        javafx.application.Platform.runLater(() -> refresh(tile));
    }

    /**
     * Submethod for update so runLater is easier to write
     * changes the color of the Rectangle representing the changed tile
     *
     * @param tile the tile which has changed
     */
    private void refresh(PlaceTile tile) {
        PlaceColor color = tile.getColor();
        int row = tile.getRow();
        int col = tile.getCol();
        rectangles[row][col].setFill(Color.rgb(color.getRed(), color.getGreen(), color.getBlue()));
        tooltips[row][col].setText(String.format("(%d, %d)\n%s\n%s",
                row, col, tile.getOwner(), new Date(tile.getTime()).toString()));
    }

    /**
     * Create a new tile with the row and column of the old one,
     * the currently selected color, and the current time
     *
     * @param tile the old tile at the same position
     * @return the new tile
     */
    private PlaceTile getNewTile(PlaceTile tile) {
        return new PlaceTile(tile.getRow(), tile.getCol(), username,
                model.getCurrentColor(), System.currentTimeMillis());
    }

    /**
     * Start method, run by Application; sets up the GUI components
     *
     * @param stage the stage on which to work
     */
    public void start(Stage stage) {
        BorderPane pane = new BorderPane();
        pane.setCenter(createMainGrid());
        pane.setBottom(createColorSelection());
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.setTitle("Place: " + username);
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
        stage.show();
    }

    /**
     * Create the main GridPane for the GUI; used in start()
     *
     * @return GridPane containing Rectangles representing the board
     */
    private GridPane createMainGrid() {
        GridPane grid = new GridPane();
        PlaceTile[][] board = model.getBoard();
        rectangles = new Rectangle[board.length][board[0].length];
        tooltips = new Tooltip[board.length][board[0].length];
        for (int row = 0; row < rectangles.length; row++) {
            for (int col = 0; col < rectangles[0].length; col++) {
                Rectangle rect = new Rectangle(col, row, 800 / board.length, 750 / board[0].length);
                PlaceTile tile = board[row][col];
                PlaceColor color = tile.getColor();
                rect.setFill(Color.rgb(color.getRed(), color.getGreen(), color.getBlue()));
                Tooltip tooltip = new Tooltip(String.format("(%d, %d)\n%s\n%s",
                        tile.getRow(), tile.getCol(), tile.getOwner(), new Date(tile.getTime()).toString()));
                tooltips[row][col] = tooltip;
                Tooltip.install(rect, tooltip);
                rect.setOnMouseClicked(e -> serverConn.sendMove(getNewTile(tile)));
                rectangles[row][col] = rect;
                grid.add(rect, col, row);
            }
        }
        return grid;
    }

    /**
     * Create the color selection button row
     *
     * @return HBox with color selection buttons
     */
    private HBox createColorSelection() {
        HBox buttons = new HBox();
        ToggleGroup group = new ToggleGroup();
        for (int c = 0; c < 16; c++) {
            PlaceColor color = PlaceColor.values()[c];
            ToggleButton b = new ToggleButton(color.toString());
            b.setBackground(new Background(new BackgroundFill(
                    Color.rgb(color.getRed(), color.getGreen(), color.getBlue()),
                    CornerRadii.EMPTY, Insets.EMPTY)));
            b.setOnAction(event -> model.setCurrentColor(color));
            b.setToggleGroup(group);
            b.setMinWidth(0);
            b.setPrefWidth(50);
            b.setPrefHeight(50);
            buttons.getChildren().add(b);
        }
        return buttons;
    }

    /**
     * Init method, run by Application
     * Creates the model and controller for this view
     */
    public void init() {
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
     * Close the connection to the server; used by Application
     */
    public void stop() {
        if (serverConn != null) {
            serverConn.close();
        }
    }
}
