package place.client.model;

import place.PlaceBoard;
import place.PlaceColor;
import place.PlaceTile;

import java.util.Observable;

public class ClientModel extends Observable {
    private PlaceBoard board;
    private PlaceColor currentColor = PlaceColor.BLACK;

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

    public PlaceColor getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(PlaceColor currentColor) {
        this.currentColor = currentColor;
    }
}
