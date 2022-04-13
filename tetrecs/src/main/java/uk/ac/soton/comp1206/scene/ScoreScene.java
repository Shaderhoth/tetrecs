package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import uk.ac.soton.comp1206.event.CommunicationsListener;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utilities.FileHandler;
import uk.ac.soton.comp1206.utilities.Multimedia;
import uk.ac.soton.comp1206.utilities.ScoreComparator;

import java.util.ArrayList;


public class ScoreScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(ScoreScene.class);
    private static final Communicator communicator = new Communicator("ws://ofb-labs.soton.ac.uk:9700");
    private CommunicationsListener communicationsListener;
    private int score = 0;
    private VBox mainPane;
    private SimpleListProperty<Pair<SimpleStringProperty,Integer>> localScores = new SimpleListProperty();
    private SimpleListProperty<Pair<SimpleStringProperty,Integer>> remoteScores = new SimpleListProperty();
    private VBox localScorePane;
    private VBox remoteScorePane;
    private StackPane scorePane;
    private Multimedia media;
    private Pair newScore;
    private SimpleStringProperty name = new SimpleStringProperty("Player");
    private boolean global = false;

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
        scorePane = new StackPane();
        scorePane.setMaxWidth(gameWindow.getWidth());
        scorePane.setMaxHeight(gameWindow.getHeight());
        scorePane.getStyleClass().add("menu-background");
        root.getChildren().add(scorePane);


        loadScores();

    }
    private void toggleScreen(){
        if (localScorePane != null && remoteScorePane != null){
            for (Node pane:scorePane.getChildren()) {
                ((ScoresList) ((VBox) pane).getChildren().get(1)).clearList();
            }
            scorePane.getChildren().removeAll(scorePane.getChildren().get(0));
            if(global){
                global = !global;
                scorePane.getChildren().add(localScorePane);
                ((ScoresList) localScorePane.getChildren().get(1)).reveal();
            }else{
                global = !global;
                scorePane.getChildren().add(remoteScorePane);
                ((ScoresList) remoteScorePane.getChildren().get(1)).reveal();
            }
        }
    }
    private VBox makePane(String titleText, SimpleListProperty<Pair<SimpleStringProperty,Integer>> scores){
        mainPane = new VBox();
        Text title = new Text(titleText);
        title.getStyleClass().add("bigtitle");
        mainPane.getChildren().add(title);
        mainPane.setAlignment(Pos.CENTER);
        mainPane.setSpacing(16);
        ScoresList scoresList;
        if (score > 0) {
            if (score > scores.get(scores.getSize() - 1).getValue()) {
                name.addListener(this::nameUpdated);
                newScore = new Pair(name, score);
                scores.remove(scores.get(scores.getSize() - 1));
                scores.add(newScore);
                scores.sort(new ScoreComparator());
                writeScores(fileName);
                scoresList = makeScores(scores, scores.lastIndexOf(newScore));
            } else {
                scoresList = makeScores(scores);
            }
        }else{
            scoresList = makeScores(scores);
        }
        mainPane.getChildren().add(scoresList);
        Text instructions = new Text("Press Enter to toggle Local/Global Scores and press Esc to save your score and leave");
        instructions.getStyleClass().add("messages");
        mainPane.getChildren().add(instructions);

        return mainPane;
    }
    private void loadScores(){
        loadOnlineScores();
        loadLocalScores(fileName);
        localScores.sort(new ScoreComparator());
        localScorePane = makePane("Local High Scores", localScores);
        logger.info("local scores loaded");
        ((ScoresList) localScorePane.getChildren().get(1)).reveal();
        scorePane.getChildren().add(localScorePane);
    }

    private void nameUpdated(ObservableValue<? extends String> observableValue, String s, String s1) {
        logger.info("nameUpdated");
        for (Pair score: localScores) {
            logger.info(score);
        }

        writeScores(fileName);
    }


    private ScoresList makeScores(SimpleListProperty<Pair<SimpleStringProperty,Integer>> scores, int index){
        ScoresList scoresList = new ScoresList(scores, index);
        return scoresList;

    }
    private ScoresList makeScores(SimpleListProperty<Pair<SimpleStringProperty,Integer>> scores){
        ScoresList scoresList = new ScoresList(scores);
        return scoresList;

    }
    private void loadLocalScores(String fileName){
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
        localScores = new SimpleListProperty(FXCollections.observableArrayList(tempList));


    }
    private void loadOnlineScores(){
        communicator.addListener((message) -> Platform.runLater(() -> this.receiveMessage(message)));
        communicator.send("HISCORES");


    }
    private void receiveMessage(String message){
        if (message.startsWith("HISCORES")){
            ArrayList<Pair<SimpleStringProperty,Integer>> tempList= new ArrayList<>();
            for (String score: message.substring(9).split("\n")) {
                String[] line = score.split(":");
                tempList.add(new Pair(new SimpleStringProperty(line[0]), Integer.valueOf(line[1])));
            }
            remoteScores = new SimpleListProperty(FXCollections.observableArrayList(tempList));
            remoteScorePane = makePane("Global High Scores", remoteScores);
            logger.info("remote scores loaded");

        }
    }
    private void writeScores(String fileName){
        logger.info("Writing Scores");
        FileHandler file = new FileHandler(fileName);
        file.setWriter();
        for (Pair<SimpleStringProperty,Integer> score: localScores) {
            file.writeLine(score.getKey().getValue() + ":" + score.getValue());

        }
        file.writeFinish();
    }
    private void exit(){
        writeOnlineScore();
        gameWindow.startMenu();
    }
    private void writeOnlineScore(){
        communicator.send(("HISCORE " + name.getValue() + ":" + score));
        logger.info("Sent: " + ("HISCORE " + name.getValue() + ":" + score));
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
                    case ESCAPE:    exit(); break;
                    case ENTER:    toggleScreen(); break;
                }
            }
        });
    }

}