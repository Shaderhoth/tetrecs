package uk.ac.soton.comp1206.component;

import javafx.animation.AnimationTimer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.css.converter.ColorConverter;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.*;
import javafx.scene.shape.Polygon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Visual User Interface component representing a single block in the grid.
 *
 * Extends Canvas and is responsible for drawing itself.
 *
 * Displays an empty square (when the value is 0) or a coloured square depending on value.
 *
 * The GameBlock value should be bound to a corresponding block in the Grid model.
 */
public class GameBlock extends Canvas {

    private static final Logger logger = LogManager.getLogger(GameBlock.class);
    private int currentlyCovered = 0;
    /**
     * The set of colours for different pieces
     */
    public static final Color[] COLOURS = {
            Color.BLACK,
            Color.DEEPPINK,
            Color.RED,
            Color.ORANGE,
            Color.YELLOW,
            Color.YELLOWGREEN,
            Color.LIME,
            Color.GREEN,
            Color.DARKGREEN,
            Color.DARKTURQUOISE,
            Color.DEEPSKYBLUE,
            Color.AQUA,
            Color.AQUAMARINE,
            Color.BLUE,
            Color.MEDIUMPURPLE,
            Color.PURPLE
    };

    private final GameBoard gameBoard;

    private double width;
    private double height;

    /**
     * The column this block exists as in the grid
     */
    private final int x;

    /**
     * The row this block exists as in the grid
     */
    private final int y;

    /**
     * The value of this block (0 = empty, otherwise specifies the colour to render as)
     */
    private final IntegerProperty value = new SimpleIntegerProperty(0);

    /**
     * Create a new single Game Block
     * @param gameBoard the board this block belongs to
     * @param x the column the block exists in
     * @param y the row the block exists in
     * @param width the width of the canvas to render
     * @param height the height of the canvas to render
     */
    public GameBlock(GameBoard gameBoard, int x, int y, double width, double height) {
        this.gameBoard = gameBoard;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;

        //A canvas needs a fixed width and height
        setWidth(width);
        setHeight(height);

        //Do an initial paint
        paint();

        //When the value property is updated, call the internal updateValue method
        value.addListener(this::updateValue);
    }
    public GameBoard getGameBoard(){
        return gameBoard;
    }

    /**
     * When the value of this block is updated,
     * @param observable what was updated
     * @param oldValue the old value
     * @param newValue the new value
     */
    private void updateValue(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        paint();
    }

    /**
     * Handle painting of the block canvas
     */
    public void paint() {
        currentlyCovered = 0;
        //If the block is empty, paint as empty
        if(value.get() == 0) {
            paintEmpty();
        } else {
            //If the block is not empty, paint with the colour represented by the value
            paintColor(COLOURS[value.get()]);
        }
    }

    /**
     * Paint this canvas empty
     */
    public void fadeOut(){
        int tempColour = getValue();
        getGameBoard().grid.set(getX(),getY(),0);
        AnimationTimer timer = new AnimationTimer(){
            int speed = 100;
            int i = 100;
            long lastTick = 0;
            @Override
            public void handle(long now){
                if (i<=0){
                    int temp = currentlyCovered;
                    paint();
                    if (temp > 0){
                        paintOver(temp);
                    }
                    stop();
                }
                else if(lastTick == 0 ){
                    lastTick = now ;
                    return;
                }
                else if(now - lastTick > 1000000000 / speed){
                    paintFade(tempColour, (float) i / 100);
                    if (currentlyCovered != 0){
                        paintOver(currentlyCovered);
                    }
                    lastTick = now;
                    i--;
                }

            }
        };
        timer.start();

    }
    private void paintEmpty() {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0,0,width,height);

        //Fill
        gc.setFill(Color.rgb(0,0,0,0.3));
        gc.fillRect(0,0, width, height);


        //Border
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0,0,width,height);
    }

    /**
     * Paint this canvas with the given colour
     * @param colour the colour to paint
     */
    private void paintColor(Paint colour) {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0,0,width,height);

        //Colour fill
        gc.setFill(colour);
        gc.fillRect(0,0, width, height);
        String c = String.valueOf(colour);

        Stop[] stops = new Stop[] { new Stop(0,Color.rgb(
                Math.toIntExact(Math.round(Integer.valueOf(c.substring(2, 4), 16) * 0.8)),
                Math.toIntExact(Math.round(Integer.valueOf(c.substring(4, 6), 16) * 0.8)),
                Math.toIntExact(Math.round(Integer.valueOf(c.substring(6, 8), 16) * 0.8)),
                Math.toIntExact(Math.round(Integer.valueOf(c.substring(8, 10), 16)/255))
        )), new Stop(1, Color.rgb(
                Math.toIntExact(Math.round(Integer.valueOf(c.substring(2, 4), 16) * 0.4)),
                Math.toIntExact(Math.round(Integer.valueOf(c.substring(4, 6), 16) * 0.4)),
                Math.toIntExact(Math.round(Integer.valueOf(c.substring(6, 8), 16) * 0.4)),
                Math.toIntExact(Math.round(Integer.valueOf(c.substring(8, 10), 16)/255))
        ))};


        LinearGradient linear = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);

        gc.setFill(linear);
        gc.fillPolygon(new double[]{0, width, width}, new double[]{0, 0, height}, 3);

        //Border
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0,0,width,height);

    }

    /**
     * Get the column of this block
     * @return column number
     */
    public void drawCircle(){
        var gc = getGraphicsContext2D();
        gc.setFill(Color.rgb(0,0,0,0.5));
        gc.fillOval(0,0,width, height);
    }
    public int getX() {
        return x;
    }

    /**
     * Get the row of this block
     * @return row number
     */
    public int getY() {
        return y;
    }

    /**
     * Get the current value held by this block, representing it's colour
     * @return value
     */
    public int getValue() {
        return this.value.get();
    }

    /**
     * Bind the value of this block to another property. Used to link the visual block to a corresponding block in the Grid.
     * @param input property to bind the value to
     */
    public void bind(ObservableValue<? extends Number> input) {
        value.bind(input);
    }
    // Resize based on scale factor
    public void rs(double factor){
        var gc = getGraphicsContext2D();
        //Clear
        gc.clearRect(0,0,width,height);
        width = width * factor;
        height = height * factor;
        setWidth(width);
        setHeight(height);
        paint();
    }
    public void paintOver(int gp) {
        currentlyCovered = gp;
        Color colour = COLOURS[gp];
        var gc = getGraphicsContext2D();
        String c = String.valueOf(colour);


        //Colour fill
        gc.setFill(Color.rgb(
                Math.toIntExact(Math.round(Integer.valueOf(c.substring(2, 4), 16))),
                Math.toIntExact(Math.round(Integer.valueOf(c.substring(4, 6), 16))),
                Math.toIntExact(Math.round(Integer.valueOf(c.substring(6, 8), 16))),
                ((double) (Integer.valueOf(c.substring(8, 10), 16))/768)
        ));
        gc.fillRect(0,0, width, height);

        Stop[] stops = new Stop[] { new Stop(0,Color.rgb(
                Math.toIntExact(Math.round(Integer.valueOf(c.substring(2, 4), 16) * 0.8)),
                Math.toIntExact(Math.round(Integer.valueOf(c.substring(4, 6), 16) * 0.8)),
                Math.toIntExact(Math.round(Integer.valueOf(c.substring(6, 8), 16) * 0.8)),
                ((double) (Integer.valueOf(c.substring(8, 10), 16))/768)
        )), new Stop(1, Color.rgb(
                Math.toIntExact(Math.round(Integer.valueOf(c.substring(2, 4), 16) * 0.4)),
                Math.toIntExact(Math.round(Integer.valueOf(c.substring(4, 6), 16) * 0.4)),
                Math.toIntExact(Math.round(Integer.valueOf(c.substring(6, 8), 16) * 0.4)),
                ((double) (Integer.valueOf(c.substring(8, 10), 16))/768)
        ))};


        LinearGradient linear = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);

        gc.setFill(linear);
        gc.fillPolygon(new double[]{0, width, width}, new double[]{0, 0, height}, 3);

        //Border
        gc.setStroke(Color.WHITE);
        gc.strokeRect(0,0,width,height);
    }public void paintFade(int gp, float percent) {
        Color colour = COLOURS[gp];
        var gc = getGraphicsContext2D();
        String c = String.valueOf(colour);

        paintEmpty();
        //Colour fill
        gc.setFill(Color.rgb(
                Math.toIntExact(Math.round(Integer.valueOf(c.substring(2, 4), 16))),
                Math.toIntExact(Math.round(Integer.valueOf(c.substring(4, 6), 16))),
                Math.toIntExact(Math.round(Integer.valueOf(c.substring(6, 8), 16))),
                ((double) (Integer.valueOf(c.substring(8, 10), 16))/255*percent)
        ));
        gc.fillRect(0,0, width, height);

        Stop[] stops = new Stop[] { new Stop(0,Color.rgb(
                Math.toIntExact(Math.round(Integer.valueOf(c.substring(2, 4), 16) * 0.8)),
                Math.toIntExact(Math.round(Integer.valueOf(c.substring(4, 6), 16) * 0.8)),
                Math.toIntExact(Math.round(Integer.valueOf(c.substring(6, 8), 16) * 0.8)),
                ((double) (Integer.valueOf(c.substring(8, 10), 16))/255*percent)
        )), new Stop(1, Color.rgb(
                Math.toIntExact(Math.round(Integer.valueOf(c.substring(2, 4), 16) * 0.4)),
                Math.toIntExact(Math.round(Integer.valueOf(c.substring(4, 6), 16) * 0.4)),
                Math.toIntExact(Math.round(Integer.valueOf(c.substring(6, 8), 16) * 0.4)),
                ((double) (Integer.valueOf(c.substring(8, 10), 16))/255*percent)
        ))};


        LinearGradient linear = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);

        gc.setFill(linear);
        gc.fillPolygon(new double[]{0, width, width}, new double[]{0, 0, height}, 3);

        //Border
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0,0,width,height);
    }

}
