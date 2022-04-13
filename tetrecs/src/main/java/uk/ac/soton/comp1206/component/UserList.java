package uk.ac.soton.comp1206.component;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.scene.ChallengeScene;
import uk.ac.soton.comp1206.utilities.Multimedia;

import java.util.ArrayList;
import java.util.List;

public class UserList extends VBox {

    private static final Logger logger = LogManager.getLogger(UserList.class);
    private VBox users;
    private ScrollPane scroller;
    private TextField username;
    private CheckBox mute;
    private HBox startButton;
    private SimpleListProperty<String> usersList = new SimpleListProperty();

    private final int width = 256;
    private boolean visible;

    public UserList(){
        setPrefWidth(width);
        setSpacing(16);
        setPadding(new Insets(8,8,8,8));
        getStyleClass().add("userlist");
        setAlignment(Pos.TOP_CENTER);

        build();
    }

    public void build() {
        var image = new ImageView(new Image(this.getClass().getResource("/images/ECS.png").toExternalForm()));
        image.setPreserveRatio(true);
        image.setFitWidth(64);
        getChildren().add(image);

        //Add modifiable username field
        username = new TextField();
        getChildren().add(username);

        //Add userList
        users = new VBox();
        users.setSpacing(16);
        users.setPadding(new Insets(8,8,8,8));

        //Add scrollpane
        scroller = new ScrollPane();
        scroller.setContent(users);
        scroller.setFitToWidth(true);
        scroller.getStyleClass().add("userlist-pane");

        //Add mutebox
        mute = new CheckBox("Notifications");
        mute.getStyleClass().add("checkbox");
        mute.selectedProperty().bindBidirectional(Multimedia.getAudioEnabled());
        getChildren().add(mute);

        getChildren().add(users);
        image.setOnMouseClicked((e) -> toggleSidebar());
        usersList.addListener(this::updateUsers);
    }
    public HBox addStartButton(){
        if (startButton == (null)) {
            startButton = new HBox();
            Text startText = new Text("START");
            startText.getStyleClass().add("title");
            startButton.getStyleClass().add("channelItemButton");
            startButton.setAlignment(Pos.CENTER);
            startButton.getChildren().add(startText);
            getChildren().add(startButton);
            return startButton;
        }
        return null;

    }

    private void updateUsers(ListChangeListener.Change<? extends String> change) {
        users.getChildren().removeAll(users.getChildren());
        for (String user: usersList) {
            if (!user.equals("")) {
                addUser(user);
            }
        }
    }

    public TextField getUsernameField() {
        return username;
    }public SimpleListProperty<String> getUsers() {
        return usersList;
    }
    public void addUser(String username){
        var userBox = new HBox();
        userBox.getStyleClass().add("user");
        userBox.setSpacing(8);
        var user = new Text(username);
        user.getStyleClass().add("channelItemText");
        userBox.getChildren().add(user);
        userBox.getStyleClass().add("channelItem");

        HBox.setHgrow(user,Priority.ALWAYS);
        userBox.setMinHeight(32);
        userBox.setAlignment(Pos.CENTER);

        users.getChildren().add(userBox);
    }

    private void toggleSidebar() {
        if(visible) {
            visible = false;
            for(var child : getChildren()) {
                child.setVisible(false);
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
                    child.setVisible(true);
                }
            });
        }
    }
}
