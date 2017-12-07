package place.server.model;

import place.PlaceBoard;
import place.PlaceTile;

import java.util.Observable;

public class ServerModel extends Observable {
    private PlaceBoard board;

    public ServerModel(int dim) {
        board = new PlaceBoard(dim);
    }

    public PlaceBoard getPlaceBoard() {
        return board;
    }

    public void setTile(PlaceTile tile) {
        board.setTile(tile);
        setChanged();
        this.notifyObservers(tile);
    }

    public boolean isValid(PlaceTile tile) {
        return board.isValid(tile);
    }

}
