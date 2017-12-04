package place.client.model;

import place.PlaceBoard;
import place.PlaceTile;

import java.util.Observable;

public class ClientModel extends Observable {
    public PlaceBoard board;

    public ClientModel(){

    }

    public void createBoard(int dim){
        board = new PlaceBoard(dim);
    }

    public PlaceTile[][] getBoard() {
        return board.getBoard();
    }

    public void setTile(PlaceTile tile){
        board.setTile(tile);
        this.notifyObservers();
    }
    public boolean isValid(PlaceTile tile){
        return board.isValid(tile);
    }

}
