package uk.ac.soton.comp1206.event;

import java.util.Timer;
/**
 * The Game Loop Listener is used for listening to cycles within the gameloop
 */
public interface GameLoopListener {
    /**
     * Handle a game loop
     * @param time the length of time for the current cycle
     */
    public void gameLoop(int time);

}
