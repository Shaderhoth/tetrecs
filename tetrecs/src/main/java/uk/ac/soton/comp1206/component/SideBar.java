package uk.ac.soton.comp1206.component;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Timer;

/**
 * A sidepane to store all the useful information while playing a game
 */
public class SideBar extends VBox {
    /**
     * Logger for debug purposes
     */
    private static final Logger logger = LogManager.getLogger(SideBar.class);
    /**
     * The current score of the user
     */
    private Text score;
    /**
     * The local top score
     */
    private Text topScore;
    /**
     * The current level of the user
     */
    private Text level;
    /**
     * The number of lives remaining
     */
    private Text lives;
    /**
     * The current multiplier
     */
    private Text multiplier;
    /**
     * The Animated Timer object
     */
    private UITimer timer;
    /**
     * An image box containing an image
     */
    private HBox imgBox = new HBox();
    /**
     * The width of the sidebar
     */
    private final int width = 256;
    /**
     * Is the sidebar visible?
     */
    private boolean visible = true;

    /**
     * Initialise the sidebar
     */
    public SideBar(){
        //set prefs
        setPrefWidth(width);
        setSpacing(16);
        setPadding(new Insets(8,8,8,8));
        getStyleClass().add("sidebar");
        setAlignment(Pos.TOP_CENTER);

        build();
    }

    /**
     * Build the sidebar
     */
    public void build() {

        imgBox = new SideBarLabel();
        getChildren().add(imgBox);

        timer = new UITimer(width, 32, 12000);
        getChildren().add(timer);

        topScore = addLabel();
        addBox(topScore, "Top Score");
        score = addLabel();
        addBox(score, "Score");
        level = addLabel();
        addBox(level, "Level");
        lives = addLabel();
        addBox(lives, "Lives");
        multiplier = addLabel();
        addBox(multiplier, "Multiplier");

        /*
        //Add scrollpane
        scroller = new ScrollPane();
        scroller.setContent(users);
        scroller.setFitToWidth(true);
        scroller.getStyleClass().add("userlist-pane");


        //Add mutebox
        mute = new CheckBox("Notifications");
        mute.getStyleClass().add("checkbox");
        mute.selectedProperty().bindBidirectional(Utility.audioEnabledProperty());
        getChildren().add(mute);
        */
        imgBox.setOnMouseClicked((e) -> toggleSidebar());
    }

    /**
     * Create an animated timer
     * @param time the amount of time for the timer to count down
     */
    public void setTimer(int time){
        timer.resetTimer(time);
    }

    /**
     * Add a label to the sidebar
     * @return the Text object of the label
     */
    private Text addLabel(){
        Text text = new Text();
        text.setFont(Font.font("style/Orbitron-Black.ttf", FontWeight.BOLD, FontPosture.REGULAR, 25));
        text.setFill(Color.WHITE);
        text.setStroke(Color.BLUE);
        text.setStrokeWidth(1);
        return text;
    }

    /**
     * Create a box containing data and add it to the sidebar
     * @param text the text object containing the value of the data
     * @param name the name of the data
     */
    private void addBox(Text text, String name){
        var box = new SideBarLabel(text, name);
        getChildren().add(box);
    }

    /**
     * A method to get the ScoreField
     * @return the Score
     */
    public Text getScoreField() {
        return score;
    }

    /**
     * A method to get the TopScoreField
     * @return the TopScore
     */
    public Text getTopScoreField() {
        return topScore;
    }

    /**
     * A method to get the LevelField
     * @return the Level
     */
    public Text getLevelField() {
        return level;
    }

    /**
     * A method to get the LivesField
     * @return the Lives
     */
    public Text getLivesField() {
        return lives;
    }

    /**
     * A method to get the MultiplierField
     * @return the Multiplier
     */
    public Text getMultiplierField() {
        return multiplier;
    }



    /**
     * Toggles the visibility of the sidebar either shrinking it and its components or enlarging them
     */
    private void toggleSidebar() {
        logger.info("Toggle Visibility, Currently visible: " + visible);
        if(visible) {
            visible = false;
            for(var child : getChildren()) {
                if(child instanceof SideBarLabel) {
                    ((SideBarLabel) child).toggleVisibility();
                }else if(child instanceof PieceBoard ) {
                    ((PieceBoard) child).resize(0.5);
                }else if(child instanceof UITimer) {
                    ((UITimer) child).rs(0.5);
                }

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
                    if(child instanceof SideBarLabel) {
                        ((SideBarLabel) child).toggleVisibility();
                    }else if(child instanceof PieceBoard) {
                        ((PieceBoard) child).resize(2);
                    }else if(child instanceof UITimer) {
                        ((UITimer) child).rs(2);
                    }
                }
            });
        }
    }
}