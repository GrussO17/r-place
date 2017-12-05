package place.server.model;

import place.PlaceBoard;
import place.PlaceTile;

import java.util.Observable;

public class ServerModel extends Observable {
    public PlaceBoard board;

    public ServerModel(int dim) {
        board = new PlaceBoard(dim);
    }

    public PlaceBoard getPlaceBoard(){return board;}

    public PlaceTile[][] getBoard() {
        return board.getBoard();
    }

    public void setTile(PlaceTile tile) {
        board.setTile(tile);
        this.notifyObservers();
    }

    public boolean isValid(PlaceTile tile) {
        return board.isValid(tile);
    }

}
