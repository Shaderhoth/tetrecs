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


public class EndScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(EndScene.class);
    private final int score;
    private Multimedia media;
    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public EndScene(GameWindow gameWindow, int score) {
        super(gameWindow);
        this.score = score;
        logger.info("Creating End Scene");
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());



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
        root.setOnMouseClicked((e) -> gameWindow.startMenu());

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
                    case ESCAPE:    gameWindow.startMenu(); break;
                }
            }
        });
    }

}