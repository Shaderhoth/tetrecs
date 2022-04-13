package uk.ac.soton.comp1206.component;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * An animated image to represent the amount of time the user has remaining to make their move
 */
public class UITimer extends Canvas {
    /**
     * The width of the timer
     */
    private double width;
    /**
     * The height of the timer
     */
    private double height;
    /**
     * The Animation timer which updates the image
     */
    private AnimationTimer timer;

    /**
     * Initialise the timer
     * @param width the width of the timer object
     * @param height the height of the timer object
     * @param time the amount of time for the timer to count down
     */
    public UITimer(double width, double height, int time) {
        this.width = width;
        this.height = height;

        //A canvas needs a fixed width and height
        setWidth(width);
        setHeight(height);

        //Do an initial paint
        paint();
        resetTimer(time);

    }

    /**
     * Resize the timer
     * @param factor the scale factor by which to resize
     */
    public void rs(double factor){
        this.width = width;
        this.height = height;
        var gc = getGraphicsContext2D();
        //Clear
        gc.clearRect(0,0,width,height);
        width = width * factor;
        height = height * factor;
        setWidth(width);
        setHeight(height);
    }

    /**
     * Reset the timer once complete or if the user has made a move
     * @param time
     */
    public void resetTimer(int time){
        if (timer != null){
            timer.stop();
        }
        if (time > 0) {
            paint();
            makeTimer(time);
        }
    }

    /**
     * Create an animation to represent the countdown of the timer
     * @param time the amount of thime the timer is supposed to count down
     */
    private void makeTimer(int time){
        timer = new AnimationTimer(){
            long startTick = 0;
            @Override
            public void handle(long now){
                if(startTick == 0 ){
                    startTick = now ;
                    return;
                }
                if(((now - startTick)/1000000) < (time)){
                    paintTimer(((now - startTick)/1000000) / (double) (time));
                }else{
                    stop();
                }
            }
        };
        timer.start();
    }

    /**
     * Render them background of the timer
     */
    private void paint() {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0,0,width,height);

        //Fill
        gc.setFill(Color.rgb(0,0,0,1));
        gc.fillRect(0,0, width, height);


        //Border
        gc.setStroke(Color.WHITESMOKE);
        gc.strokeRect(0,0,width,height);
    }

    /**
     * Paint on the colour for the coloured countdown bar
     * @param percent the percent of time remaining
     */
    private void paintTimer(double percent) {
        var gc = getGraphicsContext2D();

        gc.setFill(Color.rgb(0,0,0,1));
        gc.fillRect(0,0, width, height);


        gc.setFill(Color.rgb(Math.toIntExact(Math.round(255*(percent/2+0.25))),0,Math.toIntExact(Math.round(255*(1-(percent/2+0.25)))),1));
        gc.fillRect(0,0, width*(1-percent), height);

        //Border
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0,0,width*(1-percent),height);

    }
}
