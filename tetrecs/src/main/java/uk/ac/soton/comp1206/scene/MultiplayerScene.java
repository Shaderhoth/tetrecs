package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.event.EventHandler;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.*;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utilities.Multimedia;

import java.util.LinkedList;
import java.util.Queue;

public class MultiplayerScene extends ChallengeScene{
    /**
     * Necessary for communication with the server
     */
    private Communicator communicator;
    /**
     * Loggers are always important
     */
    private static final Logger logger = LogManager.getLogger(MultiplayerScene.class);
    /**
     * The Side Pane
     */
    private UserList userList;
    /**
     * A queue of pieces to be used in the game
     */
    private Queue<Integer> queue = new LinkedList<>();
    /**
     * The last known scores (sent when loading the score screen)
     */
    private String lastKnownScores = "";

    /**
     * Create a new Multi Player challenge scene
     *
     * @param gameWindow the Game Window
     */
    public MultiplayerScene(GameWindow gameWindow, Communicator communicator, UserList userList) {
        super(gameWindow);
        this.userList = userList;
        this.communicator = communicator;
        //Add listener for incoming messages
        communicator.clearListeners();
        communicator.addListener((msg) -> Platform.runLater(() -> receiveMessage(msg)));
        //build up some pieces in the queue (the required 2 and an additional buffer piece)
        communicator.send("PIECE");
        communicator.send("PIECE");
        communicator.send("PIECE");


    }

    /**
     * generate the gameboard at 1/3 size
     * @return the gameboard
     */
    @Override
    protected GameBoard makeBoard(){
        return new GameBoard(game.getGrid(),gameWindow.getWidth()/3,gameWindow.getWidth()/3);
    }

    /**
     * Handle an incoming message from the Communicator
     * @param message The message that has been received, in the form User:Message
     */
    private void receiveMessage(String message){
        if (message.startsWith("MSG ")){
            userList.addMessage(message);
        }else if (message.startsWith("USERS ")){
            userList.addUsers(message);
        }else if (message.startsWith("SCORES ")){
            lastKnownScores = message;
            userList.addUsers(message);
        }else if (message.startsWith("PIECE ")){
            queue.offer(Integer.valueOf(message.substring(6)));
        }else if (message.startsWith("ERROR ")){
            alert(message);
        }else if (message.startsWith("BOARD ")){
            //Extension??
        }
    }

    /**
     * Trigger a user prompt
     * @param message the alert message
     */
    private void alert(String message) {

        Text promptText = new Text(message);
        Prompt alert = new Prompt("Alert", promptText, gameWindow.getWidth());
        super.challengePane.getChildren().add(alert);
        alert.setOnExit(() -> {mainPane.getChildren().removeAll(alert);});
    }


    /**
     * Setup the game object and model
     */
    @Override
    public void setupGame() {
        logger.info("Initialising the multi player game");
        game = new MultiplayerGame(5, 5, communicator, queue);
    }


    /**
     * Finishes the game
     */
    @Override
    public void gameOver() {
        communicator.send("DIE");
        Multimedia.playAudio("fail.wav");
        game.endTimer();
        gameWindow.startEnd(game.scoreProperty().getValue(), lastKnownScores, userList.getUsername());

    }
    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        super.build();

        //Userlist pane on left
        super.mainPane.setLeft(userList);


    }


    /**
     * Conveys block clicked information to the game
     * @param event the click event
     * @param gameBlock the block that was clicked
     */
    @Override
    protected void blockClicked(MouseEvent event, GameBlock gameBlock) {
        super.blockClicked(event, gameBlock);
        board.requestFocus();

    }

    /**
     * Exit the game
     */
    private void exit(){
        communicator.send("DIE");
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
                    case ESCAPE:
                        exit(); break;
                    case W:
                    case UP:
                        if(!userList.msgIsFocused()) moveAim(0, -1); break;
                    case A:
                    case LEFT:
                        if(!userList.msgIsFocused()) moveAim(-1, 0); break;
                    case S:
                    case DOWN:
                        if(!userList.msgIsFocused()) moveAim(0, 1); break;
                    case D:
                    case RIGHT:
                        if(!userList.msgIsFocused()) moveAim(1, 0); break;
                    case ENTER:
                    case X:
                        if(!userList.msgIsFocused()) game.blockClicked();
                    case Q:
                    case OPEN_BRACKET:
                    case Z:
                        if(!userList.msgIsFocused()) game.rotateCurrentPiece(1); break;
                    case E:
                    case CLOSE_BRACKET:
                    case C:
                        if(!userList.msgIsFocused()) game.rotateCurrentPiece(3); break;
                    case R:
                    case SPACE:
                        if(!userList.msgIsFocused()) game.swapCurrentPiece(); break;


                }
            }
        });
        logger.info("Initialising Challenge");
        game.start();
    }
}
