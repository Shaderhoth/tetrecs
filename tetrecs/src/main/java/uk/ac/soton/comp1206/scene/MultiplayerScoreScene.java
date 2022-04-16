package uk.ac.soton.comp1206.scene;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.ScoresList;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utilities.FileHandler;
import uk.ac.soton.comp1206.utilities.ScoreComparator;

import java.util.ArrayList;

/**
 * The multiplayer variant of the score screen showing the rounds scores along with local and high scores
 */
public class MultiplayerScoreScene extends ScoreScene{
    /**
     * Another logger?
     * I make comments about things that are happening
     */
    private static final Logger logger = LogManager.getLogger(ScoreScene.class);

    /**
     * a counter checking which pane is showing
     */
    private int index = 0;
    /**
     * The list of multiplayer scores
     */
    private SimpleListProperty<Pair<SimpleStringProperty,Integer>> multiplayerScores = new SimpleListProperty();
    /**
     * the pane containing the multiplayer scores
     */
    protected BorderPane multiplayerScorePane;

    public MultiplayerScoreScene(GameWindow gameWindow, int score, String scores, String name) {
        super(gameWindow, score, name);
        multiplayerScores = extractScores(scores);
    }
    /**
     * Load the local and remote scores
     */
    @Override
    protected void loadScores(){
        multiplayerScorePane = makePane("Multiplayer High Scores", multiplayerScores);
        loadOnlineScores();
        localScorePane = makePane("Local High Scores", loadLocalScores(fileName));
        scorePane.getChildren().add(multiplayerScorePane);
        ((ScoresList) (multiplayerScorePane.getCenter())).reveal();
    }


    /**
     * Toggle which pane is showing
     */
    @Override
    protected void toggleScreen(){
        if (localScorePane != null && remoteScorePane != null){
            index = (index + 1) % 3;
            for (Node pane:scorePane.getChildren()) {
                ((ScoresList) ((BorderPane) pane).getCenter()).clearList();
            }
            scorePane.getChildren().removeAll(scorePane.getChildren().get(0));
            var pane = new BorderPane[]{multiplayerScorePane, localScorePane, remoteScorePane}[index];
            scorePane.getChildren().add(pane);
            ((ScoresList) (pane.getCenter())).reveal();
        }
    }

    /**
     * loads the multiplayer scores from the score string
     * @param context the list of scores
     * @return the list of scores
     */
    private SimpleListProperty<Pair<SimpleStringProperty,Integer>> extractScores(String context){

        ArrayList<Pair<SimpleStringProperty,Integer>> tempList= new ArrayList<>();
        for (String user : context.substring(7).split("\n")) {
            var components = user.split(":");
            if (!components[0].equals(name.getValue())) {
                tempList.add(new Pair<>(new SimpleStringProperty(components[0]), Integer.valueOf(components[1])));

            }
        }
        tempList.sort(new ScoreComparator());
        SimpleListProperty<Pair<SimpleStringProperty,Integer>> scores = new SimpleListProperty(FXCollections.observableArrayList(tempList));
        scores.sort(new ScoreComparator());
        logger.info("multiplayer scores loaded");
        return scores;
    }
}
