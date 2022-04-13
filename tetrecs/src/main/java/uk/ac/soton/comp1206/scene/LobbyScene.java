package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
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


public class LobbyScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(LobbyScene.class);
    private static final Communicator communicator = new Communicator("ws://ofb-labs.soton.ac.uk:9700");
    private Multimedia media;
    private Timer timer;
    private TimerTask task;
    private VBox channelPane;
    private StackPane lobbyPane;
    private String channel = null;
    private boolean hosting = false;
    private String name = null;
    private String[] users = null;

    private long delay = 10000;
    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public LobbyScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Lobby Scene");
    }

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
            users = message.substring(6).split("\n");
            gameWindow.startGameLobby(channel,hosting,name,users, communicator);
        }else if (message.startsWith("ERROR ")){
            alert(message);
        }
    }
    private void createChannel(MouseEvent event) {
        TextField getChannelName = new TextField("");
        Prompt prompt = new Prompt("Create Channel", getChannelName, gameWindow.getWidth());
        lobbyPane.getChildren().add(prompt);
        prompt.setOnExit(() -> {
            lobbyPane.getChildren().removeAll(prompt);
            communicator.send("CREATE " + ((TextField) prompt.getChildren().get(1)).getText());
        });

    }
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
        communicator.addListener((message) -> Platform.runLater(() -> this.receiveMessage(message)));
        requestChannels();



        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());
        lobbyPane = new StackPane();
        lobbyPane.setMaxWidth(gameWindow.getWidth());
        lobbyPane.setMaxHeight(gameWindow.getHeight());
        lobbyPane.getStyleClass().add("menu-background");
        root.getChildren().add(lobbyPane);


        BorderPane mainPane = new BorderPane();
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
        mainPane.setCenter(channelPane);
        channelPane.setAlignment(Pos.CENTER);

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