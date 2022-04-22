package uk.ac.soton.comp1206.scene;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utilities.Multimedia;

import java.util.Random;

/**
 * The Scene containing all the instructions and a preview of the blocks
 */
public class InstructionsScene extends BaseScene {
    /**
     * An extremely useful debugging tool
     */
    private static final Logger logger = LogManager.getLogger(InstructionsScene.class);
    /**
     * A media player which isnt being used currently
     */
    private Multimedia media;
    /**
     * The Bottom pane on top of which everything else is placed
     */
    private StackPane instructionsPane;
    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public InstructionsScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the scene layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());
        Multimedia.playMedia("menu.mp3");


        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());
        instructionsPane = new StackPane();
        instructionsPane.setMaxWidth(gameWindow.getWidth());
        instructionsPane.setMaxHeight(gameWindow.getHeight());
        instructionsPane.getStyleClass().add("scene-background");

        root.getChildren().add(instructionsPane);

        var mainPane = new BorderPane();
        if (Multimedia.getVideo() != null){
            instructionsPane.getChildren().add(Multimedia.getVideo());
        }else {
            mainPane.setBackground(gameWindow.getBackground());
        }
        instructionsPane.getChildren().add(mainPane);

        //Awful title
        var title = new Text("TetrECS");
        title.getStyleClass().add("bigtitle");
        mainPane.setTop(title);

        var image = new ImageView(new Image(this.getClass().getResource("/images/Instructions.png").toExternalForm()));
        image.setPreserveRatio(true);
        image.setFitWidth(gameWindow.getWidth()/4*3);
        instructionsPane.getChildren().add(image);
        instructionsPane.setAlignment(Pos.TOP_RIGHT);



        GridPane pieces = new GridPane();
//        for (int i = 0; i < 15; i++) {
//            PieceBoard p = new PieceBoard(gameWindow.getWidth()/7,gameWindow.getWidth()/7);
//            p.displayPiece(GamePiece.createPiece(i));
//            if (i<1){
//                pieces.add(p,0, i);
//            }else{
//                pieces.add(p,(i-1)/2, i%2+1);
//            }
//        }

        for (int i = 0; i < 15; i++) {
            PieceBoard p = new PieceBoard(gameWindow.getWidth()/7,gameWindow.getWidth()/7);
            p.displayPiece(GamePiece.createPiece(i));
            pieces.add(p,i, 0);
        }

        //Add a scrollpane
        ScrollPane scroller = new ScrollPane();
        scroller.getStyleClass().add("messagePane");
        scroller.setContent(pieces);
        scroller.setMinHeight(gameWindow.getWidth()/7+32);
        scroller.setPadding(new Insets(8));
        scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        mainPane.setBottom(scroller);


    }


    /**
     * Initialise the scene
     */
    @Override
    public void initialise() {

        getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case ALT:    instructionsPane.getChildren().get(instructionsPane.getChildren().size()-1).setVisible(! instructionsPane.getChildren().get(instructionsPane.getChildren().size()-1).isVisible()); break;

                    case ESCAPE:    gameWindow.startMenu(); break;
                }
            }
        });
    }

}