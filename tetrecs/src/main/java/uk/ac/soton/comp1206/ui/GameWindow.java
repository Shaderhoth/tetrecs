package uk.ac.soton.comp1206.ui;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.component.UserList;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.scene.*;
import uk.ac.soton.comp1206.utilities.Multimedia;

import java.util.Objects;

/**
 * The GameWindow is the single window for the game where everything takes place. To move between screens in the game,
 * we simply change the scene.
 *
 * The GameWindow has methods to launch each of the different parts of the game by switching scenes. You can add more
 * methods here to add more screens to the game.
 */
public class GameWindow {

    /**
     * The background
     */
    private Background background = new Background(new BackgroundImage(new Image(Objects.requireNonNull(this.getClass().getResource("/backgrounds/1.jpg")).toExternalForm()), BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,BackgroundSize.DEFAULT));
     /**
     * The background image
     */
    private Image image;
    /**
     * ...
     * Posts info to the console
     */
    private static final Logger logger = LogManager.getLogger(GameWindow.class);
    /**
     * the width of the window
     */
    private final int width;
    /**
     * The height of the window
     */
    private final int height;
    /**
     * The Stage
     */
    private final Stage stage;
    /**
     * The current scene being shown
     */
    private BaseScene currentScene;
    /**
     * The current scene
     */
    private Scene scene;
    /**
     * The communicator
     */
    final Communicator communicator;



    /**
     * Create a new GameWindow attached to the given stage with the specified width and height
     * @param stage stage
     * @param width width
     * @param height height
     */
    public GameWindow(Stage stage, int width, int height) {
        this.width = width;
        this.height = height;

        this.stage = stage;

        //Setup window
        setupStage();

        //Setup resources
        setupResources();

        //Setup default scene
        setupDefaultScene();

        //Setup communicator
        communicator = new Communicator("ws://ofb-labs.soton.ac.uk:9700");

        //Go to menu
        startMenu();
    }

    /**
     * Setup the font and any other resources we need
     */
    private void setupResources() {
        logger.info("Loading resources");

        //We need to load fonts here due to the Font loader bug with spaces in URLs in the CSS files
        Font.loadFont(getClass().getResourceAsStream("/style/Orbitron-Regular.ttf"),32);
        Font.loadFont(getClass().getResourceAsStream("/style/Orbitron-Bold.ttf"),32);
        Font.loadFont(getClass().getResourceAsStream("/style/Orbitron-ExtraBold.ttf"),32);
    }

    /**
     * Display the main menu
     */
    public void startMenu() {
        loadScene(new MenuScene(this));
    }

    /**
     * Display the options screen
     */
    public void startOptions() {
        loadScene(new OptionsScene(this));
    }

    /**
     * Show the End Screen
     * @param score the score achieved by the user
     */
    public void startEnd(int score) {
        loadScene(new EndScene(this, score));
    }

    /**
     * Show the End Screen
     * @param score the score achieved by the user
     * @param scores a list of multiplayer scores
     */
    public void startEnd(int score, String scores, String name) {
        loadScene(new EndScene(this, score, scores, name));
    }

    /**
     * Show the score screen
     * @param score the score achieved by the user
     */
    public void startScore(int score, String name) {
        loadScene(new ScoreScene(this, score, name));
    }

    /**
     * Show the score screen
     * @param score the score achieved by the user
     * @param scores a list of multiplayer scores
     */
    public void startMultiplayerScore(int score, String scores, String name) {
        loadScene(new MultiplayerScoreScene(this, score, scores, name));
    }

    /**
     * Show the game lobby
     * @param channel the name of the game
     * @param hosting am I the host?
     * @param name what is my name?
     * @param users the list of users connected to the game?
     * @param communicator I need some way to talk to the server
     */
    public void startGameLobby(String channel, boolean hosting, String name, String users, Communicator communicator) {
        loadScene(new GameLobbyScene(this, channel, hosting, name, users, communicator));
    }

    /**
     * Start the score screen without a score (for viewing purposes)
     */
    public void startScore() {
        loadScene(new ScoreScene(this));
    }

    /**
     * Show the main lobby to check for available multiplayer games
     */
    public void startLobby() {
        loadScene(new LobbyScene(this));
    }

    /**
     * Display the single player challenge
     */
    public void startChallenge() {
        loadScene(new ChallengeScene(this));
    }

    /**
     * Display the multiplayer challenge
     */
    public void startMultiplayer(Communicator communicator, UserList userList) {
        loadScene(new MultiplayerScene(this, communicator, userList));
    }

    /**
     * Show the instructions screen
     */
    public void showInstructions() { loadScene(new InstructionsScene(this)); }

    /**
     * Setup the default settings for the stage itself (the window), such as the title and minimum width and height.
     */
    public void setupStage() {
        stage.setTitle("TetrECS");
        stage.setMinWidth(width);
        stage.setMinHeight(height + 20);
        stage.setOnCloseRequest(ev -> App.getInstance().shutdown());

    }

    /**
     * Load a given scene which extends BaseScene and switch over.
     * @param newScene new scene to load
     */
    public void loadScene(BaseScene newScene) {
        Multimedia.playAudio("transition.wav");
        //Cleanup remains of the previous scene
        cleanup();

        //Create the new scene and set it up
        newScene.build();
        currentScene = newScene;
        scene = newScene.setScene();
        stage.setScene(scene);

        //Initialise the scene when ready
        Platform.runLater(() -> currentScene.initialise());
    }

    /**
     * Setup the default scene (an empty black scene) when no scene is loaded
     */
    public void setupDefaultScene() {
        this.scene = new Scene(new Pane(),width,height, Color.BLACK);
        stage.setScene(this.scene);
    }

    /**
     * When switching scenes, perform any cleanup needed, such as removing previous listeners
     */
    public void cleanup() {
        logger.info("Clearing up previous scene");
        communicator.clearListeners();
    }

    /**
     * Get the current scene being displayed
     * @return scene
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Get the width of the Game Window
     * @return width
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Get the height of the Game Window
     * @return height
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Get the communicator
     * @return communicator
     */
    public Communicator getCommunicator() {
        return communicator;
    }

    /**
     * Get the background
     * @return the background
     */
    public Background getBackground() {

        return background;

    }/**
     * Set the background image
     */
    public void setBackground(String path) {
        logger.info("New background: " + path);
        image = new Image(Objects.requireNonNull(this.getClass().getResource("/backgrounds/" + path)).toExternalForm());
        background = new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,new BackgroundSize(image.getWidth() / Math.min((image.getWidth() / getWidth()),(image.getHeight()/getHeight())), image.getHeight() / Math.min((image.getWidth() / getWidth()),(image.getHeight()/getHeight())), false, false, false, false)));
    }

}
