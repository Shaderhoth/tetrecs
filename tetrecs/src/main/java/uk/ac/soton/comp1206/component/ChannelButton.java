package uk.ac.soton.comp1206.component;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import uk.ac.soton.comp1206.network.Communicator;

/**
 * A button to display and allow connection to an available game
 */
public class ChannelButton extends HBox {
    private final Communicator communicator;
    private String channel;

    /**
     * Initialises the channel button
     * @param channel the name of the channel
     * @param communicator the communicator object
     */
    public ChannelButton(String channel, Communicator communicator){
        this.channel = channel;
        this.communicator = communicator;
        Text buttonText = new Text(channel);
        buttonText.getStyleClass().add("channelItemText");
        getChildren().add(buttonText);
        setOnMouseClicked(this::joinGame);
        getStyleClass().add("channelItem");

    }

    /**
     * Joins the game displayed by the button
     * @param event The mouse click event
     */
    private void joinGame(MouseEvent event) {
        communicator.send("JOIN " + channel);
    }
}
