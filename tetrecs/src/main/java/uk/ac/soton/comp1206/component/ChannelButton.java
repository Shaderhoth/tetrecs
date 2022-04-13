package uk.ac.soton.comp1206.component;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import uk.ac.soton.comp1206.network.Communicator;

public class ChannelButton extends HBox {
    private final Communicator communicator;
    private String channel;
    public ChannelButton(String channel, Communicator communicator){
        this.channel = channel;
        this.communicator = communicator;
        Text buttonText = new Text(channel);
        buttonText.getStyleClass().add("channelItemText");
        getChildren().add(buttonText);
        setOnMouseClicked(this::joinGame);
        getStyleClass().add("channelItem");

    }

    private void joinGame(MouseEvent event) {
        communicator.send("JOIN " + channel);
    }
}
