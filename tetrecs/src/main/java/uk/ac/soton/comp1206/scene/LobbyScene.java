package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.ChannelButton;
import uk.ac.soton.comp1206.component.Prompt;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utilities.Multimedia;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A lobby containing a list of all available multiplayer games
 */
public class LobbyScene extends BaseScene {
    /**
     * Logging is cool
     */
    private static final Logger logger = LogManager.getLogger(LobbyScene.class);
    /**
     * A tool to talk to the server which for some reason requires you to connect to a vpn
     */
    private static final Communicator communicator = new Communicator("ws://ofb-labs.soton.ac.uk:9700");
    /**
     * A media player tool
     */
    private Multimedia media;
    /**
     * A timer to ping the server for available games
     */
    private Timer timer;
    /**
     * The process being executed by the timer
     */
    private TimerTask task;
    /**
     * A pane to store the list of available games
     */
    private VBox channelPane;
    /**
     * The main pane
     */
    private StackPane lobbyPane;
    /**
     * The channel you are connecting to
     */
    private String channel = null;
    /**
     * whether you are the host of the game
     */
    private boolean hosting = false;
    /**
     * The nickname you are assigned by the server
     */
    private String name = null;
    /**
     * A list of users connected to the game
     */
    private String users = null;
    /**
     * The delay before each successive request of active games
     */
    private long delay = 10000;
    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public LobbyScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Lobby Scene");
    }

    /**
     * A thread which requests a list of available games from the server every {delay} milliseconds
     */
    private void requestChannels(){

        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                communicator.send("LIST");
            }
        };
        timer.scheduleAtFixedRate(task, 0, delay);
    }

    /**
     * A script which executes whenever you receive data from the server
     * @param message the data received
     */
    private void receiveMessage(String message){
        if (message.startsWith("CHANNELS ")){
            channelPane.getChildren().removeAll(channelPane.getChildren());
            for (String channel: message.substring(9).split("\n")) {
                if (!channel.equals("")){
                    logger.info(channel);
                    channelPane.getChildren().add(new ChannelButton(channel, communicator));
                }

            }
        }else if (message.startsWith("JOIN ")){
            if (timer != null){
                timer.cancel();
            }
            channel = message.substring(5);
        }else if (message.startsWith("HOST")){
            hosting = true;
        }else if (message.startsWith("NICK ")){
            name = message.substring(5);
        }else if (message.startsWith("USERS ")){
            users = message;
            gameWindow.startGameLobby(channel,hosting,name,users, communicator);
        }else if (message.startsWith("ERROR ")){
            alert(message);
        }
    }

    /**
     * Creates a channel to represent a game to join
     * @param event
     */
    private void createChannel(MouseEvent event) {
        TextField getChannelName = new TextField("");
        Prompt prompt = new Prompt("Create Channel", getChannelName, gameWindow.getWidth());
        lobbyPane.getChildren().add(prompt);
        prompt.setOnExit(() -> {
            lobbyPane.getChildren().removeAll(prompt);
            communicator.send("CREATE " + ((TextField) prompt.getChildren().get(1)).getText());
        });

    }

    /**
     * Creates an alert window if an error occurs
     * @param message
     */
    private void alert(String message) {

        Text promptText = new Text(message);
        Prompt alert = new Prompt("ALERT", promptText, gameWindow.getWidth());
        lobbyPane.getChildren().add(alert);
        alert.setOnExit(() -> {lobbyPane.getChildren().removeAll(alert);});
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());
        Multimedia.playMedia("menu.mp3");
        communicator.addListener((message) -> Platform.runLater(() -> this.receiveMessage(message)));
        requestChannels();



        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());
        lobbyPane = new StackPane();
        lobbyPane.setMaxWidth(gameWindow.getWidth());
        lobbyPane.setMaxHeight(gameWindow.getHeight());
        lobbyPane.getStyleClass().add("scene-background");
        lobbyPane.setBackground(gameWindow.getBackground());
        root.getChildren().add(lobbyPane);


        BorderPane mainPane = new BorderPane();
        mainPane.setBackground(gameWindow.getBackground());
        mainPane.setMaxWidth(gameWindow.getWidth());
        mainPane.setMinWidth(gameWindow.getWidth());
        lobbyPane.getChildren().add(mainPane);

        HBox topBar = new HBox();
        Text title = new Text("Lobby");
        title.getStyleClass().add("bigtitle");
        topBar.getChildren().add(title);
        topBar.setMaxWidth(gameWindow.getWidth());
        topBar.setMinWidth(gameWindow.getWidth());
        topBar.setAlignment(Pos.CENTER);
        mainPane.setTop(topBar);


        channelPane = new VBox();
        channelPane.setAlignment(Pos.CENTER);

        //Add a scrollpane
        ScrollPane scroller = new ScrollPane();
        scroller.getStyleClass().add("messagePane");
        scroller.setContent(channelPane);
        scroller.setFitToWidth(true);
        mainPane.setCenter(scroller);
        scroller.setPadding(new Insets(16));

        HBox bottomBar = new HBox();
        HBox newChannel = new HBox();
        Text newChannelText = new Text("Create Channel");
        newChannelText.getStyleClass().add("channelItemText");
        newChannel.getChildren().add(newChannelText);
        newChannel.getStyleClass().add("channelItemButton");
        newChannel.setOnMouseClicked(this::createChannel);
        newChannel.setMaxWidth(gameWindow.getWidth()/3);
        newChannel.setMinWidth(gameWindow.getWidth()/3);
        newChannel.setAlignment(Pos.CENTER);
        bottomBar.getChildren().add(newChannel);
        bottomBar.setMaxWidth(gameWindow.getWidth());
        bottomBar.setMinWidth(gameWindow.getWidth());
        bottomBar.setAlignment(Pos.CENTER);
        mainPane.setBottom(bottomBar);
        lobbyPane.setAlignment(Pos.CENTER);



    }

    /**
     * Exits the lobby
     */
    public void exit() {
        communicator.send("QUIT");
        gameWindow.startMenu();
    }


    /**
     * Initialise the lobby
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