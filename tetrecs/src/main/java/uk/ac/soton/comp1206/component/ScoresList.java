package uk.ac.soton.comp1206.component;

import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.scene.ScoreScene;

/**
 * A Pane containing all the scores
 */
public class ScoresList extends VBox {
    /**
     * A logger for debugging purposes
     */
    private static final Logger logger = LogManager.getLogger(ScoresList.class);
    /**
     * The score achieved in the most recent game by the user
     */
    private Pair<SimpleStringProperty, Integer> newScore = null;
    /**
     * A list of all the scores
     */
    SimpleListProperty<Pair<SimpleStringProperty, Integer>> scores;
    /**
     * The entry location of the players name
     */
    private TextField name = null;
    /**
     * The Animation timer to trigger the animation displaying the scores
     */
    private AnimationTimer timer;
    /**
     * The location of the user on the scoreboard
     */
    private Integer index = -1;

    /**
     * Initialise the list with the top scores
     * @param scores list of scores (paired with names)
     */
    public ScoresList(SimpleListProperty<Pair<SimpleStringProperty,Integer>> scores){
        this.scores = scores;
        setAlignment(Pos.CENTER);
        scores.addListener(this::resetList);
        setAlignment(Pos.CENTER);
    }

    /**
     * Initialise the list using the top scores including the index of the user on the scoreboard
     * @param scores list of scores (paired with names)
     * @param index the index location of the user on the scoreboard
     */
    public ScoresList(SimpleListProperty<Pair<SimpleStringProperty,Integer>> scores, Integer index){
        this.scores = scores;
        this.index = index;
        setAlignment(Pos.CENTER);
        scores.addListener(this::resetList);
        setAlignment(Pos.CENTER);
    }

    /**
     * Activates when scores are updated
     * @param change the change in the scores
     */
    private void resetList(ListChangeListener.Change<? extends Pair> change) {
        setList();
    }

    /**
     * Makes a score Pane
     * @param score the Score object containing the username and value of the score
     * @return the pane containing the information on the score
     */
    public BorderPane makeScore(Pair<SimpleStringProperty, Integer> score){
        BorderPane pane = new BorderPane();
        pane.setMinWidth(getWidth()*4/5);
        pane.setMaxWidth(getWidth()*4/5);

        Text s = new Text(score.getValue().toString());
        s.getStyleClass().add("title");
        s.setStroke(Color.BLUEVIOLET);
        s.setTextAlignment(TextAlignment.RIGHT);
        pane.setRight(s);

        Text name = new Text(score.getKey().getValue());
        name.getStyleClass().add("title");
        name.setTextAlignment(TextAlignment.LEFT);
        name.setStroke(Color.BLUEVIOLET);
        pane.setLeft(name);
        return pane;

    }

    /**
     * Generates a score pane with a modifiable name
     * Used if the score was achieved by the player on the most recent round
     * @param score the Score object containing the username and value of the score
     * @return the pane containing the information on the score
     */
    public BorderPane makeNewScore(Pair<SimpleStringProperty, Integer> score){
        BorderPane pane = new BorderPane();
        pane.setMinWidth(getWidth()*4/5);
        pane.setMaxWidth(getWidth()*4/5);

        Text s = new Text(score.getValue().toString());
        s.getStyleClass().add("titlealternate");
        s.setTextAlignment(TextAlignment.RIGHT);
        s.setStroke(Color.RED);
        logger.info(s.getText() + s.getStroke());
        pane.setRight(s);

        TextField name = new TextField(score.getKey().getValue());
        this.name = name;
        logger.info(name.getStyleClass());
        getNameField().textProperty().bindBidirectional(score.getKey());
        name.setMinWidth(getWidth()*2/5);
        name.setMaxWidth(getWidth()*2/5);
        name.getStyleClass().add("titlealternate");
        name.setBackground(Background.EMPTY);
        name.setAlignment(Pos.CENTER_LEFT);
        name.setPadding(new Insets(0));
        pane.setLeft(name);

        return pane;

    }

    /**
     * returns the name field containing the user's score
     * @return the input box containing the name of the user
     */
    public TextField getNameField(){
        return name;
    }

    /**
     * clears the list of scores and stops the timer from adding new scores
     */
    public void clearList(){
        if (timer != null){
            timer.stop();
        }
        if (getChildren() != null) {
            getChildren().removeAll(getChildren());
        }
    }

    /**
     * refreshes the list
     */
    private void setList(){
        clearList();
        reveal();
    }

    /**
     * displays the list of scores using an animation
     */
    public void reveal(){
        clearList();
        timer = new AnimationTimer() {
            int speed = 10;
            int i = 0;
            long lastTick = 0;
            @Override
            public void handle(long now) {
                if (i >= scores.getSize()){
                    stop();
                }
                else if(lastTick == 0 ){
                    lastTick = now;
                    return;
                }
                else if(now - lastTick > 1000000000 / speed){
                    if (i == index){
                        getChildren().add(makeNewScore(scores.get(i)));
                    }else {
                        getChildren().add(makeScore(scores.get(i)));
                    }
                    lastTick = now;
                    i++;
                }
            }
        };
        timer.start();
    }
}
