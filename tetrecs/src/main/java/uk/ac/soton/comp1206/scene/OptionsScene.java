package uk.ac.soton.comp1206.scene;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utilities.Multimedia;

import java.io.File;
import java.util.Objects;
import java.util.Random;

/**
 * The Scene containing all the instructions and a preview of the blocks
 */
public class OptionsScene extends BaseScene {
    /**
     * An extremely useful debugging tool
     */
    private static final Logger logger = LogManager.getLogger(InstructionsScene.class);
    /**
     * A media player which isnt being used currently
     */
    private Multimedia media;
    /**
     * A pane showing the various categories
     */
    private VBox categoryPane;
    /**
     * A pane showing the various options/modifiers
     */
    private VBox modifierPane;
    /**
     * The main pane
     */
    private BorderPane mainPane;
    /**
     * The base pane on which everything else is stacked
     */
    private StackPane optionsPane;
    /**
     * Create a new scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public OptionsScene(GameWindow gameWindow) {
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
        optionsPane = new StackPane();
        optionsPane.setMaxWidth(gameWindow.getWidth());
        optionsPane.setMaxHeight(gameWindow.getHeight());
        optionsPane.getStyleClass().add("scene-background");

        root.getChildren().add(optionsPane);

        mainPane = new BorderPane();
        if (Multimedia.getVideo() != null){
            optionsPane.getChildren().add(Multimedia.getVideo());
        }else {
            mainPane.setBackground(gameWindow.getBackground());
        }
        optionsPane.getChildren().add(mainPane);

        var title = new Text("TetrECS");
        title.getStyleClass().add("bigtitle");
        mainPane.setTop(title);
        optionsPane.setAlignment(Pos.CENTER);

        categoryPane = new VBox();
        categoryPane.setMinWidth(gameWindow.getWidth()/5);
        mainPane.setLeft(categoryPane);
        modifierPane = new VBox();



        //Add a scrollpane
        ScrollPane scroller = new ScrollPane();
        scroller.getStyleClass().add("messagePane");
        scroller.setContent(modifierPane);
        scroller.setFitToWidth(true);
        scroller.setPadding(new Insets(16));
        mainPane.setCenter(scroller);


        var graphics = makeCategory("Graphics");
        graphics.setOnMouseClicked((mouseEvent) -> {showGraphics();});
        categoryPane.getChildren().add(graphics);

        var sound = makeCategory("Sound");
        sound.setOnMouseClicked((mouseEvent) -> {showSound();});
        categoryPane.getChildren().add(sound);


    }

    /**
     * clear the modifiable value pane so new attributes can be show
     */
    private void clear(){
        if(modifierPane != null){
            modifierPane.getChildren().removeAll(modifierPane.getChildren());
        }
    }

    /**
     * Makes a category
     * @param name the name of the category
     * @return the category node
     */
    private HBox makeCategory(String name){
        HBox category = new HBox();
        category.getStyleClass().add("optionItem");
        category.getChildren().add(subtitle(name));
        category.setAlignment(Pos.BASELINE_CENTER);
        category.setMinWidth(categoryPane.getWidth()/2);
        return category;
    }

    /**
     * Generate subtitles
     * @param text the text for the subtitle
     * @return the subtitle node
     */
    private Text subtitle(String text){
        Text subtitle = new Text(text);
        subtitle.getStyleClass().add("mini-title");
        subtitle.setTextAlignment(TextAlignment.LEFT);
        return subtitle;
    }

    /**
     * Show all that graphics properties that can be modified, and the methods to modify them
     */
    private void showSound(){
        clear();
        modifierPane.getChildren().add(subtitle("Media Volume"));
        var mediaSlider = new Slider(0,1,Multimedia.getMediaVolume().get());
        modifierPane.getChildren().add(mediaSlider);
        mediaSlider.valueProperty().bindBidirectional(Multimedia.getMediaVolume());


        modifierPane.getChildren().add(subtitle("Audio Volume"));
        var audioSlider = new Slider(0,1,Multimedia.getAudioVolume().get());
        modifierPane.getChildren().add(audioSlider);
        audioSlider.valueProperty().bindBidirectional(Multimedia.getAudioVolume());
    }

    /**
     * Show all that graphics properties that can be modified, and the methods to modify them
     */
    private void showGraphics(){
        clear();

        modifierPane.getChildren().add(subtitle("Background"));


        HBox backgrounds = new HBox();
        backgrounds.setAlignment(Pos.BOTTOM_LEFT);

        File directoryPath = new File("src/main/resources/backgrounds");

        for (String path: directoryPath.list()) {
            var box = new HBox();
            var image = new ImageView(new Image(this.getClass().getResource("/backgrounds/" + path).toExternalForm()));
            image.setPreserveRatio(true);
            image.setFitWidth(modifierPane.getWidth()/2);
            image.setOnMouseClicked((mouseevent) -> {
                mainPane.setBackground(Background.EMPTY);
                gameWindow.setBackground(path);
                optionsPane.getChildren().removeAll(Multimedia.getVideo());
                Multimedia.setVideo();
                mainPane.setBackground(gameWindow.getBackground());});
            box.getChildren().add(image);
            box.setBorder(new Border(new BorderStroke(Color.BLACK,BorderStrokeStyle.SOLID,CornerRadii.EMPTY,BorderWidths.DEFAULT)));
            box.setMaxHeight(image.getFitHeight());
            backgrounds.getChildren().add(box);
            
        }

        //Add a scrollpane
        ScrollPane scroller = new ScrollPane();
        scroller.getStyleClass().add("messagePane");
        scroller.setContent(backgrounds);
        scroller.setFitToHeight(true);
        scroller.setPadding(Insets.EMPTY);
        scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        modifierPane.getChildren().add(scroller);

        modifierPane.getChildren().add(subtitle("Resolution"));
        var entry = new HBox();
        var width = new TextField("1920");
        var height = new TextField("1080");
        var button = new Button("Submit");
        button.setOnMouseClicked((mouseEvent) -> {updateResolution(width.getText(),height.getText());
            gameWindow.startMenu();} );
        entry.getChildren().add(width);
        entry.getChildren().add(height);
        entry.getChildren().add(button);
        modifierPane.getChildren().add(entry);


        modifierPane.getChildren().add(subtitle("Antialias"));

        CheckBox antialias = new CheckBox("Antialiasing");
        antialias.getStyleClass().add("checkbox");
        antialias.selectedProperty().bindBidirectional(gameWindow.antiAlias);
        modifierPane.getChildren().add(antialias);


        modifierPane.getChildren().add(subtitle("Background Video (Press ALT to access)"));
        var vEntry = new HBox();
        var videoLink = new TextField("https://www.youtube.com/watch?v=dQw4w9WgXcQ");
        var vButton = new Button("Submit");
        vButton.setOnMouseClicked((mouseEvent) -> {Multimedia.setVideo(videoLink.getText());gameWindow.startMenu();});
        vEntry.getChildren().add(videoLink);
        vEntry.getChildren().add(vButton);
        modifierPane.getChildren().add(vEntry);


    }

    private void updateResolution(String width, String height){
        gameWindow.resize(Integer.parseInt(width), Integer.parseInt(height));

        build();
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
                    case ALT:    optionsPane.getChildren().get(optionsPane.getChildren().size()-1).setVisible(! optionsPane.getChildren().get(optionsPane.getChildren().size()-1).isVisible()); break;

                    case ESCAPE:    gameWindow.startMenu(); break;
                }
            }
        });
    }

}