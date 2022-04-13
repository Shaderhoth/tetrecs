package uk.ac.soton.comp1206.utilities;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.App;

public class Multimedia {
    private static MediaPlayer mediaPlayer;
    private static MediaPlayer audioPlayer;

    private static SimpleBooleanProperty audioEnabled = new SimpleBooleanProperty(true);
    private static final Logger logger = LogManager.getLogger(Multimedia.class);
    public static void playAudio(String file) {
        if (!audioEnabled.get()) return;

        String toPlay = Multimedia.class.getResource("/sounds/" + file).toExternalForm();
        logger.info("Playing audio: " + toPlay);

        try {
            Media play = new Media(toPlay);
            audioPlayer = new MediaPlayer(play);
            audioPlayer.play();
        } catch (Exception e) {
            audioEnabled.set(false);
            e.printStackTrace();
            logger.error("Unable to play audio file, disabling audio");
        }
    }public Multimedia(String file) {
        if (!audioEnabled.get()) return;

        String toPlay = Multimedia.class.getResource("/music/" + file).toExternalForm();
        logger.info("Playing music: " + toPlay);
        try {
            Media play = new Media(toPlay);
            mediaPlayer = new MediaPlayer(play);
            mediaPlayer.setAutoPlay(true);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
        } catch (Exception e) {
            audioEnabled.set(false);
            e.printStackTrace();
            logger.error("Unable to play audio file, disabling audio");
        }
    }
    public void stop(){
        mediaPlayer.stop();
    }
    public static SimpleBooleanProperty getAudioEnabled(){
        return audioEnabled;
    }
}
