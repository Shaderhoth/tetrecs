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
 * Chat window which will display chat messages and a way to send new messages
 */
public class GameLobbyScene extends BaseScene{

    private static final Logger logger = LogManager.getLogger(GameLobbyScene.class);
    private Communicator communicator;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    private TextFlow messages;
    private TextField messageToSend;
    private ScrollPane scroller;
    private UserList userList;
    private BorderPane lobbyPane;
    private StackPane basePane;
    private SimpleListProperty<String> users;
    private boolean scrollToBottom = false;
    private boolean host = false;
    private SimpleStringProperty username = new SimpleStringProperty("");


    /**
     * Create a new Chat Window, linked to the main App and the Communicator
     * @param gameWindow the game window
     * @param channel the channel name
     * @param hosting whether the user is the host
     * @param name the user's name
     * @param users the initial list of users
     */
    public GameLobbyScene(GameWindow gameWindow,String channel,boolean hosting,String name,String[] users, Communicator communicator) {
        super(gameWindow);
        this.host = hosting;
        this.username.setValue(name);
        ArrayList<String> tempList= new ArrayList<>();
        for (String user:users) {
            tempList.add(user);
        }
        this.users = new SimpleListProperty(FXCollections.observableArrayList(tempList));
        this.communicator = communicator;
        logger.info("Creating Game Lobby Scene");



    }

    /**
     * Build the scene layout
     */

    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        //Setup scene with a border pane
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());
        basePane = new StackPane();
        lobbyPane = new BorderPane();
        lobbyPane.setMaxWidth(gameWindow.getWidth());
        lobbyPane.setMaxHeight(gameWindow.getHeight());
        lobbyPane.getStyleClass().add("menu-background");
        root.getChildren().add(basePane);
        basePane.getChildren().add(lobbyPane);


        //Userlist pane on right
        userList = new UserList();
        userList.getUsers().bindBidirectional(users);
        lobbyPane.setRight(userList);
        userList.getUsernameField().textProperty().bindBidirectional(username);
        users.add(username.getName());
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
        HBox sendMessageBar = new HBox();
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
            sendCurrentMessage(messageToSend.getText());
        });
        username.addListener(this::updateName);


        //Add listener for updating scroller
        gameWindow.getScene().addPostLayoutPulseListener(this::jumpToBottom);

    }

    private void updateName(ObservableValue<? extends String> observableValue, String s, String s1) {
        if (!s.equals(s1)) {
            communicator.send("NICK " + s1);
        }
    }

    private void addUsers(String message){
        for (String user:message.substring(6).split("\n")) {
            if (!users.contains(user)){
                users.add(user);
            }
        }
    }

    private void rename(String message){
        if(message.contains((":"))){
            var components = message.substring(5).split(":", 2);
            if (users.contains(components[0])){
                users.set(users.indexOf(components[0]),components[1]);
            }else{
                users.add(components[1]);
            }
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
            addUsers(message);
        }else if (message.startsWith("NICK ")){
            rename(message);
        }else if (message.startsWith("START ")){

        }else if (message.startsWith("HOST")){
            host = true;
            HBox startButton = userList.addStartButton();
            if (startButton != null){
                startButton.setOnMouseClicked((x) -> {communicator.send("START");});
            }
        }else if (message.startsWith("ERROR ")){
            alert(message);
        }
    }
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


    public void addMessage(String message) {

        var components = message.substring(4).split(":", 2);
        var username = components[0];
        var text = components[1];

        //Play incoming message sound
        Multimedia.playAudio("message.wav");

        //Make the message into a Text node
        Text receivedMessage = new Text(username + ": " + text + "\n");
        receivedMessage.getStyleClass().add("messages");

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
    public void exit() {
        communicator.send("PART");
        gameWindow.startLobby();
    }
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
