package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.Prompt;
import uk.ac.soton.comp1206.component.UserList;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utilities.Multimedia;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Game Lobby including chat window and user list
 */
public class GameLobbyScene extends BaseScene{
    /**
     * Its like a woodcutter except totally different
     */
    private static final Logger logger = LogManager.getLogger(OptionsScene.class);
    /**
     * Necessary for communication with the server
     */
    private Communicator communicator;
    /**
     * The chat window where all the user messages are contained
     */
    private TextFlow messages;
    /**
     * The message entry box
     */
    private TextField messageToSend;
    /**
     * The current message container
     */
    private HBox sendMessageBar;
    /**
     * A scrollable pane to store the messages
     */
    private ScrollPane scroller;
    /**
     * The Side Pane
     */
    private UserList userList;
    /**
     * The main pain for your lobby
     */
    private BorderPane lobbyPane;
    /**
     * encapsulates the main pain so I can overlay the prompt
     */
    private StackPane basePane;
    /**
     * The list of users connected to your lobby
     */
    private String users;
    /**
     * Do I jump to the bottom?
     */
    private boolean scrollToBottom = false;
    /**
     * Am I the host
     */
    private boolean host = false;
    /**
     * What is my username?
     */
    private SimpleStringProperty username = new SimpleStringProperty("");


    /**
     * Create a new Chat Window, linked to the main App and the Communicator
     * @param gameWindow the game window
     * @param channel the channel name
     * @param hosting whether the user is the host
     * @param name the user's name
     * @param users the initial list of users
     */
    public GameLobbyScene(GameWindow gameWindow,String channel,boolean hosting,String name,String users, Communicator communicator) {
        super(gameWindow);
        this.host = hosting;
        this.username.setValue(name);
        this.users = users;
        this.communicator = communicator;
        logger.info("Creating Game Lobby Scene");



    }

    /**
     * Build the scene layout
     */

    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        Multimedia.playMedia("menu.mp3");
        //Setup scene with a border pane
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());
        basePane = new StackPane();
        lobbyPane = new BorderPane();
        lobbyPane.setMaxWidth(gameWindow.getWidth());
        lobbyPane.setMaxHeight(gameWindow.getHeight());
        lobbyPane.getStyleClass().add("scene-background");
        lobbyPane.setBackground(gameWindow.getBackground());
        basePane.setBackground(gameWindow.getBackground());
        root.getChildren().add(basePane);
        basePane.getChildren().add(lobbyPane);


        //Userlist pane on right
        userList = new UserList();
        lobbyPane.setRight(userList);
        userList.getUsernameField().textProperty().bindBidirectional(username);
        userList.addUsers(users);

        if (host){
            HBox startButton = userList.addStartButton();
            if (startButton != null){
                startButton.setOnMouseClicked((x) -> {communicator.send("START");});
            }
        }


        //Add listener for incoming messages
        communicator.clearListeners();
        communicator.addListener((msg) -> Platform.runLater(() -> receiveMessage(msg)));


        //Create a horizontal bar with a text box and send button
        messageToSend = new TextField();
        messageToSend.setPromptText("Enter message");
        Button sendMessage = new Button("Send");
        sendMessageBar = new HBox();
        sendMessageBar.getChildren().add(messageToSend);
        sendMessageBar.getChildren().add(sendMessage);
        HBox.setHgrow(messageToSend,Priority.ALWAYS);
        lobbyPane.setBottom(sendMessageBar);

        //Create a textflow to hold all messages
        messages = new TextFlow();
        messages.getStyleClass().add("messages");

        //Add a scrollpane
        scroller = new ScrollPane();
        scroller.getStyleClass().add("messagePane");
        scroller.setContent(messages);
        scroller.setFitToWidth(true);
        lobbyPane.setCenter(scroller);
        scroller.setPadding(new Insets(16));

        //Make the send button send a message
        sendMessage.setOnAction((event)-> sendCurrentMessage(messageToSend.getText()));

        //Make pressing enter on the text field send a message
        messageToSend.setOnKeyPressed((event) -> {
            if (event.getCode() != KeyCode.ENTER) return;
            if (!messageToSend.isFocused()) return;
            sendCurrentMessage(messageToSend.getText());
        });
        username.addListener(this::updateName);


        //Add listener for updating scroller
        gameWindow.getScene().addPostLayoutPulseListener(this::jumpToBottom);

    }

    /**
     * Triggers whenever the username is updated
     * @param observableValue the trigger event
     * @param s the oldname
     * @param s1 the new name
     */
    private void updateName(ObservableValue<? extends String> observableValue, String s, String s1) {
        if (!s.equals(s1)) {
            communicator.send("NICK " + s1);
        }
    }
    /**
     * Handle an incoming message from the Communicator
     * @param message The message that has been received, in the form User:Message
     */
    private void receiveMessage(String message){
        if (message.startsWith("MSG ")){
            addMessage(message);
        }else if (message.startsWith("USERS ")){
            addNotification(message.replace("\n"," "));
            userList.addUsers(message);
        }else if (message.startsWith("NICK ")){
            if (message.contains(":")) {
                var components = message.substring(5).split(":");
                addNotification("Nickname changed: " + components[0] + " -> " + components[1]);
            }
//            rename(message);
        }else if (message.startsWith("START")){
            addNotification(message);
            userList.setPlaying(scroller, messages, sendMessageBar, communicator);
            gameWindow.startMultiplayer(communicator,userList);
        }else if (message.startsWith("SCORES ")){
            userList.addUsers(message);
        }else if (message.startsWith("HOST")){
            addNotification("You have been made host");
            host = true;
            HBox startButton = userList.addStartButton();
            if (startButton != null){
                startButton.setOnMouseClicked((x) -> {communicator.send("START");});
            }
        }else if (message.startsWith("ERROR ")){
            addNotification(message);
            alert(message);
        }
    }

    /**
     * Trigger a user prompt
     * @param message the alert message
     */
    private void alert(String message) {

        Text promptText = new Text(message);
        Prompt alert = new Prompt("Alert", promptText, gameWindow.getWidth());
        basePane.getChildren().add(alert);
        alert.setOnExit(() -> {basePane.getChildren().removeAll(alert);});
    }



    /**
     * Move the scroller to the bottom
     */
    private void jumpToBottom() {
        if (!scrollToBottom) return;
        scroller.setVvalue(1.0f);
        scrollToBottom = false;
    }

    /**
     * Add a received message to the messages TextFlow
     * @param message the message to be added
     */
    public void addMessage(String message) {

        var components = message.substring(4).split(":", 2);
        var username = components[0];
        var text = components[1];

        //Play incoming message sound
        Multimedia.playAudio("message.wav");

        //Make the message into a Text node
        Text receivedMessage = new Text(username + ": " + text + "\n");
        receivedMessage.getStyleClass().add("messages");
        receivedMessage.setStroke(Color.WHITE);

        //Add this message to the TextFlow
        messages.getChildren().add(receivedMessage);

        //Scroll to bottom
        if(scroller.getVvalue() == 0.0f || scroller.getVvalue() > 0.9f) {
            scrollToBottom = true;
        }
    }
    /**
     * Add a received notification to the messages TextFlow
     * @param message the notification to be added
     */
    public void addNotification(String message) {

        //Play incoming message sound
        Multimedia.playAudio("message.wav");

        //Make the message into a Text node
        Text receivedMessage = new Text(message + "\n");
        receivedMessage.getStyleClass().add("messages");
        receivedMessage.setFill(Color.YELLOW);
        receivedMessage.setStroke(Color.YELLOW);

        //Add this message to the TextFlow
        messages.getChildren().add(receivedMessage);

        //Scroll to bottom
        if(scroller.getVvalue() == 0.0f || scroller.getVvalue() > 0.9f) {
            scrollToBottom = true;
        }
    }

    /**
     * Send an outgoing message from the Chatwindow
     * @param text The text of the message to send to the Communicator
     */
    private void sendCurrentMessage(String text) {
        //Send the message to the communicator
        communicator.send("MSG " + text);

        //Clear the text input box
        messageToSend.clear();
    }

    /**
     * Leave the game lobby and go back to the main waiting room
     */
    public void exit() {
        communicator.send("PART");
        gameWindow.startLobby();
    }

    /**
     * Start the scene
     */
    @Override
    public void initialise() {

        getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case ESCAPE:    exit(); break;
                }
            }
        });
    }
}
