package uk.ac.soton.comp1206.component;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

public class SideBar extends VBox {
    private static final Logger logger = LogManager.getLogger(SideBar.class);
    private Text score;
    private Text topScore;
    private Text level;
    private Text lives;
    private Text multiplier;
    private UITimer timer;
    private CheckBox mute;
    private HBox imgBox = new HBox();
    private final int width = 256;
    private boolean visible = true;

    public SideBar(){
        //set prefs
        setPrefWidth(width);
        setSpacing(16);
        setPadding(new Insets(8,8,8,8));
        getStyleClass().add("sidebar");
        setAlignment(Pos.TOP_CENTER);

        build();
    }

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
    public void setTimer(int time){
        timer.resetTimer(time);
    }
    private Text addLabel(){
        Text text = new Text();
        text.setFont(Font.font("style/Orbitron-Black.ttf", FontWeight.BOLD, FontPosture.REGULAR, 25));
        text.setFill(Color.WHITE);
        text.setStroke(Color.BLUE);
        text.setStrokeWidth(1);
        return text;
    }
    private void addBox(Text text, String name){
        var box = new SideBarLabel(text, name);
        getChildren().add(box);
    }

    public Text getScoreField() {
        return score;
    }public Text getTopScoreField() {
        return topScore;
    }public Text getLevelField() {
        return level;
    }public Text getLivesField() {
        return lives;
    }public Text getMultiplierField() {
        return multiplier;
    }
    private void toggleSidebar() {
        logger.info("Toggle Visibility, Currently visible: " + visible);
        if(visible) {
            visible = false;
            for(var child : getChildren()) {
                if(child instanceof SideBarLabel) {
                    ((SideBarLabel) child).toggleVisibility();
                }
                if(child instanceof PieceBoard) {
                    ((PieceBoard) child).resize(0.5);
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
                    }
                    if(child instanceof PieceBoard) {
                        ((PieceBoard) child).resize(2);
                    }
                }
            });
        }
    }
}