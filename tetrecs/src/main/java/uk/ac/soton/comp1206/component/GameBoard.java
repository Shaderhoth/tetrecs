package uk.ac.soton.comp1206.component;

import javafx.animation.AnimationTimer;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.BlockClickedListener;
import uk.ac.soton.comp1206.event.BlockHoveredListener;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;

/**
 * A GameBoard is a visual component to represent the visual GameBoard.
 * It extends a GridPane to hold a grid of GameBlocks.
 *
 * The GameBoard can hold an internal grid of it's own, for example, for displaying an upcoming block. It also be
 * linked to an external grid, for the main game board.
 *
 * The GameBoard is only a visual representation and should not contain game logic or model logic in it, which should
 * take place in the Grid.
 */
public class GameBoard extends GridPane {

    private static final Logger logger = LogManager.getLogger(GameBoard.class);

    /**
     * Number of columns in the board
     */
    private final int cols;

    /**
     * Number of rows in the board
     */
    private final int rows;

    /**
     * The visual width of the board - has to be specified due to being a Canvas
     */
    private final double width;

    /**
     * The visual height of the board - has to be specified due to being a Canvas
     */
    private final double height;

    /**
     * The grid this GameBoard represents
     */
    final Grid grid;

    /**
     * The blocks inside the grid
     */
    GameBlock[][] blocks;

    /**
     * The listener to call when a specific block is clicked
     */
    private BlockClickedListener blockClickedListener;

    /**
     * The listener to call when a specific block is hovered over
     */
    private BlockHoveredListener blockHoveredListener;



    /**
     * The next piece in play
     */
    private GamePiece nextPiece;


    /**
     * index of the row currently being targeted
     */
    private int x = 1;
    /**
     * index of the column currently being targeted
     */
    private int y = 1;


    /**
     * Create a new GameBoard, based off a given grid, with a visual width and height.
     * @param grid linked grid
     * @param width the visual width
     * @param height the visual height
     */
    public GameBoard(Grid grid, double width, double height) {
        this.cols = grid.getCols();
        this.rows = grid.getRows();
        this.width = width;
        this.height = height;
        this.grid = grid;

        //Build the GameBoard
        build();
    }

    /**
     * Create a new GameBoard with it's own internal grid, specifying the number of columns and rows, along with the
     * visual width and height.
     *
     * @param cols number of columns for internal grid
     * @param rows number of rows for internal grid
     * @param width the visual width
     * @param height the visual height
     */
    public GameBoard(int cols, int rows, double width, double height) {
        this.cols = cols;
        this.rows = rows;
        this.width = width;
        this.height = height;
        this.grid = new Grid(cols,rows);

        //Build the GameBoard
        build();
    }

    /**
     * Build the GameBoard by creating a block at every x and y column and row
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
     * Create a block at the given x and y position in the GameBoard
     * @param x column
     * @param y row
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
        block.setOnMouseMoved((e) -> blockHovered(e, block));

        return block;
    }

    /**
     * Set the listener to handle an event when a block is clicked
     * @param listener listener to add
     */
    public void setOnBlockClick(BlockClickedListener listener) {
        this.blockClickedListener = listener;
    }

    /**
     * Triggered when a block is clicked. Call the attached listener.
     * @param event mouse event
     * @param block block clicked on
     */
    private void blockClicked(MouseEvent event, GameBlock block) {
        logger.info("Block clicked: {}", block);

        if(blockClickedListener != null) {
            blockClickedListener.blockClicked(event, block);

        }
    }

    /**
     * Sets the listener to check whether a block is being hovered over
     * @param listener the Block Hovered Listener
     * @see BlockHoveredListener
     */
    public void setOnBlockHover(BlockHoveredListener listener) {
        this.blockHoveredListener = listener;
    }

    /**
     * Triggered when a block is clicked. Call the attached listener.
     * @param event mouse event
     * @param block block clicked on
     */
    private void blockHovered(MouseEvent event, GameBlock block) {
        if(blockHoveredListener != null) {
            blockHoveredListener.blockHovered(event, block);

        }
    }

    /**
     * Resets the board
     */
    public void resetBoard(){
        for(var y = 0; y < rows; y++) {
            for (var x = 0; x < cols; x++) {
                blocks[x][y].reset();
            }
        }

    }

    /**
     * Targets a location and highlights the location of the current piece in play
     * @param x the row index of the targeted location
     * @param y the column index of the targeted location
     */
    public void target(int x, int y){
        this.x = x;
        this.y = y;
        blocks[x][y].drawCircle();
        if (nextPiece != null) {
            int[][] bs = nextPiece.getBlocks();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if ((x + i) <= cols && (y + j) <= rows && (x + i) > 0 && (y + j) > 0) {
                        if (bs[i][j] != 0 && grid.get((x + i - 1), (y + j - 1)) != 0) {
                            blocks[x + i - 1][y + j - 1].paintOver(nextPiece.getValue());
                        } else if (bs[i][j] != 0) {
                            blocks[x + i - 1][y + j - 1].paintOver(nextPiece.getValue());
                        }
                    }
                }
            }
        }
    }

    /**
     * Updates the current piece in play and resets the board
     * @param p
     */
    public void setNextPiece(GamePiece p){
        nextPiece = p;
        resetBoard();
        logger.info("Reset board");
        target(x,y);
    }

    /**
     * Triggers the fadeout animation of every piece which has been destroyed
     * @param coords a collection of coordinates of destroyed pieces
     */
    public void fadeOut(int[][] coords){
        AnimationTimer timer = new AnimationTimer(){
            int speed = 5;
            long lastTick = 0;
            int i = 0;
            @Override
            public void handle(long now){
                if (i>= coords.length){
                    stop();
                }
                else if(lastTick == 0 ){
                    lastTick = now ;
                    return;
                }
                else if(now - lastTick > 1000000000 / speed){
                    lastTick = now ;
                    int[] coord = coords[i];
                    blocks[coord[0]][coord[1]].fadeOut(coord[2]);
                    i++;
                }

            }
        };
        timer.start();
    }

}
