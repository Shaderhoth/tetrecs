package uk.ac.soton.comp1206.component;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UITimer extends Canvas {
    private static final Logger logger = LogManager.getLogger(UITimer.class);
    private double width;
    private double height;
    private AnimationTimer timer;
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
    public void resetTimer(int time){
        if (timer != null){
            timer.stop();
        }
        if (time > 0) {
            paint();
            makeTimer(time);
        }
    }private void makeTimer(int time){
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
