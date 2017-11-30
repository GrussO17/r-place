package place.client.model;

import place.PlaceBoard;

import java.util.Observable;

public class ClientModel extends Observable {
    public PlaceBoard board;

    public ClientModel(){

    }
    public void createBoard(int dim){
        board = new PlaceBoard(dim);
    }

}
