package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * The Pieces Spawned Listener is used for listening for the creation of any game pieces
 */
public interface PieceSpawnedListener {
    /**
     * Handles destroyed pieces
     * @param nextPiece the gamepiece object which is next in line to be played
     * @param followPiece the gamepiece object which will be played after the nextPiece object
     */
    public void pieceSpawned(GamePiece nextPiece, GamePiece followPiece);
}
