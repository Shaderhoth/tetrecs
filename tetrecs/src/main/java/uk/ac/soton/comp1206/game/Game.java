package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.event.BlockClickedListener;
import uk.ac.soton.comp1206.event.PieceSpawnedListener;
import uk.ac.soton.comp1206.event.PiecesDestroyedListener;
import uk.ac.soton.comp1206.scene.ChallengeScene;
import uk.ac.soton.comp1206.utilities.Multimedia;

import java.util.ArrayList;
import java.util.List;
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

    private PieceSpawnedListener pieceSpawnedListener;
    private PiecesDestroyedListener piecesDestroyedListener;
    private static final Logger logger = LogManager.getLogger(Game.class);
    private int x = 0;
    private int y = 0;


    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;
    private GamePiece currentPiece;
    private GamePiece followingPiece;
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
        followingPiece = spawnPiece();
        nextPiece();
        initialiseGame();
    }

    public void initialiseGame() {
        logger.info("Initialising game");
    }

    public void updatePieceBoards(){
        if (pieceSpawnedListener != null) {
            pieceSpawnedListener.pieceSpawned(currentPiece, followingPiece);
        }
    }
    public void blockClicked() {
        if (grid.canPlayPiece(currentPiece,getX(),getY())){
            grid.playPiece(currentPiece,getX(),getY());
            Multimedia.playAudio("place.wav");
        }else {
            Multimedia.playAudio("explode.wav");
        }

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

    public void setAim(int x, int y){
        this.x = x;
        this.y = y;
    }
    public int getX(){
        return x;
    }public int getY(){
        return y;
    }

    public GamePiece spawnPiece(){
        GamePiece g = GamePiece.createPiece(new Random().nextInt(0,14));

        return g;
    }
    public void setOnPieceSpawned(PieceSpawnedListener listener) {
        this.pieceSpawnedListener = listener;
    }
    public void nextPiece(){
        currentPiece = followingPiece;
        followingPiece  = spawnPiece();
        updatePieceBoards();
    }
    public void rotateCurrentPiece(int count){
        currentPiece.rotate(count);
        updatePieceBoards();
    }
    public void swapCurrentPiece(){
        GamePiece temp = currentPiece;
        currentPiece = followingPiece;
        followingPiece  = temp;
        updatePieceBoards();
    }
    public void score(int lines, int blocks){
        score.setValue(score.getValue() + lines * blocks * 10 * multiplier.getValue());

    }

    public void afterPiece(){
        ArrayList<Integer> columnsToClear = new ArrayList();
        List<Integer[]> coords = new ArrayList<Integer[]>();
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
            if (count == getRows()){
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
        for (Integer col: columnsToClear) {
            for (int i = 0; i < getRows(); i++) {
                if (grid.get(col,i) != 0){
                    coords.add(new Integer[]{col, i});
                    blocks++;
                }
            }
        }
        if (piecesDestroyedListener != null && coords.size() > 0) {
            int[][] cs = new int[coords.size()][2];
            for (int i = 0; i < coords.size(); i++) {
                cs[i] = new int[]{coords.get(i)[0], coords.get(i)[1]};
            }
            piecesDestroyedListener.piecesDestroyed(cs);
        }
        score(lines, blocks);
        if (lines > 0){
            multiplier.setValue(multiplier.getValue()+1);
        }else{
            multiplier.setValue(1);
        }

    }
    public void setOnPiecesDestroyed(PiecesDestroyedListener listener) {
        this.piecesDestroyedListener = listener;
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
