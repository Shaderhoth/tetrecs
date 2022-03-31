package uk.ac.soton.comp1206.scene;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class MenuOption extends HBox{
    private double initialWidth;
    public MenuOption(String name){
        initialWidth = getWidth();
        this.setAlignment(Pos.CENTER);

        getStyleClass().add("menuItem");
        getChildren().add(addLabel(name));
    }private Text addLabel(String t) {
        Text text = new Text(t);
        text.getStyleClass().add("menuText");
        return text;
    }
}
