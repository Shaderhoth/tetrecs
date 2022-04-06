package uk.ac.soton.comp1206.component;


import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
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
