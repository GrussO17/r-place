package place.server.model;

import place.PlaceBoard;
import place.PlaceTile;

import java.util.Observable;

/**
 * The model for the server to use;
 * is an Observable in order to notify client threads
 */
public class ServerModel extends Observable {
    private PlaceBoard board;

    /**
     * Create the client model; makes a PlaceBoard with the given dimension
     *
     * @param dim the side dimension of the new board
     */
    public ServerModel(int dim) {
        board = new PlaceBoard(dim);
    }

    /**
     * Return the PlaceBoard used by this model (so server can send board to new clients)
     *
     * @return the PlaceBoard used for the model
     */
    public PlaceBoard getPlaceBoard() {
        return board;
    }

    /**
     * Update a tile in the model
     *
     * @param tile the new tile
     */
    public void setTile(PlaceTile tile) {
        board.setTile(tile);
        setChanged();
        this.notifyObservers(tile);
    }

    /**
     * Check if a tile is valid to place on this board
     *
     * @param tile the tile to check
     * @return true if the tile is valid to place
     */
    public boolean isValid(PlaceTile tile) {
        return board.isValid(tile);
    }

}
