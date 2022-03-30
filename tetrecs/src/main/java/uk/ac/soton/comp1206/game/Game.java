package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.scene.ChallengeScene;

import java.util.ArrayList;
import java.util.Random;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    private IntegerProperty score = new SimpleIntegerProperty(0);
    private IntegerProperty level = new SimpleIntegerProperty(0);
    private IntegerProperty lives = new SimpleIntegerProperty(3);
    private IntegerProperty multiplier = new SimpleIntegerProperty(1);

    private static final Logger logger = LogManager.getLogger(Game.class);

    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;
    private GamePiece currentPiece;
    /**
     * The grid model linked to the game
     */
    protected final Grid grid;

    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        currentPiece = spawnPiece();
        initialiseGame();
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");
    }

    /**
     * Handle what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        /*
        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();

        //Get the new value for this block
        int previousValue = grid.get(x,y);
        int newValue = previousValue + 1;
        if (newValue  > GamePiece.PIECES) {
            newValue = 0;
        }

        //Update the grid with the new value
        grid.set(x,y,newValue);

         */
        grid.playPiece(currentPiece,gameBlock.getX(),gameBlock.getY());
        afterPiece();
        nextPiece();

    }

    /**
     * Get the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    public GamePiece spawnPiece(){
        return GamePiece.createPiece(new Random().nextInt(0,14));
    }
    public void nextPiece(){
        currentPiece = spawnPiece();
    }
    public void score(int lines, int blocks){
        score.setValue(score.getValue() + lines * blocks * 10 * multiplier.getValue());

    }

    public void afterPiece(){
        logger.info("Running afterPiece");
        ArrayList<Integer> columnsToClear = new ArrayList();
        int lines = 0;
        int blocks = 0;
        int count;
        for (int i = 0; i < getCols(); i++) {
            count = 0;
            for (int j = 0; j < getRows(); j++) {
                if (grid.get(i,j) != 0){
                    count++;
                }
            }
            logger.info("Column: " + i + " Count: " + count);
            if (count == getRows()){
                logger.info("Clearing column " + i);
                columnsToClear.add(i);
                lines++;
            }
        }
        for (int i = 0; i < getRows(); i++) {
            count = 0;
            for (int j = 0; j < getCols(); j++) {
                if (grid.get(j,i) != 0){
                    count++;
                }
            }
            logger.info("Row: " + i + " Count: " + count);
            if (count == getCols()){
                logger.info("Clearing row " + i);
                lines++;
                for (int j = 0; j < getCols(); j++) {
                    if (grid.get(j,i) != 0){
                        grid.set(j,i,0);
                        blocks++;
                    }

                }
            }
        }
        for (Integer col: columnsToClear) {
            for (int i = 0; i < getRows(); i++) {
                if (grid.get(col,i) != 0){
                    grid.set(col,i,0);
                    blocks++;
                }
            }
        }
        score(lines, blocks);
        if (lines > 0){
            multiplier.setValue(multiplier.getValue()+1);
        }else{
            multiplier.setValue(1);
        }

    }
    public IntegerProperty scoreProperty() {
        return score;
    }public IntegerProperty levelProperty() {
        return level;
    }public IntegerProperty livesProperty() {
        return lives;
    }public IntegerProperty multiplierProperty() {
        return multiplier;
    }

}
