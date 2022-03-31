package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.game.GamePiece;


public interface PieceSpawnedListener {
    public void pieceSpawned(GamePiece nextPiece, GamePiece followPiece);
}
