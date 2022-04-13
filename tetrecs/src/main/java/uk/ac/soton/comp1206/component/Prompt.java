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

public class Prompt extends VBox{
    private PromptExitListener promptExitListener;

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

    private void alertKeyRelease(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER) || keyEvent.getCode().equals(KeyCode.ESCAPE)){
            onExit();
        }
    }
    private void alertConfirmed(MouseEvent mouseEvent) {
        onExit();
    }
    public void setOnExit(PromptExitListener promptExitListener){
        this.promptExitListener = promptExitListener;
    }

    public void onExit() {
        promptExitListener.onExit();
    }
}
