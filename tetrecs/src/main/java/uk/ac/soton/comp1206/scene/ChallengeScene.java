package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.component.SideBar;
import uk.ac.soton.comp1206.event.PieceSpawnedListener;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utilities.Multimedia;

import java.util.Objects;


/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene implements PieceSpawnedListener {
    /**
     * Loggers are always important
     */
    private static final Logger logger = LogManager.getLogger(ChallengeScene.class);
    /**
     * The board showing the current piece in play
     */
    private PieceBoard pieceBoard;
    /**
     * The board showing the next piece in play
     */
    private PieceBoard nextpieceBoard;
    /**
     * The board showing the current game
     */
    protected GameBoard board;
    /**
     * The main window pane
     */
    protected BorderPane mainPane;
    /**
     * The base pane for the window
     */
    protected StackPane challengePane;
    /**
     * The sidebar showing various stats and useful info
     */
    private SideBar sidePane;
    /**
     * The media object
     * The thing that plays the music
     */
    Multimedia media;
    /**
     * The game
     */
    protected Game game;

    /**
     * Create a new Single Player challenge scene
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {

        logger.info("Building " + this.getClass().getName());

        setupGame();
        Multimedia.playMedia("game.wav");
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("scene-background");
        root.getChildren().add(challengePane);

        mainPane = new BorderPane();
        mainPane.setBackground(gameWindow.getBackground());
        challengePane.getChildren().add(mainPane);

        board = makeBoard();
        mainPane.setCenter(board);

        sidePane = new SideBar();
        mainPane.setRight(sidePane);

        sidePane.getTopScoreField().textProperty().bind(game.topScoreProperty().asString());
        sidePane.getScoreField().textProperty().bind(game.scoreProperty().asString());
        sidePane.getLevelField().textProperty().bind(game.levelProperty().asString());
        sidePane.getLivesField().textProperty().bind(game.livesProperty().asString());
        sidePane.getMultiplierField().textProperty().bind(game.multiplierProperty().asString());
        pieceBoard = new PieceBoard(128,128);
        sidePane.getChildren().add(pieceBoard);
        nextpieceBoard = new PieceBoard(96,96);
        sidePane.getChildren().add(nextpieceBoard);

        board.setOnBlockHover(this::blockHovered);
        board.setOnBlockClick(this::blockClicked);
        pieceBoard.setOnBlockClick(this::rotatePiece);
        nextpieceBoard.setOnBlockClick(this::swapPiece);
        game.setOnPieceSpawned(this::pieceSpawned);
        game.setOnPiecesDestroyed(this::piecesDestroyed);
        game.setOnGameLoop(this::gameLooped);
        game.scoreProperty().addListener(this::checkLevelUp);

        game.setOnGameOver(() -> Platform.runLater(() -> gameOver()));



    }

    /**
     * Makes a game-board
     * Done separately to the rest, so I can override it on the multiplayer scene while having the rest of the build
     * class work
     * @return game board
     */
    protected GameBoard makeBoard(){
        return new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);
    }

    /**
     * Check whether the user has leveled up
     * @param observable the trigger event (Score being updated)
     */
    private void checkLevelUp(Observable observable) {
        if (game.scoreProperty().getValue()/1000 > game.levelProperty().getValue()) {
            game.levelProperty().setValue(game.scoreProperty().getValue()/1000 );
        }
    }

    /**
     * Finishes the game
     */
    public void gameOver() {
        Multimedia.playAudio("fail.wav");
        game.endTimer();
        gameWindow.startEnd(game.scoreProperty().getValue());

    }

    /**
     * Updates the timer on the sidepane each time the game is looped
     * @param time the amount of time the timer needs to represent
     */
    private void gameLooped(int time){
        logger.info("Setting timer " + time);
        sidePane.setTimer(time);
    }

    /**
     * Get the currently targeted block and set the target in the game
     * @param event the trigger mouse hover event
     * @param gameBlock the gameblock that was hovered over
     */
    private void blockHovered(MouseEvent event, GameBlock gameBlock) {
        board.resetBoard();
        game.setAim(gameBlock.getX(), gameBlock.getY());
        board.target(game.getX(),game.getY());

    }

    /**
     * Conveys block clicked information to the game
     * @param event the click event
     * @param gameBlock the block that was clicked
     */
    protected void blockClicked(MouseEvent event, GameBlock gameBlock) {
        logger.info(gameBlock.getGameBoard().equals(board));
        if (event.getButton() == MouseButton.PRIMARY) {
            game.blockClicked();
        }else if (event.getButton() == MouseButton.SECONDARY){
            game.rotateCurrentPiece(1);

        }

    }

    /**
     * Triggers the rotation of the piece on a board
     * @param event the trigger click event
     * @param gameBlock the block that was clicked
     */
    private void rotatePiece(MouseEvent event, GameBlock gameBlock) {
        Multimedia.playAudio("rotate.wav");
        game.rotateCurrentPiece(1);
        board.resetBoard();
        board.target(game.getX(),game.getY());
    }

    /**
     * Triggers the swapping of the current and the next piece
     * @param event the trigger click event
     * @param gameBlock the block that was clicked
     */
    private void swapPiece(MouseEvent event, GameBlock gameBlock) {
        Multimedia.playAudio("pling.wav");
        game.swapCurrentPiece();
    }

    /**
     * Triggers when a piece is spawned sending that information to the various boards
     * @param nextGamePiece the current game piece
     * @param followingGamePiece the next game piece
     */
    public void pieceSpawned(GamePiece nextGamePiece, GamePiece followingGamePiece) {
        pieceBoard.displayPiece(nextGamePiece);
        nextpieceBoard.displayPiece(followingGamePiece);
        board.setNextPiece(nextGamePiece);

    }

    /**
     * Triggers the fadeout effect of any blocks which were destroyed
     * @param coords
     */
    public void piecesDestroyed(int[][] coords){
        board.fadeOut(coords);
    }
    /**
     * Setup the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(5, 5);
    }

    /**
     * Change where the gameblock is being aimed towards
     * @param x the change in column info
     * @param y the change in row info
     */
    public void moveAim(int x, int y){
        board.resetBoard();
        game.setAim(Math.min(Math.max(game.getX()+x, 0), game.getCols()-1), Math.min(Math.max(game.getY()+y, 0), game.getRows()-1));
        board.target(game.getX(),game.getY());
    }

    /**
     * Exit the game
     */
    private void exit(){
        game.endTimer();
        gameWindow.startMenu();
    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case ESCAPE:    exit(); break;
                    case W:
                    case UP:
                        moveAim(0, -1); break;
                    case A:
                    case LEFT:
                        moveAim(-1, 0); break;
                    case S:
                    case DOWN:
                        moveAim(0, 1); break;
                    case D:
                    case RIGHT:
                        moveAim(1, 0); break;
                    case ENTER:
                    case X:
                        game.blockClicked();
                    case Q:
                    case OPEN_BRACKET:
                    case Z:
                        game.rotateCurrentPiece(1); break;
                    case E:
                    case CLOSE_BRACKET:
                    case C:
                        game.rotateCurrentPiece(3); break;
                    case R:
                    case SPACE:
                        game.swapCurrentPiece(); break;


                }
            }
        });
        logger.info("Initialising Challenge");
        game.start();
    }

}
