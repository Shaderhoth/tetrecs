package uk.ac.soton.comp1206.event;


/**
 * The Pieces Destroyed Listener is used for listening for the destruction of any game pieces
 */
public interface PiecesDestroyedListener {
    /**
     * Handles destroyed pieces
     * @param coords an array containing the coordinates of any destroyed pieces
     */
    public void piecesDestroyed(int[][] coords);
}
