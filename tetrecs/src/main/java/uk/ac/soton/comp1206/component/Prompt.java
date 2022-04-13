package uk.ac.soton.comp1206.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import uk.ac.soton.comp1206.event.PromptExitListener;

/**
 * A popup alert/prompt triggered when data needs to be entered or an issue occurs
 */
public class Prompt extends VBox{
    /**
     * A listener to check when the prompt is closed
     */
    private PromptExitListener promptExitListener;

    /**
     * Initialised the prompt object
     * @param title the title of the prompt
     * @param node the data on the prompt (can be entry or just text or even an image... probably)
     * @param width the width of the prompt
     */
    public Prompt(String title, Node node, double width){
        Text promptTitle = new Text(title);
        promptTitle.getStyleClass().add("title");
        node.getStyleClass().add("channelItemText");

        HBox confirm = new HBox();
        confirm.setBorder(new Border(new BorderStroke(Color.WHITESMOKE, BorderStrokeStyle.SOLID,CornerRadii.EMPTY,new BorderWidths(2))));
        Text confirmText = new Text("Confirm");
        confirmText.getStyleClass().add("channelItemText");
        confirm.getChildren().add(confirmText);
        confirm.setOnMouseClicked(this::alertConfirmed);
        confirm.setAlignment(Pos.CENTER);
        confirm.getStyleClass().add("channelItemButton");
        getChildren().add(promptTitle);
        getChildren().add(node);
        getChildren().add(confirm);
        setFocusTraversable(false);
        setOnKeyReleased(this::alertKeyRelease);
        getStyleClass().add("channelItem");
        setPrefHeight(getMinHeight());
        setPrefWidth(getMinWidth());
        setMaxHeight(64);
        setPadding(new Insets(12));
        setMaxWidth(width/3);
        setAlignment(Pos.CENTER);
    }

    /**
     * Activates when a key is released (typed but with less steps)
     * @param keyEvent the key event causing the method to trigger (the key the user typed/released)
     */
    private void alertKeyRelease(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER) || keyEvent.getCode().equals(KeyCode.ESCAPE)){
            onExit();
        }
    }

    /**
     * Activates when the user confirms the alert
     * @param mouseEvent the mouse click event causing the method to trigger
     */
    private void alertConfirmed(MouseEvent mouseEvent) {
        onExit();
    }

    /**
     * Defines a listener to activate when the prompt is exited
     * @param promptExitListener
     */
    public void setOnExit(PromptExitListener promptExitListener){
        this.promptExitListener = promptExitListener;
    }

    /**
     * Triggers the listener
     */
    public void onExit() {
        if (promptExitListener != null) {
            promptExitListener.onExit();
        }
    }
}
