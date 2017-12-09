package place.client.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
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

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

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


    public static void main(String[] args) {
        Application.launch(args);
    }

    public void update(Observable t, Object o) {
        assert t == this.model : "Update from non-model Observable";
        PlaceTile tile = (PlaceTile)o;
        PlaceColor color = tile.getColor();
        int row = tile.getRow();
        int col = tile.getCol();
        rectangles[row][col].setFill(Color.rgb(color.getRed(), color.getGreen(), color.getBlue()));
        tooltips[row][col].setText(String.format("(%d, %d)\n%s\n%s",
                row, col, tile.getOwner(), new Date(tile.getTime()).toString()));

    }

    public PlaceTile getNewTile(PlaceTile tile){
        return new PlaceTile(tile.getRow(), tile.getCol(), username,
                model.getCurrentColor(), System.currentTimeMillis());
    }

    public void start(Stage stage) {
        rectangles = new Rectangle[model.getBoard().length][model.getBoard()[0].length];
        tooltips = new Tooltip[model.getBoard().length][model.getBoard()[0].length];
        BorderPane pane = new BorderPane();
        GridPane grid = new GridPane();
        PlaceTile[][] board = model.getBoard();
        for (int row = 0; row < rectangles.length; row++) {
            for (int col = 0; col < rectangles[0].length; col++) {
                Rectangle rect = new Rectangle(col, row, 10, 10);
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
        pane.setCenter(grid);

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
        pane.setBottom(buttons);
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.setTitle("Place: " + username);
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
        stage.show();
    }


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
            e.printStackTrace();
        }
    }

    public void stop() {
        this.serverConn.close();
    }
}
