package uk.ac.soton.comp1206.scene;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.MenuOption;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utilities.Multimedia;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {
    /**
     * Logs are cool
     * Logs are fun
     * Its 6 am
     * I need to sleep
     */
    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    /**
     * It plays music
     */
    private Multimedia media;
    /**
     * The Bottom pane on top of which everything else is placed
     */
    private StackPane menuPane;
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
        Multimedia.playMedia("menu.mp3");
        menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("scene-background");
        root.getChildren().add(menuPane);

        var mainPane = new BorderPane();
        if (Multimedia.getVideo() != null){
            menuPane.getChildren().add(Multimedia.getVideo());
        }else {
            mainPane.setBackground(gameWindow.getBackground());
        }
        menuPane.getChildren().add(mainPane);


        HBox imageBox = new HBox();
        var image = new Image(this.getClass().getResource("/images/TetrECS.png").toExternalForm());
        var imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(256);

        imageBox.getChildren().add(imageView);
        imageBox.setAlignment(Pos.BOTTOM_LEFT);
        imageBox.setMinHeight((imageView.getFitWidth() + imageView.getFitHeight())*(Math.sin(Math.toRadians(20))));
        imageBox.setMaxHeight((imageView.getFitWidth() + imageView.getFitHeight())*(Math.sin(Math.toRadians(20))));
        mainPane.setTop(imageBox);
        AnimationTimer timer = new AnimationTimer(){
            @Override
            public void handle(long now){

                imageView.setRotate(Math.sin((double) now/1000000000) * 20);
            }
        };
        timer.start();

        //For now, let us just add a button that starts the game. I'm sure you'll do something way better.
        VBox options = new VBox();
        mainPane.setRight(options);
        options.setAlignment(Pos.CENTER);
        options.setSpacing(gameWindow.getHeight()/30);

        options.getChildren().add(createButton("SINGLE PLAYER"));
        options.getChildren().add(createButton("MULTI PLAYER"));
        options.getChildren().add(createButton("SCORES"));
        options.getChildren().add(createButton("INSTRUCTIONS"));
        options.getChildren().add(createButton("OPTIONS"));
        options.setAlignment(Pos.CENTER_RIGHT);

    }

    /**
     * Makes a button
     * @param name the name of the button
     * @return the button what was created
     */
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
        }else if (name.equals("SCORES")) {
            button.setOnMouseClicked(this::showScores);
        }else if (name.equals("MULTI PLAYER")) {
            button.setOnMouseClicked(this::startLobby);
        }else if (name.equals("OPTIONS")) {
            button.setOnMouseClicked(this::startOptions);
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
                    case ALT:    menuPane.getChildren().get(menuPane.getChildren().size()-1).setVisible(! menuPane.getChildren().get(menuPane.getChildren().size()-1).isVisible()); break;

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
        gameWindow.startChallenge();
    }

    /**
     * Handle when the Instructions button is pressed
     * @param event the click event on the showInstructions button
     */
    private void showInstructions(MouseEvent event) {
        gameWindow.showInstructions();
    }

    /**
     * Handle when the Scores button is pressed
     * @param event the click event on the showScores button
     */
    private void showScores(MouseEvent event) {
        gameWindow.startScore();
    }

    /**
     * Handle when the Lobby button is pressed
     * @param event the click event on the startLobby button
     */
    private void startLobby(MouseEvent event) {
        gameWindow.startLobby();
    }

    /**
     * Handle when the Options button is pressed
     * @param event the click event on the startLobby button
     */
    private void startOptions(MouseEvent event) {
        gameWindow.startOptions();
    }

}
