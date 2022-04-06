package uk.ac.soton.comp1206.scene;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.ScoresList;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utilities.FileHandler;
import uk.ac.soton.comp1206.utilities.Multimedia;
import uk.ac.soton.comp1206.utilities.ScoreComparator;

import java.util.ArrayList;


public class ScoreScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(ScoreScene.class);
    private int score = 0;
    private VBox mainPane;
    SimpleListProperty<Pair<SimpleStringProperty,Integer>> scores = new SimpleListProperty();
    private Multimedia media;
    private Pair newScore;
    private SimpleStringProperty name;
    private String fileName = "scores.txt";
    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public ScoreScene(GameWindow gameWindow, int score) {
        super(gameWindow);
        this.score = score;
        logger.info("Creating End Scene");
    }public ScoreScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating End Scene");
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());



        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());
        var scorePane = new StackPane();
        scorePane.setMaxWidth(gameWindow.getWidth());
        scorePane.setMaxHeight(gameWindow.getHeight());
        scorePane.getStyleClass().add("menu-background");
        root.getChildren().add(scorePane);


        mainPane = new VBox();
        scorePane.getChildren().add(mainPane);
        mainPane.setAlignment(Pos.CENTER);
        loadScores(fileName);
        logger.info("scores loaded");
        scores.sort(new ScoreComparator());
        for (Pair score: scores) {
            logger.info(score);

        }
        if (score > 0) {
            if (score > scores.get(scores.getSize() - 1).getValue()) {
                name = new SimpleStringProperty("Player");
                name.addListener(this::nameUpdated);
                newScore = new Pair(name, score);
                scores.remove(scores.get(scores.getSize() - 1));
                scores.add(newScore);
                scores.sort(new ScoreComparator());
                writeScores(fileName);
                showScores(scores.lastIndexOf(newScore));
            } else {
                showScores();
            }
        }else{
            showScores();
        }


    }

    private void nameUpdated(ObservableValue<? extends String> observableValue, String s, String s1) {
        logger.info("nameUpdated");
        for (Pair score: scores) {
            logger.info(score);
        }

        writeScores(fileName);
    }


    private void showScores(int index){
        var title = new Text("High Scores");
        title.getStyleClass().add("bigtitle");
        mainPane.getChildren().add(title);
        ScoresList scoresList = new ScoresList(scores);
        mainPane.getChildren().add(scoresList);
        scoresList.reveal(index);
        /*
        if (scoresList.getNameField() != null) {
            scoresList.getNameField().textProperty().bindBidirectional(name);
        }

         */

    }
    private void showScores(){
        var title = new Text("High Scores");
        title.getStyleClass().add("bigtitle");
        mainPane.getChildren().add(title);
        mainPane.setAlignment(Pos.CENTER);
        ScoresList scoresList = new ScoresList(scores);
        mainPane.getChildren().add(scoresList);
        scoresList.reveal();

    }
    private void loadScores(String fileName){
        ArrayList<Pair<SimpleStringProperty,Integer>> tempList= new ArrayList<>();
        FileHandler file = new FileHandler(fileName);
        file.setReader();
        if (file.fileExists()){
            while (file.fileIsReady()){
                String[] line = file.getLine().split(":");
                tempList.add(new Pair(new SimpleStringProperty(line[0]), Integer.valueOf(line[1])));
            }
        }else{
            file = new FileHandler("defaultScores.txt");
            file.setReader();
            while (file.fileIsReady()){
                String[] line = file.getLine().split(":");
                tempList.add(new Pair(new SimpleStringProperty(line[0]), Integer.valueOf(line[1])));
            }
        }
        tempList.sort(new ScoreComparator());
        scores = new SimpleListProperty(FXCollections.observableArrayList(tempList));


    }
    private void writeScores(String fileName){
        logger.info("Writing Scores");
        FileHandler file = new FileHandler(fileName);
        file.setWriter();
        for (Pair<SimpleStringProperty,Integer> score: scores) {
            file.writeLine(score.getKey().getValue() + ":" + score.getValue());

        }
        file.writeFinish();
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