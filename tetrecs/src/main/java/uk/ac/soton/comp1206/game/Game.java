package uk.ac.soton.comp1206.game;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.event.*;
import uk.ac.soton.comp1206.scene.ChallengeScene;
import uk.ac.soton.comp1206.utilities.FileHandler;
import uk.ac.soton.comp1206.utilities.Multimedia;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    /**
     * A logger, because that's always useful especially when debugging
     */
    private static final Logger logger = LogManager.getLogger(Game.class);

    /**
     * The score of the current game
     */
    private IntegerProperty score = new SimpleIntegerProperty(0);

    /**
     * The top score ever achieved locally
     */
    private IntegerProperty topScore = new SimpleIntegerProperty(0);

    /**
     * The current level of the user
     */
    private IntegerProperty level = new SimpleIntegerProperty(0);
    /**
     * number of remaining lives
     */
    protected IntegerProperty lives = new SimpleIntegerProperty(3);

    /**
     * The current score multiplier
     */
    private IntegerProperty multiplier = new SimpleIntegerProperty(1);

    /**
     * A listener to check for pieces being spawned
     */
    private PieceSpawnedListener pieceSpawnedListener;

    /**
     *A listener to check for pieces being destroyed
     */
    private PiecesDestroyedListener piecesDestroyedListener;

    /**
     *A listener to check for the next round of play
     */
    private GameLoopListener gameLoopListener;

    /**
     *A listener to check for the game being over
     */
    protected GameOverListener gameOverListener;

    /**
     * The currently targeted X coordinate
     */
    private int x = 0;

    /**
     * The currently targeted Y coordinate
     */
    private int y = 0;

    /**
     * A repeating thread executing the gameloop to encourage the user to act fast
     */
    protected Timer timer;

    /**
     * The task being executed by the timer
     */
    private TimerTask task;

    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The piece which is currently in play
     */
    protected GamePiece currentPiece;

    /**
     * The piece to be played after the current piece
     */
    protected GamePiece followingPiece;
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
        try {
            FileHandler file = new FileHandler("scores.txt");
            file.setReader();
            topScore.set(Integer.valueOf(file.getLine().split(":")[1]));
        } catch (Exception e) {
            logger.error(e);
            topScore.set(0);
        }
        this.cols = cols;
        this.rows = rows;

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);
        resetTimer();
        logger.info("Timer");

    }

    /**
     * Reset the current timer
     * @see Timer
     * @see TimerTask
     */
    private void resetTimer(){
        endTimer();
        if (gameLoopListener != null) {
            gameLoopListener.gameLoop(getTimerDelay());
        }
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                gameLoop();
                if (livesProperty().getValue()<0){
                    if (gameLoopListener != null) {
                        gameLoopListener.gameLoop(-1);
                    }
                    cancel();
                }
            }
        };
        timer.scheduleAtFixedRate(task, getTimerDelay(), getTimerDelay());

    }

    /**
     * End the current timer
     * @see Timer
     */
    public void endTimer(){
        if (timer != null) {
            timer.cancel();
        }

    }


    /**
     * Activated onc the timer has finished
     * Decreases the lives
     * Resets the timer
     * If necessary, activates the gameOverListener
     */
    private void gameLoop(){
        livesProperty().set(livesProperty().get()-1);
        Multimedia.playAudio("explode.wav");
        if (livesProperty().getValue() <0){
            if (gameOverListener != null) {
                gameOverListener.gameOver();
            }
        }
        nextPiece();
        multiplierProperty().set(1);
        if (gameLoopListener != null) {
            gameLoopListener.gameLoop(getTimerDelay());
        }
    }
    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        followingPiece = spawnPiece();
        nextPiece();
        initialiseGame();

    }

    /**
     * Initialise the game
     * its like starting the game but using a bigger word cuz we're fancy
     * I don't know this is a separate method from start, but it is, so I'm gonna just leave it like this
     */
    public void initialiseGame() {
        logger.info("Initialising game");
    }

    /**
     * Activates when a block is clicked to either place the piece or decrease lives
     * Ends the game if necessary
     */
    public void blockClicked() {
        if (grid.canPlayPiece(currentPiece,getX(),getY())){
            grid.playPiece(currentPiece,getX(),getY());
            Multimedia.playAudio("place.wav");
        }else {
            lives.setValue(lives.getValue()-1);
            if (lives.getValue() <0){
                if (gameOverListener != null) {
                    timer.cancel();
                    gameOverListener.gameOver();
                }

            }
            Multimedia.playAudio("explode.wav");
        }

        afterPiece();
        nextPiece();
        resetTimer();

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

    /**
     * Sets the currently targeted position where the piece will be played
     * @param x the horizontal location of the piece
     * @param y the vertical location of the piece
     */
    public void setAim(int x, int y){
        this.x = x;
        this.y = y;
    }


    /**
     * Gets the X location of the currently targeted location
     * @return the horizontal aspect of the currently targeted location
     */
    public int getX(){
        return x;
    }

    /**
     * Gets the Y location of the currently targeted location
     * @return the vertical aspect of the currently targeted location
     */
    public int getY(){
        return y;
    }

    /**
     * Creates a new Game Piece
     * @return the newly created GamePiece
     * @see GamePiece
     */
    protected GamePiece spawnPiece(){
        GamePiece g = GamePiece.createPiece(new Random().nextInt(0,14));

        return g;
    }



    /**
     * Generates the following piece
     * Updates the next piece
     * Updates the piece boards
     */
    protected void nextPiece(){
        currentPiece = followingPiece;
        followingPiece  = spawnPiece();
        updatePieceBoards();
    }
    /**
     * Rotates the current piece
     * Updates the piece boards
     * @param count the number of rotations by which the piece is spun
     */
    public void rotateCurrentPiece(int count){
        currentPiece.rotate(count);
        updatePieceBoards();
    }
    /**
     * Swaps the current piece and the following piece
     * Updates the piece boards
     */
    public void swapCurrentPiece(){
        GamePiece temp = currentPiece;
        currentPiece = followingPiece;
        followingPiece  = temp;
        updatePieceBoards();
    }


    /**
     * Update the piece boards using the pieceSpawned listener
     */
    protected void updatePieceBoards(){
        if (pieceSpawnedListener != null) {
            pieceSpawnedListener.pieceSpawned(currentPiece, followingPiece);
        }
    }

    /**
     * Set the GameLoopListener
     * @param listener the Game Loop Listener
     * @see GameLoopListener
     */
    public void setOnGameLoop(GameLoopListener listener) {
        this.gameLoopListener = listener;
    }

    /**
     * Set the GameOverListener
     * @param listener the Game Over Listener
     * @see GameOverListener
     */
    public void setOnGameOver(GameOverListener listener) {
        this.gameOverListener = listener;
    }


    /**
     * Set the PieceSpawnedListener
     * @param listener the Piece Spawned Listener
     * @see PieceSpawnedListener
     */
    public void setOnPieceSpawned(PieceSpawnedListener listener) {
        this.pieceSpawnedListener = listener;
    }



    /**
     * Activates on the destruction of blocks to provide the target locations and allow the destruction effect to run
     * @param listener the Pieces Destroyed Listener
     */
    public void setOnPiecesDestroyed(PiecesDestroyedListener listener) {
        this.piecesDestroyedListener = listener;
    }

    /**
     * Update the score after blocks are cleared
     * @param lines the number of lines cleared this round
     * @param blocks the number of blocks cleared this round
     */
    private void score(int lines, int blocks){
        score.setValue(score.getValue() + lines * blocks * 10 * multiplier.getValue());
        topScore.setValue(Math.max(score.getValue(), topScore.getValue()));

    }
    /**
     * Activates after a piece is placed
     * Calculates the number of blocks, rows and columns which have been cleared
     * Updates the pieces destroyed listener
     */
    private void afterPiece(){
        ArrayList<Integer> columnsToClear = new ArrayList();
        List<Integer[]> coords = new ArrayList<Integer[]>();
        int lines = 0;
        int blocks = 0;
        int count;
        //check rows
        for (int i = 0; i < getCols(); i++) {
            count = 0;
            for (int j = 0; j < getRows(); j++) {
                if (grid.get(i,j) != 0){
                    count++;
                }
            }
            if (count == getRows()){
                columnsToClear.add(i);
                lines++;
            }
        }
        //check columns and add blocks to be removed
        for (int i = 0; i < getRows(); i++) {
            count = 0;
            for (int j = 0; j < getCols(); j++) {
                if (grid.get(j,i) != 0){
                    count++;
                }
            }
            if (count == getCols()){
                lines++;
                for (int j = 0; j < getCols(); j++) {
                    if (grid.get(j,i) != 0){
                        coords.add(new Integer[]{j, i});
                        blocks++;
                    }

                }
            }
        }
        //add blocks from cleared columns to be removed while avoiding duplicates
        for (Integer col: columnsToClear) {
            for (int i = 0; i < getRows(); i++) {
                if (grid.get(col,i) != 0){
                    coords.add(new Integer[]{col, i});
                    blocks++;
                }
            }
        }
        //update the listener
        if (piecesDestroyedListener != null && coords.size() > 0) {
            int[][] cs = new int[coords.size()][2];
            for (int i = 0; i < coords.size(); i++) {
                cs[i] = new int[]{coords.get(i)[0], coords.get(i)[1], grid.get(coords.get(i)[0],coords.get(i)[1])};
                grid.set(coords.get(i)[0],coords.get(i)[1],0);
            }
            piecesDestroyedListener.piecesDestroyed(cs);
        }
        //update the score
        score(lines, blocks);

        //update the multiplier
        if (lines > 0){
            multiplier.setValue(multiplier.getValue()+1);
        }else{
            multiplier.setValue(1);
        }

    }

    /**
     * The Score Property
     * @return the current score
     */
    public IntegerProperty scoreProperty() {
        return score;
    }

    /**
     * The Top Score Property
     * @return the top score achieved locally
     */
    public IntegerProperty topScoreProperty() {
        return topScore;
    }

    /**
     * The Level Property
     * @return the level of the user
     */
    public IntegerProperty levelProperty() {
        return level;
    }

    /**
     * The Lives Property
     * @return the number of lives remaining
     */
    public IntegerProperty livesProperty() {
        return lives;
    }

    /**
     * The Multiplier property
     * @return the current multiplier
     */
    public IntegerProperty multiplierProperty() {
        return multiplier;
    }

    /**
     * The amount of time before the next piece is supposed to be placed
     * @return the amount of time
     */
    private int getTimerDelay(){
        if (12000 - 500 * levelProperty().get() > 2500){
            return 12000 - 500 * levelProperty().get();
        }else {
            return  2500;
        }
    }

}
