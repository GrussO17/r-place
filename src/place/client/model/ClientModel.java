package place.client.model;

import place.PlaceBoard;
import place.PlaceTile;

import java.util.Observable;

public class ClientModel extends Observable {
    private PlaceBoard board;

    public void createBoard(int dim) {
        board = new PlaceBoard(dim);
    }

    public PlaceTile[][] getBoard() {
        return board.getBoard();
    }

    public void setTile(PlaceTile tile) {
        board.setTile(tile);
        setChanged();
        this.notifyObservers(tile);
    }
}
