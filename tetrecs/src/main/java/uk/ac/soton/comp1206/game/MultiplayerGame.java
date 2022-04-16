package uk.ac.soton.comp1206.game;

import javafx.beans.Observable;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.utilities.Multimedia;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * A multiplayer variant of the Game class
 */
public class MultiplayerGame extends Game {
    private final Communicator communicator;
    private Queue<Integer> queue;

    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     *
     * @param cols         number of columns
     * @param rows         number of rows
     * @param communicator the communicator to talk to the server
     * @param queue        the collection of pieces to add to the queue
     */
    public MultiplayerGame(int cols, int rows, Communicator communicator, Queue<Integer> queue) {
        super(cols, rows);
        this.communicator = communicator;
        super.scoreProperty().addListener(this::updateScore);
        super.livesProperty().addListener(this::updateLives);
        this.queue = queue;

    }

    private void updateScore(Observable observable) {
        communicator.send("SCORE " + super.scoreProperty().getValue().toString());
    }

    private void updateLives(Observable observable) {
        communicator.send("LIVES " + super.livesProperty().getValue().toString());
    }

    /**
     * Fetch the following piece
     * Updates the next piece
     * Updates the piece boards
     */
    @Override
    public void nextPiece() {
        communicator.send("PIECE");
        super.currentPiece = super.followingPiece;
        super.followingPiece = spawnPiece();
        updatePieceBoards();
    }

    /**
     * Creates a new Game Piece
     *
     * @return the newly created GamePiece
     * @see GamePiece
     */
    @Override
    public GamePiece spawnPiece() {
        GamePiece g = GamePiece.createPiece(queue.poll());
        return g;
    }

    /**
     * Activates when a block is clicked to either place the piece or decrease lives
     * Ends the game if necessary
     */
    @Override
    public void blockClicked() {
        super.blockClicked();
        sendBoard();
    }

    /**
     * Send the updated board to the server for verification
     */
    public void sendBoard() {
        String message = "BOARD";
        for (int i = 0; i < getCols(); i++) {
            for (int j = 0; j < getRows(); j++) {
                message = message + " " + grid.get(i, j);
            }
        }

        communicator.send(message);
    }
}
