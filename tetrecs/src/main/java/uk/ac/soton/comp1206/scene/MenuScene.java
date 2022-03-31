package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utilities.Multimedia;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    private Multimedia media;
    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());
        media = new Multimedia("menu.mp3");
        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        //Awful title
        var title = new Text("TetrECS");
        title.getStyleClass().add("title");
        mainPane.setTop(title);

        //For now, let us just add a button that starts the game. I'm sure you'll do something way better.
        VBox options = new VBox();
        mainPane.setRight(options);
        options.setAlignment(Pos.CENTER);
        options.setSpacing(gameWindow.getHeight()/30);

        options.getChildren().add(createButton("SINGLE PLAYER"));
        options.getChildren().add(createButton("MULTI PLAYER"));
        options.getChildren().add(createButton("INSTRUCTIONS"));
        options.getChildren().add(createButton("OPTIONS"));
        options.setAlignment(Pos.CENTER_RIGHT);

    }
    private HBox createButton(String name){
        //For now, let us just add a button that starts the game. I'm sure you'll do something way better.
        var button = new MenuOption(name);
        button.hoverProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
            if (newValue) {
                button.setMinWidth(gameWindow.getWidth()/2);
                button.setMaxWidth(gameWindow.getWidth()/2);
            } else {
                button.setMinWidth(gameWindow.getWidth()/3);
                button.setMaxWidth(gameWindow.getWidth()/3);
            }
        });
        button.setMinWidth(gameWindow.getWidth()/3);
        button.setMaxWidth(gameWindow.getWidth()/3);
        button.setMinHeight(gameWindow.getHeight()/20);
        button.setMaxHeight(gameWindow.getHeight()/20);
        button.setAlignment(Pos.CENTER);

        //Bind the button action to the startGame method in the menu

        if (name.equals("SINGLE PLAYER")) {
            button.setOnMouseClicked(this::startGame);
        }else if (name.equals("INSTRUCTIONS")) {
            button.setOnMouseClicked(this::showInstructions);
        }

        return button;
    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {
        getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case ESCAPE:    Platform.exit(); break;
                }
            }
        });
    }

    /**
     * Handle when the Start Game button is pressed
     * @param event event
     */
    private void startGame(MouseEvent event) {
        media.stop();
        gameWindow.startChallenge();
    }private void showInstructions(MouseEvent event) {
        gameWindow.showInstructions();
    }

}
