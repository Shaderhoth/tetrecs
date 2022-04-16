package uk.ac.soton.comp1206.scene;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utilities.Multimedia;

/**
 * The End Scene which appears when the game is over
 */
public class EndScene extends BaseScene {
    /**
     * I'm struggling to find new was of saying that the logger makes logs or is extremely useful
     */
    private static final Logger logger = LogManager.getLogger(EndScene.class);
    /**
     * The Score the user managed to achieve in their game
     */
    private final int score;
    /**
     * A string containing all the scores of a multiplayer
     */
    private final String scores;
    /**
     * The name of the user
     */
    private final String name;

    /**
     * A very useful media player
     */
    private Multimedia media;
    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public EndScene(GameWindow gameWindow, int score) {
        super(gameWindow);
        this.score = score;
        scores = null;
        name = "Player";
        logger.info("Creating End Scene");
    }
    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public EndScene(GameWindow gameWindow, int score, String scores, String name) {
        super(gameWindow);
        this.score = score;
        this.scores = scores;
        this.name = name;
        logger.info("Creating End Scene");
    }

    /**
     * Build the scene layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        media = new Multimedia("end.wav");

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());
        var endPane = new StackPane();
        endPane.setMaxWidth(gameWindow.getWidth());
        endPane.setMaxHeight(gameWindow.getHeight());
        endPane.getStyleClass().add("menu-background");
        root.getChildren().add(endPane);

        VBox mainPane = new VBox();
        root.getChildren().add(mainPane);
        //Awful title
        var title = new Text("GAME OVER");
        title.getStyleClass().add("bigtitle");
        mainPane.getChildren().add(title);
        var text = new Text("Score " + score);
        text.getStyleClass().add("title");
        mainPane.getChildren().add(text);
        mainPane.setAlignment(Pos.CENTER);
        root.setOnMouseClicked((e) -> exit());

    }

    /**
     * End the end
     */
    private void exit(){
        media.stop();
        if (scores != null){
            gameWindow.startMultiplayerScore(score, scores, name);
        }else {
            gameWindow.startScore(score, name);
        }
    }


    /**
     * The start of the end
     */
    @Override
    public void initialise() {

        getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    default:    exit(); break;
                }
            }
        });
    }

}