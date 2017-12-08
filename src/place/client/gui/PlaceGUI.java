package place.client.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import place.PlaceColor;
import place.PlaceException;
import place.PlaceTile;
import place.client.model.ClientModel;
import place.client.network.NetworkClient;

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


    public static void main(String[] args) {
        Application.launch(args);
    }

    public void update(Observable t, Object o) {
        assert t == this.model : "Update from non-model Observable";

    }

    public void start(Stage stage) {
        BorderPane pane = new BorderPane();

        Canvas canvas = new Canvas(800, 800);
        for (PlaceTile[] row : model.getBoard()) {
            for (PlaceTile tile : row) {
                //canvas.
            }
        }
        pane.setCenter(canvas);

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
