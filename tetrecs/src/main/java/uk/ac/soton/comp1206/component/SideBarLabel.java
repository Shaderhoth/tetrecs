package uk.ac.soton.comp1206.component;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

/**
 * A label containing a name and value on the sidebar
 */
public class SideBarLabel extends HBox {
    /**
     * The value of the label (linked to a property such as the score or lives)
     */
    private Text label;
    /**
     * The descriptor of the label (a name such as 'score' or 'lives')
     */
    private String labelText;
    /**
     * Is the label visible or hidden?
     */
    private boolean visible = true;
    /**
     * Is the label permanent?
     * (Can the visibility be toggled?)
     */
    private final boolean permanent;

    /**
     * Initialise the label with the text and data
     * @param text the text containing the value e.g 0
     * @param name the name of the label e.g level or score
     */
    public SideBarLabel(Text text, String name){
        HBox.setHgrow(this, Priority.ALWAYS);
        this.setAlignment(Pos.CENTER);
        permanent = false;
        labelText = name + ": ";
        label = addLabel(labelText);
        getChildren().add(label);
        getChildren().add(text);
    }

    /**
     * Create an empty label and stick an image in it
     */
    public SideBarLabel(){
        this.permanent = true;
        var image = new ImageView(new Image(this.getClass().getResource("/images/TetrECS.png").toExternalForm()));
        this.setAlignment(Pos.CENTER);
        image.setPreserveRatio(true);
        image.setFitWidth(128);
        getChildren().add(image);
    }

    /**
     * Converts the name of the label into a text object
     * @param t the name of the label
     * @return the text object containing the name of the label
     */
    private Text addLabel(String t){
        Text text = new Text(t);
        text.setFont(Font.font("style/Orbitron-Black.ttf", FontWeight.BOLD, FontPosture.REGULAR, 25));
        text.setFill(Color.WHITE);
        text.setStroke(Color.BLACK);
        text.setStrokeWidth(1);
        return text;
    }

    /**
     * toggles the visibility of the label
     */
    public void toggleVisibility(){
        if(visible && !permanent) {
            visible = false;
            getChildren().set(0,new Text());
        } else if(!permanent){
            visible = true;
            getChildren().set(0,label);
        }
    }
}
