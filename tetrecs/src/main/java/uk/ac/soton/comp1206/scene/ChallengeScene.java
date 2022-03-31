package uk.ac.soton.comp1206.scene;

import javafx.beans.property.Property;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.event.PieceSpawnedListener;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utilities.Multimedia;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene implements PieceSpawnedListener {

    private static final Logger logger = LogManager.getLogger(ChallengeScene.class);
    private PieceBoard pieceBoard;
    private PieceBoard nextpieceBoard;
    private GameBoard board;
    private SideBar sidePane;
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
        Multimedia media = new Multimedia("game.wav");
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        board = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);
        mainPane.setCenter(board);

        sidePane = new SideBar();
        mainPane.setRight(sidePane);
        sidePane.getScoreField().textProperty().bind(game.scoreProperty().asString());
        sidePane.getLevelField().textProperty().bind(game.livesProperty().asString());
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

    }

    /**
     * Handle when a block is clicked
     * @param gameBlock the Game Block that was clocked
     */
    private void blockHovered(MouseEvent event, GameBlock gameBlock) {
        board.resetBoard();
        game.setAim(gameBlock.getX(), gameBlock.getY());
        board.target(game.getX(),game.getY());

    }
    private void blockClicked(MouseEvent event, GameBlock gameBlock) {
        logger.info(gameBlock.getGameBoard().equals(board));
        if (event.getButton() == MouseButton.PRIMARY) {
            game.blockClicked();
        }else if (event.getButton() == MouseButton.SECONDARY){
            game.rotateCurrentPiece(1);

        }

    }private void rotatePiece(MouseEvent event, GameBlock gameBlock) {
        Multimedia.playAudio("rotate.wav");
        game.rotateCurrentPiece(1);
        board.resetBoard();
        board.target(game.getX(),game.getY());
    }private void swapPiece(MouseEvent event, GameBlock gameBlock) {
        Multimedia.playAudio("pling.wav");
        game.swapCurrentPiece();
    }
    public void pieceSpawned(GamePiece nextGamePiece, GamePiece followingGamePiece) {
        pieceBoard.displayPiece(nextGamePiece);
        nextpieceBoard.displayPiece(followingGamePiece);
        board.setNextPiece(nextGamePiece);

    }
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
    public void moveAim(int x, int y){
        board.resetBoard();
        game.setAim(game.getX()+x, game.getY()+y);
        board.target(game.getX(),game.getY());
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
                    case ESCAPE:    gameWindow.startMenu(); break;
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
