package place.client.model;

import place.PlaceBoard;
import place.PlaceColor;
import place.PlaceTile;

import java.util.Observable;

/**
 * The model used by the client; mostly a wrapper for PlaceBoard
 */
public class ClientModel extends Observable {
    private PlaceBoard board;
    private PlaceColor currentColor = PlaceColor.BLACK;

    /**
     * This allows the model to be created before the initial board is received
     *
     * @param board the new board
     */
    public void createBoard(PlaceBoard board) {
        this.board = board;
    }

    /**
     * Get the board stored in this model as an array of tiles
     *
     * @return the board
     */
    public PlaceTile[][] getBoard() {
        return board.getBoard();
    }

    /**
     * Set a tile in the client's model, notify of change
     *
     * @param tile the new tile
     */
    public void setTile(PlaceTile tile) {
        board.setTile(tile);
        setChanged();
        this.notifyObservers(tile);
    }

    /**
     * Get the color the user has currently selected
     *
     * @return the chosen color
     */
    public PlaceColor getCurrentColor() {
        return currentColor;
    }

    /**
     * Set the user's current chosen color
     *
     * @param currentColor the color which is chosen
     */
    public void setCurrentColor(PlaceColor currentColor) {
        this.currentColor = currentColor;
    }
}
