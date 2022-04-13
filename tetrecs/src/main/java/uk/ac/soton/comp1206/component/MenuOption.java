package uk.ac.soton.comp1206.component;


import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * A button on the menuScene to select a possible option
 */
public class MenuOption extends HBox{
    /**
     * Initialise the Menu option
     * @param name set the name of the selection option
     */
    public MenuOption(String name){
        this.setAlignment(Pos.CENTER);

        getStyleClass().add("menuItem");
        getChildren().add(addLabel(name));
    }

    /**
     * Adds the label to the button so the user knows what the button does
     * @param t the title of the label
     * @return the Text node created using the label title
     */
    private Text addLabel(String t) {
        Text text = new Text(t);
        text.getStyleClass().add("menuText");
        return text;
    }
}
