package uk.ac.soton.comp1206.component;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.scene.ChallengeScene;
import uk.ac.soton.comp1206.scene.ScoreScene;
import uk.ac.soton.comp1206.utilities.Multimedia;

import java.util.ArrayList;
import java.util.List;

/**
 * Heavily inspired by the UserList from ECS Chat
 * Basically the SideBar but with different information, and with the only common aspects being that they are both bars
 * on the side of the screen that can shrink into the side
 */
public class UserList extends VBox {
    /**
     * Tells you what's going on
     */
    private static final Logger logger = LogManager.getLogger(UserList.class);
    /**
     * A pane containing the collection of users
     */
    private VBox users;
    /**
     * A scrollable pane
     */
    private ScrollPane scroller;
    /**
     * The window containing the messages
     */
    private BorderPane chatWindow;
    /**
     * An input box containing the name of the user
     */
    private TextField username;
    /**
     * A start button which only appears if you are host
     * Starts the game
     */
    private HBox startButton;
    /**
     * The chat window where all the user messages are contained
     */
    private TextFlow messages;
    /**
     * A list of the users in the game lobby
     */
    private SimpleListProperty<String> usersList = new SimpleListProperty();
    /**
     * The width of the sidebar
     */
    private final int width = 256;
    /**
     * Is the sidebar visible?
     */
    private boolean visible;
    /**
     * Am I in a game?
     */
    private boolean playing = false;
    /**
     * Do I jump to the bottom?
     */
    private boolean scrollToBottom = false;


    /**
     * Initialise the list
     */
    public UserList(){
        setPrefWidth(width);
        setPadding(new Insets(8,8,8,8));
        getStyleClass().add("userlist");

        build();
    }

    /**
     * Build the layout
     */
    public void build() {
        var image = new ImageView(new Image(this.getClass().getResource("/images/ECS.png").toExternalForm()));
        image.setPreserveRatio(true);
        image.setFitWidth(64);
        setSpacing(8);
        setAlignment(Pos.TOP_CENTER);
        getChildren().add(image);

        //Add a username field
        username = new TextField();
        getChildren().add(username);

        //Add the list of users
        users = new VBox();
        users.setSpacing(16);
        users.setPadding(new Insets(8,8,8,8));

        //make users scrollable
        scroller = new ScrollPane();
        scroller.setContent(users);
        scroller.setFitToWidth(true);
        scroller.getStyleClass().add("userlist-pane");

        getChildren().add(users);
        image.setOnMouseClicked((e) -> toggleSidebar());
    }

    /**
     * Make a start button
     * @return the start button object
     */
    public HBox addStartButton(){
        if (startButton == (null)) {
            startButton = new HBox();
            Text startText = new Text("START");
            startText.getStyleClass().add("title");
            startButton.getStyleClass().add("channelItemButton");
            startButton.setAlignment(Pos.CENTER);
            startButton.getChildren().add(startText);
            getChildren().add(startButton);
            return startButton;
        }
        return null;

    }

    /**
     * Clear the list of users
     */
    public void clearUsers() {
        users.getChildren().removeAll(users.getChildren());
    }

    /**
     * Sets the pane to show the chatwindow and leaderboard
     */
    public void setPlaying(ScrollPane scroller, TextFlow messages, HBox sendMessageBar, Communicator communicator){
        if (startButton != null) {
            getChildren().removeAll(startButton);
        }
        chatWindow = new BorderPane();
        chatWindow.setTop(scroller);
        chatWindow.setBottom(sendMessageBar);
        chatWindow.setPrefHeight(getHeight());
        getChildren().add(chatWindow);
        getScene().addPostLayoutPulseListener(this::jumpToBottom);
        this.messages = messages;
    }
    public boolean msgIsFocused(){
        return (((HBox) chatWindow.getBottom()).getChildren().get(0)).isFocused();
    }


    public void reqMsgFocus() {
        (((HBox) chatWindow.getBottom()).getChildren().get(0)).requestFocus();
    }
    /**
     * Add a message to the chat window
     * @param message The message to add
     */
    public void addMessage(String message){
        if (messages != null){
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
    }

    /**
     * gets the username field
     * @return the username
     */
    public TextField getUsernameField() {
        return username;
    }

    /**
     * Gets a list of the users
     * @return the list of users
     */
    public SimpleListProperty<String> getUsers() {
        return usersList;
    }

    /**
     * Add a user to the users pane
     * @param username the name of the user
     */
    public void addUser(String username){
        var userBox = new HBox();
        userBox.getStyleClass().add("user");
        userBox.setSpacing(8);
        var user = new Text(username);
        user.getStyleClass().add("channelItemText");
        userBox.getChildren().add(user);
        userBox.getStyleClass().add("channelItem");

        HBox.setHgrow(user,Priority.ALWAYS);
        userBox.setMinHeight(32);
        userBox.setAlignment(Pos.CENTER);

        users.getChildren().add(userBox);
    }
    /**
     * Add a user to the users pane
     * @param components a collection of components to store the user data
     */
    public void addUser(String[] components){
        var userBox = new HBox();
        userBox.getStyleClass().add("user");
        userBox.setSpacing(8);
        var name = new Text(components[0]);
        name.getStyleClass().add("channelItemText");
        userBox.getChildren().add(name);
        var score = new Text(components[1]);
        score.getStyleClass().add("channelItemText");
        userBox.getChildren().add(score);
        var lives = new Text(components[2]);
        lives.getStyleClass().add("channelItemText");
        userBox.getChildren().add(lives);
        userBox.getStyleClass().add("channelItem");
        if (components[2].equals("DEAD")){
            for (var child: userBox.getChildren().toArray()) {
                ((Text) child).setFill(Color.RED);
            }
        }
        HBox.setHgrow(name,Priority.ALWAYS);
        userBox.setMinHeight(32);
        userBox.setSpacing(8);
        userBox.setAlignment(Pos.CENTER);

        users.getChildren().add(userBox);
    }

    /**
     * Adds a list of users to the user pane
     * @param message the message containing the list of users to be parsed
     */
    public void addUsers(String message){
        logger.info(message);
        clearUsers();
        if (message.startsWith("USERS ")) {
            for (String user : message.substring(6).split("\n")) {
                addUser(user);
            }
        } else if (message.startsWith("SCORES ")){

            for (String user : message.substring(7).split("\n")) {
                var components = user.split(":");
                addUser(components);
            }
        }
    }

    /**
     * Toggle the visibility of the sidebar
     */
    private void toggleSidebar() {
        if(visible) {
            visible = false;
            for(var child : getChildren()) {
                if(child instanceof ImageView) continue;
                child.setVisible(false);
            }
            Duration duration = Duration.millis(512);
            Timeline timeline = new Timeline(
                    new KeyFrame(duration,new KeyValue(this.prefWidthProperty(), 64, Interpolator.EASE_BOTH))
            );
            timeline.play();
        } else {
            visible = true;
            Duration duration = Duration.millis(512);
            Timeline timeline = new Timeline(
                    new KeyFrame(duration,new KeyValue(this.prefWidthProperty(), width, Interpolator.EASE_BOTH))
            );
            timeline.play();
            timeline.setOnFinished((e) -> {
                for(var child : getChildren()) {
                    if(child instanceof ImageView) continue;
                    child.setVisible(true);
                }
            });
        }
    }
    public String getUsername(){
        return username.getText();
    }


    /**
     * Move the scroller to the bottom
     */
    private void jumpToBottom() {
        if (!scrollToBottom) return;
        scroller.setVvalue(1.0f);
        scrollToBottom = false;
    }
}
