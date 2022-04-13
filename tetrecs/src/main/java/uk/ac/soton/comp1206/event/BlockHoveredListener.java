package uk.ac.soton.comp1206.event;

import javafx.scene.input.MouseEvent;
import uk.ac.soton.comp1206.component.GameBlock;

/**
 * The Block Hovered listener is used to handle the event when a block in a GameBoard is hovered over. It passes the
 * GameBlock that was hovered over in the message
 */
public interface BlockHoveredListener {

    /**
     * Handle a block clicked event
     * @param block the block that was hovered over
     */
    public void blockHovered(MouseEvent event, GameBlock block);
}
