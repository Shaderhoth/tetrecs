package uk.ac.soton.comp1206.component;

import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.BlockClickedListener;
import uk.ac.soton.comp1206.event.PieceSpawnedListener;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;

public class PieceBoard extends GameBoard{
    public PieceSpawnedListener pieceSpawnedListener;
    private static final Logger logger = LogManager.getLogger(PieceBoard.class);
    private BlockClickedListener blockClickedListener;
    private final int cols;
    private final int rows;
    private final double width;
    private final double height;
    final Grid grid;

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
    public void displayPiece(GamePiece p){
        grid.clear();
        grid.playPiece(p, 1,1);
        blocks[1][1].drawCircle();
    }
    public void setOnBlockClick(BlockClickedListener listener) {
        this.blockClickedListener = listener;
    }
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

    private void blockClicked(MouseEvent event, GameBlock block) {
        logger.info("Block clicked: {}", block);

        if(blockClickedListener != null) {
            blockClickedListener.blockClicked(event, block);
        }
    }
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
