package uk.ac.soton.comp1206.component;

import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.BlockClickedListener;
import uk.ac.soton.comp1206.event.PieceSpawnedListener;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;

/**
 * A smaller variant of the game-board to display a singular piece
 */
public class PieceBoard extends GameBoard{
    /**
     * The listener to check when a piece is spawned
     */
    public PieceSpawnedListener pieceSpawnedListener;
    /**
     * Another woodcutter to tell us stuff we already know
     * Likes to gossip a lot
     */
    private static final Logger logger = LogManager.getLogger(PieceBoard.class);
    /**
     *
     */
    private BlockClickedListener blockClickedListener;
    private final int cols;
    private final int rows;
    private final double width;
    private final double height;
    final Grid grid;

    /**
     * Initialise the piece board
     * @param width the output width of the board
     * @param height the output height of the board
     */
    public PieceBoard(double width, double height) {
        super(3, 3, width, height);
        this.cols = 3;
        this.rows = 3;
        this.width = width;
        this.height = height;
        this.grid = new Grid(cols,rows);

        //Build the GameBoard
        build();
    }

    /**
     * Displays a specific gamepiece on the board
     * @param p the game piece to be displayed
     */
    public void displayPiece(GamePiece p){
        grid.clear();
        grid.playPiece(p, 1,1);
        blocks[1][1].drawCircle();
    }

    /**
     * Sets a listener to activate whenever a block from the board is clicked on
     * @param listener listener to add
     */
    public void setOnBlockClick(BlockClickedListener listener) {
        this.blockClickedListener = listener;
    }

    /**
     * Builds the game board
     */
    protected void build() {
        logger.info("Building grid: {} x {}",cols,rows);

        setMaxWidth(width);
        setMaxHeight(height);

        setGridLinesVisible(true);

        blocks = new GameBlock[cols][rows];

        for(var y = 0; y < rows; y++) {
            for (var x = 0; x < cols; x++) {
                createBlock(x,y);
            }
        }
    }

    /**
     * Generates a block at a certain location
     * @param x column to place the block
     * @param y row to place the block
     * @return the created game block object
     * @see GameBlock
     */
    protected GameBlock createBlock(int x, int y) {
        var blockWidth = width / cols;
        var blockHeight = height / rows;

        //Create a new GameBlock UI component
        GameBlock block = new GameBlock(this, x, y, blockWidth, blockHeight);

        //Add to the GridPane
        add(block,x,y);

        //Add to our block directory
        blocks[x][y] = block;

        //Link the GameBlock component to the corresponding value in the Grid
        block.bind(grid.getGridProperty(x,y));

        //Add a mouse click handler to the block to trigger GameBoard blockClicked method
        block.setOnMouseClicked((e) -> blockClicked(e, block));

        return block;
    }

    /**
     * A mouse event triggered whenever a block is clicked
     * @param event the mouse click event
     * @param block the block which was clicked on
     */
    private void blockClicked(MouseEvent event, GameBlock block) {
        logger.info("Block clicked: {}", block);

        if(blockClickedListener != null) {
            blockClickedListener.blockClicked(event, block);
        }
    }

    /**
     * Resize the board by a scale factor
     * @param factor the factor to resize the board by
     */
    public void resize(double factor){
        setMaxWidth(getWidth()*factor);
        setMinWidth(getWidth()*factor);
        setMaxHeight(getHeight()*factor);
        setMinHeight(getHeight()*factor);
        for (GameBlock[] cols: blocks) {
            for (GameBlock block: cols) {
                block.rs(factor);
            }

        }
    }
}
