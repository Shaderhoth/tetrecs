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

/**
 * Heavily inspired by the UserList from ECS Chat
 * Basically the SideBar but with different information, and with the only common aspects being that they are both bars
 * on the side of the screen that can shrink into the side
 */
public class UserList extends VBox {
    /**
     * A pane containing the collection of users
     */
    private VBox users;
    /**
     * A scrollable pane
     */
    private ScrollPane scroller;
    /**
     * An input box containing the name of the user
     */
    private TextField username;
    /**
     * A start button which only appears if you are host
     * Starts the game
     */
    private HBox startButton;
    /**
     * A list of the users in the game lobby
     */
    private SimpleListProperty<String> usersList = new SimpleListProperty();
    /**
     * The width of the sidebar
     */
    private final int width = 256;
    /**
     * Is the sidebar visible?
     */
    private boolean visible;

    /**
     * Initialise the list
     */
    public UserList(){
        setPrefWidth(width);
        setSpacing(16);
        setPadding(new Insets(8,8,8,8));
        getStyleClass().add("userlist");
        setAlignment(Pos.TOP_CENTER);

        build();
    }

    /**
     * Build the layout
     */
    public void build() {
        var image = new ImageView(new Image(this.getClass().getResource("/images/ECS.png").toExternalForm()));
        image.setPreserveRatio(true);
        image.setFitWidth(64);
        getChildren().add(image);

        //Add a username field
        username = new TextField();
        getChildren().add(username);

        //Add the list of users
        users = new VBox();
        users.setSpacing(16);
        users.setPadding(new Insets(8,8,8,8));

        //make users scrollable
        scroller = new ScrollPane();
        scroller.setContent(users);
        scroller.setFitToWidth(true);
        scroller.getStyleClass().add("userlist-pane");

        getChildren().add(users);
        image.setOnMouseClicked((e) -> toggleSidebar());
        usersList.addListener(this::updateUsers);
    }

    /**
     * Make a start button
     * @return the start button object
     */
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

    /**
     * Update the list of users
     * @param change the listener which triggers the method
     */
    private void updateUsers(ListChangeListener.Change<? extends String> change) {
        users.getChildren().removeAll(users.getChildren());
        for (String user: usersList) {
            if (!user.equals("")) {
                addUser(user);
            }
        }
    }

    /**
     * gets the username field
     * @return the username
     */
    public TextField getUsernameField() {
        return username;
    }

    /**
     * Gets a list of the users
     * @return the list of users
     */
    public SimpleListProperty<String> getUsers() {
        return usersList;
    }

    /**
     * Add a user to the users pane
     * @param username the name of the user
     */
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

    /**
     * Toggle the visibility of the sidebar
     */
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
