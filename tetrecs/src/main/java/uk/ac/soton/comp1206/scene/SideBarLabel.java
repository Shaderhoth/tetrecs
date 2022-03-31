package uk.ac.soton.comp1206.scene;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.*;
import javafx.util.Duration;

public class SideBarLabel extends HBox {
    private Text label;
    private String labelText;
    private boolean visible = true;
    private final boolean permanent;
    public SideBarLabel(Text text, String name){
        HBox.setHgrow(this, Priority.ALWAYS);
        this.setAlignment(Pos.CENTER);
        permanent = false;
        labelText = name + ": ";
        label = addLabel(labelText);
        getChildren().add(label);
        getChildren().add(text);
    }public SideBarLabel(){
        this.permanent = true;
        var image = new ImageView(new Image(this.getClass().getResource("/images/TetrECS.png").toExternalForm()));
        this.setAlignment(Pos.CENTER);
        image.setPreserveRatio(true);
        image.setFitWidth(128);
        getChildren().add(image);
    }private Text addLabel(String t){
        Text text = new Text(t);
        text.setFont(Font.font("style/Orbitron-Black.ttf", FontWeight.BOLD, FontPosture.REGULAR, 25));
        text.setFill(Color.WHITE);
        text.setStroke(Color.BLACK);
        text.setStrokeWidth(1);
        return text;
    }public void toggleVisibility(){
        if(visible && !permanent) {
            visible = false;
            getChildren().set(0,new Text());
        } else if(!permanent){
            visible = true;
            getChildren().set(0,label);
        }
    }
}
